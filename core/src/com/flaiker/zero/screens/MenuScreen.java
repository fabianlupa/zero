/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.flaiker.zero.Zero;
import com.flaiker.zero.helper.DefaultActorListener;
import com.flaiker.zero.services.rmtasks.LoadIngameAssetsTask;
import com.flaiker.zero.services.rmtasks.RefreshAtlasesTask;

/**
 * Screen which holds the menu
 */
public class MenuScreen extends AbstractScreen {
    public static final String LOG = MenuScreen.class.getSimpleName();

    public MenuScreen(Zero zero) {
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
        table.add("A platformer game").align(1).spaceBottom(20);
        table.row();

        // Button "Start"
        TextButton startGameButton = new TextButton("START", skin);
        startGameButton.setColor(1, 1, 1, 0.9f);
        startGameButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new LevelSelectionScreen(zero));
            }
        });
        table.add(startGameButton).expand().fill().pad(0, 150, 25, 150);
        table.row();

        // Button "Options"
        TextButton optionsButton = new TextButton("OPTIONS", skin);
        optionsButton.setColor(1, 1, 1, 0.9f);
        optionsButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new OptionsScreen(zero));
            }
        });
        table.add(optionsButton).expand().fill().pad(0, 150, 25, 150);
        table.row();

        // Button "Exit"
        TextButton exitButton = new TextButton("EXIT", skin);
        exitButton.setColor(1, 1, 1, 0.9f);
        exitButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Gdx.app.exit();
            }
        });
        table.add(exitButton).expand().fill().pad(0, 150, 25, 150);
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
