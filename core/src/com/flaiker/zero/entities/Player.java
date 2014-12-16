package com.flaiker.zero.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.helper.AnimationManager;
import com.flaiker.zero.helper.ContactCallback;
import com.flaiker.zero.screens.GameScreen;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Player extends AbstractEntity implements InputProcessor {
    private static final float MAX_SPEED_X       = 7f;
    private static final float ACCELERATION_X    = 150;
    private static final float MAX_SPEED_Y       = 100f;
    private static final float ACCELERATION_JUMP = 2000f;

    private int              numFootContacts;
    private AnimationManager animationManager;

    public Player(World world, float xPos, float yPos) {
        super(world, "player", xPos, yPos);
        animationManager = new AnimationManager(sprite);
        animationManager.registerAnimation("player", "walk" , AbstractEntity.getEntityTextureAtlas(), 1 / 8f);
    }

    public boolean isPlayerOnGround() { return numFootContacts > 0; }

    @Override
    protected Body createBody(World world) {
        // create the player
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body playerBody = world.createBody(bdef);
        playerBody.setUserData(this);

        shape.setAsBox(getEntityWidth() / 2f, getEntityHeight() / 2f);
        fdef.shape = shape;
        //fdef.friction = 0.5f;
        fdef.density = 0f;
        playerBody.createFixture(fdef).setUserData("player");
        shape.setAsBox(0.2f, 0.1f, new Vector2(0, -sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f + 0.1f), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        playerBody.createFixture(fdef).setUserData(new ContactCallback() {
            @Override
            public void onContactStart() {
                numFootContacts++;
            }

            @Override
            public void onContactStop() {
                numFootContacts--;
            }
        });

        return playerBody;
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }

    @Override
    public void update() {
        super.update();
        if (getRequestedDirection() == Direction.NONE) {
            animationManager.stopAnimation();
        } else {
            if (getRequestedDirection() == Direction.LEFT) animationManager.runAnimation("walk", AnimationManager.AnimationDirection.LEFT);
            else if (getRequestedDirection() == Direction.RIGHT)
                animationManager.runAnimation("walk", AnimationManager.AnimationDirection.RIGHT);
        }
        animationManager.updateSprite();
        animationManager.updateAnimationFrameDuration("walk", 1f / Math.abs(body.getLinearVelocity().x));
    }

    @Override
    protected float getMaxSpeedX() {
        return MAX_SPEED_X;
    }

    @Override
    protected float getAccelerationX() {
        return ACCELERATION_X;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;

        switch (keycode) {
            case Input.Keys.LEFT:
                setRequestedDirection(Direction.LEFT);
                keyProcessed = true;
                break;
            case Input.Keys.RIGHT:
                setRequestedDirection(Direction.RIGHT);
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                if (isPlayerOnGround()) body.applyForceToCenter(0f, ACCELERATION_JUMP, true);
                keyProcessed = true;
                break;
            case Input.Keys.R:
                body.setTransform(3f, 6f, 0f);
                body.setLinearVelocity(0f, 0f);
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;

        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                // Stop moving when no key is pressed
                if (!(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)))
                    setRequestedDirection(Direction.NONE);
                // Move in other direction if two keys were pressed
                if ((keycode == Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) setRequestedDirection(Direction.RIGHT);
                if ((keycode == Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.LEFT)) setRequestedDirection(Direction.LEFT);
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
}
