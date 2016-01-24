/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.*;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.GL30;
import com.flaiker.zero.assetpipeline.AssetBuilder;
import com.flaiker.zero.assetpipeline.AtlasGenerator;
import com.flaiker.zero.assetpipeline.tileset.TilesetSerializer;
import com.flaiker.zero.assetpipeline.tileset.algorithms.BlocksAlgorithm;
import com.flaiker.zero.assetpipeline.tileset.algorithms.SpawnsAlgorithm;
import com.flaiker.zero.tiles.TileRegistry;
import org.mockito.Mockito;

/**
 * Launcher to start a full asset regeneration. Can be invoked using the gradle task assets (gradlew desktop:assets)
 */
public class AssetProductionLauncher {
    public static void main(String[] args) {
        // Initialize LibGDX for command line usage
        HeadlessNativesLoader.load();
        Gdx.graphics = new MockGraphics();
        Gdx.net = new HeadlessNet();
        Gdx.files = new HeadlessFiles();
        Gdx.gl = Mockito.mock(GL30.class);

        new HeadlessApplication(new AssetPipelineApplication(), new HeadlessApplicationConfiguration());
    }

    public static class AssetPipelineApplication extends ApplicationAdapter {
        public static final String LOG = "AssetPipeline";

        private AssetBuilder      assetBuilder;
        private AtlasGenerator    atlasGenerator;
        private TilesetSerializer tilesetSerializer;

        public AssetPipelineApplication() {
            assetBuilder = new AssetBuilder();
            atlasGenerator = new AtlasGenerator();
            tilesetSerializer = new TilesetSerializer();
        }

        @Override
        public void create() {
            Gdx.app.log(LOG, "Initializing TileRegistry");
            TileRegistry.initialize();

            Gdx.app.log(LOG, "Starting asset tasks");

            try {
                Gdx.app.log(LOG, "Rebuilding blocks");
                assetBuilder.rebuildBlocks();
                Gdx.app.log(LOG, "Generating atlases");
                atlasGenerator.generate();
                Gdx.app.log(LOG, "Generating tilesets");
                tilesetSerializer.serialize(Gdx.files.local("atlases/blocks.atlas"),
                                            Gdx.files.local("tilesets/blocks.xml"), new BlocksAlgorithm());
                tilesetSerializer.serialize(Gdx.files.local("atlases/spawns.atlas"),
                                            Gdx.files.local("tilesets/spawns.xml"), new SpawnsAlgorithm());

            } catch (Exception e) {
                Gdx.app.log(LOG, "Failure: " + e.toString());
            }

            Gdx.app.log(LOG, "Finished assets tasks");
            Gdx.app.exit();
        }
    }
}
