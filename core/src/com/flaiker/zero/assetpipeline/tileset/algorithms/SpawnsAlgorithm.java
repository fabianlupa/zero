/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline.tileset.algorithms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;

/**
 * Implementation of {@link TilepropertiesWriter} specific for spawns
 */
public class SpawnsAlgorithm implements TilepropertiesWriter {
    @Override
    public void write(TextureAtlas.TextureAtlasData data, XmlWriter writer) throws IOException {
        int atlasId = 0;

        for (TextureAtlas.TextureAtlasData.Region region : data.getRegions()) {
            // @formatter:off
            writer.element("tile").attribute("id", atlasId);
                writer.element("properties");
                    writer.element("property").attribute("name", "type")
                                              .attribute("value", region.name);
                    writer.pop();
                writer.pop();
            writer.pop();
            // @formatter:on

            atlasId++;
        }
    }
}
