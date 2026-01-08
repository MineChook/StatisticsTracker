package site.minechook.statisticstracker.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public List<SerializableBlockStatistic> blockBrokenStatistics = new ArrayList<>();
    public boolean toggled = false;
    
    public static class SerializableBlockStatistic {
        public String blockId;
        public boolean toggled;
        
        public SerializableBlockStatistic(String blockId, boolean toggled) {
            this.blockId = blockId;
            this.toggled = toggled;
        }
    }
}
