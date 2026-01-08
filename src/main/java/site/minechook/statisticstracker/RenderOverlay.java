package site.minechook.statisticstracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import site.minechook.statisticstracker.config.Configuration;
import site.minechook.statisticstracker.util.Util;

import java.util.List;

public class RenderOverlay {

    public static List<Util.BlockBrokenStatistic> blockBrokenStatistics;

    public static void renderGameOverlayEvent(DrawContext drawContext, RenderTickCounter renderTickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (Configuration.isToggled()) {
            int i = 0;
            for (Util.BlockBrokenStatistic blockBrokenStatistic : blockBrokenStatistics) {
                if (blockBrokenStatistic.toggled) {
                    Text blockName = Text.translatable(blockBrokenStatistic.block.getTranslationKey());
                    Text text = Text.literal(blockName.getString() + ": " + blockBrokenStatistic.count);
                    drawContext.drawText(client.textRenderer, text, 20, 20 - (i * 10), 0xFF000000, false);
                    i++;
                }
            }
        }
    }
}
