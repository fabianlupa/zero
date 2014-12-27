package com.flaiker.zero.services;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Flaiker on 26.12.2014.
 */
public class ResourceManager {
    private static final String ATLAS_INPUT_ENTITIES_PATH = "entities";
    private static final String ATLAS_INPUT_BLOCKS_PATH   = "blocks";
    private static final String ATLAS_INPUT_SPAWNS_PATH   = "spawns";
    private static final String ATLAS_OUTPUT_PATH         = "atlases";

    private static final String[] INGAME_ATLAS_PATHS = {"atlases/blocks.png", "atlases/entities.png"};

    private AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();
    }

    public void refreshAtlases() {
        // entities
        TexturePacker.process(ATLAS_INPUT_ENTITIES_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_ENTITIES_PATH);
        // blocks
        TexturePacker.process(ATLAS_INPUT_BLOCKS_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_BLOCKS_PATH);
        // spawns
        TexturePacker.process(ATLAS_INPUT_SPAWNS_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_SPAWNS_PATH);
    }

    public void loadIngameAssets() {
        for (String atlasPath : INGAME_ATLAS_PATHS) {
            assetManager.load(atlasPath, TextureAtlas.class);
        }
    }

    public void unloadIngameAssets() {
        for (String atlasPath : INGAME_ATLAS_PATHS) {
            assetManager.unload(atlasPath);
        }
    }

    public float getLoadingPercent() {
        return assetManager.getProgress();
    }

    public boolean isDoneLoading() {
        return assetManager.update();
    }
}
