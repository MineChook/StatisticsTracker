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
                    if (Configuration.getCornerPosition() == null) return;
                    int x = 0, y = 0;
                    boolean bottom = false;
                    if (Configuration.getCornerPosition() == Util.CornerPositions.TOP_LEFT) {
                        x = 10;
                        y = 10;
                    }
                    if (Configuration.getCornerPosition() == Util.CornerPositions.TOP_RIGHT) {
                        x = MinecraftClient.getInstance().getWindow().getScaledWidth() - text.toString().length() / 2 * 9;
                        y = 10;
                    }
                    if (Configuration.getCornerPosition() == Util.CornerPositions.BOTTOM_LEFT) {
                        x = 10;
                        y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 20;
                        bottom = true;
                    }
                    if (Configuration.getCornerPosition() == Util.CornerPositions.BOTTOM_RIGHT) {
                        x = MinecraftClient.getInstance().getWindow().getScaledWidth() - text.toString().length() / 2 * 9;
                        y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 20;
                        bottom = true;
                    }
                    if (bottom) {
                        drawContext.drawText(client.textRenderer, text, x, y - (i * 10), Configuration.getColor(), false);
                    } else {
                        drawContext.drawText(client.textRenderer, text, x, y + (i * 10), Configuration.getColor(), false);
                    }
                    i++;
                }
            }
        }
    }
}
