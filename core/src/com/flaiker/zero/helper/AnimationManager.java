package com.flaiker.zero.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Flaiker on 14.12.2014.
 */
public class AnimationManager {
    public static final String LOG = AnimationManager.class.getSimpleName();

    private HashMap<String, Animation> animationList;
    private Sprite                     sprite;
    private Animation                  currentAnimation;
    private TextureRegion              unanimatedRegion;
    private float                      elapsedTime;
    private AnimationDirection         animationDirection;

    public AnimationManager(Sprite sprite) {
        animationList = new HashMap<>();
        this.sprite = sprite;
        this.currentAnimation = null;
        this.unanimatedRegion = new TextureRegion(sprite.getTexture(), sprite.getU(), sprite.getV(), sprite.getU2(), sprite.getV2());
        this.animationDirection = AnimationDirection.RIGHT;
        this.elapsedTime = 0;
    }

    public void registerAnimation(String key, Animation animation) {
        animationList.put(key, animation);
    }

    public void registerAnimation(String key, TextureAtlas atlas, float frameDuration) {
        SortedMap<Integer, TextureRegion> list = new TreeMap<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            String[] name = region.name.split("_");
            if (name.length == 2 && name[0].equals("player") && name[1].startsWith("walk")) {
                String animationName[] = name[1].split("-");
                if (animationName.length == 2) {
                    try {
                        int frame = Integer.parseInt(animationName[1]);
                        list.put(frame, region);
                    } catch (NumberFormatException e) {
                        Gdx.app.log(LOG, "Frame could not be parsed");
                    }
                }
            }
        }
        animationList.put(key, new Animation(frameDuration, list.values().toArray(new TextureRegion[list.size()])));
    }

    public void updateAnimationFrameDuration(String key, float newDuration) {
        Animation foundAnimation = animationList.get(key);
        if (foundAnimation != null) {
            foundAnimation.setFrameDuration(newDuration);
        }
    }

    public void runAnimation(String key) {
        runAnimation(key, AnimationDirection.RIGHT);
    }

    public void runAnimation(String key, AnimationDirection direction) {
        Animation foundAnimation = animationList.get(key);

        if (foundAnimation != null) {
            if (currentAnimation != foundAnimation) elapsedTime = 0;
            currentAnimation = foundAnimation;
            animationDirection = direction;
        }
    }

    public void updateSprite() {
        if (currentAnimation != null) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            sprite.setRegion(currentAnimation.getKeyFrame(elapsedTime, true));
        } else {
            sprite.setRegion(unanimatedRegion);
        }
        if (animationDirection == AnimationDirection.LEFT) sprite.setFlip(true, false);
        else if (animationDirection == AnimationDirection.RIGHT) sprite.setFlip(false, false);
    }

    public void stopAnimation() {
        currentAnimation = null;
        sprite.setFlip(false, false);
    }

    public enum AnimationDirection {
        LEFT, RIGHT
    }
}
