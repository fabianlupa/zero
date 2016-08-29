/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.abilities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.flaiker.zero.box2d.WorldBodyInjector;
import com.flaiker.zero.entities.AbstractLivingEntity;
import com.flaiker.zero.entities.Player;

/**
 * Base class for abilities that can be used by the player
 */
public abstract class AbstractAbility {
    protected final WorldBodyInjector wbi;
    protected final AbstractLivingEntity player;

    private String          name;

    public AbstractAbility(String name, WorldBodyInjector wbi, AbstractLivingEntity player) {
        this.name = name;
        this.wbi = wbi;
        this.player = player;
    }

    public String getName() {
        return name;
    }

    /**
     * Use the ability
     */
    public abstract void use();

    /**
     * @return Ability is ready to be used
     */
    public boolean canUse() {
        return true;
    }

    /**
     * Update ability's state
     * @param delta Time in milliseconds since last update
     */
    public void update(float delta) {
    }

    /**
     * Render the ability
     * @param batch Batch to use for rendering
     */
    public void render(Batch batch) {
    }
}
