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
import com.flaiker.zero.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for game objects that can be rendered using a {@link Sprite} and can be added to a Box2D {@link World} for
 * physics simulation.
 * <p>
 * Initialization happens using type introspection / reflection using {@link com.flaiker.zero.tiles.TileRegistry} as an
 * entry point. Therefore the constructor of a concrete non abstract class must have no arguments. Initialization of
 * necessary state for the instance is done in {@link #init()} and {@link #customInit()} respectively.
 */
public abstract class AbstractBox2dObject {
    private final TextureAtlas       textureAtlas;

    protected Sprite  sprite;
    protected Body    body;
    protected Vector2 spawnVector;
    private   boolean initialized;
    private   boolean markedForDeletion;
    private   boolean disposed;

    private List<BodyCreatedCallback> callbacks;

    public AbstractBox2dObject(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        callbacks = new ArrayList<>();
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
        sprite.setPosition(spawnVector.x * Game.PIXEL_PER_METER, spawnVector.y * Game.PIXEL_PER_METER);

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
        if (body == null) {
            body = createBody(world);
            for (BodyCreatedCallback callback : callbacks) callback.onBodyCreated(body);
        }
        else throw new IllegalStateException("Body has already been added to world");
    }

    /**
     * Gets the body of the object, must be implemented in concrete class
     * @param world World to add the body to
     * @return Body of the object
     */
    protected abstract Body createBody(World world);

    /**
     * Add a callback for after the body has been created
     *
     * @param callback The callback to be added
     * @throws IllegalArgumentException Callback has already been added
     */
    public void addBodyCreatedCallback(BodyCreatedCallback callback) throws IllegalArgumentException {
        if (callbacks.contains(callback)) throw new IllegalArgumentException("Callback already added");

        callbacks.add(callback);
    }

    /**
     * Render the sprite of the object
     * @param batch Batch to draw the sprite on
     */
    public void render(Batch batch) {
        if (body == null) throw new IllegalStateException("Cannot render uninitialized body");

        if (!markedForDeletion) sprite.draw(batch);
    }

    /**
     * Update the object using game logic
     *
     * @param delta Time since last update
     */
    public void update(float delta) {
        if (body == null) throw new IllegalStateException("Cannot update uninitialized body");
    }

    /**
     *  Mark the object to be removed from the world
     *
     * @throws IllegalStateException Object has already been disposed
     */
    public void dispose() {
        if (disposed) throw new IllegalStateException("Object has already been disposed");

        disposed = true;
    }

    /**
     * Mark this object to be disposed in the next update loop iteration
     *
     * @throws IllegalStateException Object has already been marked for deletion
     */
    public void markForDeletion() {
        if (markedForDeletion) throw new IllegalStateException("Object is already marked for deletion");

        markedForDeletion = true;
    }

    public void setPositionX(float x) {
        body.getPosition().set(x, body.getPosition().y);
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public float getBodyX() {
        return body.getPosition().x;
    }

    public float getBodyY() {
        return body.getPosition().y;
    }

    public float getBodyXCenter() {
        return body.getPosition().x + body.getLocalCenter().x;
    }

    public float getBodyYCenter() {
        return body.getPosition().y + body.getLocalCenter().y;
    }

    public Vector2 getBodyPosition() {
        return body.getPosition();
    }

    public float getSpriteX() {
        return sprite.getX() / Game.PIXEL_PER_METER;
    }

    public float getSpriteY() {
        return sprite.getY() / Game.PIXEL_PER_METER;
    }

    public float getEntityWidth() {
        return sprite.getWidth() / Game.PIXEL_PER_METER;
    }

    public float getEntityHeight() {
        return sprite.getHeight() / Game.PIXEL_PER_METER;
    }

    /**
     * Callback interface for after {@link #addBodyToWorld(World)} has been called
     */
    public interface BodyCreatedCallback {
        /**
         * Body has been created
         * @param body The body that has been added to the world
         */
        void onBodyCreated(Body body);
    }
}
