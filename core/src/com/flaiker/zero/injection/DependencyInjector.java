/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.injection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to manage and inject dependencies
 * <p/>
 * This dependency injector implementation only supports managing a single instance for each dependency type.
 *
 * @see CanInject
 * @see InjectDependency
 */
public class DependencyInjector {
    private Map<String, Object> managedDependencies;

    public DependencyInjector() {
        managedDependencies = new HashMap<>();
    }

    /**
     * @param objects Dependency instances to be managed
     */
    public DependencyInjector(Object... objects) {
        this();
        addDependencies(objects);
    }

    /**
     * Add an object to be managed as a dependency by this injector
     *
     * @param dependency The object to add
     * @throws IllegalArgumentException Object is already managed
     */
    public void addDependency(Object dependency) {
        String key = dependency.getClass().getName();

        if (managedDependencies.containsKey(key))
            throw new IllegalArgumentException("Dependency type already managed");

        managedDependencies.put(dependency.getClass().getName(), dependency);
    }

    /**
     * Add multiple objects to be managed as dependencies by this injector
     *
     * @param objects Objects to be managed
     * @throws IllegalArgumentException At least one object is already managed
     */
    public void addDependencies(Object... objects) {
        for (Object object : objects) addDependency(object);
    }

    /**
     * Remove an object to not be managed anymore by this injector
     *
     * @param dependency The object ot remove
     * @throws IllegalArgumentException Object is not managed
     */
    public void removeDependency(Object dependency) {
        String key = dependency.getClass().getName();
        Object dep = managedDependencies.get(key);

        if (dep == null || dep != dependency)
            throw new IllegalArgumentException("Dependency not managed");

        managedDependencies.remove(key);
    }

    /**
     * Clear internal list of managed dependencies
     */
    public void clearDependencies() {
        managedDependencies.clear();
    }

    /**
     * Inject dependencies into the object if the class is annotated as relevant for this process
     *
     * @param object The object to inject dependencies into
     * @throws InjectionFailedException Injection failed
     */
    public void injectDependenciesIfNecessary(Object object) throws InjectionFailedException {
        if (object.getClass().isAnnotationPresent(CanInject.class)) injectDependency(object);
    }

    /**
     * Inject all requested dependencies into annotated methods of the object
     *
     * @param object The object to inject dependencies into
     * @throws InjectionFailedException Injection failed
     */
    public void injectDependency(Object object) throws InjectionFailedException {
        // Find methods that have the correct annotation
        for (Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(InjectDependency.class)) {
                // For each of them build a list of objects that should be injected
                List<Object> toBeInjectedList = new ArrayList<>();
                for (Class<?> clazz : method.getAnnotation(InjectDependency.class).value()) {
                    Object dep = managedDependencies.get(clazz.getName());
                    if (dep != null) toBeInjectedList.add(dep);
                    else {
                        throw new InjectionFailedException("Could not inject dependency of type\"" +
                                clazz.getName() + "\" as it is not managed.");
                    }
                }
                // Try to invoke the method
                try {
                    method.invoke(object, toBeInjectedList.toArray());
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    throw new InjectionFailedException("Failed to inject dependency.", e);
                }
            }
        }
    }
}
