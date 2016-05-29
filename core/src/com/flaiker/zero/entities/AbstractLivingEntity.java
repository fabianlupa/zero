/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

/**
 * Base class for entities that "live", e. g. can move
 */
public abstract class AbstractLivingEntity extends AbstractEntity {
    protected int currentHealth;
    protected int maxHealth;
    private Direction requestedDirection;
    private Direction lastRequestedDirection;
    private float     lastLinearVelocityX;

    public AbstractLivingEntity(int maxHealth) {
        super();
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        this.requestedDirection = Direction.NONE;
        lastRequestedDirection = Direction.RIGHT;
    }

    /**
     * Move the entity according to its {@link #requestedDirection} and {@link #lastLinearVelocityX}
     */
    protected void move() {
        switch (requestedDirection) {
            case RIGHT:
                body.applyForceToCenter(getAccelerationX(), 0f, true);
                if (body.getLinearVelocity().x > getMaxSpeedX())
                    body.setLinearVelocity(getMaxSpeedX(), body.getLinearVelocity().y);
                break;
            case LEFT:
                body.applyForceToCenter(-getAccelerationX(), 0f, true);
                if (body.getLinearVelocity().x < -getMaxSpeedX())
                    body.setLinearVelocity(-getMaxSpeedX(), body.getLinearVelocity().y);
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

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
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
            body.setTransform(spawnVector, 0f);
            body.setLinearVelocity(0f, 0f);
        }
    }

    protected abstract float getMaxSpeedX();

    protected abstract float getAccelerationX();

    protected void setRequestedDirection(Direction direction) {
        lastRequestedDirection = requestedDirection;
        requestedDirection = direction;
    }

    public Direction getRequestedDirection() {
        return requestedDirection;
    }

    public Direction getLastRequestedDirection() {
        return lastRequestedDirection;
    }

    public Direction getViewDirection() {
        if (requestedDirection != Direction.NONE) return requestedDirection;
        else return lastRequestedDirection;
    }

    public enum Direction {
        LEFT, RIGHT, NONE
    }
}
