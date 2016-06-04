/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

/**
 * Interface to inject ability to add an {@link AbstractBox2dObject} to a box2d {@link
 * com.badlogic.gdx.physics.box2d.World World}
 */
public interface WorldBodyInjector {
    /**
     * Add {@link AbstractBox2dObject} to {@link com.badlogic.gdx.physics.box2d.World World} and initialize it using
     * {@link com.flaiker.zero.injection.DependencyInjector DependencyInjector}
     *
     * @param box2dObject The object to add
     */
    void addBodyToWorld(AbstractBox2dObject box2dObject);

    /**
     * Add {@link AbstractBox2dObject} to {@link com.badlogic.gdx.physics.box2d.World World}
     *
     * @param box2dObject The object to add
     */
    void addBodyToWorldWithoutDependencyInjection(AbstractBox2dObject box2dObject);
}
