/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.tiles;

import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.blocks.AbstractEdgedBlock;

/**
 * Class to describe block metadata defined by the {@link RegistrableBlock}-annotation
 */
public class BlockMetadata {
    public final int     id;
    public final String  name;
    public final boolean edged;

    public BlockMetadata(int id, String name, boolean edged) {
        this.id = id;
        this.name = name;
        this.edged = edged;
    }

    public static BlockMetadata factory(RegistrableBlock annotation, Class<? extends AbstractBlock> block) {
        boolean edged = AbstractEdgedBlock.class.isAssignableFrom(block);
        return new BlockMetadata(annotation.id(), annotation.name(), edged);
    }
}
