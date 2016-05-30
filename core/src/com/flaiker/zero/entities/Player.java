/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.flaiker.zero.abilities.AbstractAbility;
import com.flaiker.zero.helper.AnimationManager;
import com.flaiker.zero.box2d.ContactCallback;
import com.flaiker.zero.screens.GameScreen;
import com.flaiker.zero.services.ConsoleManager;
import com.flaiker.zero.tiles.RegistrableSpawn;

import java.util.ArrayList;
import java.util.List;

/**
 * The player of the game
 */
@RegistrableSpawn(type = "player")
public class Player extends AbstractLivingEntity implements InputProcessor, ConsoleManager.CommandableInstance {
    public static final String LOG = Player.class.getSimpleName();

    private static final float  MAX_SPEED_X       = 4f;
    private static final float  ACCELERATION_X    = 150;
    private static final float  MAX_SPEED_Y       = 100f;
    private static final float  ACCELERATION_JUMP = 2000f;
    private static final int    MAX_HEALTH        = 5;
    private static final String ATLAS_PATH        = "player";

    private static final float DENSITY     = 1f;
    private static final float FRICTION    = 1f;
    private static final float RESTITUTION = 0f;

    private int              numFootContacts;
    private boolean          canJump;
    private AnimationManager animationManager;
    private AbstractAbility  selectedAbility;
    private boolean          noGravOn;
    private boolean          noClipOn;
    private boolean          flyOn;

    public Player() {
        super(MAX_HEALTH);
        selectedAbility = null;
    }

    @Override
    protected String getAtlasPath() {
        return ATLAS_PATH;
    }

    @Override
    protected void customInit() throws IllegalStateException {
        animationManager = new AnimationManager(sprite);
        animationManager.setMaximumAddedIdleTime(2f);
        animationManager.setMinimumIdleTime(5f);
        animationManager.registerAnimation("player", "walk", AbstractEntity.ENTITY_TEXTURE_ATLAS, 1 / 8f);
        animationManager.registerIdleAnimation("player", "idle", AbstractEntity.ENTITY_TEXTURE_ATLAS, 1 / 4f);
    }

    public boolean isPlayerOnGround() {
        return numFootContacts > 0;
    }

    public boolean getCanJump() {
        return canJump;
    }

    public void switchSelectedAbility(AbstractAbility ability) {
        selectedAbility = ability;
        Gdx.app.log(LOG, "Selected ability changed to: " + selectedAbility.getName());
    }

    @Override
    protected Body createBody(World world) {
        // create the player
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(getSpriteX(), getSpriteY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        Body playerBody = world.createBody(bdef);
        playerBody.setUserData(this);

        shape.setAsBox(getEntityWidth() / 2f, getEntityHeight() / 2f);
        fdef.shape = shape;
        fdef.friction = FRICTION;
        fdef.density = DENSITY;
        fdef.restitution = RESTITUTION;
        playerBody.createFixture(fdef).setUserData("player");
        shape.setAsBox(0.35f, 0.1f, new Vector2(0, -sprite.getHeight() / GameScreen.PIXEL_PER_METER / 2f + 0.1f), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        playerBody.createFixture(fdef).setUserData(new ContactCallback() {
            @Override
            public void onContactStart() {
                numFootContacts++;
                canJump = true;
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
    public void update(float delta) {
        super.update(delta);
        animationManager.updateSprite();
        animationManager.updateAnimationFrameDuration("walk", 0.3f / Math.abs(body.getLinearVelocity().x));
    }

    @Override
    protected void setRequestedDirection(Direction direction) {
        super.setRequestedDirection(direction);
        switch (direction) {
            case LEFT:
                animationManager.runAnimation("walk", AnimationManager.AnimationDirection.LEFT);
                break;
            case RIGHT:
                animationManager.runAnimation("walk", AnimationManager.AnimationDirection.RIGHT);
                break;
            case NONE:
                animationManager.stopAnimation();
                break;
        }

    }

    @Override
    protected void move() {
        // Override all move logic if fly cheat is enabled
        if (!flyOn) super.move();
        else {
            Vector2 newPosition = body.getPosition();

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newPosition.x += 10f / GameScreen.PIXEL_PER_METER;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) newPosition.x -= 10f / GameScreen.PIXEL_PER_METER;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) newPosition.y += 10f / GameScreen.PIXEL_PER_METER;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) newPosition.y -= 10f / GameScreen.PIXEL_PER_METER;

            body.setTransform(newPosition, 0f);
        }
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
                if (getCanJump()) {
                    body.setLinearVelocity(body.getLinearVelocity().x, 0);
                    body.applyForceToCenter(0f, ACCELERATION_JUMP, true);
                    canJump = false;
                }
                keyProcessed = true;
                break;
            case Input.Keys.R:
                body.setTransform(3f, 6f, 0f);
                body.setLinearVelocity(0f, 0f);
                keyProcessed = true;
                break;
            case Input.Keys.F:
                if (selectedAbility.canUse()) selectedAbility.use();
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
                if ((keycode == Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                    setRequestedDirection(Direction.RIGHT);
                if ((keycode == Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.LEFT))
                    setRequestedDirection(Direction.LEFT);
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

    @Override
    public List<ConsoleManager.ConsoleCommand> getConsoleCommands() {
        List<ConsoleManager.ConsoleCommand> outList = new ArrayList<>();
        outList.add(new ConsoleManager.ConsoleCommand("sethealth", parValuePairs -> {
            if (parValuePairs.containsKey("val")) {
                int newCurrentHealth;
                try {
                    newCurrentHealth = Integer.parseInt(parValuePairs.get("val"));
                } catch (NumberFormatException e) {
                    Gdx.app.log(LOG, "val is not an int");
                    return;
                }
                currentHealth = newCurrentHealth;
            }
        }));
        outList.add(new ConsoleManager.ConsoleCommand("setmaxhealth", parValuePairs -> {
            if (parValuePairs.containsKey("val")) {
                int newMaxHealth;
                try {
                    newMaxHealth = Integer.parseInt(parValuePairs.get("val"));
                } catch (NumberFormatException e) {
                    Gdx.app.log(LOG, "val is not an int");
                    return;
                }
                maxHealth = newMaxHealth;
            }
        }));
        outList.add(new ConsoleManager.ConsoleCommand("nograv", parValuePairs1 -> {
            if (noGravOn) {
                noGravOn = false;
                body.setGravityScale(1);
            } else {
                noGravOn = true;
                body.setGravityScale(0);
                body.setLinearVelocity(0f, 0f);
            }
        }));
        outList.add(new ConsoleManager.ConsoleCommand("noclip", parValuePairs -> {
            if (noClipOn) {
                noClipOn = false;
                body.getFixtureList().forEach(f -> f.setSensor(false));
            } else {
                noClipOn = true;
                body.getFixtureList().forEach(f -> f.setSensor(true));
            }
        }));
        outList.add(new ConsoleManager.ConsoleCommand("fly", parValuePairs -> {
            flyOn = !flyOn;
        }));

        return outList;
    }
}
