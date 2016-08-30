/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero;

import com.badlogic.gdx.Gdx;

/**
 * Class for managing the activation state of cheats
 * <p/>
 * Cheats can be enabled/disabled using either the methods this class provides or by using the utility methods on each
 * enum constant. It is recommended to import the enum statically for more readability.
 * <code>
 * import static com.flaiker.zero.CheatManager.Cheat.*;
 * </code>
 */
public class CheatManager {
    public static String LOG = CheatManager.class.getSimpleName();

    public static boolean isCheatEnabled(Cheat cheat) {
        return cheat.isEnabled();
    }

    public static void enableCheat(Cheat cheat) {
        cheat.enable();
    }

    public static void disableCheat(Cheat cheat) {
        cheat.disable();
    }

    public static void switchCheatState(Cheat cheat) {
        cheat.switchState();
    }

    public static void setEnabled(Cheat cheat, boolean on) {
        cheat.set(on);
    }

    public enum Cheat {
        FLY,
        NO_CLIPPING,
        NO_GRAVITATION,
        NO_COOLDOWN;

        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void enable() {
            enabled = true;
            Gdx.app.log(LOG, String.format("Enabled cheat \"%s\"", this.toString()));
        }

        public void disable() {
            enabled = false;
            Gdx.app.log(LOG, String.format("Disabled cheat \"%s\"", this.toString()));
        }

        public void switchState() {
            enabled = !enabled;
            Gdx.app.log(LOG, String.format("Switched cheat \"%s\" to \"%s\"", this.toString(), enabled));
        }

        public void set(boolean on) {
            enabled = on;
            Gdx.app.log(LOG, String.format("Set cheat \"%s\" to \"%s\"", this.toString(), enabled));
        }
    }
}
