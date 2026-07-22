package com.iridium.iridiumteams.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.Reward;
import com.iridium.iridiumteams.api.TeamLevelUpEvent;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
import com.iridium.iridiumteams.database.TeamReward;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TeamLevelUpListener<T extends Team, U extends IridiumUser<T>> implements Listener {
    private final IridiumTeams<T, U> iridiumTeams;

    @EventHandler(ignoreCancelled = true)
    public void onTeamLevelUp(TeamLevelUpEvent<T, U> event) {
        for (U member : iridiumTeams.getTeamManager().getTeamMembers(event.getTeam())) {
            Player player = member.getPlayer();
            if(player == null) continue;
            player.sendMessage(StringUtils.color(iridiumTeams.getMessages().teamLevelUp
                    .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                    .replace("%level%", String.valueOf(event.getTeam().getLevel()))
            ));
        }

        if (event.isFirstTimeAsLevel() && event.getLevel() > 1) {
            if(!iridiumTeams.getConfiguration().giveLevelRewards) return;
            
            List<Map.Entry<Integer, Reward>> entries = iridiumTeams.getConfiguration().levelRewards.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Reward>comparingByKey().reversed())
                    .collect(Collectors.toList());

            int oldLevel = event.getOldLevel();
            int newLevel = event.getLevel();

            for (int currentLvl = oldLevel + 1; currentLvl <= newLevel; currentLvl++) {
                
                for (Map.Entry<Integer, Reward> entry : entries) {
                    if (currentLvl % entry.getKey() == 0) {
                        Reward reward = entry.getValue();
                        
                        iridiumTeams.getTeamManager().addTeamReward(new TeamReward(event.getTeam(), reward, currentLvl));
                        
                        break;
                    }
                }
            }
        }
    }
}