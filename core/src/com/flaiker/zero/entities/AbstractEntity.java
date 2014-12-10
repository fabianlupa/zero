package com.flaiker.zero.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.helper.AbstractBox2dObject;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractEntity extends AbstractBox2dObject {
    private Vector2 spawnVector;

    public AbstractEntity(World world, String texturePath, float xPosMeter, float yPosMeter) {
        super(world, texturePath, xPosMeter, yPosMeter);
        spawnVector = new Vector2(xPosMeter, yPosMeter);
    }

    public void update() {
        // update the sprite's position and rotation to the box2d body properties
        setSpritePosition(body.getPosition().x - getEntityWidth() / 2f, body.getPosition().y - getEntityHeight() / 2f);
        setSpriteRotation(MathUtils.radiansToDegrees * body.getAngle());

        // check if entity has left the mapbounds, if so teleport it back into it
        if (getSpriteX() < 0) {
            body.setTransform(getEntityWidth() / 2f, body.getPosition().y, body.getAngle());
            body.setLinearVelocity(0f, body.getLinearVelocity().y);
        } //TODO: right edge of the map
        if (getSpriteY() < 0) {
            body.setTransform(spawnVector, 0f);
            body.setLinearVelocity(0f, 0f);
        }
    }

    protected void setSpritePosition(float xMeter, float yMeter) {
        sprite.setPosition(xMeter * GameScreen.PIXEL_PER_METER, yMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteX(float xMeter) {
        sprite.setX(xMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteY(float yMeter) {
        sprite.setY(yMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteRotation(float degrees) {
        sprite.setRotation(degrees);
    }
}
