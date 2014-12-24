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

    private Vector2 spawnVector;

    public AbstractEntity(World world, String atlasPath, float xPosMeter, float yPosMeter) {
        super(world, atlasPath, ENTITY_TEXTURE_ATLAS, xPosMeter, yPosMeter);
        spawnVector = new Vector2(xPosMeter, yPosMeter);
    }

    public Vector2 getSpawnVector() {
        return spawnVector;
    }

    @Override
    public void update() {
        super.update();
        // update the sprite's position and rotation to the box2d body properties
        setSpritePosition(body.getPosition().x - getEntityWidth() / 2f, body.getPosition().y - getEntityHeight() / 2f);
        setSpriteRotation(MathUtils.radiansToDegrees * body.getAngle());
    }

    protected void setSpritePosition(float xMeter, float yMeter) {
        sprite.setPosition(xMeter * GameScreen.PIXEL_PER_METER, yMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteX(float xMeter) {
        sprite.setX(xMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteY(float yMeter) {
        sprite.setY(yMeter * GameScreen.PIXEL_PER_METER);
    }

    protected void setSpriteRotation(float degrees) {
        sprite.setRotation(degrees);
    }
}
