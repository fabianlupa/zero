package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Flaiker on 22.11.2014.
 */
public interface Box2dConverterInterface {
    public void setPosition(float xPos, float yPos);
    public void render(Batch batch);
    public void setRotation(float degrees);
}

