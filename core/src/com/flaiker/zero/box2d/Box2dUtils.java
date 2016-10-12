/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Static utility class for box2d related functionality.
 */
public abstract class Box2dUtils {
    /**
     * Initialize a {@link FixtureDef}.
     *
     * @param fdef The FixtureDef to initialize
     */
    public static void clearFixtureDefAttributes(FixtureDef fdef) {
        fdef.restitution = 0f;
        fdef.density = 0f;
        fdef.friction = 0.2f;
        fdef.isSensor = false;
        fdef.shape = null;
        fdef.filter.categoryBits = CollisionBits.CATEGORY_DEFAULT;
        fdef.filter.groupIndex = 0;
        fdef.filter.maskBits = CollisionBits.MASK_DEFAULT;
    }
}
