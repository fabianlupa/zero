package com.flaiker.zero.blocks;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 22.11.2014.
 */
public class WhiteBlock extends AbstractBlock {
    public WhiteBlock(World world, float xPos, float yPos) {
        super(world, "whiteBlock.png", xPos, yPos);
    }
}
