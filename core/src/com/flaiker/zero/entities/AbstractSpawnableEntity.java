/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import java.util.Map;

/**
 * An entity that may be spawned using spawn positions in a map
 */
public abstract class AbstractSpawnableEntity extends AbstractEntity {
    /**
     * Apply additional arguments from the map object
     * <p>
     * When loading a spawn additional arguments specified in {@link com.flaiker.zero.tiles.RegistrableSpawn#adArgs()}
     * will be injected using this method.
     *
     * @param adArgPairs Map of key value pairs
     */
    public void applyAdArgs(Map<String, String> adArgPairs) {
    }

    /**
     * Helper method to retrieve values from a addArgs map
     *
     * @param adArgsPairs  Map containing the keys and values
     * @param key          Key to be searched
     * @param defaultValue Value to be used if the key is not found or value is null
     * @return Chosen value
     */
    protected String getAdArgsValueOrDefault(Map<String, String> adArgsPairs, String key, String defaultValue) {
        if (!adArgsPairs.containsKey(key)) return defaultValue;
        if (adArgsPairs.get(key) == null) return defaultValue;
        return adArgsPairs.get(key);
    }
}
