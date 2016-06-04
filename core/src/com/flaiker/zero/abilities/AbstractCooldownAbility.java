/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.abilities;

import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.entities.Player;

/**
 * Base class for abilities that have a cooldown on their {@link #canUse()}
 */
public abstract class AbstractCooldownAbility extends AbstractAbility {
    private final float cooldown;
    private       float currentCooldown;

    public AbstractCooldownAbility(String name, float cooldown,
                                   WorldBodyInjector wbi, Player player) {
        super(name, wbi, player);

        this.cooldown = cooldown;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public float getCooldownPercentage() {
        return cooldown / currentCooldown;
    }

    @Override
    public void use() {
        currentCooldown = cooldown;
    }

    @Override
    public boolean canUse() {
        return currentCooldown == 0;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (currentCooldown != 0) {
            currentCooldown -= delta;
        }

        if (currentCooldown < 0) currentCooldown = 0;
    }
}
