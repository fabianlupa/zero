/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.flaiker.zero.screens.AbstractScreen;
import com.flaiker.zero.screens.MenuScreen;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.services.PreferencesManager;
import com.flaiker.zero.services.ResourceManager;

public class Zero extends Game {
    public static final String LOG = Zero.class.getSimpleName();

    private boolean            debugMode;
    // Services
    private PreferencesManager preferencesManager;
    private ConsoleManager     consoleManager;
    private ResourceManager    resourceManager;

    public Zero(boolean debug) {
        debugMode = debug;
    }

    private void initializeConsoleCommands() {
        consoleManager.clearCommands();

        consoleManager.addCommand(new ConsoleManager.ConsoleCommand("fullscreen", parValuePairs -> {
            if (parValuePairs.containsKey("true")) {
                Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
                                            Gdx.graphics.getDesktopDisplayMode().height, true);
                Gdx.app.log(LOG, "Enabled fullscreen");
            } else if (parValuePairs.containsKey("false")) {
                Gdx.graphics.setDisplayMode((int) AbstractScreen.SCREEN_WIDTH, (int) AbstractScreen.SCREEN_HEIGHT,
                                            false);
                Gdx.app.log(LOG, "Disabled fullscreen");
            }
        }));

        consoleManager.addCommand(new ConsoleManager.ConsoleCommand("exit", parValuePairs -> Gdx.app.exit()));
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    @Override
    public void create() {
        Gdx.app.log(LOG, "Creating game on " + Gdx.app.getType());
        preferencesManager = new PreferencesManager();
        consoleManager = new ConsoleManager();
        resourceManager = new ResourceManager();
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

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        initializeConsoleCommands();
        if (screen instanceof ConsoleManager.CommandableInstance)
            consoleManager.addCommands(((ConsoleManager.CommandableInstance) screen).getConsoleCommands());
    }

    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }

    public ConsoleManager getConsoleManager() {
        return consoleManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
