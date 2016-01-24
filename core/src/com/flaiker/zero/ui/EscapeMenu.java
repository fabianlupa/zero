/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.flaiker.zero.Zero;
import com.flaiker.zero.helper.DefaultActorListener;
import com.flaiker.zero.screens.AbstractScreen;
import com.flaiker.zero.screens.GameScreen;
import com.flaiker.zero.screens.MenuScreen;

public class EscapeMenu {
    private final Zero       zero;
    private final GameScreen gameScreen;
    private       Table      menuTable;
    private       boolean    isVisible;

    public EscapeMenu(final Zero zero, final GameScreen gameScreen, Skin skin) {
        this.zero = zero;
        this.gameScreen = gameScreen;
        isVisible = false;
        menuTable = new Table(skin);
        menuTable.setVisible(false);
        menuTable.setSize(200, 400);
        menuTable.setPosition(AbstractScreen.SCREEN_WIDTH / 2 - menuTable.getWidth() / 2,
                              AbstractScreen.SCREEN_HEIGHT / 2 - menuTable.getHeight() / 2);
        menuTable.setBackground(new NinePatchDrawable(skin.getPatch("default-round-down")));
        menuTable.pad(10);
        menuTable.setColor(1, 1, 1, 0.8f);

        // Title
        Label titleLabel = new Label("PAUSED", skin, "digital7-64", Color.WHITE);
        menuTable.add(titleLabel).spaceBottom(20).align(1);
        menuTable.row();

        // Button "Resume"
        TextButton resumeButton = new TextButton("RESUME", skin);
        resumeButton.setColor(1, 1, 1, 1);
        resumeButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                unpauseGame();

            }
        });
        menuTable.add(resumeButton).expand().fill().spaceBottom(20);
        menuTable.row();

        // Button "Exit-Level"
        TextButton exitLevelButton = new TextButton("EXIT LEVEL", skin);
        exitLevelButton.setColor(1, 1, 1, 1);
        exitLevelButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new MenuScreen(zero));
            }
        });
        menuTable.add(exitLevelButton).expand().fill().spaceBottom(20);
        menuTable.row();

        // Button "Exit-Game"
        TextButton exitGameButton = new TextButton("EXIT GAME", skin);
        exitGameButton.setColor(1, 1, 1, 1);
        exitGameButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Gdx.app.exit();
            }
        });
        menuTable.add(exitGameButton).expand().fill();
        menuTable.row();
    }

    public Table getEscapeMenuTable() {
        return menuTable;
    }

    public void pauseGame() {
        showMenu();
        gameScreen.pauseGame();
    }

    public void unpauseGame() {
        hideMenu();
        gameScreen.unpauseGame();
    }

    private void showMenu() {
        menuTable.setVisible(true);
        isVisible = true;
    }

    private void hideMenu() {
        menuTable.setVisible(false);
        isVisible = false;
    }

    public void switchPauseState() {
        if (isVisible) unpauseGame();
        else pauseGame();
    }
}
