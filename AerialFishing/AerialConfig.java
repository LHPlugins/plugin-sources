package com.lhplugins.AerialFishing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AerialFishing")
public interface AerialConfig extends Config {

    @ConfigSection(
            name = "Instructions",
            description = "",
            position = 0
    )
    String instructionsTitle = "instructionsTitle";

    @ConfigSection(
            name = "Configuration",
            description = "",
            position = 2,
            closedByDefault = true
    )
    String configurationTitle = "configurationTitle";

    @ConfigSection(
            name = "Debug",
            description = "",
            position = 4,
            closedByDefault = true
    )
    String debugTitle = "debugTitle";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions. Don't enter anything into this field",
            position = -1,
            section = "instructionsTitle"
    )
    default String instructions(){
        return "Have knife in inventory and cormorant's glove equipped.";
    }

    /*
        **Configuration section**
     */

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable UI",
            position = 0,
            section = "configurationTitle"
    )
    default boolean enableUI() {
        return true;
    }

    @ConfigItem(
            keyName = "MaxTick",
            name = "Maximum Tick",
            description = "Maximum tick to wait before clicking again",
            position = 1,
            section = "configurationTitle"
    )
    default int maxTick() {
        return 3;
    }

    @ConfigItem(
            keyName = "afkIfOtherPlayers",
            name = "AFK if other players",
            description = "AFK if other players",
            position = 2,
            section = "configurationTitle"
    )
    default boolean afkIfPlayers() {
        return false;
    }

    /*
        **Debug section**
     */

    @ConfigItem(
            keyName = "Debug",
            name = "Debug",
            description = "Debug",
            position = 2,
            section = "debugTitle"
    )
    default boolean debug() {
        return false;
    }
}
