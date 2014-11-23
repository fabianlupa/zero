package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.blocks.WhiteBlock;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Map {
    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera         camera;

    public Map(String fileName, OrthographicCamera camera) {
        this.camera = camera;
        loadMap(fileName);
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
                if (cell != null && cell.getTile() != null) new WhiteBlock(world, col * tileSize, row * tileSize);
            }
        }
    }

    public void render() {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
}
