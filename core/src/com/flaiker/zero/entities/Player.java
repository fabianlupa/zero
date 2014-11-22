package com.flaiker.zero.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Player extends AbstractEntity implements InputProcessor{
    private static final float MAX_SPEED_X = 5f;
    private static final float MAX_SPEED_Y = 100f;
    private static final float ACCELERATION_X = 50f;

    private final Body box2dBody;

    private Direction requestedDirection;

    public Player(Body box2dBody, float xPos, float yPos) {
        super("player.png", xPos, yPos);
        this.box2dBody = box2dBody;
        this.requestedDirection = Direction.NONE;
    }

    private void move() {
        switch (requestedDirection) {
            case RIGHT:
                box2dBody.applyForceToCenter(ACCELERATION_X, 0f, true);
                if (box2dBody.getLinearVelocity().x > MAX_SPEED_X)
                    box2dBody.setLinearVelocity(MAX_SPEED_X, box2dBody.getLinearVelocity().y);
                break;
            case LEFT:
                box2dBody.applyForceToCenter(-ACCELERATION_X, 0f, true);
                if (box2dBody.getLinearVelocity().x < -MAX_SPEED_X)
                    box2dBody.setLinearVelocity(-MAX_SPEED_X, box2dBody.getLinearVelocity().y);
                break;
            case NONE:
                if (box2dBody.getLinearVelocity().x > 0) {
                    box2dBody.applyForceToCenter(-ACCELERATION_X, 0f, true);
                    if(box2dBody.getLinearVelocity().x < 0) box2dBody.setLinearVelocity(0, box2dBody.getLinearVelocity().y);
                } else if (box2dBody.getLinearVelocity().x < 0) {
                    box2dBody.applyForceToCenter(ACCELERATION_X, 0f, true);
                    if(box2dBody.getLinearVelocity().x > 0) box2dBody.setLinearVelocity(0, box2dBody.getLinearVelocity().y);
                }
                break;
        }
    }

    private void setRequestedDirection(Direction direction) {
        requestedDirection = direction;
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }

    @Override
    public void update() {
        super.update();
        move();
    }

    public static enum Direction {
        LEFT, RIGHT, NONE
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;

        // Bewegung des Spielers an Tasten binden
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
                box2dBody.applyForceToCenter(0f, 2000f, true);
                keyProcessed = true;
                break;
            case Input.Keys.R:
                box2dBody.setTransform(1f,2f,0f);
                box2dBody.setLinearVelocity(0f, 0f);
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;

        // Bewegung des Spielers an Tasten binden
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                setRequestedDirection(Direction.NONE);
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
