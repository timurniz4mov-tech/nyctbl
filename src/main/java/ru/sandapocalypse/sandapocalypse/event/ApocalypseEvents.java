package ru.sandapocalypse.sandapocalypse.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import ru.sandapocalypse.sandapocalypse.registry.ModEntities;
import ru.sandapocalypse.sandapocalypse.registry.ModItems;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.data.DesertApocalypseData;
import ru.sandapocalypse.sandapocalypse.network.NetworkHandler;
import ru.sandapocalypse.sandapocalypse.registry.ModBlocks;

@Mod.EventBusSubscriber(modid = SandApocalypse.MOD_ID)
public final class ApocalypseEvents {
    private static final String THIRST_KEY = "SandApocalypseThirst";
    private static final String THIRST_TIMER_KEY = "SandApocalypseThirstTimer";
    private static final String LAST_SYNC_KEY = "SandApocalypseLastThirstSync";

    private ApocalypseEvents() {}

    @SubscribeEvent
    public static void onPlayerAdded(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        // Force an initial HUD sync on every login, even when the saved thirst value is unchanged.
        player.getPersistentData().putInt(LAST_SYNC_KEY, -1);
        if (level.dimension() != Level.OVERWORLD) return;

        DesertApocalypseData data = DesertApocalypseData.get(level);
        if (!data.hasStarted()) {
            trySpawnDesertHeart(level, player, data);
        }
    }

