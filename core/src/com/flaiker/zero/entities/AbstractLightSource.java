package com.flaiker.zero.entities;

import box2dLight.Light;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 26.12.2014.
 */
public abstract class AbstractLightSource extends AbstractEntity {
    private RayHandler rayHandler;
    private Light      light;

    private float bodyLightOffsetX     = 0f;
    private float bodyLightOffsetY     = 0f;
    private float bodyLightAngleOffset = 0f;

    public AbstractLightSource(RayHandler rayHandler, String atlasPath, float xPosMeter, float yPosMeter) {
        super(atlasPath, xPosMeter, yPosMeter);
        this.rayHandler = rayHandler;
    }

    protected void setPositionalLightOffsets(float bodyLightOffsetX, float bodyLightOffsetY, float bodyLightAngleOffset) {
        this.bodyLightOffsetX = bodyLightOffsetX;
        this.bodyLightOffsetY = bodyLightOffsetY;
        this.bodyLightAngleOffset = bodyLightAngleOffset;
    }

    @Override
    public void addBodyToWorld(World world) {
        super.addBodyToWorld(world);
        light = createLight(rayHandler);
        attachLightToBody(body, light);
    }

    protected abstract Light createLight(RayHandler rayHandler);

    private void attachLightToBody(Body body, Light light) {
        if (body != null && light != null) {
            if (light instanceof PositionalLight) {
                ((PositionalLight) light).attachToBody(body, bodyLightOffsetX, bodyLightOffsetY, bodyLightAngleOffset);
            } else light.attachToBody(body);
        } else throw new IllegalStateException("light or body have not been initialized, light could not be attached to body");
    }
}
