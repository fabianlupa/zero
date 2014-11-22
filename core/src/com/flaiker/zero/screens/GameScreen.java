package com.flaiker.zero.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.Zero;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen {
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    private static final int   PIXEL_PER_METER     = 64;

    private final World              world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;

    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private float accumulator = 0;

    public GameScreen(Zero zero) {
        super(zero);
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();

        // create the player
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(0.5f, 6);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body playerBody = world.createBody(bdef);

        shape.setAsBox(0.5f, 0.5f);
        fdef.shape = shape;
        playerBody.createFixture(fdef).setUserData("player");

        // load the map
        loadMap("map1.tmx");
    }

    private void loadMap(String fileName) {
        tiledMap = new TmxMapLoader().load("maps/" + fileName);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("mgLayer");
        float tileSize = collisionLayer.getTileWidth();

        // make Box2d objects/bodies out of the tiles
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < collisionLayer.getHeight(); row++) {
            for (int col = 0; col < collisionLayer.getWidth(); col++) {
                // get cell
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);

                if (cell != null && cell.getTile() != null) {
                    // create a body + fixture from cell
                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set((col + 0.5f) * tileSize / PIXEL_PER_METER, (row + 0.5f) * tileSize / PIXEL_PER_METER);

                    ChainShape cs = new ChainShape();
                    Vector2[] v = new Vector2[4];
                    v[0] = new Vector2(-tileSize / 2 / PIXEL_PER_METER, -tileSize / 2 / PIXEL_PER_METER);
                    v[1] = new Vector2(-tileSize / 2 / PIXEL_PER_METER, tileSize / 2 / PIXEL_PER_METER);
                    v[2] = new Vector2(tileSize / 2 / PIXEL_PER_METER, tileSize / 2 / PIXEL_PER_METER);
                    v[3] = new Vector2(tileSize / 2 / PIXEL_PER_METER, -tileSize / 2 / PIXEL_PER_METER);
                    cs.createLoop(v);
                    fdef.friction = 0;
                    fdef.shape = cs;
                    fdef.isSensor = false;
                    world.createBody(bdef).createFixture(fdef);
                }
            }
        }
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void preUIrender(float delta) {
        // render TiledMap
        mapRenderer.setView(camera);
        //mapRenderer.render();

        // render Box2d
        box2dCamera.update();
        debugRenderer.render(world, box2dCamera.combined);

        doPhysicsStep(delta);
    }

    @Override
    protected String getName() {
        return GameScreen.class.getSimpleName();
    }
}