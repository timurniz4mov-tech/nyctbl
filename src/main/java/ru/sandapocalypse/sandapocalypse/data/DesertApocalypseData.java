package ru.sandapocalypse.sandapocalypse.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** World-persistent state for the single apocalypse source and its spreading sand front. */
public class DesertApocalypseData extends SavedData {
    private static final String DATA_NAME = "sandapocalypse_world";
    private static final int MAX_FRONTIER = 16000;

    private boolean started;
    private long corePosition = 0L;
    private long stormUntil = 0L;
    private long nextStorm = 0L;
    private final List<Long> frontier = new ArrayList<>();
    private final Set<Long> knownPositions = new HashSet<>();

    public static DesertApocalypseData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DesertApocalypseData::load,
                DesertApocalypseData::new,
                DATA_NAME
        );
    }

    public static DesertApocalypseData load(CompoundTag tag) {
        DesertApocalypseData data = new DesertApocalypseData();
        data.started = tag.getBoolean("started");
        data.corePosition = tag.getLong("core");
        data.stormUntil = tag.getLong("stormUntil");
        data.nextStorm = tag.getLong("nextStorm");
        long[] savedFrontier = tag.getLongArray("frontier");
        for (long packed : savedFrontier) {
            if (data.knownPositions.add(packed)) {
                data.frontier.add(packed);
            }
        }
        while (data.frontier.size() > MAX_FRONTIER) {
            Long removed = data.frontier.remove(0);
            data.knownPositions.remove(removed);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("started", started);
        tag.putLong("core", corePosition);
        tag.putLong("stormUntil", stormUntil);
        tag.putLong("nextStorm", nextStorm);
        long[] positions = new long[frontier.size()];
        for (int i = 0; i < frontier.size(); i++) {
            positions[i] = frontier.get(i);
        }
        tag.putLongArray("frontier", positions);
        return tag;
    }

    public boolean hasStarted() {
        return started;
    }

    public BlockPos getCorePosition() {
        return BlockPos.of(corePosition);
    }

    public void start(BlockPos core, BlockPos firstSandBlock) {
        started = true;
        corePosition = core.asLong();
        addFrontier(firstSandBlock);
        setDirty();
    }

    public long getStormUntil() { return stormUntil; }
    public long getNextStorm() { return nextStorm; }
    public void setStorm(long until, long next) { stormUntil = until; nextStorm = next; setDirty(); }

    public int frontierSize() {
        return frontier.size();
    }

    public BlockPos randomFrontier(net.minecraft.util.RandomSource random) {
        if (frontier.isEmpty()) {
            return null;
        }
        return BlockPos.of(frontier.get(random.nextInt(frontier.size())));
    }

    public void addFrontier(BlockPos pos) {
        long packed = pos.asLong();
        if (!knownPositions.add(packed)) {
            return;
        }
        frontier.add(packed);
        if (frontier.size() > MAX_FRONTIER) {
            Long removed = frontier.remove(0);
            knownPositions.remove(removed);
        }
        setDirty();
    }
}
