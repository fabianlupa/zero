package com.flaiker.zero.blocks;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Flaiker on 23.11.2014.
 */
public class MetalBlock extends AbstractEdgedBlock {
    public MetalBlock(World world, float xPosMeter, float yPosMeter, EdgeDirection edgeDirection) {
        super(world, "metal", xPosMeter, yPosMeter, edgeDirection);
    }
}
