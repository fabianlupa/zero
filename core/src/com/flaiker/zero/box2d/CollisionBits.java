/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

/**
 * Bitmasks for collision management
 *
 * @see com.badlogic.gdx.physics.box2d.Filter
 */
public final class CollisionBits {
    public static final short CATEGORY_DEFAULT = 0x0001;
    public static final short CATEGORY_PLAYER  = 0x0002;
    public static final short CATEGORY_MOBS    = 0x0004;
    public static final short CATEGORY_BLOCKS  = 0x0008;
    public static final short CATEGORY_DECO    = 0x0016;

    public static final short MASK_DEFAULT     = -1;
    public static final short MASK_PLAYER      = CATEGORY_MOBS | CATEGORY_BLOCKS;
    public static final short MASK_MOBS        = CATEGORY_BLOCKS | CATEGORY_PLAYER;
    public static final short MASK_ENVIRONMENT = MASK_DEFAULT;

    private CollisionBits() {
        // Private constructor for service class
    }
}
