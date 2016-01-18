/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

import box2dLight.RayHandler;

/**
 * Interface for parameter injection of a {@link RayHandler}
 */
public interface LightSourceInjectorInterface {
    void initializeRayHandler(RayHandler rayHandler);
}
