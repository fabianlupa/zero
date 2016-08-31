package com.flaiker.zero.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.flaiker.zero.entities.AbstractMob;
import com.flaiker.zero.entities.Player;

/**
 * A contact callback that sets and removes a player instance attribute in {@link AbstractMob} for handling damage on
 * touch. This does not support multiple players!
 */
public class DamagePlayerOnTouchContactCallback implements AdvancedContactCallback {
    private final AbstractMob mob;

    public DamagePlayerOnTouchContactCallback(AbstractMob mob) {
        this.mob = mob;
    }

    @Override
    public void onContactStart(Body contactedBody) {
        if (contactedBody.getUserData() instanceof Player) {
            mob.setTouchingPlayer((Player) contactedBody.getUserData());
        }
    }

    @Override
    public void onContactStop(Body contactedBody) {
        if (contactedBody.getUserData() instanceof Player) {
            mob.clearTouchingPlayer();
        }
    }
}
