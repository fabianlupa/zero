/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.entities.Player;

/**
 * Base class for abilities that can be used by the player
 */
public abstract class AbstractAbility {
    //protected static final TextureAtlas ABILITY_TEXTURE_ATLAS = new TextureAtlas("atlases/entities.atlas");

    protected final WorldBodyInjector wbi;
    protected final Player            player;

    private String          name;
    private ImageTextButton imageTextButton;

    public AbstractAbility(String name, String atlasPath, Skin skin, WorldBodyInjector wbi, Player player) {
        this.name = name;
        this.imageTextButton = createImageTextButton(atlasPath, skin);
        this.wbi = wbi;
        this.player = player;
    }

    private ImageTextButton createImageTextButton(String atlasPath, Skin skin) {
        imageTextButton = new ImageTextButton(name, skin);
        Image image = new Image(new Texture("whiteBlock.png")); //TODO: change back to textureatlas ones it is generated
        imageTextButton.getImageCell().setActor(image);
        imageTextButton.setUserObject(this);
        return imageTextButton;
    }

    public ImageTextButton getImageTextButton() {
        return imageTextButton;
    }

    public String getName() {
        return name;
    }

    /**
     * Use the ability
     */
    public abstract void use();

    /**
     * @return Ability is ready to be used
     */
    public boolean canUse() {
        return true;
    }

    /**
     * Update ability's state
     * @param delta Time in milliseconds since last update
     */
    public void update(float delta) {
    }

    /**
     * Render the ability
     * @param delta Time in milliseconds since the last time it was rendered
     */
    public void render(float delta) {
    }
}
