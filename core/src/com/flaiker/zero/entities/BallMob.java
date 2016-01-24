/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.flaiker.zero.helper.AnimationManager;
import com.flaiker.zero.helper.ContactCallback;
import com.flaiker.zero.box2d.LightSourceInjectorInterface;
import com.flaiker.zero.screens.GameScreen;
import com.flaiker.zero.tiles.RegistrableSpawn;

/**
 * Concrete class for a rolling eye-like-looking mob that also acts as a lightsource with simple ai
 */
@RegistrableSpawn(type = "ballMob")
public class BallMob extends AbstractMob implements LightSourceInjectorInterface {
    private static final float  MAX_SPEED_X    = 2f;
    private static final float  ACCELERATION_X = 100f;
    private static final String ATLAS_PATH     = "ballMob";
    private static final int    HEALTH         = 5;

    private AnimationManager animationManager;
    private boolean wallRight = false;
    private boolean wallLeft  = false;
    private PointLight pointLight;
    private RayHandler rayHandler;

    public BallMob() {
        super(HEALTH);
    }

    @Override
    public void initializeRayHandler(RayHandler rayHandler) {
        if (this.rayHandler != null) throw new IllegalStateException("RayHandler already initialized");
        this.rayHandler = rayHandler;
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    protected void customInit() throws IllegalStateException {
        animationManager = new AnimationManager(sprite);
        animationManager.setMaximumAddedIdleTime(4f);
        animationManager.setMinimumIdleTime(5f);
        animationManager.registerIdleAnimation("ballMob", "idle", AbstractEntity.ENTITY_TEXTURE_ATLAS, 1 / 16f);

        if (rayHandler == null) throw new IllegalStateException("RayHandler not initialized");

        pointLight = new PointLight(rayHandler, 25, new Color(1, 1, 1, 0.5f), 2, spawnVector.x, spawnVector.y);
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();

        // create body
        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setLinearDamping(5f);
        body.setUserData(this);

        // create rotating circle
        shape.setRadius(getEntityWidth() / 2f);
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 10000f;
        fdef.restitution = 0f;
        body.createFixture(fdef);
        fdef.density = 0f;
        fdef.friction = 0f;

        // create sensors on different body
        Body sensorBody = world.createBody(bdef);
        PolygonShape boxShape = new PolygonShape();

        // sensor right
        boxShape.setAsBox(0.01f, sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f - 0.2f,
                          new Vector2(sprite.getWidth() / GameScreen.PIXEL_PER_METER / 2f, 0), 0);
        fdef.shape = boxShape;
        fdef.isSensor = true;
        sensorBody.createFixture(fdef).setUserData(new ContactCallback() {
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
        boxShape.setAsBox(-0.01f, sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f - 0.2f,
                          new Vector2(-sprite.getWidth() / GameScreen.PIXEL_PER_METER / 2f, 0), 0);
        fdef.shape = boxShape;
        fdef.isSensor = true;
        sensorBody.createFixture(fdef).setUserData(new ContactCallback() {
            @Override
            public void onContactStart() {
                wallLeft = true;
            }

            @Override
            public void onContactStop() {
                wallLeft = false;
            }
        });

        // join bodies together
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.bodyA = body;
        revoluteJointDef.bodyB = sensorBody;
        revoluteJointDef.enableLimit = false;
        revoluteJointDef.collideConnected = false;
        world.createJoint(revoluteJointDef);

        return body;
    }

    @Override
    public void update() {
        super.update();
        if (getRequestedDirection() == Direction.NONE) setRequestedDirection(Direction.RIGHT);
        aiWalk();
        animationManager.updateSprite();

        pointLight.setPosition(body.getPosition().x, body.getPosition().y);
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
