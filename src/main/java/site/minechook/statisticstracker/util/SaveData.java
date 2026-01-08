package site.minechook.statisticstracker.util;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public List<SerializableBlockStatistic> blockBrokenStatistics = new ArrayList<>();
    public boolean toggled = false;
    public int color = 0xFFFFFFFF;
    public Util.CornerPositions cornerPosition = Util.CornerPositions.TOP_LEFT;
    
    public static class SerializableBlockStatistic {
        public String blockId;
        public boolean toggled;
        
        public SerializableBlockStatistic(String blockId, boolean toggled) {
            this.blockId = blockId;
            this.toggled = toggled;
        }
    }
}
