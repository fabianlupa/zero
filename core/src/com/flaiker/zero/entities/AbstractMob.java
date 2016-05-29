/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

/**
 * Base class for enemies / non player controlled {@link AbstractLivingEntity}s
 */
public abstract class AbstractMob extends AbstractLivingEntity {
    public AbstractMob(int maxHealth) {
        super(maxHealth);
    }
}
