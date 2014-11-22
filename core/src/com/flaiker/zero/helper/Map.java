package com.flaiker.zero.helper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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

    public void addTilesAsBodiesToWorld(String layerName, World world, int pixelPerMeter) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layerName);
        float tileSize = layer.getTileWidth();

        // make Box2d objects/bodies out of the tiles
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                // get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if (cell != null && cell.getTile() != null) {
                    // create a body + fixture from cell
                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set((col + 0.5f) * tileSize / pixelPerMeter, (row + 0.5f) * tileSize / pixelPerMeter);

                    ChainShape cs = new ChainShape();
                    Vector2[] v = new Vector2[4];
                    v[0] = new Vector2(-tileSize / 2 / pixelPerMeter, -tileSize / 2 / pixelPerMeter);
                    v[1] = new Vector2(-tileSize / 2 / pixelPerMeter, tileSize / 2 / pixelPerMeter);
                    v[2] = new Vector2(tileSize / 2 / pixelPerMeter, tileSize / 2 / pixelPerMeter);
                    v[3] = new Vector2(tileSize / 2 / pixelPerMeter, -tileSize / 2 / pixelPerMeter);
                    cs.createLoop(v);
                    //fdef.friction = 0.5f;
                    fdef.shape = cs;
                    fdef.isSensor = false;
                    Body tileBody = world.createBody(bdef);
                    tileBody.setUserData(new WhiteBlock(col * pixelPerMeter, row * pixelPerMeter));
                    tileBody.createFixture(fdef);
                }
            }
        }
    }

    public void render() {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
}
