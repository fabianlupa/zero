/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flaiker.zero.Zero;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        new LwjglApplication(new Zero(), config);
    }
}
