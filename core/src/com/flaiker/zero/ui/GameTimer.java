/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.flaiker.zero.screens.AbstractScreen;

import java.util.concurrent.TimeUnit;

/**
 * Created by Flaiker on 22.12.2014.
 */
public class GameTimer {
    private TextButton timerButton;
    private long       gameTimeMillis;

    public GameTimer(Skin skin) {
        gameTimeMillis = 0l;
        timerButton = new TextButton("TIME", skin);
        timerButton.setWidth(200f);
        timerButton.setPosition(AbstractScreen.SCREEN_WIDTH / 2 - timerButton.getWidth() / 2,
                                AbstractScreen.SCREEN_HEIGHT - timerButton.getHeight());
        timerButton.setDisabled(true);
    }

    public TextButton getTimerButton() {
        return timerButton;
    }

    public void updateTime(float delta) {
        gameTimeMillis += delta * 1000f;
        String formattedTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(gameTimeMillis),
                                             TimeUnit.MILLISECONDS.toSeconds(gameTimeMillis) -
                                             TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(gameTimeMillis)));
        timerButton.setText(formattedTime);
    }
}
