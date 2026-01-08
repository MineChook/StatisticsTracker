package site.minechook.statisticstracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import org.lwjgl.glfw.GLFW;
import site.minechook.statisticstracker.config.Configuration;
import site.minechook.statisticstracker.util.Util;

import java.util.logging.Logger;

public class StatisticsTrackerClient implements ClientModInitializer {

    private static int keybindCooldown = 0;
    private static int sendPacketCooldown = 0;
    private static final KeyBinding keyBinding = new KeyBinding("key.minechook.statistics_tracker.toggle", GLFW.GLFW_KEY_F10, "category.minechook.statistics_tracker");

    public static final Logger LOGGER = Logger.getLogger("statisticstracker");

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.isPressed()) {
                if (keybindCooldown > 0) {
                    keybindCooldown--;
                    return;
                }
                Configuration.setToggled(!Configuration.isToggled());
                keybindCooldown = 10;
            }
            keybindCooldown--;
            if (sendPacketCooldown > 0) {
                sendPacketCooldown --;
            } else {
                blockBrokenStatisticUpdate();
                sendPacketCooldown = 20;
            }
        });

        KeyBindingHelper.registerKeyBinding(keyBinding);
        HudRenderCallback.EVENT.register(RenderOverlay::renderGameOverlayEvent);

        Configuration.load();
    }

    private void blockBrokenStatisticUpdate() {
        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;
        networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
        for (Util.BlockBrokenStatistic blockBrokenStatistic : RenderOverlay.blockBrokenStatistics) {
            blockBrokenStatistic.count = Util.getBlockMinedStatistics(blockBrokenStatistic.block);
        }
    }
}
