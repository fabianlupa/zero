package com.flaiker.zero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.flaiker.zero.screens.AbstractScreen;
import com.flaiker.zero.screens.MenuScreen;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.services.PreferencesManager;

import java.util.HashMap;

public class Zero extends Game {
    public static final String LOG = Zero.class.getSimpleName();

    // Services
    private PreferencesManager preferencesManager;
    private ConsoleManager     consoleManager;

    private void initializeConsoleCommands() {
        consoleManager.clearCommands();

        consoleManager.addCommand(new ConsoleManager.ConsoleCommand("fullscreen", new ConsoleManager.CommandExecutor() {
            @Override
            public void OnCommandFired(HashMap<String, String> parValuePairs) {
                if (parValuePairs.containsKey("true")) {
                    Gdx.graphics
                            .setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
                    Gdx.app.log(LOG, "Enabled fullscreen");
                } else if (parValuePairs.containsKey("false")) {
                    Gdx.graphics.setDisplayMode((int) AbstractScreen.SCREEN_WIDTH, (int) AbstractScreen.SCREEN_HEIGHT, false);
                    Gdx.app.log(LOG, "Disabled fullscreen");
                }
            }
        }));

        consoleManager.addCommand(new ConsoleManager.ConsoleCommand("exit", new ConsoleManager.CommandExecutor() {
            @Override
            public void OnCommandFired(HashMap<String, String> parValuePairs) {
                Gdx.app.exit();
            }
        }));
    }

    @Override
    public void create() {
        Gdx.app.log(LOG, "Creating game on " + Gdx.app.getType());
        preferencesManager = new PreferencesManager();
        consoleManager = new ConsoleManager();
        initializeConsoleCommands();
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

        if (getScreen() == null) setScreen(new MenuScreen(this));
    }

    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }

    public ConsoleManager getConsoleManager() {
        return consoleManager;
    }
}
