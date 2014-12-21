package com.flaiker.zero.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.helper.AnimationManager;
import com.flaiker.zero.helper.ContactCallback;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 10.12.2014.
 */
public class RobotMob extends AbstractMob {
    private static final float MAX_SPEED_X    = 1f;
    private static final float ACCELERATION_X = 500f;

    private boolean wallRight = false;
    private boolean wallLeft  = false;
    private AnimationManager animationManager;

    public RobotMob(World world, float xPosMeter, float yPosMeter) {
        super(world, "robotMob", xPosMeter, yPosMeter, 5);
        animationManager = new AnimationManager(sprite);
        animationManager.registerAnimation("robotMob", "walk" , AbstractEntity.getEntityTextureAtlas(), 1 / 16f);
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setUserData(this);

        shape.setAsBox(getEntityWidth() / 2f, getEntityHeight() / 2f);
        fdef.shape = shape;
        body.createFixture(fdef);

        // sensor right
        shape.setAsBox(0.1f, sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f - 0.2f,
                       new Vector2(sprite.getWidth() / GameScreen.PIXEL_PER_METER / 2f - 0.1f, 0), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData(new ContactCallback() {
            @Override
            public void onContactStart() {
                wallRight = true;
            }

            @Override
            public void onContactStop() {
                wallRight = false;
            }
        });

        // sensor left
        shape.setAsBox(-0.1f, sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f - 0.2f,
                       new Vector2(-sprite.getWidth() / GameScreen.PIXEL_PER_METER / 2f + 0.1f, 0), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData(new ContactCallback() {
            @Override
            public void onContactStart() {
                wallLeft = true;
            }

            @Override
            public void onContactStop() {
                wallLeft = false;
            }
        });

        return body;
    }

    @Override
    public void update() {
        super.update();
        if (getRequestedDirection() == Direction.NONE) {
            setRequestedDirection(Direction.RIGHT);
            animationManager.runAnimation("walk", AnimationManager.AnimationDirection.LEFT);
        }
        aiWalk();
        animationManager.updateSprite();
        //animationManager.updateAnimationFrameDuration("walk", 1f / Math.abs(body.getLinearVelocity().x));
    }

    private void aiWalk() {
        if (wallRight && getRequestedDirection() == Direction.RIGHT) {
            setRequestedDirection(Direction.LEFT);
        } else if (wallLeft && getRequestedDirection() == Direction.LEFT) {
            setRequestedDirection(Direction.RIGHT);
        }
    }

    @Override
    protected float getMaxSpeedX() {
        return MAX_SPEED_X;
    }

    @Override
    protected float getAccelerationX() {
        return ACCELERATION_X;
    }
}
