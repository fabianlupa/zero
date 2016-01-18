/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Generate texture atlases for blocks, entities and spawns
 */
public class AtlasGenerator {
    private final TexturePacker.Settings texturePackerSettings;
    private final Settings               settings;

    /**
     * Constructor using default {@link Settings}
     */
    public AtlasGenerator() {
        texturePackerSettings = new TexturePacker.Settings();
        texturePackerSettings.grid = true;

        settings = new Settings();
    }

    /**
     * Constructor using custom settings
     *
     * @param texturePackerSettings Settings for TexturePacker
     * @param settings              Settings for AtlasGenerator
     */
    public AtlasGenerator(TexturePacker.Settings texturePackerSettings, Settings settings) {
        this.texturePackerSettings = texturePackerSettings;
        this.settings = settings;
    }

    /**
     * Start atlas generation using the configuration
     */
    public void generate() {
        generateEntityAtlas();
        generateBlockAtlas();
        generateSpawnAtlas();
    }

    private void generateBlockAtlas() {
        TexturePacker.processIfModified(texturePackerSettings, settings.blocksInputPath, settings.outputPath, "blocks");
    }

    private void generateEntityAtlas() {
        TexturePacker.processIfModified(texturePackerSettings, settings.entityInputPath, settings.outputPath,
                                        "entities");
    }

    private void generateSpawnAtlas() {
        TexturePacker.processIfModified(texturePackerSettings, settings.spawnsInputPath, settings.outputPath, "spawns");
    }

    /**
     * Configuration for {@link AtlasGenerator}, contains default values
     */
    public static final class Settings {
        public String entityInputPath = "entities";
        public String blocksInputPath = "blocks/generated";
        public String spawnsInputPath = "spawns";
        public String outputPath      = "atlases";

        public Settings() {
        }

        public Settings(String entityInputPath, String blocksInputPath, String spawnsInputPath, String outputPath) {
            this.entityInputPath = entityInputPath;
            this.blocksInputPath = blocksInputPath;
            this.spawnsInputPath = spawnsInputPath;
            this.outputPath = outputPath;
        }
    }
}
