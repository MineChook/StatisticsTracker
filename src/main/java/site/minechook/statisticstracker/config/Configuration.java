package site.minechook.statisticstracker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import site.minechook.statisticstracker.RenderOverlay;
import site.minechook.statisticstracker.StatisticsTrackerClient;
import site.minechook.statisticstracker.util.SaveData;
import site.minechook.statisticstracker.util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_PATH = new File("config/statisticstracker.json");

    private static boolean toggled = true;
    private static int color = 0xFFFFFFFF;
    private static Util.CornerPositions cornerPosition = Util.CornerPositions.TOP_LEFT;

    public static List<Util.BlockBrokenStatistic> blockBrokenStatistics;

    private static List<Util.BlockBrokenStatistic> initializeBlocksList() {
        List<Util.BlockBrokenStatistic> blocksMap = new ArrayList<>();
        for (Block block : Registries.BLOCK) {
            blocksMap.add(new Util.BlockBrokenStatistic(block));
        }
        return blocksMap;
    }

    public static boolean isToggled() {
        return toggled;
    }

    public static void setToggled(boolean toggled) {
        Configuration.toggled = toggled;
    }

    public static int getColor() {
        return color;
    }

    public static void setColor(int color) {
        Configuration.color = color;
    }

    public static Util.CornerPositions getCornerPosition() {
        return cornerPosition;
    }

    public static void setCornerPosition(Util.CornerPositions cornerPosition) {
        Configuration.cornerPosition = cornerPosition;
    }

    public static Screen getConfigScreen(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("category.minechook.statistics_tracker"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.setSavingRunnable(Configuration::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("clothconfig-category.minechook.statistics_tracker"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("key.minechook.statistics_tracker.toggle"), toggled)
                .setDefaultValue(false)
                .setSaveConsumer(Configuration::setToggled)
                .build());

        general.addEntry(entryBuilder.startAlphaColorField(Text.translatable("color.minechook.statistics_tracker.text_color"), color)
                .setDefaultValue(0xFFFFFFFF)
                .setSaveConsumer(Configuration::setColor)
                .build());

        general.addEntry(entryBuilder.startEnumSelector(Text.translatable("position.minechook.statistics_tracker"), Util.CornerPositions.class, cornerPosition)
                .setDefaultValue(Util.CornerPositions.TOP_LEFT)
                .setSaveConsumer(Configuration::setCornerPosition)
                .build());

        ConfigCategory blocksBroken = builder.getOrCreateCategory(Text.translatable("clothconfig-category.minechook.statistics_tracker.blocks_broken"));

        for (Util.BlockBrokenStatistic blockBrokenStatistic : blockBrokenStatistics) {
            String blockTranslateKey = blockBrokenStatistic.block.getTranslationKey();
            blocksBroken.addEntry(entryBuilder.startBooleanToggle(Text.translatable(blockTranslateKey), blockBrokenStatistic.toggled)
                    .setDefaultValue(false)
                    .setSaveConsumer(value -> blockBrokenStatistic.toggled = value)
                    .build()
            );
        }

        return builder.build();

    }

    private static void save() {
        blockBrokenStatistics = RenderOverlay.blockBrokenStatistics;
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            SaveData config = new SaveData();
            config.toggled = toggled;
            config.color = color;
            config.cornerPosition = cornerPosition;
            config.blockBrokenStatistics = new ArrayList<>();
            for (Util.BlockBrokenStatistic stat : blockBrokenStatistics) {
                String blockId = Registries.BLOCK.getId(stat.block).toString();
                config.blockBrokenStatistics.add(new SaveData.SerializableBlockStatistic(blockId, stat.toggled));
            }
        
            GSON.toJson(config, writer);
        } catch (IOException e) {
            StatisticsTrackerClient.LOGGER.warning("Failed to save config: " + e.getMessage());
        }
    }

    public static void load() {
        if (!CONFIG_PATH.exists()) {
            CONFIG_PATH.getParentFile().mkdirs();
            save();
            RenderOverlay.blockBrokenStatistics = initializeBlocksList();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            SaveData config = GSON.fromJson(reader, SaveData.class);
            if (config != null && config.blockBrokenStatistics != null && !config.blockBrokenStatistics.isEmpty()) {
                blockBrokenStatistics = new ArrayList<>();
                for (SaveData.SerializableBlockStatistic serializable : config.blockBrokenStatistics) {
                    Block block = Registries.BLOCK.get(Identifier.of(serializable.blockId));
                    if (block != null) {
                        Util.BlockBrokenStatistic stat = new Util.BlockBrokenStatistic(block, 0, serializable.toggled);
                        blockBrokenStatistics.add(stat);
                    }
                }
                toggled = config.toggled;
                color = config.color;
                cornerPosition = config.cornerPosition;
            } else {
                blockBrokenStatistics = initializeBlocksList();
            }
            RenderOverlay.blockBrokenStatistics = blockBrokenStatistics;
        } catch (Exception e) {
            StatisticsTrackerClient.LOGGER.warning("Failed to load config, using defaults: " + e.getMessage());
            blockBrokenStatistics = initializeBlocksList();
            save();
            RenderOverlay.blockBrokenStatistics = blockBrokenStatistics;
        }
    }
}
