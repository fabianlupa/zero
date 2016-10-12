/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.abilities.AbstractAbility;
import com.flaiker.zero.abilities.FireballAbility;
import com.flaiker.zero.abilities.LaserAbility;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.box2d.AbstractBox2dObject;
import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.box2d.WorldContactListener;
import com.flaiker.zero.entities.AbstractEntity;
import com.flaiker.zero.entities.AbstractSpawnableEntity;
import com.flaiker.zero.entities.Player;
import com.flaiker.zero.helper.Map;
import com.flaiker.zero.helper.SpawnArgs;
import com.flaiker.zero.injection.DependencyInjector;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.tiles.TileRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.flaiker.zero.ui.screens.AbstractScreen.SCREEN_HEIGHT;
import static com.flaiker.zero.ui.screens.AbstractScreen.SCREEN_WIDTH;

public class Game implements ConsoleManager.CommandableInstance {
    public static final int PIXEL_PER_METER = 64;

    private static final String LOG = Game.class.getSimpleName();

    // Box2d related constants
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    private static final float GRAVITY             = -10f;

    // Game objects
    private final World  world;
    private final Map    map;
    private       Player player;

    // Rendering related
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;
    private final Camera             camera;
    private final RayHandler         rayHandler;

    private final DependencyInjector dependencyInjector;
    private final InputMultiplexer inputMultiplexer;

    private float       accumulator;
    private Array<Body> bodies;

    private List<AbstractAbility> abilities;

    public Game(Map map, Camera camera) {
        // Box2D
        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new WorldContactListener());
        bodies = new Array<>();

        // Game objects
        this.map = map;

        // Set up camera
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        this.camera = camera;

        // Set up rendering
        debugRenderer = new Box2DDebugRenderer();
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.3f, 0.3f, 0.3f, 1f);
        rayHandler.setBlurNum(3);

        dependencyInjector = new DependencyInjector();
        dependencyInjector.addDependency(rayHandler);

        inputMultiplexer = new InputMultiplexer();

        initializeWorld();

        abilities = new ArrayList<>();
        abilities.add(new FireballAbility(wbi, player));
        abilities.add(new LaserAbility(wbi, player));
        player.switchSelectedAbility(abilities.get(1));
    }

    public void render(Batch batch, RenderMode renderMode) {
        // Update tiledmaprenderer for this frame
        map.updateCamera();

        batch.enableBlending();

        // Render background using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderBackground();

        // Render collision layer using Box2d
        box2dCamera.update();
        if (renderMode == RenderMode.BOX2D) debugRenderer.render(world, box2dCamera.combined);

        // Render blocks and entities
        world.getBodies(bodies);
        for (Body b : bodies) {
            Object userData = b.getUserData();
            if (userData instanceof AbstractEntity) {
                AbstractEntity entity = (AbstractEntity) userData;
                if (renderMode == RenderMode.GAME || renderMode == RenderMode.TILED)
                    entity.render(batch);
            } else if (userData instanceof AbstractBlock) {
                if (renderMode == RenderMode.GAME) ((AbstractBlock) userData).render(batch);
            }
        }

        // Render abilities
        for (AbstractAbility ability : abilities) ability.render(batch);

        // Render collision layer using tiled
        if (renderMode == RenderMode.TILED) map.renderCollisionLayer();

        // Render foreground layer using tiled
        if (renderMode == RenderMode.TILED || renderMode == RenderMode.GAME) map.renderForeground();

        // Render lights using Box2d
        if (renderMode == RenderMode.GAME) {
            batch.disableBlending();
            rayHandler.setCombinedMatrix(box2dCamera);
            rayHandler.updateAndRender();
        }
    }

    public void update(float delta) {
        // Update camera
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

        // Step forward in box2d
        doPhysicsStep(delta);

        // Update abilities
        for (AbstractAbility ability : abilities) ability.update(delta);

        world.getBodies(bodies);
        for (Body b : bodies) {
            if (b.getUserData() instanceof AbstractBox2dObject) {
                AbstractBox2dObject box2dObject = (AbstractBox2dObject) b.getUserData();

                // Update all box2d objects
                box2dObject.update(delta);

                // Remove entities that are marked for deletion
                if (box2dObject.isMarkedForDeletion()) {
                    box2dObject.dispose();
                    // Delete all joined bodies
                    b.getJointList().forEach(j -> world.destroyBody(j.other));
                    // Delete the main body itself
                    world.destroyBody(b);
                }
            }
        }
    }

    public InputProcessor getInputProcessor() {
        return inputMultiplexer;
    }

    private void initializeWorld() {
        // Add the maps tiles to the box2d world
        map.addTilesAsBodiesToWorld(world);

        // Spawn entities add their spawns
        for (SpawnArgs spawnArgs : map.getSpawns()) {
            Optional<Class<? extends AbstractSpawnableEntity>> entityType =
                    TileRegistry.getInstance().getEntityClassByType(spawnArgs.getType());

            if (entityType.isPresent()) {
                AbstractSpawnableEntity newEntity;
                try {
                    newEntity = entityType.get().newInstance();
                    newEntity.initializeSpawnPosition(spawnArgs.getX(), spawnArgs.getY());
                    newEntity.applyAdArgs(spawnArgs.getAdArgs());

                    dependencyInjector.injectDependenciesIfNecessary(newEntity);

                    newEntity.init();
                    newEntity.addBodyToWorld(world);

                    if (newEntity instanceof Player) {
                        inputMultiplexer.addProcessor((Player) newEntity);
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
    }

    private void clear() {
        // Remove all bodies from the world
        world.getBodies(bodies);
        int bodyCount = bodies.size;
        for (int i = 0; i < bodyCount; i++) world.destroyBody(bodies.get(i));
    }

    private void restart() {
        clear();
        initializeWorld();
        accumulator = 0f;
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    private WorldBodyInjector wbi = new WorldBodyInjector() {
        @Override
        public void addBodyToWorld(AbstractBox2dObject box2dObject) {
            dependencyInjector.injectDependenciesIfNecessary(box2dObject);
            box2dObject.init();
            addBodyToWorldWithoutDependencyInjection(box2dObject);
        }

        @Override
        public void addBodyToWorldWithoutDependencyInjection(AbstractBox2dObject box2dObject) {
            box2dObject.addBodyToWorld(world);
        }
    };

    @Override
    public List<ConsoleManager.ConsoleCommand> getConsoleCommands() {
        List<ConsoleManager.ConsoleCommand> outList = new ArrayList<>();

        outList.addAll(player.getConsoleCommands());

        return outList;
    }

    public enum RenderMode {
        GAME, BOX2D, TILED
    }
}
