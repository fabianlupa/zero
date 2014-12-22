package com.flaiker.zero.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.helper.AbstractBox2dObject;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractEntity extends AbstractBox2dObject {
    public static final TextureAtlas ENTITY_TEXTURE_ATLAS = new TextureAtlas("atlases/entities.atlas");

    public AbstractEntity(World world, String atlasPath, float xPosMeter, float yPosMeter) {
        super(world, atlasPath, ENTITY_TEXTURE_ATLAS, xPosMeter, yPosMeter);
    }
}
