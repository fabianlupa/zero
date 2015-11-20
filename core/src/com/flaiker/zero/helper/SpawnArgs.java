/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

import java.util.HashMap;

/**
 * Created by Flaiker on 12.12.2014.
 */
public class SpawnArgs {
    private float                   x;
    private float                   y;
    private SpawnType               spawnType;
    private HashMap<String, Object> adArgs;

    public SpawnArgs(float xPos, float yPos, SpawnType spawnType) {
        this.x = xPos;
        this.y = yPos;
        this.spawnType = spawnType;
        adArgs = new HashMap<>();
    }

    public void addAdditionalArgs(String key, Object value) {
        adArgs.put(key, value);
    }

    public HashMap<String, Object> getAdArgs() {
        return adArgs;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public SpawnType getSpawnType() {
        return spawnType;
    }

    public enum SpawnType {
        MOB_ROBOT, MOB_BALL, MOB_ZOMBIE, ITEM_HEALTH_BOOST, STATIC_LAMPROPE, STATIC_LAMPHORIZONTAL
    }
}
