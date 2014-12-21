package com.flaiker.zero.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Flaiker on 21.12.2014.
 */
public class AbilityListItem {
    private ImageTextButton imageTextButton;
    private String          text;

    public AbilityListItem(String text, Skin skin) {
        this.text = text;
        imageTextButton = new ImageTextButton(text, skin);
        Image image = new Image(new Texture("whiteBlock.png"));
        imageTextButton.getImageCell().setActor(image);
    }

    public String getText() {
        return text;
    }

    public ImageTextButton getImageTextButton() {
        return imageTextButton;
    }
}
