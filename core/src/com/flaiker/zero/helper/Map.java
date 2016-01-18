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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.blocks.AbstractEdgedBlock;
import com.flaiker.zero.tiles.TileRegistry;

import java.util.ArrayList;
import java.util.List;

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
    private List<SpawnArgs>            spawns;
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
        spawns = new ArrayList<>();
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
            if (object instanceof TiledMapTileMapObject) {
                MapProperties tileProperties = tiledMap.getTileSets().getTile((Integer) object.getProperties().get(GID))
                                                       .getProperties();
                Vector2 centerObjectPos = new Vector2();
                centerObjectPos.x = ((TiledMapTileMapObject) object).getX();
                centerObjectPos.y = ((TiledMapTileMapObject) object).getY();
                centerObjectPos.add(mapTileSize / 2f, mapTileSize / 2f);
                if (tileProperties.containsKey(SPAWN_LAYER_OBJECT_TYPE_NAME)) {
                    String typeName = tileProperties.get(SPAWN_LAYER_OBJECT_TYPE_NAME, String.class);
                    SpawnArgs args = new SpawnArgs(centerObjectPos.x / mapTileSize, centerObjectPos.y / mapTileSize,
                                                   typeName);
                    object.getProperties().getKeys()
                          .forEachRemaining(k -> args.addAdditionalArgs(k, object.getProperties()
                                                                                 .get(k).toString()));
                    spawns.add(args);
                }
            }
        }
    }

    public String getErrorString() {
        if (foregroundLayer == null || backgroundLayer == null || collisionLayer == null || spawnLayer == null)
            return "Not all layers could be loaded.";
        if (mapTileSize == 0f) return "Maptilesize could not be loaded.";

        return null;
    }

    public void addTilesAsBodiesToWorld(World world) {
        if (collisionLayer == null) return;

        for (int row = 0; row < collisionLayer.getHeight(); row++) {
            for (int col = 0; col < collisionLayer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    String id = tile.getProperties().get("id", String.class);
                    final int finalCol = col;
                    final int finalRow = row;
                    TileRegistry.getInstance().getBlockClassById(Integer.parseInt(id)).ifPresent(c -> {
                        try {
                            AbstractBlock block = c.newInstance();
                            block.initializeSpawnPosition(finalCol, finalRow);
                            if (block instanceof AbstractEdgedBlock) {
                                String direction = tile.getProperties().get("direction", String.class);
                                ((AbstractEdgedBlock) block).initDirection(
                                        AbstractEdgedBlock.EdgeDirection.getEdgeDirectionFromString(direction).get());
                            }
                            block.init();
                            block.addBodyToWorld(world);

                        } catch (InstantiationException | IllegalAccessException e) {
                            Gdx.app.log(LOG, "Could not load block/tile with id=" + id);
                        }
                    });
                }
            }
        }
    }

    public void updateCamera() {
        mapRenderer.setView(camera);
    }

    public void renderBackground() {
        mapRenderer.getBatch().setColor(Color.GRAY);
        mapRenderer.renderTileLayer(backgroundLayer);
        mapRenderer.getBatch().setColor(Color.WHITE);
    }

    public void renderForeground() {
        mapRenderer.getBatch().setColor(Color.LIGHT_GRAY);
        mapRenderer.renderTileLayer(foregroundLayer);
        mapRenderer.getBatch().setColor(Color.WHITE);
    }

    public void renderCollisionLayer() {
        mapRenderer.renderTileLayer(collisionLayer);
    }

    public List<SpawnArgs> getSpawns() {
        return spawns;
    }
}
