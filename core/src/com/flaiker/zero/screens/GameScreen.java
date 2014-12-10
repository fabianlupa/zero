package com.flaiker.zero.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.Zero;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.entities.AbstractEntity;
import com.flaiker.zero.entities.Player;
import com.flaiker.zero.entities.RobotMob;
import com.flaiker.zero.helper.Map;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen implements InputProcessor {
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    public static final  int   PIXEL_PER_METER     = 64;

    private final World              world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;

    private Map              map;
    private Player           player;
    private RenderMode       renderMode;
    private InputMultiplexer inputMultiplexer;
    private float            accumulator;
    private Array<Body>      bodies;

    public GameScreen(Zero zero) {
        super(zero);
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        renderMode = RenderMode.GAME;
        inputMultiplexer = new InputMultiplexer(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        bodies = new Array<>();
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
        player = new Player(world, playerSpawnPos.x, playerSpawnPos.y);
        inputMultiplexer.addProcessor(player);

        // create a testmob
        RobotMob testMob = new RobotMob(world, 4, 5);
    }

    @Override
    public void preUIrender(float delta) {
        // start tiledmaprenderer for this frame
        map.updateCamera();

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
                entity.update();
                if (renderMode == RenderMode.GAME || renderMode == RenderMode.TILED) entity.render(batch);
            } else if (userData instanceof AbstractBlock) {
                if (renderMode == RenderMode.GAME) ((AbstractBlock) userData).render(batch);
            }
        }

        // render collisionlayer using tiled
        if (renderMode == RenderMode.TILED) map.renderCollisionLayer();

        // render foregroundlayer using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderForeground();

        doPhysicsStep(delta);
        updateLogic(delta);
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

    private enum RenderMode {
        GAME, BOX2D, TILED
    }
}