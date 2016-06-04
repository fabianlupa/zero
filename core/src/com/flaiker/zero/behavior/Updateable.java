/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.behavior;

/**
 * Interface for describing objects which's state can be updated
 */
public interface Updateable {
    /**
     * Update this object
     *
     * @param delta Time in seconds since the last update
     */
    void update(float delta);
}
