/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.services.rmtasks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Flaiker on 27.12.2014.
 */
public class LoadIngameAssetsTask extends AbstractTask {
    private static final String[] INGAME_ATLAS_PATHS = {"atlases/blocks.atlas", "atlases/entities.atlas"};
    private AssetManager assetManager;
    private boolean      started;

    public LoadIngameAssetsTask(AssetManager assetManager) {
        super(2f, false, "Loading ingame assets...");
        this.assetManager = assetManager;
        started = false;
    }

    @Override
    public void run() {
        started = true;
        for (String atlasPath : INGAME_ATLAS_PATHS) {
            assetManager.load(atlasPath, TextureAtlas.class);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDone() {
        return started && assetManager.update();
    }

    @Override
    public float getPercentageCompleted() {
        return assetManager.getProgress();
    }
}
