package com.flaiker.zero.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Flaiker on 22.12.2014.
 */
public abstract class AbstractAbility {
    //protected static final TextureAtlas ABILITY_TEXTURE_ATLAS = new TextureAtlas("atlases/entities.atlas");

    private String          name;
    private ImageTextButton imageTextButton;

    public AbstractAbility(String name, String atlasPath, Skin skin) {
        this.name = name;
        this.imageTextButton = createImageTextButton(atlasPath, skin);
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
}
