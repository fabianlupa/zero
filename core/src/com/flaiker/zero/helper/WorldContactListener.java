/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getUserData() instanceof ContactCallback) {
            ((ContactCallback) fa.getUserData()).onContactStart();
        }

        if (fb.getUserData() instanceof ContactCallback) {
            ((ContactCallback) fb.getUserData()).onContactStart();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getUserData() instanceof ContactCallback) {
            ((ContactCallback) fa.getUserData()).onContactStop();
        }

        if (fb.getUserData() instanceof ContactCallback) {
            ((ContactCallback) fb.getUserData()).onContactStop();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
