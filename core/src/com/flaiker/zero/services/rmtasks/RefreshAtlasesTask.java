/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.services.rmtasks;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Flaiker on 27.12.2014.
 */
public class RefreshAtlasesTask extends AbstractTask {
    private static final String ATLAS_INPUT_ENTITIES_PATH = "entities";
    private static final String ATLAS_INPUT_BLOCKS_PATH   = "blocks";
    private static final String ATLAS_INPUT_SPAWNS_PATH   = "spawns";
    private static final String ATLAS_OUTPUT_PATH         = "atlases";

    private boolean done = false;

    public RefreshAtlasesTask() {
        super(0.5f, true, "Generating textureatlases...");
    }

    @Override
    public void run() {
        // entities
        TexturePacker.process(ATLAS_INPUT_ENTITIES_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_ENTITIES_PATH);
        // blocks
        TexturePacker.process(ATLAS_INPUT_BLOCKS_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_BLOCKS_PATH);
        // spawns
        TexturePacker.process(ATLAS_INPUT_SPAWNS_PATH, ATLAS_OUTPUT_PATH, ATLAS_INPUT_SPAWNS_PATH);

        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public float getPercentageCompleted() {
        return 0;
    }
}
