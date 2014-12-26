package com.flaiker.zero.entities;

import box2dLight.ChainLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Flaiker on 25.12.2014.
 */
public class LampHorizontal extends AbstractEntity {
    private ChainLight chainLight;
    private RayHandler rayHandler;

    public LampHorizontal(RayHandler rayHandler, float xPosMeter, float yPosMeter) {
        super("lampHorizontal", xPosMeter, yPosMeter);
        this.rayHandler = rayHandler;
    }

    private void createLight(RayHandler rayHandler) {
        chainLight = new ChainLight(rayHandler, 25, new Color(1, 1, 0, 1f), 4, -1, new float[]{-0.5f, 0, 0, 0, 0.5f, 0});
    }

    @Override
    protected Body createBody(World world) {
        Vector2 spawnVector = getSpawnVector();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(spawnVector.x, spawnVector.y + 0.5f - getEntityHeight() / 2f);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body lampBody = world.createBody(bdef);
        lampBody.setUserData(this);

        shape.setAsBox(getEntityWidth() / 2f, getEntityHeight() / 2f);
        fdef.shape = shape;
        lampBody.createFixture(fdef);

        createLight(rayHandler);
        chainLight.attachToBody(lampBody);

        return lampBody;
    }
}
