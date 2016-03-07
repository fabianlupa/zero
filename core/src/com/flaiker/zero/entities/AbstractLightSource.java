/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.entities;

import box2dLight.Light;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.flaiker.zero.box2d.LightSourceInjectorInterface;

/**
 * Base class for entities that use Box2DLights
 */
public abstract class AbstractLightSource extends AbstractEntity implements LightSourceInjectorInterface {
    private RayHandler rayHandler;
    private Light      light;

    private float bodyLightOffsetX     = 0f;
    private float bodyLightOffsetY     = 0f;
    private float bodyLightAngleOffset = 0f;

    public AbstractLightSource() {
        super();
    }

    @Override
    public void initializeRayHandler(RayHandler rayHandler) {
        if (this.rayHandler != null) throw new IllegalStateException("RayHandler already initialized");
        this.rayHandler = rayHandler;
    }

    @Override
    protected void customInit() throws IllegalStateException {
        if (rayHandler == null) throw new IllegalStateException("RayHandler not initialized");
    }

    protected void setPositionalLightOffsets(float bodyLightOffsetX, float bodyLightOffsetY,
                                             float bodyLightAngleOffset) {
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
        } else throw new IllegalStateException("light or body have not been initialized, " +
                                               "light could not be attached to body");
    }
}
