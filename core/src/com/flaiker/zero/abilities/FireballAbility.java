/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.abilities;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.entities.AbstractLivingEntity;
import com.flaiker.zero.entities.FireballEntity;
import com.flaiker.zero.entities.Player;

/**
 * Ability to throw a fireball ({@link FireballEntity})
 */
public class FireballAbility extends AbstractCooldownAbility {
    private static final float COOLDOWN       = 1f;
    private static final float INITIAL_FORCE  = 5000f;
    private static final float SPAWN_DISTANCE = 1f;

    public FireballAbility(Skin skin, WorldBodyInjector wbi, Player player) {
        super("Fireball", "fireball", skin, COOLDOWN, wbi, player);
    }

    @Override
    public void use() {
        super.use();

        // Calculate the starting position
        float x = player.getViewDirection() == AbstractLivingEntity.Direction.LEFT
                  ? player.getBodyX() - SPAWN_DISTANCE
                  : player.getBodyX() + SPAWN_DISTANCE;
        float y = player.getBodyY();

        // Create fireball entity
        FireballEntity fireball = new FireballEntity();
        fireball.initializeSpawnPosition(x, y);
        fireball.init();

        // Apply the initial force
        fireball.addBodyCreatedCallback(c -> {
            float impulse = player.getViewDirection() == AbstractLivingEntity.Direction.LEFT
                            ? -INITIAL_FORCE : INITIAL_FORCE;
            c.applyForceToCenter(impulse, 0f, true);
        });

        // Add it to the world
        wbi.addBodyToWorld(fireball);
    }
}
