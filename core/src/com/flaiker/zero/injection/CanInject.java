/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.injection;

import java.lang.annotation.*;

/**
 * Marks a type for being relevant for dependency injection using {@link InjectDependency} and
 * {@link DependencyInjector}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CanInject {
}
