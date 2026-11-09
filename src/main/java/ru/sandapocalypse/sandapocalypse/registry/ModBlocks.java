package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.block.DesertHeartBlock;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SandApocalypse.MOD_ID);

    public static final RegistryObject<Block> DESERT_HEART = BLOCKS.register("desert_heart", () ->
            new DesertHeartBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 6)));

    private ModBlocks() {}
}
