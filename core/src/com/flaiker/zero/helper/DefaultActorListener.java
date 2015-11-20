/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * An utility {@link com.badlogic.gdx.scenes.scene2d.InputListener} class.
 * <p>
 * Defines the {@link #touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)} method
 * returning <code>true</code> by default, so the
 * {@link #touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)} method gets invoked.
 */
public abstract class DefaultActorListener extends InputListener {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return true;
    }
}
