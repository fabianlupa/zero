/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline.tileset.algorithms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link TilepropertiesWriter} specific for blocks
 */
public class BlocksAlgorithm implements TilepropertiesWriter {
    @Override
    public void write(TextureAtlas.TextureAtlasData data, XmlWriter writer) throws IOException {
        Pattern pattern = Pattern.compile("^(?<id>\\d\\d)-(?<mat>\\S*)-(?<dir>\\S*)$");
        Pattern patternWithoutEdges = Pattern.compile("^(?<id>\\d\\d)-(?<mat>\\S*)$");

        int atlasId = 0;

        for (TextureAtlas.TextureAtlasData.Region region : data.getRegions()) {
            Matcher matcher = pattern.matcher(region.name);
            Matcher matcher2 = patternWithoutEdges.matcher(region.name);
            if (matcher.find()) {
                String id = matcher.group("id");
                String name = matcher.group("mat");
                String direction = matcher.group("dir");

                // @formatter:off
                writer.element("tile").attribute("id", atlasId);
                    writer.element("properties");
                        writer.element("property").attribute("name", "name")
                                                  .attribute("value", name);
                        writer.pop();
                        writer.element("property").attribute("name", "id")
                                                  .attribute("value", id);
                        writer.pop();
                        writer.element("property").attribute("name", "direction")
                                                  .attribute("value", direction);
                        writer.pop();
                    writer.pop();
                writer.pop();
                // @formatter:on

                atlasId++;
            } else if (matcher2.find()) {
                String id = matcher2.group("id");
                String name = matcher2.group("mat");

                // @formatter:off
                writer.element("tile").attribute("id", atlasId);
                    writer.element("properties");
                        writer.element("property").attribute("name", "name")
                                                  .attribute("value", name);
                        writer.pop();
                        writer.element("property").attribute("name", "id")
                                                  .attribute("value", id);
                        writer.pop();
                    writer.pop();
                writer.pop();
                // @formatter:on

                atlasId++;
            }
        }
    }
}
