/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.blocks.AbstractEdgedBlock;
import com.flaiker.zero.blocks.MetalBlock;
import com.flaiker.zero.entities.LampHorizontal;
import com.flaiker.zero.entities.LampRope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flaiker on 22.11.2014.
 */

/**
 * Holder-Class for a map made in Tiled (.tmx).
 * <p>
 * Needs to have the following layers:
 * <ul>
 * <li>fgLayer - rendered by {@link com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer}</li>
 * <li>mgLayer - rendered using Box2d, provides collision with entities</li>
 * <li>bgLayer - rendered by {@link com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer}</li>
 * <li>ojLayer - not rendered, defines spawnpositions of entities</li>
 * </ul>
 * </p>
 * <p>
 * The width of a tile is taken from the mgLayer-tileset. Tiles must be squares.
 * </p>
 */
public class Map {
    public static final String LOG = Map.class.getSimpleName();

    private static final String COLLISION_LAYER_NAME                          = "mgLayer";
    private static final String FOREGROUND_LAYER_NAME                         = "fgLayer";
    private static final String BACKGROUND_LAYER_NAME                         = "bgLayer";
    private static final String SPAWN_LAYER_NAME                              = "ojLayer";
    private static final String SPAWN_LAYER_OBJECT_TYPE_NAME                  = "type";
    private static final String SPAWN_LAYER_OBJECT_TYPE_PLAYER_NAME           = "player";
    private static final String SPAWN_LAYER_OBJECT_TYPE_MOB_NAME              = "mob";
    private static final String SPAWN_LAYER_OBJECT_TYPE_STATIC_NAME           = "static";
    private static final String SPAWN_LAYER_OBJECT_MOB_SUBTYPE_NAME           = "mobname";
    private static final String SPAWN_LAYER_OBJECT_MOB_SUBTYPE_ROBOT_NAME     = "robot";
    private static final String SPAWN_LAYER_OBJECT_MOB_SUBTYPE_BALL_NAME      = "ball";
    private static final String SPAWN_LAYER_OBJECT_STATIC_SUBTYPE_NAME        = "ojname";
    private static final String SPAWN_LAYER_OBJECT_STATIC_LAMPROPE_NAME       = "lampRope";
    private static final String SPAWN_LAYER_OBJECT_STATIC_LAMPHORIZONTAL_NAME = "lampHorizontal";
    private static final String GID                                           = "gid";

    private static String lastError;

    private float             mapTileSize;
    private TiledMapTileLayer foregroundLayer;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer backgroundLayer;
    private MapLayer          spawnLayer;

    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera         camera;
    private Vector2                    playerSpawnPosition;
    private List<SpawnArgs>            mobSpawnPositions;
    private List<SpawnArgs>            objectSpawnPositions;
    private SpriteBatch                batch;

    public static Map create(String fileName, OrthographicCamera camera, SpriteBatch batch) {
        if (camera == null) {
            lastError = "Camera cannot be null.";
            return null;
        }
        if (!Gdx.files.local("maps/" + fileName).exists()) {
            lastError = "To be loaded map at \"maps/" + fileName + "\" does not exist.";
            return null;
        }
        if (batch == null) {
            lastError = "Batch cannot be null.";
            return null;
        }

        Map map = new Map(fileName, camera, batch);
        if (map.getErrorString() != null) {
            lastError = map.getErrorString();
            return null;
        } else return map;
    }

    public static String getLastError() {
        return lastError;
    }

    private Map(String fileName, OrthographicCamera camera, SpriteBatch batch) {
        this.camera = camera;
        this.batch = batch;
        mobSpawnPositions = new ArrayList<>();
        objectSpawnPositions = new ArrayList<>();
        loadMap(fileName);
        loadSpawns();
    }

