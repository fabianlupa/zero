package com.flaiker.zero.screens;

import com.flaiker.zero.Zero;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen {
    public GameScreen(Zero zero) {
        super(zero);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void preUIrender(float delta) {}

    @Override
    protected String getName() {
        return GameScreen.class.getSimpleName();
    }
}