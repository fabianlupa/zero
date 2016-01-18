/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline.tileset;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.XmlWriter;
import com.flaiker.zero.assetpipeline.tileset.algorithms.TilepropertiesWriter;

import java.io.IOException;

/**
 * Serialize texture atlases to Tiled-compatible tileset files
 */
public class TilesetSerializer {
    /**
     * Generate tileset file using default serialization algorithm
     *
     * @param atlasFile  FileHandle to the atlas to be serialized
     * @param outputFile FileHandle to the output location/file
     * @throws IOException
     */
    public void serialize(FileHandle atlasFile, FileHandle outputFile) throws IOException {
        this.serialize(atlasFile, outputFile, null);
    }

    /**
     * Generate tileset file using the specified serialization algorithm
     *
     * @param atlasFile            FileHandle to the atlas to be serialized
     * @param outputFile           FileHandle to the output location/file
     * @param tilepropertiesWriter Serialization algorithm for tileproperties
     * @throws IOException
     */
    public void serialize(FileHandle atlasFile, FileHandle outputFile, TilepropertiesWriter tilepropertiesWriter)
            throws IOException {
        TextureAtlas.TextureAtlasData data = new TextureAtlas.TextureAtlasData(atlasFile, atlasFile.parent(), false);

        if (data.getPages().size == 0 || data.getRegions().size == 0)
            throw new IllegalArgumentException("Atlas has no regions/pages");

        XmlWriter writer = new XmlWriter(outputFile.writer(false));

        // @formatter:off
        writer.element("tileset").attribute("firstgid", 1)
                                 .attribute("name", data.getPages().first().textureFile.nameWithoutExtension())
                                 .attribute("tilewidth", 64)
                                 .attribute("tileheight", 64)
                                 .attribute("spacing", 2).attribute("margin", 2);
            writer.element("image").attribute("source", "../" + data.getPages().first().textureFile.path())
                                   .attribute("width", (int) data.getPages().first().width)
                                   .attribute("height", (int) data.getPages().first().height);
            writer.pop();
            if (tilepropertiesWriter != null) tilepropertiesWriter.write(data, writer);
        writer.pop();
        writer.flush();
        // @formatter:on
    }
}
