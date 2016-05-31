/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.flaiker.zero.ui.screens.GameScreen;
import com.flaiker.zero.tiles.RegistrableSpawn;

import java.util.Map;

/**
 * Swinging lamp on a rope, length of the rope and default pan can be specified
 */
@RegistrableSpawn(type = "lampRope", adArgs = {LampRope.AD_ARGS_HEIGHT_KEY, LampRope.AD_ARGS_PAN_KEY})
public class LampRope extends AbstractLightSource {
    public static final String AD_ARGS_HEIGHT_KEY     = "height";
    public static final String AD_ARGS_PAN_KEY        = "pan";
    public static final String AD_ARGS_HEIGHT_DEFAULT = "1";
    public static final String AD_ARGS_PAN_DEFAULT    = "0.2";
    public static final String ATLAS_PATH             = "lampRope";
    public static final String ATLAS_ROPE_PATH        = "lampRope-rope";

    private Sprite ropeSprite;
    private float  height;
    private float  initialPanMargin;

    public LampRope() {
        super();
    }

    @Override
    protected void customInit() throws IllegalStateException {
        super.customInit();

        ropeSprite = new Sprite(ENTITY_TEXTURE_ATLAS.findRegion(ATLAS_ROPE_PATH));
        ropeSprite.setSize(ropeSprite.getWidth(), height * GameScreen.PIXEL_PER_METER - sprite.getHeight());
        ropeSprite.setOrigin(ropeSprite.getWidth() / 2f, ropeSprite.getHeight());
        ropeSprite.setPosition(spawnVector.x * GameScreen.PIXEL_PER_METER - ropeSprite.getWidth() / 2f,
                               spawnVector.y * GameScreen.PIXEL_PER_METER - ropeSprite.getHeight());
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    public void applyAdArgs(Map<String, String> adArgPairs) {
        height = Float.parseFloat(getAdArgsValueOrDefault(adArgPairs, AD_ARGS_HEIGHT_KEY, AD_ARGS_HEIGHT_DEFAULT));
        initialPanMargin = Float.parseFloat(getAdArgsValueOrDefault(adArgPairs, AD_ARGS_PAN_KEY, AD_ARGS_PAN_DEFAULT));
    }

    @Override
    protected Light createLight(RayHandler rayHandler) {
        setPositionalLightOffsets(0f, 0f, -90f);
        return new ConeLight(rayHandler, 25, new Color(1, 1, 1, 0.9f), 7, 5, 7.5f, 0, 40);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        ropeSprite.setRotation(sprite.getRotation());
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        ropeSprite.draw(batch);
    }

    @Override
    protected Body createBody(World world) {
        Vector2 spawnVector = this.spawnVector;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(spawnVector.x, spawnVector.y);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body anchorBody = world.createBody(bdef);
        shape.setAsBox(0.01f, 0.01f);
        fdef.shape = shape;
        fdef.isSensor = true;
        anchorBody.createFixture(fdef);
        fdef.isSensor = false;

        bdef.position.set(spawnVector.x + initialPanMargin,
                          spawnVector.y - height - sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body lampBody = world.createBody(bdef);
        lampBody.setUserData(this);
        shape.set(new Vector2[]{new Vector2(-sprite.getWidth() / 2f / GameScreen.PIXEL_PER_METER,
                                            -sprite.getHeight() / 2 / GameScreen.PIXEL_PER_METER),
                                new Vector2(sprite.getWidth() / 2f / GameScreen.PIXEL_PER_METER,
                                            -sprite.getHeight() / 2 / GameScreen.PIXEL_PER_METER),
                                new Vector2(0, sprite.getHeight() / 2 / GameScreen.PIXEL_PER_METER)});
        fdef.shape = shape;
        fdef.density = 100;
        lampBody.createFixture(fdef);

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(anchorBody, lampBody, anchorBody.getWorldCenter());
        revoluteJointDef.localAnchorA.set(0, 0f);
        revoluteJointDef.localAnchorB.set(0, height - sprite.getHeight() / GameScreen.PIXEL_PER_METER);
        revoluteJointDef.enableLimit = false;
        world.createJoint(revoluteJointDef);

        return lampBody;
    }
}
