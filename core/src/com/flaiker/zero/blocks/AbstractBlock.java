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

    public AbstractBlock(World world, String atlasPath, float xPos, float yPos) {
        super(world, atlasPath, blockTextureAtlas, xPos, yPos);
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(getXInMeter() + 0.5f, getYInMeter() + 0.5f);

        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];
        v[0] = new Vector2(-getEntityHeightInMeter() / 2f, -getEntityWidthInMeter() / 2f);
        v[1] = new Vector2(-getEntityWidthInMeter() / 2f, getEntityWidthInMeter() / 2f);
        v[2] = new Vector2(getEntityWidthInMeter() / 2f, getEntityWidthInMeter() / 2f);
        v[3] = new Vector2(getEntityWidthInMeter() / 2f, -getEntityWidthInMeter() / 2f);
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
