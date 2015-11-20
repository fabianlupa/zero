/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.Zero;
import com.flaiker.zero.abilities.FireballAbility;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.entities.*;
import com.flaiker.zero.helper.Map;
import com.flaiker.zero.helper.SpawnArgs;
import com.flaiker.zero.helper.WorldContactListener;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.ui.AbilityList;
import com.flaiker.zero.ui.EscapeMenu;
import com.flaiker.zero.ui.GameTimer;
import com.flaiker.zero.ui.Healthbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen implements InputProcessor, ConsoleManager.CommandableInstance {
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    public static final  int   PIXEL_PER_METER     = 64;

    private final World              world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;
    private final RayHandler         rayHandler;

    private Map         map;
    private Player      player;
    private RenderMode  renderMode;
    private float       accumulator;
    private Array<Body> bodies;
    private Healthbar   healthbar;
    private AbilityList abilityList;
    private EscapeMenu  escapeMenu;
    private GameTimer   gameTimer;

    private boolean paused;

    public GameScreen(Zero zero) {
        super(zero);
        paused = false;
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.3f, 0.3f, 0.3f, 1f);
        rayHandler.setBlurNum(3);
        renderMode = RenderMode.GAME;
        world.setContactListener(new WorldContactListener());
        addInputProcessor(this);
        bodies = new Array<>();
        escapeMenu = new EscapeMenu(zero, this, skin);
        gameTimer = new GameTimer(skin);
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    private void updateLogic(float delta) {
        // update camera
        float playerCenterPos = player.getSpriteX() + player.getEntityWidth() / 2f;
        boolean playerOutLeft = playerCenterPos < (SCREEN_WIDTH / 2f / PIXEL_PER_METER);
        boolean playerOutRight = false; //playerCenterPos > (getMap().getMapWidthAsScreenUnits() - (SCREEN_WIDTH / 2));

        if (!playerOutLeft && !playerOutRight) {
            box2dCamera.position.x = player.getSpriteX() + player.getEntityWidth() / 2f;
            camera.position.x = (player.getSpriteX() + player.getEntityWidth() / 2f) * PIXEL_PER_METER;
        } else {
            if (playerOutLeft) {
                box2dCamera.position.x = SCREEN_WIDTH / PIXEL_PER_METER / 2f;
                camera.position.x = SCREEN_WIDTH / 2f;
            }
            //else camera.position.x = getMap().getMapWidthAsScreenUnits() - (SCREEN_WIDTH / 2f);
        }

        // update healthbar
        healthbar.setMaxHealth(player.getMaxHealth());
        healthbar.setCurrentHealth(player.getCurrentHealth());

        // update timer
        gameTimer.updateTime(delta);
    }

    public void pauseGame() {
        paused = true;
    }

    public void unpauseGame() {
        paused = false;
    }

    @Override
    public void show() {
        super.show();
        // load the map
        map = Map.create("map1.tmx", camera, batch);
        if (map == null) {
            Gdx.app.log(LOG, "Map could not be loaded:\n" + Map.getLastError());
            zero.setScreen(new MenuScreen(zero));
            return;
        } else {
            map.addTilesAsBodiesToWorld(world);
        }

        // create the player
        Vector2 playerSpawnPos = map.getPlayerSpawnPosition();
        player = new Player(playerSpawnPos.x, playerSpawnPos.y);
        player.addBodyToWorld(world);
        addInputProcessor(player);

        // create the mobs
        for (SpawnArgs mobSpawn : map.getMobSpawnPositions()) {
            switch (mobSpawn.getSpawnType()) {
                case MOB_ROBOT:
                    RobotMob testMob = new RobotMob(mobSpawn.getX(), mobSpawn.getY());
                    testMob.addBodyToWorld(world);
                    break;
                case MOB_BALL:
                    BallMob testBall = new BallMob(mobSpawn.getX(), mobSpawn.getY(), rayHandler);
                    testBall.addBodyToWorld(world);
                    break;
            }
        }

        // create static objects
        for (SpawnArgs objectSpawn : map.getObjectSpawnPositions()) {
            switch (objectSpawn.getSpawnType()) {
                case STATIC_LAMPROPE:
                    float height = (float) objectSpawn.getAdArgs().getOrDefault(LampRope.AD_ARGS_HEIGHT_KEY,
                                                                                LampRope.AD_ARGS_HEIGHT_DEFAULT);
                    float pan = (float) objectSpawn.getAdArgs().getOrDefault(LampRope.AD_ARGS_PAN_KEY,
                                                                             LampRope.AD_ARGS_PAN_DEFAULT);
                    LampRope lampRope = new LampRope(rayHandler, objectSpawn.getX(), objectSpawn.getY(), height, pan);
                    lampRope.addBodyToWorld(world);
                    break;
                case STATIC_LAMPHORIZONTAL:
                    Color color = (Color) objectSpawn.getAdArgs()
                                                     .getOrDefault(LampHorizontal.AD_ARGS_COLOR_KEY,
                                                                   Color.valueOf(LampHorizontal.AD_ARGS_COLOR_DEFAULT));
                    LampHorizontal lampHorizontal = new LampHorizontal(rayHandler, objectSpawn.getX(),
                                                                       objectSpawn.getY(), color);
                    lampHorizontal.addBodyToWorld(world);
                    break;
            }
        }

        healthbar = new Healthbar();
        healthbar.setPosition(0, SCREEN_HEIGHT - 55);
        healthbar.setSize(224, 55);
        uiStage.addActor(healthbar);

        abilityList = new AbilityList(player, skin);
        uiStage.addActor(abilityList.getActor());
        // testabilities to make the list not empty
        abilityList.addAbility(new FireballAbility(skin));
        abilityList.addAbility(new FireballAbility(skin));

        uiStage.addActor(escapeMenu.getEscapeMenuTable());

        uiStage.addActor(gameTimer.getTimerButton());
    }

    @Override
    public void preUIrender(float delta) {
        // start tiledmaprenderer for this frame
        map.updateCamera();

        batch.enableBlending();

        // render background using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderBackground();

        // render collisionlayer using Box2d
        box2dCamera.update();
        if (renderMode == RenderMode.BOX2D) debugRenderer.render(world, box2dCamera.combined);

        world.getBodies(bodies);
        for (Body b : bodies) {
            Object userData = b.getUserData();
            if (userData instanceof AbstractEntity) {
                AbstractEntity entity = (AbstractEntity) userData;
                if (!isPaused()) entity.update();
                if (renderMode == RenderMode.GAME || renderMode == RenderMode.TILED) entity.render(batch);
            } else if (userData instanceof AbstractBlock) {
                if (renderMode == RenderMode.GAME) ((AbstractBlock) userData).render(batch);
            }
        }

        // render collisionlayer using tiled
        if (renderMode == RenderMode.TILED) map.renderCollisionLayer();

        // render foregroundlayer using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderForeground();

        // render lights using Box2d
        if (renderMode == RenderMode.GAME) {
            batch.disableBlending();
            rayHandler.setCombinedMatrix(box2dCamera.combined);
            rayHandler.updateAndRender();
        }

        if (!isPaused()) {
            doPhysicsStep(delta);
            updateLogic(delta);
        }
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
                renderMode = RenderMode.GAME;
                Gdx.app.log(LOG, "Set rendermode to GAME");
                keyProcessed = true;
                break;
            case Input.Keys.F2:
                renderMode = RenderMode.BOX2D;
                Gdx.app.log(LOG, "Set rendermode to BOX2D");
                keyProcessed = true;
                break;
            case Input.Keys.F3:
                renderMode = RenderMode.TILED;
                Gdx.app.log(LOG, "Set rendermode to TILED");
                keyProcessed = true;
                break;
            case Input.Keys.TAB:
                abilityList.switchState();
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                escapeMenu.switchPauseState();
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
        outList.addAll(player.getConsoleCommands());

        return outList;
    }

    private enum RenderMode {
        GAME, BOX2D, TILED
    }
}