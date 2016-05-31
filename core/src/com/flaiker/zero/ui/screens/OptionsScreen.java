/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.flaiker.zero.Zero;
import com.flaiker.zero.helper.DefaultActorListener;

/**
 * Options screen
 */
public class OptionsScreen extends AbstractScreen {
    public static final String LOG = OptionsScreen.class.getSimpleName();

    public OptionsScreen(Zero zero) {
        super(zero);
    }

    @Override
    public void show() {
        super.show();

        Table table = new Table(skin);
        table.setFillParent(true);
        uiStage.addActor(table);

        table.add().padBottom(50).row();

        // Title
        Label titleLabel = new Label("ZERO", skin, "digital7-92", Color.WHITE);
        table.add(titleLabel).spaceBottom(5).align(1);
        table.row();

        // Subtitle
        table.add("Options").align(1).spaceBottom(20);
        table.row();

        // FPS-Counter-Checkbox
        final CheckBox fpsCounterCheckbox = new CheckBox("FPS-Counter", skin);
        fpsCounterCheckbox.setChecked(zero.getPreferencesManager().isFpsCounterEnabled());
        fpsCounterCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean enabled = fpsCounterCheckbox.isChecked();
                zero.getPreferencesManager().setFPSCounterEnabled(enabled);
            }
        });
        table.add(fpsCounterCheckbox).expand().fill().pad(0, 150, 25, 150);
        table.row();

        // Button "Back"
        TextButton backButton = new TextButton("BACK", skin);
        backButton.setColor(1, 1, 1, 0.9f);
        backButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new MenuScreen(zero));
            }
        });
        table.add(backButton).fill().pad(0, 150, 25, 150);
        table.row();

        // Footer
        table.add(new Label("www.flaiker.com", skin)).row();
        table.add().padBottom(25).row();

        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    protected String getName() {
        return LOG;
    }
}
