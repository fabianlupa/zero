/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.box2d.CollisionBits;

public class LaserEntity extends AbstractEntity {
    private static final String ATLAS_PATH = "laser";

    private final float laserDuration;

    private float   lifeTimeLeft;
    private boolean alive = true;

    public LaserEntity(float laserDuration) {
        this.laserDuration = laserDuration;
        lifeTimeLeft = laserDuration;
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.fixedRotation = true;
        Body body = world.createBody(bdef);
        body.setUserData(this);

        shape.setAsBox(getEntityWidth() / 2f, getEntityHeight() / 2f);
        fdef.shape = shape;
        fdef.density = 0f;
        fdef.friction = 0f;
        fdef.restitution = 0f;
        //fdef.filter.categoryBits = CollisionBits.CA
        fdef.filter.maskBits = CollisionBits.MASK_MOBS;
        body.createFixture(fdef);

        return body;
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        sprite.setScale(sprite.getScaleX() + 0.2f, 1);
        //sprite.setAlpha(sprite.getColor().a - 0.01f);
        sprite.setColor(sprite.getColor().sub(0f, 0f, 0f, 0.01f));

        lifeTimeLeft -= delta;
        if (lifeTimeLeft < 0) {
            lifeTimeLeft = 0;
            alive = false;
        }

        if (!alive) markForDeletion();
    }
}
