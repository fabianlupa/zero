package com.flaiker.zero.blocks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.box2d.CollisionBits;
import com.flaiker.zero.tiles.RegistrableBlock;

@RegistrableBlock(id = 2, name = "ice")
public class IceBlock extends AbstractBlock {
    public static final float DENSITY     = 100f;
    public static final float FRICTION    = 0.2f;
    public static final float RESTITUTION = 0f;

    @Override
    protected String getAtlasPath() {
        return "02-ice";
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
        fdef.filter.maskBits = CollisionBits.MASK_BLOCKS;
        fdef.filter.categoryBits = CollisionBits.CATEGORY_BLOCKS;
        Body tileBody = world.createBody(bdef);
        tileBody.setUserData(this);
        tileBody.createFixture(fdef);

        return tileBody;
    }
}
