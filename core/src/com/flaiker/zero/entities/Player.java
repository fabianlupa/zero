package com.flaiker.zero.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Player extends AbstractEntity implements InputProcessor, ContactListener{
    private static final float MAX_SPEED_X = 5f;
    private static final float MAX_SPEED_Y = 100f;
    private static final float ACCELERATION_X = 50f;
    private static final float ACCELERATION_JUMP = 2000f;

    private final Body box2dBody;

    private Direction requestedDirection;
    private int       numFootContacts;

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

    public boolean isPlayerOnGround() { return numFootContacts > 0; }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }

    @Override
    public void update() {
        super.update();
        move();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts++;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts--;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

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
                if(isPlayerOnGround()) box2dBody.applyForceToCenter(0f, ACCELERATION_JUMP, true);
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

        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                // Aufhören zu bewegen, wenn Taste losgelassen
                if (!(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)))
                    setRequestedDirection(Direction.NONE);
                if ((keycode == Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) setRequestedDirection(Direction.RIGHT);
                if ((keycode == Input.Keys.RIGHT)  && Gdx.input.isKeyPressed(Input.Keys.LEFT)) setRequestedDirection(Direction.LEFT);
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
