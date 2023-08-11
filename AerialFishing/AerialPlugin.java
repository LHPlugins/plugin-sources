package com.lhplugins.AerialFishing;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@PluginDescriptor(
        name = "<html>[<strong><font color=#87CEFA>LH</font></strong>] Aerial Fishing</html>",
        description = "Automated Aerial Fishing",
        enabledByDefault = false,
        tags = {"lh", "LH" }
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class AerialPlugin extends Plugin {
    @Inject
    private MousePackets mousePackets;
    @Inject
    private AerialConfig config;
    @Inject
    private Client client;
    @Inject
    private EthanApiPlugin api;
    @Inject
    OverlayManager overlayManager;
    @Inject
    private AerialOverlay overlay;

    private final int FISHING_SPOT = 8523;
    int timeout = 0;
    Instant timer;

    private Optional<Widget> knife = null;
    @Provides
    public AerialConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AerialConfig.class);
    }

    @Override
    public void startUp(){
        /*if (!License.checkLicense(config.license(), "LH_AERIAL_FISHING")){
            EthanApiPlugin.sendClientMessage("License has expired, please renew your license");
            EthanApiPlugin.stopPlugin(this);
            return;
        }*/
        if (client.getGameState() != GameState.LOGGED_IN) {
            return; // don't do anything if we're not logged in
        }
        knife = Inventory.search().withId(ItemID.KNIFE).first();
        timer = Instant.now();
        overlayManager.add(overlay);
    }

    @Override
    public void shutDown(){
        overlayManager.remove(overlay);
        timeout = 0;
        timer = null;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return; // don't do anything if we're not logged in
        }

        // Checks if there is any players around the user
        if (config.afkIfPlayers()){
            if (PlayersNearby()){
                return;
            }
        }

        // default timeout check
        if (timeout > 0){
            timeout--;
            handleInventory();
            if (config.debug()){
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Timeout: " + timeout, null);
            }
            return;
        }

        // Finds the nearest fishing spot near the user
        NPC fishingSpot = getNearest(FISHING_SPOT);
        if (fishingSpot == null){
            return;
        }

        // Handles the action of clicking on the fishing spot
        handleFishingSpot(fishingSpot);
    }

    private void handleFishingSpot(NPC spot){
        mousePackets.queueClickPacket(); //Simulate click
        NPCPackets.queueNPCAction(spot, "Catch"); //Click on fishing spot
        timeout = randomTick(config.maxTick());
        if (config.debug()){
            api.sendClientMessage("Clicked on fishing spot");
        }
    }

    private void handleInventory(){
        Optional<Widget> bluegill = Inventory.search().withId(ItemID.BLUEGILL).first();
        Optional<Widget> commonTench = Inventory.search().withId(ItemID.COMMON_TENCH).first();
        Optional<Widget> mottledEel = Inventory.search().withId(ItemID.MOTTLED_EEL).first();
        Optional<Widget> greaterSiren = Inventory.search().withId(ItemID.GREATER_SIREN).first();

        if (knife.isEmpty()){
            api.sendClientMessage("Knife not found in inventory");
            return;
        }

        if (bluegill.isEmpty() && commonTench.isEmpty() && mottledEel.isEmpty() && greaterSiren.isEmpty()){
            if (config.debug()){
                api.sendClientMessage("No fish found in inventory");
            }
            return;
        }

        if (commonTench.isPresent()){
            mousePackets.queueClickPacket(); //Simulate click
            mousePackets.queueClickPacket(); //Simulate click
            WidgetPackets.queueWidgetOnWidget(knife.get(), commonTench.get());
            if (config.debug()){
                api.sendClientMessage("Clicked on common tench");
            }
            return;
        }
        if (mottledEel.isPresent()){
            mousePackets.queueClickPacket(); //Simulate click
            mousePackets.queueClickPacket(); //Simulate click
            WidgetPackets.queueWidgetOnWidget(knife.get(), mottledEel.get());
            if (config.debug()){
                api.sendClientMessage("Clicked on mottled eel");
            }
            return;
        }
        if (greaterSiren.isPresent()){
            mousePackets.queueClickPacket(); //Simulate click
            mousePackets.queueClickPacket(); //Simulate click
            WidgetPackets.queueWidgetOnWidget(knife.get(), greaterSiren.get());
            if (config.debug()){
                api.sendClientMessage("Clicked on greater siren");
            }
            return;
        }
        if (bluegill.isPresent()){
            mousePackets.queueClickPacket(); //Simulate click
            mousePackets.queueClickPacket(); //Simulate click
            WidgetPackets.queueWidgetOnWidget(knife.get(), bluegill.get());
            if (config.debug()){
                api.sendClientMessage("Clicked on bluegill");
            }
            return;
        }
        if (config.debug()){
            api.sendClientMessage("No fish found in inventory");
        }

    }

    private int randomTick(int maxTick){
        Random random = new Random();
        return random.nextInt(maxTick) + 1;
    }

    public NPC getNearest(int npcId){
        NPC nearest = NPCs.search().withId(npcId).nearestToPlayer().orElse(null);
        return nearest;
    }

    private boolean PlayersNearby(){
        return client.getPlayers().size() > 1;
    }

}
