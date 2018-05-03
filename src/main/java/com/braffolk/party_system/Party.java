package com.braffolk.party_system;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Tuple;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Party {
    private LinkedHashMap<String, EntityPlayerMP> players;
    private Tuple<String, EntityPlayerMP> owner;

    public void create(EntityPlayerMP owner){
        this.owner = new Tuple<>(owner.getName(), owner);
        this.players = new LinkedHashMap<>();
        this.players.put(owner.getName(), owner);
    }
}
