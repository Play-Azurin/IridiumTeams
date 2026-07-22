package com.iridium.iridiumteams.listeners;

import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.SettingType;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
import com.iridium.iridiumteams.database.TeamSetting;
import lombok.AllArgsConstructor;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Golem;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@AllArgsConstructor
public class EntityChangeBlockListener<T extends Team, U extends IridiumUser<T>> implements Listener {
    private final IridiumTeams<T, U> iridiumTeams;

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        // 1. Élettelen, mechanikai entitások átengedése (játékos, leeső homok, nyilak, csónakok, armor stand)
        if (entity instanceof Player || 
            entity instanceof FallingBlock || 
            entity instanceof Projectile || 
            entity instanceof Vehicle ||
            entity instanceof ArmorStand) {
            return;
        }

        // 2. Békés mobok és Gólemek átengedése (Sniffer, Falusi, Méh, Hóember, Vasgólem, Rézgólem stb.)
        // Figyelem: A Bukkit logikátlan módon a Shulkert is "Golem"-nek veszi, ezért őt muszáj kizárni!
        if (entity instanceof Animals || 
            entity instanceof NPC || 
            entity instanceof WaterMob || 
            entity instanceof Ambient || 
            (entity instanceof Golem && !(entity instanceof Shulker))) {
            return; // Engedjük, hogy a barátságosak tegyék a dolgukat!
        }

        // --- INNENTŐL JÖN A BÜNTETÉS ---
        // Aki ide eljutott, az csakis kártékony szörny lehet (Enderman, Ravager, Wither, Sárkány).
        // Letiltjuk a blokkmódosításukat, ha az "Entity Grief" ki van lőve!
        
        iridiumTeams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation()).ifPresent(team -> {
            TeamSetting teamSetting = iridiumTeams.getTeamManager().getTeamSetting(team, SettingType.ENTITY_GRIEF.getSettingKey());
            if (teamSetting == null) return;
            if (teamSetting.getValue().equalsIgnoreCase("Disabled")) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        // Ezt zombik csinálják, szóval a tiltás marad az eredeti formában.
        iridiumTeams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation()).ifPresent(team -> {
            TeamSetting teamSetting = iridiumTeams.getTeamManager().getTeamSetting(team, SettingType.ENTITY_GRIEF.getSettingKey());
            if (teamSetting == null) return;
            if (teamSetting.getValue().equalsIgnoreCase("Disabled")) {
                event.setCancelled(true);
            }
        });
    }
}