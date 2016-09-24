/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.tiles;

import com.flaiker.zero.blocks.AbstractBlock;
import com.flaiker.zero.entities.AbstractEntity;
import com.flaiker.zero.entities.AbstractSpawnableEntity;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton based registry class for managing block and entity types based on {@link RegistrableSpawn} and
 * {@link RegistrableBlock} annotations as well as {@link AbstractBlock} and {@link AbstractEntity} classes
 * respectively.
 * <p>
 * Classes in the packages {@link #BLOCK_PACKAGE} and {@link #ENTITY_PACKAGE} are scanned if they
 * are subclasses of the base classes and are annotated to then be added to an internal map. Blocks and entity types
 * can be found and instantiated using helper methods.
 */
public class TileRegistry {
    private static final String BLOCK_PACKAGE  = "com.flaiker.zero.blocks";
    private static final String ENTITY_PACKAGE = "com.flaiker.zero.entities";
    private static TileRegistry instance;

    private final Map<Integer, BlockWrapper>                            blocks;
    private final Map<String, Class<? extends AbstractSpawnableEntity>> spawns;

    private TileRegistry() {
        blocks = new HashMap<>();
        spawns = new HashMap<>();

        registerBlocks();
        registerSpawns();
    }

    private void registerBlocks() {
        blocks.clear();

        // Use reflection to find all blocks in the specified package
        Reflections reflections = new Reflections(BLOCK_PACKAGE);
        Set<Class<? extends AbstractBlock>> abstractBlocks = reflections.getSubTypesOf(AbstractBlock.class);

        abstractBlocks.stream().filter(f -> f.isAnnotationPresent(RegistrableBlock.class)).forEach(c -> {
            RegistrableBlock annotation = c.getAnnotation(RegistrableBlock.class);
            blocks.put(annotation.id(), BlockWrapper.factory(c, annotation));
        });
    }

    private void registerSpawns() {
        spawns.clear();

        // Use reflection to find all spawns in the specified package
        Reflections reflections = new Reflections(ENTITY_PACKAGE);
        Set<Class<? extends AbstractSpawnableEntity>> abstractEntities
                = reflections.getSubTypesOf(AbstractSpawnableEntity.class);

        abstractEntities.stream().filter(f -> f.isAnnotationPresent(RegistrableSpawn.class)).forEach(c -> {
            RegistrableSpawn annotation = c.getAnnotation(RegistrableSpawn.class);
            spawns.put(annotation.type(), c);
        });
    }

    /**
     * Get a block type by its id
     *
     * @param id Id of the block
     * @return Optional of possibly found block type
     */
    public Optional<Class<? extends AbstractBlock>> getBlockClassById(int id) {
        return Optional.ofNullable(blocks.get(id)).map(b -> b.blockClass);
    }

    /**
     * Get an entity type by name
     *
     * @param type Entity type name
     * @return Optional of possibly found entity type
     */
    public Optional<Class<? extends AbstractSpawnableEntity>> getEntityClassByType(String type) {
        return Optional.ofNullable(spawns.get(type));
    }

    /**
     * Get a block wrapper instance by its block id
     *
     * @param id Id of the block
     * @return Optional of possibly found block wrapper
     */
    public Optional<BlockWrapper> getBlockWrapperById(int id) {
        return Optional.ofNullable(blocks.get(id));
    }

    /**
     * @return Set of all registered block types
     */
    public Set<Class<? extends AbstractBlock>> getAllBlockClasses() {
        return blocks.entrySet().stream().map(b -> b.getValue().blockClass).collect(Collectors.toSet());
    }

    /**
     * @return Set of all registered entity types
     */
    public Set<Class<? extends AbstractEntity>> getAllEntityClasses() {
        return spawns.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    /**
     * @return Map of all block wrappers (mapped by block id)
     */
    public Map<Integer, BlockWrapper> getBlockWrapperMap() {
        return new HashMap<>(blocks);
    }

    /**
     * @return Map of all entity classes (mapped by entity name)
     */
    public Map<String, Class<? extends AbstractEntity>> getEntityClassesMap() {
        return new HashMap<>(spawns);
    }

    /**
     * @return Set of all block wrappers
     */
    public Set<BlockWrapper> getBlockWrappers() {
        return new HashSet<>(blocks.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet()));
    }

    /**
     * @return Singleton instance of the TileRegistry
     */
    public static TileRegistry getInstance() {
        if (instance == null) initialize();

        return instance;
    }

    /**
     * Initializes the TileRegistry singleton ({@link #getInstance()}
     */
    public static void initialize() {
        if (instance == null) instance = new TileRegistry();
    }
}
