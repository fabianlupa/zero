package com.flaiker.zero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Zero extends Game {
    public static final String LOG = Zero.class.getSimpleName();

    @Override
    public void create() {
        Gdx.app.log(LOG, "Creating game on " + Gdx.app.getType());
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(LOG, "Disposing game");
    }

    @Override
    public void pause() {
        super.pause();
        Gdx.app.log(LOG, "Pausing game");
    }

    @Override
    public void resume() {
        super.resume();
        Gdx.app.log(LOG, "Resuming game");
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Gdx.app.log(LOG, "Resizing game to: " + width + " x " + height);

        //if (getScreen() == null) setScreen(new MenuScreen(this));
    }
}
