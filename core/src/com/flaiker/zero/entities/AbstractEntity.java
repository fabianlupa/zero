package com.flaiker.zero.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.helper.AbstractBox2dObject;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractEntity extends AbstractBox2dObject {
    public AbstractEntity(World world, String texturePath, float xPos, float yPos) {
        super(world, texturePath, xPos, yPos);
    }

    public void update() {
        setPosition(body.getPosition().x * GameScreen.PIXEL_PER_METER - getEntityWidth() / 2f,
                    body.getPosition().y * GameScreen.PIXEL_PER_METER - getEntityHeight() / 2f);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }

    protected void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }

    protected void setX(float x) {
        sprite.setX(x);
    }

    protected void setY(float y) {
        sprite.setY(y);
    }

    protected void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }
}
