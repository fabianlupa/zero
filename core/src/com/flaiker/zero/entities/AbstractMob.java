/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

/**
 * Created by Flaiker on 10.12.2014.
 */
public abstract class AbstractMob extends AbstractLivingEntity {
    protected       int healthPoints;
    protected final int maxHealthPoints;

    public AbstractMob(String atlasPath, float xPosMeter, float yPosMeter, int maxHealthPoints) {
        super(atlasPath, xPosMeter, yPosMeter);
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints;
    }
}
