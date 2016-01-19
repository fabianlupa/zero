/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.tiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Annotation for tagging a {@link com.flaiker.zero.entities.AbstractEntity}-type as a spawn type to the
 * {@link TileRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrableSpawn {
    /**
     * @return Type-name of the entity used to identify spawn tiles
     */
    String type();

    /**
     * @return Additional arguments that can be defined in the tile on the map and will be loaded using
     * {@link com.flaiker.zero.entities.AbstractEntity#applyAdArgs(Map)}
     */
    String[] adArgs() default {};
}
