package eu.dsix.magicbees.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class MagicBeesConfig {
    public static final Common COMMON;
    public static final Client CLIENT;
    private static final ModConfigSpec COMMON_SPEC;
    private static final ModConfigSpec CLIENT_SPEC;

    static {
        var common = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        COMMON_SPEC = common.getRight();

        var client = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
    }

    private MagicBeesConfig() {
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }

    public static final class Common {
        public final ModConfigSpec.BooleanValue magnetSound;
        public final ModConfigSpec.IntValue magnetMaxLevel;
        public final ModConfigSpec.DoubleValue magnetBaseRange;
        public final ModConfigSpec.DoubleValue magnetLevelMultiplier;
        public final ModConfigSpec.BooleanValue moonDialShowsPhaseInText;
        public final ModConfigSpec.BooleanValue showAllBees;

        public final ModConfigSpec.BooleanValue postGenRedstone;
        public final ModConfigSpec.BooleanValue postGenNetherQuartz;
        public final ModConfigSpec.BooleanValue postGenGlowstone;
        public final ModConfigSpec.BooleanValue postGenObsidianSpikes;
        public final ModConfigSpec.BooleanValue postGenEndstone;

        public final ModConfigSpec.DoubleValue hiveacynthPrincessSpawnRate;
        public final ModConfigSpec.DoubleValue hiveacynthPristineRate;
        public final ModConfigSpec.DoubleValue hiveacynthManaMultiplier;
        public final ModConfigSpec.DoubleValue hiveacynthRainResistRate;
        public final ModConfigSpec.DoubleValue hibeescusTicksMultiplier;
        public final ModConfigSpec.DoubleValue hibeescusManaCostMultiplier;
        public final ModConfigSpec.DoubleValue beegoniaManaMultiplier;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("general");
            magnetSound = builder.comment("Enables/disables the Mysterious Magnet sound.")
                    .define("magnetSound", true);
            magnetMaxLevel = builder.comment("Maximum Mysterious Magnet level.")
                    .defineInRange("magnetMaxLevel", 9, 1, 16);
            magnetBaseRange = builder.comment("Base Mysterious Magnet range.")
                    .defineInRange("magnetBaseRange", 3.0, 1.0, 8.0);
            magnetLevelMultiplier = builder.comment("Magnet range = base range + level * multiplier.")
                    .defineInRange("magnetLevelMultiplier", 0.75, 0.1, 8.0);
            moonDialShowsPhaseInText = builder.comment("Show the current moon phase in the Moon Dial tooltip.")
                    .define("moonDialShowsPhaseInText", true);
            showAllBees = builder.comment(
                            "Show ordinary Magic Bees species that legacy branch registration marked as secret. " +
                                    "The five explicitly secret Skulking species remain secret.")
                    .define("showAllBees", true);
            builder.pop();

            builder.push("worldgen");
            postGenRedstone = builder.comment("Generate redstone pockets around Deep hives in the Overworld.")
                    .define("postGenRedstone", true);
            postGenNetherQuartz = builder.comment("Generate nether quartz pockets around Infernal hives in the Nether.")
                    .define("postGenNetherQuartz", true);
            postGenGlowstone = builder.comment("Generate glowstone pockets around Infernal hives generated in the Overworld.")
                    .define("postGenGlowstone", true);
            postGenObsidianSpikes = builder.comment("Generate obsidian spikes under Oblivion hives in the End.")
                    .define("postGenObsidianSpikes", true);
            postGenEndstone = builder.comment("Generate end stone pockets around Oblivion hives generated in the Overworld.")
                    .define("postGenEndstone", true);
            builder.pop();

            builder.push("botania");
            hiveacynthPrincessSpawnRate = builder.comment("Hiveacynth princess spawn rate.")
                    .defineInRange("hiveacynthPrincessSpawnRate", 0.09, 0.0, 1.0);
            hiveacynthPristineRate = builder.comment("Hiveacynth chance for a generated princess to be pristine.")
                    .defineInRange("hiveacynthPristineRate", 0.15, 0.0, 1.0);
            hiveacynthManaMultiplier = builder.comment("Hiveacynth mana multiplier.")
                    .defineInRange("hiveacynthManaMultiplier", 1.0, 0.0, 1.0);
            hiveacynthRainResistRate = builder.comment("Chance a Hiveacynth generates a rain-resistant bee.")
                    .defineInRange("hiveacynthRainResistRate", 0.1, 0.0, 1.0);
            hibeescusTicksMultiplier = builder.comment("Hibeescus processing-time multiplier.")
                    .defineInRange("hibeescusTicksMultiplier", 1.0, 0.0, 2.0);
            hibeescusManaCostMultiplier = builder.comment("Hibeescus mana-cost multiplier.")
                    .defineInRange("hibeescusManaCostMultiplier", 1.0, 0.0, 16.0);
            beegoniaManaMultiplier = builder.comment("Beegonia mana-generation multiplier.")
                    .defineInRange("beegoniaManaMultiplier", 1.0, 0.0, 16.0);
            builder.pop();
        }
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue fancyJarRenderer;
        public final ModConfigSpec.BooleanValue oldJarModel;

        private Client(ModConfigSpec.Builder builder) {
            fancyJarRenderer = builder.comment("Render the contained bee in the Effect Jar.")
                    .define("fancyJarRenderer", true);
            oldJarModel = builder.comment("Use the legacy Effect Jar model when the modern renderer supports it.")
                    .define("oldJarmodel", false);
        }
    }
}
