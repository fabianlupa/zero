package com.flaiker.zero.helper;

/**
 * Created by Flaiker on 12.12.2014.
 */
public class SpawnArgs {
    private float     x;
    private float     y;
    private SpawnType spawnType;

    public SpawnArgs(float xPos, float yPos, SpawnType spawnType) {
        this.x = xPos;
        this.y = yPos;
        this.spawnType = spawnType;
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
        MOB_ROBOT, MOB_ZOMBIE, ITEM_HEALTH_BOOST
    }
}
