package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 23.11.2014.
 */
public abstract class AbstractBox2dObject {
    protected Sprite sprite;
    protected Body   body;

    public AbstractBox2dObject(World world, String atlasPath, TextureAtlas textureAtlas, float xPosMeter, float yPosMeter) {
        this.sprite = textureAtlas.createSprite(atlasPath);
        sprite.setPosition(xPosMeter * GameScreen.PIXEL_PER_METER, yPosMeter * GameScreen.PIXEL_PER_METER);
        body = createBody(world);
    }

    public AbstractBox2dObject(World world, String filePath, float xPos, float yPos) {
        this.sprite = new Sprite(new Texture(filePath));
        sprite.setPosition(xPos, yPos);
        body = createBody(world);
    }

    protected abstract Body createBody(World world);

    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {
    }

    public float getSpriteX() {
        return sprite.getX() / GameScreen.PIXEL_PER_METER;
    }

    public float getSpriteY() {
        return sprite.getY() / GameScreen.PIXEL_PER_METER;
    }

    public float getEntityWidth() {
        return sprite.getWidth() / GameScreen.PIXEL_PER_METER;
    }

    public float getEntityHeight() {
        return sprite.getHeight() / GameScreen.PIXEL_PER_METER;
    }
}