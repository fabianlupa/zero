package com.flaiker.zero.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Map {
    public static final String LOG = Map.class.getSimpleName();

    private static final String SPAWN_LAYER_NAME                    = "ojLayer";
    private static final String SPAWN_LAYER_OBJECT_TYPE_NAME        = "type";
    private static final String SPAWN_LAYER_OBJECT_TYPE_PLAYER_NAME = "player";

    private final float MAP_TILE_SIZE;

    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera         camera;
    private Vector2                    playerSpawnPosition;

    public Map(String fileName, OrthographicCamera camera) {
        this.camera = camera;
        loadMap(fileName);
        MAP_TILE_SIZE = ((TiledMapTileLayer)tiledMap.getLayers().get(0)).getTileWidth();
        loadSpawns();
    }

    private void loadSpawns() {
        MapLayer ojLayer = tiledMap.getLayers().get(SPAWN_LAYER_NAME);
        MapObjects objects = ojLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                MapProperties tileProperties = tiledMap.getTileSets().getTile((Integer) object.getProperties().get("gid")).getProperties();
                if (tileProperties.containsKey(SPAWN_LAYER_OBJECT_TYPE_NAME)) {
                    String typeName = tileProperties.get("type", String.class);
                    switch (typeName) {
                        case SPAWN_LAYER_OBJECT_TYPE_PLAYER_NAME:
                            if (playerSpawnPosition == null) {
                                playerSpawnPosition = new Vector2(((RectangleMapObject) object).getRectangle().getX() / MAP_TILE_SIZE,
                                                                  ((RectangleMapObject) object).getRectangle().getY() / MAP_TILE_SIZE);
                                Gdx.app.log(LOG, "Set player spawn to " + playerSpawnPosition.x + "|" + playerSpawnPosition.y);
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

    private void loadMap(String fileName) {
        tiledMap = new TmxMapLoader().load("maps/" + fileName);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public void addTilesAsBodiesToWorld(String layerName, World world) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layerName);
        float tileSize = layer.getTileWidth();

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    String material = tile.getProperties().get("material", String.class);
                    if (material != null) {
                        switch (material) {
                            case "metal":
                                String direction = tile.getProperties().get("direction", String.class);
                                AbstractEdgedBlock.EdgeDirection edgeDirection =
                                        AbstractEdgedBlock.EdgeDirection.getEdgeDirectionFromString(direction);
                                if (edgeDirection != null) new MetalBlock(world, col * tileSize, row * tileSize, edgeDirection);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public void render() {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }
}
