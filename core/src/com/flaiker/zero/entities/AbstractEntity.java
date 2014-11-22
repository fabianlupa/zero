package com.flaiker.zero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractEntity {
    private Sprite sprite;

    public AbstractEntity(String texturePath, float xPos, float yPos) {
        this.sprite = new Sprite(new Texture(texturePath));
        this.sprite.setPosition(xPos, yPos);
    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);
    }

    public void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }

    public void update() {

    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getEntityWidth() {
        return sprite.getWidth();
    }

    public float getEntityHeight() {
        return sprite.getHeight();
    }

    protected void setX(float x) {
        sprite.setX(x);
    }

    protected void setY(float y) {
        sprite.setY(y);
    }
}
