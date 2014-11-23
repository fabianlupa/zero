package com.flaiker.zero.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class Player extends AbstractEntity implements InputProcessor, ContactListener{
    private static final float MAX_SPEED_X = 5f;
    private static final float MAX_SPEED_Y = 100f;
    private static final float ACCELERATION_X = 50f;
    private static final float ACCELERATION_JUMP = 2000f;

    private Direction requestedDirection;
    private int       numFootContacts;

    public Player(World world, float xPos, float yPos) {
        super(world, "player.png", xPos, yPos);
        this.requestedDirection = Direction.NONE;
    }

    private void move() {
        switch (requestedDirection) {
            case RIGHT:
                body.applyForceToCenter(ACCELERATION_X, 0f, true);
                if (body.getLinearVelocity().x > MAX_SPEED_X)
                    body.setLinearVelocity(MAX_SPEED_X, body.getLinearVelocity().y);
                break;
            case LEFT:
                body.applyForceToCenter(-ACCELERATION_X, 0f, true);
                if (body.getLinearVelocity().x < -MAX_SPEED_X)
                    body.setLinearVelocity(-MAX_SPEED_X, body.getLinearVelocity().y);
                break;
            case NONE:
                if (body.getLinearVelocity().x > 0) {
                    body.applyForceToCenter(-ACCELERATION_X, 0f, true);
                    if(body.getLinearVelocity().x < 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                } else if (body.getLinearVelocity().x < 0) {
                    body.applyForceToCenter(ACCELERATION_X, 0f, true);
                    if(body.getLinearVelocity().x > 0) body.setLinearVelocity(0, body.getLinearVelocity().y);
                }
                break;
        }
    }

    private void setRequestedDirection(Direction direction) {
        requestedDirection = direction;
    }

    public boolean isPlayerOnGround() { return numFootContacts > 0; }

    @Override
    protected Body createBody(World world) {
        // create the player
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(0.5f, 6);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body playerBody = world.createBody(bdef);
        playerBody.setUserData(this);

        shape.setAsBox(0.5f, 0.5f);
        fdef.shape = shape;
        //fdef.friction = 0.5f;
        fdef.density = 0f;
        playerBody.createFixture(fdef).setUserData("player");
        shape.setAsBox(0.2f, 0.1f, new Vector2(0, -0.4f), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        playerBody.createFixture(fdef).setUserData("foot");
        world.setContactListener(this);

        return playerBody;
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
                if(isPlayerOnGround()) body.applyForceToCenter(0f, ACCELERATION_JUMP, true);
                keyProcessed = true;
                break;
            case Input.Keys.R:
                body.setTransform(1f,2f,0f);
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
