/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.flaiker.zero.abilities.AbstractAbility;

/**
 * Base class for enemies / non player controlled {@link AbstractLivingEntity}s
 */
public abstract class AbstractMob extends AbstractLivingEntity {
    public static final float COLLISION_ATTACK_COOLDOWN = 1.5f;
    public static final int   COLLISION_ATTACK_DAMAGE = 1;

    protected AbstractAbility selectedAbility;
    protected float           collisionAttackCooldown;
    protected Player          touchingPlayer;

    public AbstractMob(int maxHealth) {
        super(maxHealth);
    }

    public void addAbility(AbstractAbility ability) {
        selectedAbility = ability;
    }

    public void setTouchingPlayer(Player player) {
        touchingPlayer = player;
    }

    public void clearTouchingPlayer() {
        touchingPlayer = null;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (touchingPlayer != null && collisionAttackCooldown == 0f) {
            touchingPlayer.receiveDamage(COLLISION_ATTACK_DAMAGE);
            collisionAttackCooldown = COLLISION_ATTACK_COOLDOWN;
        }

        if (collisionAttackCooldown > 0) collisionAttackCooldown -= delta;
        if (collisionAttackCooldown < 0) collisionAttackCooldown = 0f;

        if (selectedAbility != null && selectedAbility.canUse()) selectedAbility.use();
    }
}
