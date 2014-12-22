package com.flaiker.zero.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.12.2014.
 */
public abstract class AbstractLivingEntity extends AbstractEntity {
    private Direction requestedDirection;
    private float     lastLinearVelocityX;
    private Vector2   spawnVector;

    public AbstractLivingEntity(World world, String atlasPath, float xPosMeter, float yPosMeter) {
        super(world, atlasPath, xPosMeter, yPosMeter);
        spawnVector = new Vector2(xPosMeter, yPosMeter);
        this.requestedDirection = Direction.NONE;
    }

    protected void move() {
        switch (requestedDirection) {
            case RIGHT:
                body.applyForceToCenter(getAccelerationX(), 0f, true);
                if (body.getLinearVelocity().x > getMaxSpeedX()) body.setLinearVelocity(getMaxSpeedX(), body.getLinearVelocity().y);
                break;
            case LEFT:
                body.applyForceToCenter(-getAccelerationX(), 0f, true);
                if (body.getLinearVelocity().x < -getMaxSpeedX()) body.setLinearVelocity(-getMaxSpeedX(), body.getLinearVelocity().y);
                break;
            case NONE:
                if (body.getLinearVelocity().x > 0) {
                    body.applyForceToCenter(-getAccelerationX(), 0f, true);
                    if (lastLinearVelocityX < 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                } else if (body.getLinearVelocity().x < 0) {
                    body.applyForceToCenter(getAccelerationX(), 0f, true);
                    if (lastLinearVelocityX > 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                }
                lastLinearVelocityX = body.getLinearVelocity().x;
                break;
        }
    }

    public void update() {
        move();

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

    protected abstract float getMaxSpeedX();

    protected abstract float getAccelerationX();

    protected void setRequestedDirection(Direction direction) {
        requestedDirection = direction;
    }

    protected Direction getRequestedDirection() {
        return requestedDirection;
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

    public static enum Direction {
        LEFT, RIGHT, NONE
    }
}
