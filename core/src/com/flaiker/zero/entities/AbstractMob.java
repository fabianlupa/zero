/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import com.flaiker.zero.abilities.AbstractAbility;

/**
 * Base class for enemies / non player controlled {@link AbstractLivingEntity}s
 */
public abstract class AbstractMob extends AbstractLivingEntity {
    protected AbstractAbility selectedAbility;

    public AbstractMob(int maxHealth) {
        super(maxHealth);
    }

    public void addAbility(AbstractAbility ability) {
        selectedAbility = ability;
    }


    @Override
    public void update(float delta) {
        super.update(delta);

        if (selectedAbility != null && selectedAbility.canUse()) selectedAbility.use();
    }
}
