package com.flaiker.zero.blocks;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 23.11.2014.
 */
public class MetalBlock extends AbstractBlock {
    public MetalBlock(World world, Direction direction, float xPos, float yPos) {
        super(world, directionToTextureConverter(direction), xPos, yPos);
    }

    private static String directionToTextureConverter(Direction direction) {
        String base = "metalBlock";
        String type = ".png";
        switch (direction) {
            case BOTTOM:
                return base + "Bottom" + type;
            case LEFT:
                return base + "Left" + type;
            case MIDDLE:
                return base + "Middle" + type;
            case RIGHT:
                return base + "Right" + type;
            case TOP:
                return base + "Top" + type;
            case BOTTOMLEFT:
                return base + "BottomLeft" + type;
            case BOTTOMRIGHT:
                return base + "BottomRight" + type;
            case TOPLEFT:
                return base + "TopLeft" + type;
            case TOPRIGHT:
                return base + "TopRight" + type;
            default: return null;
        }
    }

    public enum Direction {
        BOTTOM, LEFT, MIDDLE, RIGHT, TOP, BOTTOMLEFT, BOTTOMRIGHT, TOPLEFT, TOPRIGHT
    }
}
