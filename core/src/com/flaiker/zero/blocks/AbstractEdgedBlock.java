/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.blocks;

import com.flaiker.zero.tiles.RegistrableBlock;

import java.util.Optional;

/**
 * A block that has different directions / edges.
 * <p>
 * Additional assets will be generated from the {@link com.flaiker.zero.assetpipeline.AssetBuilder} if {@link
 * RegistrableBlock} is present. Direction is expressed using {@link EdgeDirection}.
 */
public abstract class AbstractEdgedBlock extends AbstractBlock {
    private EdgeDirection direction;

    public AbstractEdgedBlock() {
        super();
    }

    @Override
    protected void customInit() throws IllegalStateException {
        if (this.direction == null) throw new IllegalStateException("Direction not initialized");
    }

    @Override
    protected String getAtlasPath() {
        return getAtlasPathName(getAnnotationMetadata().id(), getAnnotationMetadata().name(), direction);
    }

    /**
     * Initialize the direction of the block
     * @param direction Direction of the block
     */
    public void initDirection(EdgeDirection direction) {
        if (this.direction != null) throw new IllegalStateException("Direction already initialized");
        this.direction = direction;
    }

    /**
     * Internal helper method to retrieve the annotation information to get the correct atlas name
     * @return Block metadata
     */
    private RegistrableBlock getAnnotationMetadata() {
        // TODO: make this unnecessary
        return getClass().getAnnotation(RegistrableBlock.class);
    }

    private static String getAtlasPathName(int id, String material, EdgeDirection direction) {
        return String.format("%02d", id) + "-" + material + "-" + direction;
    }

    public enum EdgeDirection {
        /** Inner Bottom Left */
        IBL,
        /** Inner Bottom Right */
        IBR,
        /** Inner Top Left */
        ITL,
        /** Inner Top Right */
        ITR,
        /** Outer Bottom Left */
        OBL,
        /** Outer Bottom Right */
        OBR,
        /** Outer Top Left */
        OTL,
        /** Outer Bottom Right */
        OTR,
        /** None **/
        B,
        /** Outer Left **/
        OL,
        /** Outer Right **/
        OR,
        /** Outer Top **/
        OT,
        /** Outer Bottom **/
        OB;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static Optional<EdgeDirection> getEdgeDirectionFromString(String directionString) {
            return Optional.ofNullable(EdgeDirection.valueOf(directionString.toUpperCase()));
        }
    }
}