    private void loadMap(String fileName) {
        tiledMap = new TmxMapLoader().load("maps/" + fileName);
        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(COLLISION_LAYER_NAME);
        foregroundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(FOREGROUND_LAYER_NAME);
        backgroundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND_LAYER_NAME);
        spawnLayer = tiledMap.getLayers().get(SPAWN_LAYER_NAME);
        mapTileSize = collisionLayer.getTileWidth();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
    }

    private void loadSpawns() {
        if (spawnLayer == null) return;

        MapObjects objects = spawnLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                MapProperties tileProperties = tiledMap.getTileSets().getTile((Integer) object.getProperties().get(GID))
                                                       .getProperties();
                Vector2 centerObjectPos = new Vector2();
                ((RectangleMapObject) object).getRectangle().getPosition(centerObjectPos);
                centerObjectPos.add(mapTileSize / 2f, mapTileSize / 2f);
                if (tileProperties.containsKey(SPAWN_LAYER_OBJECT_TYPE_NAME)) {
                    String typeName = tileProperties.get(SPAWN_LAYER_OBJECT_TYPE_NAME, String.class);
                    switch (typeName) {
                        case SPAWN_LAYER_OBJECT_TYPE_PLAYER_NAME:
                            if (playerSpawnPosition == null) {
                                playerSpawnPosition =
                                        new Vector2(centerObjectPos.x / mapTileSize, centerObjectPos.y / mapTileSize);
                                Gdx.app.log(LOG, "Set player spawn to " + playerSpawnPosition.x + "|" +
                                                 playerSpawnPosition.y);
                            }
                            break;
                        case SPAWN_LAYER_OBJECT_TYPE_MOB_NAME:
                            if (tileProperties.containsKey(SPAWN_LAYER_OBJECT_MOB_SUBTYPE_NAME)) {
                                String subtypeName = tileProperties.get(SPAWN_LAYER_OBJECT_MOB_SUBTYPE_NAME,
                                                                        String.class);
                                switch (subtypeName) {
                                    case SPAWN_LAYER_OBJECT_MOB_SUBTYPE_ROBOT_NAME:
                                        mobSpawnPositions.add(new SpawnArgs(centerObjectPos.x / mapTileSize,
                                                                            centerObjectPos.y / mapTileSize,
                                                                            SpawnArgs.SpawnType.MOB_ROBOT));
                                        break;
                                    case SPAWN_LAYER_OBJECT_MOB_SUBTYPE_BALL_NAME:
                                        mobSpawnPositions.add(new SpawnArgs(centerObjectPos.x / mapTileSize,
                                                                            centerObjectPos.y / mapTileSize,
                                                                            SpawnArgs.SpawnType.MOB_BALL));
                                        break;
                                }
                            }
                            break;
                        case SPAWN_LAYER_OBJECT_TYPE_STATIC_NAME:
                            if (tileProperties.containsKey(SPAWN_LAYER_OBJECT_STATIC_SUBTYPE_NAME)) {
                                String subtypeName = tileProperties.get(SPAWN_LAYER_OBJECT_STATIC_SUBTYPE_NAME,
                                                                        String.class);
                                SpawnArgs spawnArgs;
                                switch (subtypeName) {
                                    case SPAWN_LAYER_OBJECT_STATIC_LAMPROPE_NAME:
                                        spawnArgs = new SpawnArgs(centerObjectPos.x / mapTileSize,
                                                                  centerObjectPos.y / mapTileSize,
                                                                  SpawnArgs.SpawnType.STATIC_LAMPROPE);

                                        if (object.getProperties().containsKey(LampRope.AD_ARGS_HEIGHT_KEY)) {
                                            float height = Float.parseFloat(object.getProperties()
                                                                                  .get(LampRope.AD_ARGS_HEIGHT_KEY,
                                                                                       String.class));
                                            spawnArgs.addAdditionalArgs(LampRope.AD_ARGS_HEIGHT_KEY, height);
                                        }

                                        if (object.getProperties().containsKey(LampRope.AD_ARGS_PAN_KEY)) {
                                            float pan = Float.parseFloat(object.getProperties()
                                                                               .get(LampRope.AD_ARGS_PAN_KEY,
                                                                                    String.class));
                                            spawnArgs.addAdditionalArgs(LampRope.AD_ARGS_PAN_KEY, pan);
                                        }

                                        objectSpawnPositions.add(spawnArgs);
                                        break;
                                    case SPAWN_LAYER_OBJECT_STATIC_LAMPHORIZONTAL_NAME:
                                        spawnArgs = new SpawnArgs(centerObjectPos.x / mapTileSize,
                                                                  centerObjectPos.y / mapTileSize,
                                                                  SpawnArgs.SpawnType.STATIC_LAMPHORIZONTAL);

                                        if (object.getProperties().containsKey(LampHorizontal.AD_ARGS_COLOR_KEY)) {
                                            Color color = Color.valueOf(object.getProperties()
                                                                              .get(LampHorizontal.AD_ARGS_COLOR_KEY,
                                                                                   String.class));
                                            spawnArgs.addAdditionalArgs(LampHorizontal.AD_ARGS_COLOR_KEY, color);
                                        }

                                        objectSpawnPositions.add(spawnArgs);
                                        break;
                                }
                            }
                            break;
                        default:
                            Gdx.app.log(LOG, "Could not interpret spawn position of type " + typeName);
                            break;
                    }
                }
            }
        }
    }

    public String getErrorString() {
        if (foregroundLayer == null || backgroundLayer == null || collisionLayer == null || spawnLayer == null)
            return "Not all layers could be loaded.";
        if (mapTileSize == 0f) return "Maptilesize could not be loaded.";
        if (playerSpawnPosition == null) return "PlayerSpawnPosition could not be loaded.";

        return null;
    }

    public void addTilesAsBodiesToWorld(World world) {
        if (collisionLayer == null) return;

        for (int row = 0; row < collisionLayer.getHeight(); row++) {
            for (int col = 0; col < collisionLayer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    String material = tile.getProperties().get("material", String.class);
                    if (material != null) {
                        switch (material) {
                            case "metal":
                                String direction = tile.getProperties().get("direction", String.class);
                                AbstractEdgedBlock.EdgeDirection edgeDirection =
                                        AbstractEdgedBlock.EdgeDirection.getEdgeDirectionFromString(direction);
                                if (edgeDirection != null) {
                                    MetalBlock metalBlock = new MetalBlock(col, row, edgeDirection);
                                    metalBlock.addBodyToWorld(world);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public void updateCamera() {
        mapRenderer.setView(camera);
    }

    public void renderBackground() {
        mapRenderer.getSpriteBatch().setColor(Color.GRAY);
        mapRenderer.renderTileLayer(backgroundLayer);
        mapRenderer.getSpriteBatch().setColor(Color.WHITE);
    }

    public void renderForeground() {
        mapRenderer.getSpriteBatch().setColor(Color.LIGHT_GRAY);
        mapRenderer.renderTileLayer(foregroundLayer);
        mapRenderer.getSpriteBatch().setColor(Color.WHITE);
    }

    public void renderCollisionLayer() {
        mapRenderer.renderTileLayer(collisionLayer);
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }

    public List<SpawnArgs> getMobSpawnPositions() {
        return mobSpawnPositions;
    }

    public List<SpawnArgs> getObjectSpawnPositions() {
        return objectSpawnPositions;
    }
}
