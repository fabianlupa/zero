/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.flaiker.zero.Game;
import com.flaiker.zero.Zero;
import com.flaiker.zero.box2d.AbstractBox2dObject;
import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.helper.Map;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.ui.elements.AbilityList;
import com.flaiker.zero.ui.elements.EscapeMenu;
import com.flaiker.zero.ui.elements.GameTimer;
import com.flaiker.zero.ui.elements.Healthbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen implements InputProcessor, ConsoleManager.CommandableInstance,
                                                          WorldBodyInjector {
    private FileHandle      mapHandle;
    private Game.RenderMode renderMode;
    private Healthbar       healthbar;
    private AbilityList     abilityList;
    private EscapeMenu      escapeMenu;
    private GameTimer       gameTimer;
    private boolean         paused;
    private Game            game;

    public GameScreen(Zero zero, FileHandle mapHandle) {
        super(zero);
        paused = false;
        renderMode = Game.RenderMode.GAME;
        addInputProcessor(this);
        escapeMenu = new EscapeMenu(zero, this, skin);
        gameTimer = new GameTimer(skin);
        this.mapHandle = mapHandle;
    }


    private void updateLogic(float delta) {

        // update healthbar
        /*healthbar.setMaxHealth(player.getMaxHealth());
        healthbar.setCurrentHealth(player.getCurrentHealth());*/

        // update timer
        gameTimer.updateTime(delta);

        // Update abilities
        //for (AbstractAbility ability : abilityList.getAbilityList()) ability.update(delta);
    }

    public void pauseGame() {
        paused = true;
    }

    public void unpauseGame() {
        paused = false;
    }

    @Override
    protected void preUIrender(float delta) {
        super.preUIrender(delta);

        if (game != null) game.render(batch, renderMode);
        if (!paused && game != null) game.update(delta);
    }

    @Override
    public void show() {
        super.show();

        // load the map
        Map map = Map.create(mapHandle.name(), camera, batch);
        if (map == null) {
            Gdx.app.log(LOG, "Map could not be loaded:\n" + Map.getLastError());
            zero.setScreen(new MenuScreen(zero));
            return;
        }

        game = new Game(map, camera);

        healthbar = new Healthbar();
        healthbar.setPosition(0, SCREEN_HEIGHT - 55);
        healthbar.setSize(224, 55);
        uiStage.addActor(healthbar);

        //abilityList = new AbilityList(player, skin);
        //uiStage.addActor(abilityList.getActor());
        // testabilities to make the list not empty
        //abilityList.addAbility(new FireballAbility(skin, this, player));
        //abilityList.addAbility(new FireballAbility(skin, this, player));

        uiStage.addActor(escapeMenu.getEscapeMenuTable());

        uiStage.addActor(gameTimer.getTimerButton());
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void pause() {
        super.pause();
        escapeMenu.pauseGame();
    }

    @Override
    protected String getName() {
        return GameScreen.class.getSimpleName();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.F1:
                renderMode = Game.RenderMode.GAME;
                Gdx.app.log(LOG, "Set rendermode to GAME");
                keyProcessed = true;
                break;
            case Input.Keys.F2:
                renderMode = Game.RenderMode.BOX2D;
                Gdx.app.log(LOG, "Set rendermode to BOX2D");
                keyProcessed = true;
                break;
            case Input.Keys.F3:
                renderMode = Game.RenderMode.TILED;
                Gdx.app.log(LOG, "Set rendermode to TILED");
                keyProcessed = true;
                break;
            case Input.Keys.TAB:
                abilityList.switchState();
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                escapeMenu.switchPauseState();
                keyProcessed = true;
                break;
        }

        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public List<ConsoleManager.ConsoleCommand> getConsoleCommands() {
        List<ConsoleManager.ConsoleCommand> outList = new ArrayList<>();
        //outList.addAll(player.getConsoleCommands());

        outList.add(new ConsoleManager.ConsoleCommand("r", m -> zero.setScreen(new GameScreen(zero, mapHandle))));

        return outList;
    }

    @Override
    public void addBodyToWorld(AbstractBox2dObject box2dObject) {
        //box2dObject.addBodyToWorld(world);
    }

}