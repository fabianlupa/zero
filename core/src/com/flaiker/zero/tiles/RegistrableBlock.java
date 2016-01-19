/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.tiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for tagging a {@link com.flaiker.zero.blocks.AbstractBlock}-type as a block type to the
 * {@link TileRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrableBlock {
    /**
     * @return Tile id to identify the block
     */
    int id();

    /**
     * @return Descriptive name of the block
     */
    String name();
}
