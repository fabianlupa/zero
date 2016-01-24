/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

/**
 * Base class used by {@link WorldContactListener} to direct contact events to Box2D objects, should mainly be extended
 * anonymously
 */
public abstract class ContactCallback {
    public abstract void onContactStart();
    public abstract void onContactStop();
}
