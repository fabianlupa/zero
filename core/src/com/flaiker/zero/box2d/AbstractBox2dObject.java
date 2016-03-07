/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.box2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.screens.GameScreen;

/**
 * Base class for game objects that can be rendered using a {@link Sprite} and can be added to a Box2D {@link World} for
 * physics simulation.
 * <p>
 * Initialization happens using type introspection / reflection using {@link com.flaiker.zero.tiles.TileRegistry} as an
 * entry point. Therefore the constructor of a concrete non abstract class must have no arguments. Initialization of
 * necessary state for the instance is done in {@link #init()} and {@link #customInit()} respectively.
 */
public abstract class AbstractBox2dObject {
    protected     Sprite       sprite;
    protected     Body         body;
    protected     Vector2      spawnVector;
    private final TextureAtlas textureAtlas;
    private       boolean      initialized;

    public AbstractBox2dObject(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    /**
     * Initialize the object with required state for render and update
     *
     * @throws IllegalStateException
     */
    public final void init() throws IllegalStateException {
        if (spawnVector == null)
            throw new IllegalStateException("AbstractBox2dObject failed initialization");

        sprite = textureAtlas.createSprite(getAtlasPath());
        sprite.setPosition(spawnVector.x * GameScreen.PIXEL_PER_METER, spawnVector.y * GameScreen.PIXEL_PER_METER);

        customInit();

        initialized = true;
    }

    /**
     * Initialization logic applied after {@link #init()}
     * <p>
     * Must throw {@link IllegalStateException} if object does not have the required state for initialization
     * @throws IllegalStateException
     */
    protected void customInit() throws IllegalStateException {
    }

    public void initializeSpawnPosition(float xPosMeter, float yPosMeter) {
        if (spawnVector != null) throw new IllegalStateException("SpawnPosition already initialized");

        spawnVector = new Vector2(xPosMeter, yPosMeter);
    }

    /**
     * @return Path of the image to be used in the sprite contained in the specified texture atlas
     */
    protected abstract String getAtlasPath();

    public void addBodyToWorld(World world) {
        if (!initialized) throw new IllegalStateException("Could not add body to world, not initialized");
        if (body == null) body = createBody(world);
        else throw new IllegalStateException("Body has already been added to world");
    }

    /**
     * Gets the body of the object, must be implemented in concrete class
     * @param world World to add the body to
     * @return Body of the object
     */
    protected abstract Body createBody(World world);

    /**
     * Render the sprite of the object
     * @param batch Batch to draw the sprite on
     */
    public void render(Batch batch) {
        if (body == null) throw new IllegalStateException("Cannot render uninitialized body");
        sprite.draw(batch);
    }

    /**
     * Update the object using game logic
     */
    public void update() {
        if (body == null) throw new IllegalStateException("Cannot update uninitialized body");
    }

    public float getSpriteX() {
        return sprite.getX() / GameScreen.PIXEL_PER_METER;
    }

    public float getSpriteY() {
        return sprite.getY() / GameScreen.PIXEL_PER_METER;
    }

    public float getEntityWidth() {
        return sprite.getWidth() / GameScreen.PIXEL_PER_METER;
    }

    public float getEntityHeight() {
        return sprite.getHeight() / GameScreen.PIXEL_PER_METER;
    }
}
