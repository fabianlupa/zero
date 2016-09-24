package com.flaiker.zero.helper;

import box2dLight.Light;
import box2dLight.PositionalLight;
import com.badlogic.gdx.physics.box2d.Body;

public class LightHelper {
    public static void attachLightToBody(Body body, Light light, float offsetX, float offsetY, float offsetAngle) {
        if (body != null && light != null) {
            if (light instanceof PositionalLight) {
                ((PositionalLight) light).attachToBody(body, offsetX, offsetY, offsetAngle);
            } else light.attachToBody(body);
        } else throw new IllegalStateException("light or body have not been initialized, " +
                                               "light could not be attached to body");
    }
}
