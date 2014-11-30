package com.flaiker.zero.blocks;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 30.11.2014.
 */
public abstract class AbstractEdgedBlock extends AbstractBlock {
    public AbstractEdgedBlock(World world, String material, float xPos, float yPos, EdgeDirection edgeDirection) {
        super(world, getAtlasPathName(material, edgeDirection), xPos, yPos);
    }

    private static String getAtlasPathName(String material, EdgeDirection edgeDirection) {
        return material + "-" + edgeDirection.toString();
    }

    public enum EdgeDirection {
        LEFT, TOP, RIGHT, BOTTOM, TOPLEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, INNER_TOP_LEFT, INNER_TOP_RIGHT, INNER_BOTTOM_LEFT,
        INNER_BOTTOM_RIGHT, CENTER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static EdgeDirection getEdgeDirectionFromString(String directionString) {
            for (EdgeDirection edgeDirection : EdgeDirection.values()) {
                if(edgeDirection.name().equals(directionString)) return edgeDirection;
            }
            return null;
        }
    }
}
