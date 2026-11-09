package ru.sandapocalypse.sandapocalypse.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Colossus extends Zombie {
    public Colossus(EntityType<? extends Zombie> type, Level level) { super(type, level); }
}
