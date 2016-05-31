/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.Zero;
import com.flaiker.zero.abilities.AbstractAbility;
import com.flaiker.zero.abilities.FireballAbility;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.box2d.*;
import com.flaiker.zero.entities.AbstractEntity;
import com.flaiker.zero.entities.Player;
import com.flaiker.zero.helper.Map;
import com.flaiker.zero.helper.SpawnArgs;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.tiles.TileRegistry;
import com.flaiker.zero.ui.elements.AbilityList;
import com.flaiker.zero.ui.elements.EscapeMenu;
import com.flaiker.zero.ui.elements.GameTimer;
import com.flaiker.zero.ui.elements.Healthbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen implements InputProcessor, ConsoleManager.CommandableInstance,
                                                          WorldBodyInjector {
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    private static final float GRAVITY             = -10f;
    public static final  int   PIXEL_PER_METER     = 64;

    private final World              world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;
    private final RayHandler         rayHandler;

    private FileHandle  mapHandle;
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

    public GameScreen(Zero zero, FileHandle mapHandle) {
        super(zero);
        paused = false;
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        world = new World(new Vector2(0, GRAVITY), true);
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
        this.mapHandle = mapHandle;
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

        // Update abilities
        for (AbstractAbility ability : abilityList.getAbilityList()) ability.update(delta);

        // Remove entities that are marked for deletion
        world.getBodies(bodies);
        for (Body b : bodies) {
            if (b.getUserData() instanceof AbstractBox2dObject) {
                AbstractBox2dObject box2dObject = (AbstractBox2dObject) b.getUserData();
                if (box2dObject.isMarkedForDeletion()) world.destroyBody(b);
            }
        }
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
        map = Map.create(mapHandle.name(), camera, batch);
        if (map == null) {
            Gdx.app.log(LOG, "Map could not be loaded:\n" + Map.getLastError());
            zero.setScreen(new MenuScreen(zero));
            return;
        } else {
            map.addTilesAsBodiesToWorld(world);
        }


        // Spawn entities at loaded spawn positions
        for (SpawnArgs spawnArgs : map.getSpawns()) {
            Optional<Class<? extends AbstractEntity>> entityType =
                    TileRegistry.getInstance().getEntityClassByType(spawnArgs.getType());

            if (entityType.isPresent()) {
                AbstractEntity newEntity;
                try {
                    newEntity = entityType.get().newInstance();
                    newEntity.initializeSpawnPosition(spawnArgs.getX(), spawnArgs.getY());
                    newEntity.applyAdArgs(spawnArgs.getAdArgs());

                    if (newEntity instanceof LightSourceInjectorInterface) {
                        ((LightSourceInjectorInterface) newEntity).initializeRayHandler(rayHandler);
                    }

                    newEntity.init();
                    newEntity.addBodyToWorld(world);

                    if (newEntity instanceof Player) {
                        addInputProcessor((Player) newEntity);
                        player = (Player) newEntity;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    Gdx.app.log(LOG, "Could not spawn entity of");
                }
            } else {
                Gdx.app.log(LOG, "Entity spawn of type " + spawnArgs.getType() +
                                 " was found in map but not in TileRegistry");
            }
        }

        healthbar = new Healthbar();
        healthbar.setPosition(0, SCREEN_HEIGHT - 55);
        healthbar.setSize(224, 55);
        uiStage.addActor(healthbar);

        abilityList = new AbilityList(player, skin);
        uiStage.addActor(abilityList.getActor());
        // testabilities to make the list not empty
        abilityList.addAbility(new FireballAbility(skin, this, player));
        abilityList.addAbility(new FireballAbility(skin, this, player));

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
                if (!isPaused()) entity.update(delta);
                if (renderMode == RenderMode.GAME || renderMode == RenderMode.TILED) entity.render(batch);
            } else if (userData instanceof AbstractBlock) {
                if (renderMode == RenderMode.GAME) ((AbstractBlock) userData).render(batch);
            }
        }

        // Render abilities
        for (AbstractAbility ability : abilityList.getAbilityList()) ability.render(delta);

        // render collisionlayer using tiled
        if (renderMode == RenderMode.TILED) map.renderCollisionLayer();

        // render foregroundlayer using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderForeground();

        // render lights using Box2d
        if (renderMode == RenderMode.GAME) {
            batch.disableBlending();
            rayHandler.setCombinedMatrix(box2dCamera);
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
        outList.addAll(player.getConsoleCommands());

        outList.add(new ConsoleManager.ConsoleCommand("r", m -> zero.setScreen(new GameScreen(zero, mapHandle))));

        return outList;
    }

    @Override
    public void addBodyToWorld(AbstractBox2dObject box2dObject) {
        box2dObject.addBodyToWorld(world);
    }

    private enum RenderMode {
        GAME, BOX2D, TILED
    }
}