    private static void trySpawnDesertHeart(ServerLevel level, ServerPlayer player, DesertApocalypseData data) {
        RandomSource random = level.getRandom();
        BlockPos playerPos = player.blockPosition();

        for (int attempt = 0; attempt < 240; attempt++) {
            int distance = 10 + random.nextInt(91);
            double angle = random.nextDouble() * Math.PI * 2.0D;
            int x = playerPos.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) Math.round(Math.sin(angle) * distance);
            double actualDistanceSqr = (double) (x - playerPos.getX()) * (x - playerPos.getX())
                    + (double) (z - playerPos.getZ()) * (z - playerPos.getZ());
            if (actualDistanceSqr < 100.0D || actualDistanceSqr > 10000.0D) continue;
            BlockPos chunkProbe = new BlockPos(x, level.getMinBuildHeight(), z);
            if (!level.hasChunkAt(chunkProbe)) continue;

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (surfaceY <= level.getMinBuildHeight() + 1 || surfaceY >= level.getMaxBuildHeight() - 2) continue;

            BlockPos groundPos = new BlockPos(x, surfaceY - 1, z);
            BlockPos heartPos = groundPos.above();
            BlockState ground = level.getBlockState(groundPos);
            if (!isNaturalGround(ground)) continue;
            if (!level.isEmptyBlock(heartPos) || !level.getFluidState(heartPos).isEmpty()) continue;

            level.setBlock(groundPos, Blocks.SAND.defaultBlockState(), 3);
            level.setBlock(heartPos, ModBlocks.DESERT_HEART.get().defaultBlockState(), 3);
            data.start(heartPos, groundPos);
            player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.sandapocalypse.apocalypse_started"));
            level.sendParticles(ParticleTypes.CLOUD,
                    heartPos.getX() + 0.5D, heartPos.getY() + 1.1D, heartPos.getZ() + 0.5D,
                    24, 0.8D, 0.5D, 0.8D, 0.025D);
            return;
        }

        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.sandapocalypse.core_spawn_failed"));
    }

    private static boolean isNaturalGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM) || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.MUD) || state.is(Blocks.CLAY)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.STONE) || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE) || state.is(Blocks.GRANITE) || state.is(Blocks.TUFF)
                || state.is(Blocks.CALCITE) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD || level.getGameTime() % 20L != 0L) return;

        DesertApocalypseData data = DesertApocalypseData.get(level);
        if (!data.hasStarted() || data.frontierSize() == 0) return;
        if (level.getGameTime() % 600L == 0L) spawnDesertCreatures(level);

        long dayTime = level.getDayTime();
        updateSandstorm(level, data, dayTime);
        int dayNumber = (int) Math.max(0L, dayTime / 24000L);
        int attempts = Math.min(8, 1 + dayNumber);
        boolean daytime = dayTime % 24000L < 12000L;
        if (daytime) attempts *= 2;

        RandomSource random = level.getRandom();
        for (int i = 0; i < attempts; i++) {
            spreadOneBlock(level, data, random);
        }
    }

    private static void spawnDesertCreatures(ServerLevel level) {
        RandomSource random = level.getRandom();
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            int x = player.blockPosition().getX() + random.nextInt(25) - 12;
            int z = player.blockPosition().getZ() + random.nextInt(25) - 12;
            if (!level.hasChunkAt(new BlockPos(x, level.getMinBuildHeight(), z))) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getBlockState(pos.below()).is(Blocks.SAND) || !level.isEmptyBlock(pos)) continue;
            EntityType<?> type = random.nextInt(9) == 0 ? ModEntities.COLOSSUS.get() : ModEntities.DUNE_STALKER.get();
            Mob mob = (Mob) type.create(level);
            if (mob != null) { mob.moveTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360F, 0F); level.addFreshEntity(mob); }
        }
        if (random.nextInt(5) == 0) {
            ServerPlayer player = level.players().isEmpty() ? null : level.players().get(random.nextInt(level.players().size()));
            if (player != null) {
                int x = player.blockPosition().getX() + random.nextInt(31) - 15;
                int z = player.blockPosition().getZ() + random.nextInt(31) - 15;
                if (level.hasChunkAt(new BlockPos(x, level.getMinBuildHeight(), z))) {
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                    if (level.getBlockState(new BlockPos(x, y - 1, z)).is(Blocks.SAND)) {
                        var trader = ModEntities.DESERT_MASTER.get().create(level);
                        if (trader != null) { trader.moveTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360F, 0F); level.addFreshEntity(trader); }
                    }
                }
            }
        }
    }

    private static void spreadOneBlock(ServerLevel level, DesertApocalypseData data, RandomSource random) {
        BlockPos source = null;
        for (int sourceTry = 0; sourceTry < 24; sourceTry++) {
            BlockPos candidate = data.randomFrontier(random);
            if (candidate != null && level.hasChunkAt(candidate)) {
                source = candidate;
                break;
            }
        }
        if (source == null) return;

        for (int retry = 0; retry < 12; retry++) {
            int dx = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            if (dx == 0 && dz == 0) continue;

            int x = source.getX() + dx;
            int z = source.getZ() + dz;
            if (!level.hasChunkAt(new BlockPos(x, level.getMinBuildHeight(), z))) continue;

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (surfaceY <= level.getMinBuildHeight()) continue;
            BlockPos target = new BlockPos(x, surfaceY - 1, z);
            BlockState state = level.getBlockState(target);
            if (!isNaturalConvertible(state)) continue;

            level.setBlock(target, Blocks.SAND.defaultBlockState(), 3);
            data.addFrontier(target);
            if (random.nextInt(8) == 0) {
                level.sendParticles(ParticleTypes.CLOUD,
                        x + 0.5D, target.getY() + 1.0D, z + 0.5D,
                        3, 0.25D, 0.12D, 0.25D, 0.01D);
            }
            return;
        }
    }

    private static boolean isNaturalConvertible(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM) || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.MUD) || state.is(Blocks.CLAY)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.STONE) || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE) || state.is(Blocks.GRANITE) || state.is(Blocks.TUFF)
                || state.is(Blocks.CALCITE) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (player.level().getGameTime() % 20L != 0L) return;
        updateThirst(player);
    }

    private static void updateThirst(ServerPlayer player) {
        if (player.getAbilities().instabuild || player.isSpectator()) {
            syncIfChanged(player, 100);
            return;
        }

        var tag = player.getPersistentData();
        if (!tag.contains(THIRST_KEY)) tag.putInt(THIRST_KEY, 100);
        int thirst = Mth.clamp(tag.getInt(THIRST_KEY), 0, 100);

        int intervalSeconds = 30;
        if (player.getInventory().contains(new ItemStack(ModItems.DESERT_CHARM.get()))) intervalSeconds += 15;
        if (player.getInventory().contains(new ItemStack(ModItems.SAND_WARD.get()))) intervalSeconds += 5;
        int armorPieces = 0;
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.DESERT_HELMET.get())) armorPieces++;
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.DESERT_CHESTPLATE.get())) armorPieces++;
        if (player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.DESERT_LEGGINGS.get())) armorPieces++;
        if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.DESERT_BOOTS.get())) armorPieces++;
        intervalSeconds += armorPieces * 6;
        long timeOfDay = player.level().getDayTime() % 24000L;
        if (timeOfDay >= 0L && timeOfDay < 12000L) intervalSeconds -= 5;
        float temperature = player.level().getBiome(player.blockPosition()).value().getBaseTemperature();
        if (temperature >= 1.0F) intervalSeconds -= 7;
        boolean stormExposure = false;
        if (player.level() instanceof ServerLevel stormLevel && DesertApocalypseData.get(stormLevel).getStormUntil() > stormLevel.getGameTime() && isNearInfectedSand(player, stormLevel)) {
            stormExposure = true;
            intervalSeconds = Math.max(4, intervalSeconds / 3);
            if (!player.getInventory().contains(new ItemStack(ModItems.STORM_GOGGLES.get()))) intervalSeconds = Math.max(3, intervalSeconds - 3);
        }
        if (player.isSprinting()) intervalSeconds -= 5;
        if (player.isInWaterRainOrBubble()) intervalSeconds += 8;
        intervalSeconds = Math.max(stormExposure ? 3 : 10, intervalSeconds);

        int timer = tag.getInt(THIRST_TIMER_KEY) + 20;
        while (timer >= intervalSeconds * 20 && thirst > 0) {
            timer -= intervalSeconds * 20;
            thirst--;
        }
        tag.putInt(THIRST_TIMER_KEY, timer);
        tag.putInt(THIRST_KEY, thirst);

        if (thirst <= 20) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 0, true, false));
        }
        if (thirst <= 0 && player.level().getGameTime() % 100L == 0L) {
            player.hurt(player.damageSources().dryOut(), 1.0F);
        }

        syncIfChanged(player, thirst);
    }

    private static void syncIfChanged(ServerPlayer player, int thirst) {
        var tag = player.getPersistentData();
        if (!tag.contains(LAST_SYNC_KEY) || tag.getInt(LAST_SYNC_KEY) != thirst) {
            tag.putInt(LAST_SYNC_KEY, thirst);
            NetworkHandler.syncThirst(player, thirst);
        }
    }


    private static void updateSandstorm(ServerLevel level, DesertApocalypseData data, long dayTime) {
        long now = level.getGameTime();
        long until = data.getStormUntil();
        long next = data.getNextStorm();
        if (next == 0L) { next = now + 24000L * (2 + level.getRandom().nextInt(3)); data.setStorm(until, next); }
        if (until <= now && now >= next) {
            // A storm lasts between 45 and 90 seconds and returns every 2-4 Minecraft days.
            until = now + 900 + level.getRandom().nextInt(901);
            next = until + 24000L * (2 + level.getRandom().nextInt(3));
            data.setStorm(until, next);
            for (ServerPlayer player : level.players()) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6Началась песчаная буря! Жажда усиливается — ищи укрытие."));
        }
        if (until > now) {
            for (ServerPlayer player : level.players()) {
                if (isNearInfectedSand(player, level) && !player.getInventory().contains(new ItemStack(ModItems.STORM_GOGGLES.get()))) {
                    level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1.0D, player.getZ(), 8, 2.5D, 1.0D, 2.5D, 0.04D);
                }
            }
            if (now % 20L == 0L && until - now < 20L) {
                for (ServerPlayer player : level.players()) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eПесчаная буря стихает."));
            }
        }
    }

    private static boolean isNearInfectedSand(ServerPlayer player, ServerLevel level) {
        BlockPos center = player.blockPosition();
        for (int dx = -5; dx <= 5; dx += 2) for (int dz = -5; dz <= 5; dz += 2) {
            BlockPos pos = new BlockPos(center.getX() + dx, center.getY() - 1, center.getZ() + dz);
            if (level.hasChunkAt(pos) && level.getBlockState(pos).is(Blocks.SAND)) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void protectFromDesertMobs(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getInventory().contains(new ItemStack(ModItems.SAND_WARD.get()))) {
            var source = event.getSource().getEntity();
            if (source instanceof net.minecraft.world.entity.monster.Monster) event.setAmount(event.getAmount() * 0.70F);
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.DESERT_CHESTPLATE.get())) event.setAmount(event.getAmount() * 0.92F);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        int originalThirst = event.getOriginal().getPersistentData().contains(THIRST_KEY)
                ? event.getOriginal().getPersistentData().getInt(THIRST_KEY) : 100;
        event.getEntity().getPersistentData().putInt(THIRST_KEY, Mth.clamp(originalThirst, 0, 100));
        event.getEntity().getPersistentData().putInt(THIRST_TIMER_KEY,
                event.getOriginal().getPersistentData().getInt(THIRST_TIMER_KEY));
        event.getEntity().getPersistentData().putInt(LAST_SYNC_KEY, -1);
    }
}
