package com.iridium.iridiumteams.listeners;

import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.SettingType;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
import com.iridium.iridiumteams.database.TeamSetting;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

@AllArgsConstructor
public class BlockIgniteListener<T extends Team, U extends IridiumUser<T>> implements Listener {
    private final IridiumTeams<T, U> iridiumTeams;

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD || 
            event.getCause() == BlockIgniteEvent.IgniteCause.LAVA) {

            iridiumTeams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation()).ifPresent(team -> {
                TeamSetting teamSetting = iridiumTeams.getTeamManager().getTeamSetting(team, SettingType.FIRE_SPREAD.getSettingKey());
                if (teamSetting == null) return;
                
                if (teamSetting.getValue().equalsIgnoreCase("Disabled")) {
                    event.setCancelled(true);
                }
            });
        }
    }
}