package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 23.11.2014.
 */
public abstract class AbstractBox2dObject {
    protected Sprite  sprite;
    protected Body    body;
    protected Vector2 spawnVector;

    public AbstractBox2dObject(String atlasPath, TextureAtlas textureAtlas, float xPosMeter, float yPosMeter) {
        this.sprite = textureAtlas.createSprite(atlasPath);
        sprite.setPosition(xPosMeter * GameScreen.PIXEL_PER_METER, yPosMeter * GameScreen.PIXEL_PER_METER);
        spawnVector = new Vector2(xPosMeter, yPosMeter);
    }

    public void addBodyToWorld(World world) {
        if (body == null) body = createBody(world);
        else throw new IllegalStateException("Body has already been added to world");
    }

    protected abstract Body createBody(World world);

    public void render(Batch batch) {
        if (body == null) throw new IllegalStateException("Cannot render uninitialized body");
        sprite.draw(batch);
    }

    public Vector2 getSpawnVector() {
        return spawnVector;
    }

    public void update() {
        if (body == null) throw new IllegalStateException("Cannot update uninitialized body");
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