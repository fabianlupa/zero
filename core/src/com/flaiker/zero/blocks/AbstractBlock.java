package com.flaiker.zero.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Flaiker on 22.11.2014.
 */
public abstract class AbstractBlock {
    private Sprite sprite;

    public AbstractBlock(String texturePath, float xPos, float yPos) {
        this.sprite = new Sprite(new Texture(texturePath));
        this.sprite.setPosition(xPos, yPos);
    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
