package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Flaiker on 22.11.2014.
 */
public interface EntityConverterInterface {
    public void setPosition(float xPos, float yPos);
    public void render(Batch batch);
    public void setRotation(float degrees);
    public void update();
}

