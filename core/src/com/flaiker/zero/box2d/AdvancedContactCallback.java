/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Alternative to {@link ContactCallback} with a body parameter
 */
public interface AdvancedContactCallback {
    void onContactStart(Body contactedBody);
    void onContactStop(Body contactedBody);
}
