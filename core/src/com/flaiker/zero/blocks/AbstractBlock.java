package com.flaiker.zero.blocks;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.helper.AbstractBox2dObject;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractBlock extends AbstractBox2dObject {
    public AbstractBlock(World world, String texturePath, float xPos, float yPos) {
        super(world, texturePath, xPos, yPos);
    }

    @Override
    protected Body createBody(World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(getX() / GameScreen.PIXEL_PER_METER + 0.5f, getY() / GameScreen.PIXEL_PER_METER + 0.5f);

        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];
        v[0] = new Vector2(-getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER, -getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER);
        v[1] = new Vector2(-getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER, getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER);
        v[2] = new Vector2(getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER, getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER);
        v[3] = new Vector2(getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER, -getEntityWidth() / 2 / GameScreen.PIXEL_PER_METER);
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
