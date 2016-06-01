/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.injection;

import java.lang.annotation.*;

/**
 * Marks a method to be used for dependency injection with the types specified in {@link #value()}
 *
 * @see DependencyInjector
 * @see CanInject
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface InjectDependency {
    /**
     * @return Types of dependencies to be injected into the method (in order)
     */
    Class<?>[] value();
}
