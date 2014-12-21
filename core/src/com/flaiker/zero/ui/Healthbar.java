package com.flaiker.zero.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Flaiker on 21.12.2014.
 */
public class Healthbar extends Actor {
    private static final int BAR_INNER_WIDTH = 218;

    private Sprite  backgroundSprite;
    private Sprite  foregroundSprite;
    private Texture blockTexture;

    private int currentHealth;
    private int maxHealth;

    public Healthbar() {
        backgroundSprite = new Sprite(new Texture("ingameui/healthbar_bg.png"));
        foregroundSprite = new Sprite(new Texture("ingameui/healthbar_fg.png"));
        blockTexture = new Texture("ingameui/healthbar_block.png");
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        backgroundSprite.setPosition(getX(), getY());
        backgroundSprite.setSize(getWidth(), getHeight());
        backgroundSprite.draw(batch);

        for (int i = 0; i < currentHealth; i++) {
            batch.draw(blockTexture, getX() + 4 + i * BAR_INNER_WIDTH / maxHealth, getY() + 1, BAR_INNER_WIDTH / maxHealth,
                       blockTexture.getHeight());
        }

        foregroundSprite.setPosition(getX(), getY());
        foregroundSprite.setSize(getWidth(), getHeight());
        foregroundSprite.draw(batch);
    }
}
