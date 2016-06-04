/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.Game;
import com.flaiker.zero.box2d.AdvancedContactCallback;
import com.flaiker.zero.box2d.Box2dUtils;

/**
 * Entity representing a fireball shot by {@link com.flaiker.zero.abilities.FireballAbility}
 */
public class FireballEntity extends AbstractLightSource {
    private static final String ATLAS_PATH      = "fireball";
    private static final int    FIREBALL_DAMAGE = 2;
    private static final int    LIFETIME        = 2;

    private static final float DENSITY     = 4f;
    private static final float FRICTION    = 1f;
    private static final float RESTITUTION = 1f;

    private boolean        damageDone;
    private float          timeAlive;
    private ParticleEffect particleEffect;

    public FireballEntity() {
        super();

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/fireball.p"), Gdx.files.internal("particles"));
        particleEffect.start();
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();

        // Create body
        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setUserData(this);

        // Create fixture
        shape.setRadius(getEntityWidth() / 2f);
        fdef.shape = shape;
        fdef.density = DENSITY;
        fdef.friction = FRICTION;
        fdef.restitution = RESTITUTION;
        body.createFixture(fdef);
        Box2dUtils.clearFixtureDefAttributes(fdef);

        // Create sensor on different body
        shape.setRadius(getEntityWidth() / 2f + 0.01f);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData(new AdvancedContactCallback() {
            @Override
            public void onContactStart(Body contactedBody) {
                if (contactedBody.getUserData() instanceof AbstractLivingEntity && !damageDone) {
                    ((AbstractLivingEntity) contactedBody.getUserData()).receiveDamage(FIREBALL_DAMAGE);
                    damageDone = true;
                    markForDeletion();
                }
            }

            @Override
            public void onContactStop(Body contactedBody) {
            }
        });

        return body;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        timeAlive += delta;
        if (timeAlive > LIFETIME) markForDeletion();

        particleEffect.setPosition(getBodyX() * Game.PIXEL_PER_METER, getBodyY() * Game.PIXEL_PER_METER);
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);

        particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }

    @Override
    protected Light createLight(RayHandler rayHandler) {
        PointLight light = new PointLight(rayHandler, 25, new Color(1, 1, 1, 0.9f), 2f, 0f, 0f);
        light.setSoft(true);

        return light;
    }
}
