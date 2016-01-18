/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.tiles;

import com.flaiker.zero.blocks.AbstractBlock;

/**
 * Class to bundle a block type and its metadata
 */
public class BlockWrapper {
    public final Class<? extends AbstractBlock> blockClass;
    public final BlockMetadata                  blockMetadata;

    public BlockWrapper(Class<? extends AbstractBlock> blockClass, BlockMetadata blockMetadata) {
        this.blockClass = blockClass;
        this.blockMetadata = blockMetadata;
    }

    public static BlockWrapper factory(Class<? extends AbstractBlock> blockClass, RegistrableBlock annotation) {
        return new BlockWrapper(blockClass, BlockMetadata.factory(annotation, blockClass));
    }
}
