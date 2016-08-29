/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.assetpipeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.flaiker.zero.tiles.BlockWrapper;
import com.flaiker.zero.tiles.TileRegistry;

import java.util.HashMap;
import java.util.Set;

/**
 * Creates asset files
 */
public class AssetBuilder {
    private static final String BLOCK_PATH         = "blocks";
    private static final String BLOCK_OVERLAY_PATH = "blocks/overlays";
    private static final String BLOCK_OUTPUT_PATH  = "blocks/generated";

    public AssetBuilder() {
    }

    /**
     * Build block assets using all registered blocks in {@link TileRegistry}
     * <p>
     * Overlays them with overlays in {@link #BLOCK_OVERLAY_PATH} if specified and outputs them to {@link
     * #BLOCK_OUTPUT_PATH}. Assumes there is a corresponding image file in {@link #BLOCK_PATH} which uses the format
     * "id-name.png". Output directory will be cleared beforehand.
     */
    public void rebuildBlocks() {
        // Delete files from previous runs
        clearOutputDir();

        // Get all Blocks from TileRegistry
        TileRegistry registry = TileRegistry.getInstance();
        Set<BlockWrapper> blocks = registry.getBlockWrappers();
        if (blocks.size() == 0) throw new UnsupportedOperationException("Could not find any blocks to process");

        // Load overlays
        FileHandle overlayFolder = Gdx.files.local(BLOCK_OVERLAY_PATH);
        HashMap<String, Pixmap> overlays = new HashMap<>();
        for (FileHandle fh : overlayFolder.list()) {
            if (!fh.isDirectory()) overlays.put(fh.nameWithoutExtension(), new Pixmap(fh));
        }

        // Overlay each block with all overlays and save them to temporary directory
        for (BlockWrapper block : blocks) {
            FileHandle blockImage = new FileHandle(BLOCK_PATH + "/" + String.format("%02d", block.blockMetadata.id) +
                    "-" + block.blockMetadata.name + ".png");
            if (!blockImage.exists() || blockImage.isDirectory()) continue;
            if (block.blockMetadata.edged) {
                final Pixmap pixmap = new Pixmap(blockImage);
                PixmapIO.writePNG(Gdx.files.local(BLOCK_OUTPUT_PATH + "/" + String.format("%02d", block.blockMetadata.id) +
                                "-" + block.blockMetadata.name + "-b.png"),
                        pixmap);
                overlays.forEach((name, overlay) -> {
                    Pixmap.setBlending(Pixmap.Blending.SourceOver);
                    Pixmap newPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
                    newPixmap.drawPixmap(pixmap, 0, 0);
                    newPixmap.drawPixmap(overlay, 0, 0);
                    PixmapIO.writePNG(Gdx.files.local(BLOCK_OUTPUT_PATH + "/" +
                                    String.format("%02d", block.blockMetadata.id) + "-" +
                                    block.blockMetadata.name + "-" + name + ".png"),
                            newPixmap);
                });
            } else {
                blockImage.copyTo(new FileHandle(BLOCK_OUTPUT_PATH + "/" + blockImage.name()));
            }
        }
    }

    private void clearOutputDir() {
        Gdx.files.local(BLOCK_OUTPUT_PATH).emptyDirectory();
    }
}
