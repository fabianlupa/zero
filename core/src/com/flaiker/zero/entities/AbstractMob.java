/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

/**
 * Base class for enemies / non player controlled {@link AbstractLivingEntity}s
 */
public abstract class AbstractMob extends AbstractLivingEntity {
    protected       int healthPoints;
    protected final int maxHealthPoints;

    public AbstractMob(int maxHealthPoints) {
        super();
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints;
    }
}
