/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.blocks;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.box2d.AbstractBox2dObject;
import com.flaiker.zero.box2d.CollisionBits;

/**
 * Base class for blocks
 * <p>
 * Blocks are static game object oriented in tiles. To be loaded from a map and accounted for in asset generation they
 * may be annotated with {@link com.flaiker.zero.tiles.RegistrableBlock}.
 */
public abstract class AbstractBlock extends AbstractBox2dObject {
    private static TextureAtlas blockTextureAtlas = new TextureAtlas("atlases/blocks.atlas");

    private static final float DENSITY     = 100f;
    private static final float FRICTION    = 3f;
    private static final float RESTITUTION = 0f;

    public AbstractBlock() {
        super(blockTextureAtlas);
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(getSpriteX() + 0.5f, getSpriteY() + 0.5f);

        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];
        v[0] = new Vector2(-getEntityHeight() / 2f, -getEntityWidth() / 2f);
        v[1] = new Vector2(-getEntityWidth() / 2f, getEntityWidth() / 2f);
        v[2] = new Vector2(getEntityWidth() / 2f, getEntityWidth() / 2f);
        v[3] = new Vector2(getEntityWidth() / 2f, -getEntityWidth() / 2f);
        cs.createLoop(v);
        fdef.shape = cs;
        fdef.density = DENSITY;
        fdef.friction = FRICTION;
        fdef.restitution = RESTITUTION;
        fdef.isSensor = false;
        fdef.filter.maskBits = CollisionBits.MASK_ENVIRONMENT;
        fdef.filter.categoryBits = CollisionBits.CATEGORY_BLOCKS;
        Body tileBody = world.createBody(bdef);
        tileBody.setUserData(this);
        tileBody.createFixture(fdef);

        return tileBody;
    }
}
