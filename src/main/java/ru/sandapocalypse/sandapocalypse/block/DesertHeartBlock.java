package ru.sandapocalypse.sandapocalypse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DesertHeartBlock extends Block {
    public DesertHeartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.ASH,
                    pos.getX() + 0.2D + random.nextDouble() * 0.6D,
                    pos.getY() + 0.9D + random.nextDouble() * 0.4D,
                    pos.getZ() + 0.2D + random.nextDouble() * 0.6D,
                    0.0D, 0.015D, 0.0D);
        }
    }
}
