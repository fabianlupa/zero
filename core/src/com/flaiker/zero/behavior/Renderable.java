/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.behavior;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Interface for describing objects that can be rendered
 */
public interface Renderable {
    /**
     * Render this object
     *
     * @param batch Batch to help rendering
     */
    void render(Batch batch);
}
