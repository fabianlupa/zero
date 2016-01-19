/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.flaiker.zero.box2d.AbstractBox2dObject;
import com.flaiker.zero.screens.GameScreen;

import java.util.Map;

/**
 * Base class for entities
 * <p>
 * Entities are dynamic game objects possibly loaded as a spawn from a map using the {@link
 * com.flaiker.zero.tiles.RegistrableSpawn}-annotation.
 */
public abstract class AbstractEntity extends AbstractBox2dObject {
    public static final TextureAtlas ENTITY_TEXTURE_ATLAS = new TextureAtlas("atlases/entities.atlas");

    public AbstractEntity() {
        super(ENTITY_TEXTURE_ATLAS);
    }

    /**
     * Apply additional arguments from the map object
     * <p>
     * When loading a spawn additional arguments specified in {@link com.flaiker.zero.tiles.RegistrableSpawn#adArgs()}
     * will be injected using this method.
     *
     * @param adArgPairs Map of key value pairs
     */
    public void applyAdArgs(Map<String, String> adArgPairs) {
    }

    /**
     * Helper method to retrieve values from a addArgs map
     *
     * @param adArgsPairs  Map containing the keys and values
     * @param key          Key to be searched
     * @param defaultValue Value to be used if the key is not found or value is null
     * @return Chosen value
     */
    protected String getAdArgsValueOrDefault(Map<String, String> adArgsPairs, String key, String defaultValue) {
        if (!adArgsPairs.containsKey(key)) return defaultValue;
        if (adArgsPairs.get(key) == null) return defaultValue;
        return adArgsPairs.get(key);
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
