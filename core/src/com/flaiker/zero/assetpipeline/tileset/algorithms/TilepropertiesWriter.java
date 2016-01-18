/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline.tileset.algorithms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;

/**
 * Interface for tileproperties serialization algorithms used by {@link
 * com.flaiker.zero.assetpipeline.tileset.TilesetSerializer}
 */
public interface TilepropertiesWriter {
    /**
     * Write tileproperties to tileset file
     *
     * @param data   Tiles to be used
     * @param writer Reference to the xml writer
     * @throws IOException
     */
    void write(TextureAtlas.TextureAtlasData data, XmlWriter writer) throws IOException;
}
