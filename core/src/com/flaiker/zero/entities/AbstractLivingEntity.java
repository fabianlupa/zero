/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.badlogic.gdx.graphics.Color;

/**
 * Base class for entities that "live", e. g. can move
 */
public abstract class AbstractLivingEntity extends AbstractEntity {
    private static final Color[] DMG_ANIMATION_COLORS;
    private static final float   DMG_ANIMATION_DURATION = 0.6f;

    protected int currentHealth;
    protected int maxHealth;

    private Direction   requestedDirection;
    private Direction   lastRequestedDirection;
    private float       lastLinearVelocityX;
    private EntityState entityState;
    private float       currentDmgAnimationDuration;
    private boolean     dmgAnimationRunning;

    static {
        DMG_ANIMATION_COLORS = new Color[]{
                new Color(1f, 0f, 0f, 1f),
                new Color(1f, 0.2f, 0.2f, 1f),
                new Color(1f, 0.4f, 0.4f, 1f),
                new Color(1f, 0.6f, 0.6f, 1f),
                new Color(1f, 0.8f, 0.8f, 1f),
                new Color(1f, 1f, 1f, 1f)
        };
    }

    public AbstractLivingEntity(int maxHealth) {
        super();
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        this.requestedDirection = Direction.NONE;
        lastRequestedDirection = Direction.RIGHT;
        entityState = EntityState.LIVING;
    }

    /**
     * Move the entity according to its {@link #requestedDirection} and {@link #lastLinearVelocityX}
     */
    protected void move() {
        if (entityState != EntityState.LIVING) return;

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
                /*if (body.getLinearVelocity().x > 0) {
                    body.applyForceToCenter(-getAccelerationX(), 0f, true);
                    if (lastLinearVelocityX < 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                } else if (body.getLinearVelocity().x < 0) {
                    body.applyForceToCenter(getAccelerationX(), 0f, true);
                    if (lastLinearVelocityX > 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                }
                lastLinearVelocityX = body.getLinearVelocity().x;*/
                break;
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void receiveDamage(int dmg) {
        if (!dmgAnimationRunning) {
            currentHealth -= dmg;
            if (currentHealth < 0) currentHealth = 0;

            dmgAnimationRunning = true;
        } else {
            // Entity is invulnerable during damage animation
        }

    }

    @Override
    public void update(float delta) {
        super.update(delta);
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

        // Update damage animation
        if (dmgAnimationRunning) {
            currentDmgAnimationDuration += delta;
            sprite.setColor(DMG_ANIMATION_COLORS[(int) (currentDmgAnimationDuration / DMG_ANIMATION_DURATION
                                                        * (DMG_ANIMATION_COLORS.length - 1))]);
        }
        if (currentDmgAnimationDuration > DMG_ANIMATION_DURATION) {
            currentDmgAnimationDuration = 0f;
            dmgAnimationRunning = false;
        }

        // Check if entity is dead
        if (currentHealth == 0 && entityState == EntityState.LIVING) {
            changeEntityState(EntityState.DYING);
            //markForDeletion();
        }
    }

    protected void changeEntityState(EntityState newState) {
        entityState = newState;
        onEntityStateChanged(newState);
        if (newState == EntityState.DEAD) markForDeletion();
    }

    protected void onEntityStateChanged(EntityState newState) {
        if (newState == EntityState.DYING) changeEntityState(EntityState.DEAD);
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

    public enum EntityState {
        LIVING,
        DYING,
        DEAD
    }
}
