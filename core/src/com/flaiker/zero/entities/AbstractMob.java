package com.flaiker.zero.entities;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 10.12.2014.
 */
public abstract class AbstractMob extends AbstractEntity {
    protected       int healthPoints;
    protected final int maxHealthPoints;

    public AbstractMob(World world, String texturePath, float xPosMeter, float yPosMeter, int maxHealthPoints) {
        super(world, texturePath, xPosMeter, yPosMeter);
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints;
    }
}
