package site.minechook.statisticstracker.util;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.stat.Stats;

public class Util {

    public static class BlockBrokenStatistic {
        public Block block;
        public int count;
        public boolean toggled;

        public BlockBrokenStatistic(Block block) {
            this(block, 0, false);
        }

        public BlockBrokenStatistic(Block block, int count, boolean toggled) {
            this.block = block;
            this.count = count;
            this.toggled = toggled;
        }
    }

    public static int getBlockMinedStatistics(Block block) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return 0;
        return client.player.getStatHandler().getStat(Stats.MINED.getOrCreateStat(block));
    }
}
