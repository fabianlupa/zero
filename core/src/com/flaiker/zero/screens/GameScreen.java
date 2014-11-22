package com.flaiker.zero.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.Zero;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.blocks.WhiteBlock;
import com.flaiker.zero.entities.Player;
import com.flaiker.zero.helper.EntityConverterInterface;

/**
 * Screen where the game is played on
 */
public class GameScreen extends AbstractScreen implements InputProcessor {
    private static final float TIME_STEP           = 1 / 300f;
    private static final int   VELOCITY_ITERATIONS = 6;
    private static final int   POSITION_ITERATIONS = 2;
    private static final int   PIXEL_PER_METER     = 64;

    private final World              world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera box2dCamera;
    private final Player             player;

    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private RenderMode renderMode;

    private InputMultiplexer inputMultiplexer;

    private float accumulator = 0;

    public GameScreen(Zero zero) {
        super(zero);
        box2dCamera = new OrthographicCamera(SCREEN_WIDTH / PIXEL_PER_METER, SCREEN_HEIGHT / PIXEL_PER_METER);
        box2dCamera.position.set(SCREEN_WIDTH / PIXEL_PER_METER / 2f, SCREEN_HEIGHT / PIXEL_PER_METER / 2f, 0);
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        renderMode = RenderMode.GAME;
        inputMultiplexer = new InputMultiplexer(this);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // create the player
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(0.5f, 6);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body playerBody = world.createBody(bdef);
        player = new Player(playerBody, bdef.position.x, bdef.position.y);
        playerBody.setUserData(player);

        shape.setAsBox(0.5f, 0.5f);
        fdef.shape = shape;
        //fdef.friction = 0.5f;
        fdef.density = 0f;
        playerBody.createFixture(fdef).setUserData("player");
        inputMultiplexer.addProcessor((Player) playerBody.getUserData());

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
                    //fdef.friction = 0.5f;
                    fdef.shape = cs;
                    fdef.isSensor = false;
                    Body tileBody = world.createBody(bdef);
                    tileBody.setUserData(new WhiteBlock(col * PIXEL_PER_METER, row * PIXEL_PER_METER));
                    tileBody.createFixture(fdef);
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
        if (renderMode == RenderMode.TILED) mapRenderer.render();

        // render Box2d
        box2dCamera.update();
        if (renderMode == RenderMode.BOX2D) debugRenderer.render(world, box2dCamera.combined);

        mapRenderer.getSpriteBatch().begin();

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body b : bodies) {
            Object userData = b.getUserData();
            if (userData instanceof EntityConverterInterface) {
                EntityConverterInterface e = (EntityConverterInterface) userData;
                e.setPosition(b.getPosition().x * PIXEL_PER_METER, b.getPosition().y * PIXEL_PER_METER);
                e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
                e.update();
                if (renderMode == RenderMode.GAME || renderMode == RenderMode.TILED) e.render(mapRenderer.getSpriteBatch());
            } else if (userData instanceof AbstractBlock) {
                if (renderMode == RenderMode.GAME) ((AbstractBlock) userData).render(mapRenderer.getSpriteBatch());
            }
        }

        mapRenderer.getSpriteBatch().end();

        doPhysicsStep(delta);
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
                keyProcessed = true;
                break;
            case Input.Keys.F2:
                renderMode = RenderMode.BOX2D;
                keyProcessed = true;
                break;
            case Input.Keys.F3:
                renderMode = RenderMode.TILED;
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