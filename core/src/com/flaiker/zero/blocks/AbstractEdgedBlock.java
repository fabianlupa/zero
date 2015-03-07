/******************************************************************************
 * Copyright 2015 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.blocks;

/**
 * Created by Flaiker on 30.11.2014.
 */
public abstract class AbstractEdgedBlock extends AbstractBlock {
    public AbstractEdgedBlock(String material, float xPosMeter, float yPosMeter, EdgeDirection edgeDirection) {
        super(getAtlasPathName(material, edgeDirection), xPosMeter, yPosMeter);
    }

    private static String getAtlasPathName(String material, EdgeDirection edgeDirection) {
        return material + "-" + edgeDirection.toString();
    }

    public enum EdgeDirection {
        LEFT, TOP, RIGHT, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, INNER_TOP_LEFT, INNER_TOP_RIGHT, INNER_BOTTOM_LEFT,
        INNER_BOTTOM_RIGHT, CENTER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static EdgeDirection getEdgeDirectionFromString(String directionString) {
            for (EdgeDirection edgeDirection : EdgeDirection.values()) {
                if (edgeDirection.toString().equals(directionString)) return edgeDirection;
            }
            return null;
        }
    }
}
