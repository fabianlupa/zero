/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import box2dLight.ChainLight;
import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.tiles.RegistrableSpawn;

import java.util.Map;

/**
 * Static lamp that casts a chainlight in the specified color downwards
 */
@RegistrableSpawn(type = "lampHorizontal", adArgs = {LampHorizontal.AD_ARGS_COLOR_KEY})
public class LampHorizontal extends AbstractLightSource {
    public static final String AD_ARGS_COLOR_KEY     = "color";
    public static final String AD_ARGS_COLOR_DEFAULT = "FFFF00FF";
    public static final String ATLAS_PATH            = "lampHorizontal";

    private Color tintColor;

    public LampHorizontal() {
        super();
    }

    @Override
    public void applyAdArgs(Map<String, String> adArgPairs) {
        tintColor = Color.valueOf(getAdArgsValueOrDefault(adArgPairs, AD_ARGS_COLOR_KEY, AD_ARGS_COLOR_DEFAULT));
    }

    @Override
    protected void customInit() throws IllegalStateException {
        sprite.setColor(tintColor);
    }

    @Override
    protected Light createLight(RayHandler rayHandler) {
        return new ChainLight(rayHandler, 25, tintColor, 4, -1, new float[]{-0.5f, 0, 0, 0, 0.5f, 0});
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    protected Body createBody(World world) {
        Vector2 spawnVector = this.spawnVector;

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

        return lampBody;
    }
}
