package com.flaiker.zero.entities;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Flaiker on 10.12.2014.
 */
public class RobotMob extends AbstractMob {
    public RobotMob(World world, float xPosMeter, float yPosMeter) {
        super(world, "robotMob.png", xPosMeter, yPosMeter, 5);
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

        return body;
    }
}
