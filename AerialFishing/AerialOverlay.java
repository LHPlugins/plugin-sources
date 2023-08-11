package com.lhplugins.AerialFishing;

import javax.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Slf4j
@Singleton
public class AerialOverlay extends OverlayPanel {

    private final AerialPlugin plugin;
    private final AerialConfig config;
    String timeFormat;

    @Inject
    private AerialOverlay(final Client client, final AerialPlugin plugin, final AerialConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Aerial Fishing overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.timer == null || !config.enableUI())
        {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }

        Duration duration = Duration.between(plugin.timer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        String formatted = formatDuration(duration.toMillis(), timeFormat);
        //panelComponent.setBackgroundColor(ColorUtil.fromHex("#121212")); //Material Dark default
        panelComponent.setPreferredSize(new Dimension(150, 100));
        //panelComponent.setBorder(new Rectangle(5, 5, 5, 5));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("LH's Aerial Fishing")
                .color(ColorUtil.fromHex("#40C4FF"))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time:")
                .right(formatted)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Timeout:")
                .right(plugin.timeout + "")
                .build());

        return super.render(graphics);
    }

}
