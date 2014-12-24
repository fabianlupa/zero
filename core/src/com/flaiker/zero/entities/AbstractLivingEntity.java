package com.flaiker.zero.entities;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 22.12.2014.
 */
public abstract class AbstractLivingEntity extends AbstractEntity {
    private Direction requestedDirection;
    private float     lastLinearVelocityX;

    public AbstractLivingEntity(World world, String atlasPath, float xPosMeter, float yPosMeter) {
        super(world, atlasPath, xPosMeter, yPosMeter);
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
        super.update();
        move();
        // check if entity has left the mapbounds, if so teleport it back into it
        if (getSpriteX() < 0) {
            body.setTransform(getEntityWidth() / 2f, body.getPosition().y, body.getAngle());
            body.setLinearVelocity(0f, body.getLinearVelocity().y);
        } //TODO: right edge of the map
        if (getSpriteY() < 0) {
            body.setTransform(getSpawnVector(), 0f);
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

    public static enum Direction {
        LEFT, RIGHT, NONE
    }
}
