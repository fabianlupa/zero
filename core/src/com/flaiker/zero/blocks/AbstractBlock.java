package com.flaiker.zero.blocks;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.helper.AbstractBox2dObject;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractBlock extends AbstractBox2dObject {
    private static TextureAtlas blockTextureAtlas = new TextureAtlas("atlases/blocks.atlas");

    public AbstractBlock(String atlasPath, float xPosMeter, float yPosMeter) {
        super(atlasPath, blockTextureAtlas, xPosMeter, yPosMeter);
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
        //fdef.friction = 0.5f;
        fdef.shape = cs;
        fdef.isSensor = false;
        Body tileBody = world.createBody(bdef);
        tileBody.setUserData(this);
        tileBody.createFixture(fdef);

        return tileBody;
    }
}
