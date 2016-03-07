/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Spawn arguments for entities when loading them from a map
 */
public class SpawnArgs {
    private float               x;
    private float               y;
    private String              type;
    private Map<String, String> adArgs;

    public SpawnArgs(float xPos, float yPos, String type) {
        this.x = xPos;
        this.y = yPos;
        this.type = type;
        adArgs = new HashMap<>();
    }

    public void addAdditionalArgs(String key, String value) {
        adArgs.put(key, value);
    }

    public Map<String, String> getAdArgs() {
        return adArgs;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getType() {
        return type;
    }
}
