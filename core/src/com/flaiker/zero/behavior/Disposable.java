/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.behavior;

/**
 * Interface for describing objects that can be disposed
 */
public interface Disposable {
    /**
     * Dispose all allocated ressources
     */
    void dispose();
}
