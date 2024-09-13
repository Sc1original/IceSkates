package me.wither.simple_error_tsting;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class IceSkatesListener implements Listener {

    @EventHandler
    public void OnSneak(PlayerToggleSneakEvent event) {

        if(!event.isSneaking()) return;
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer.canBend(CoreAbility.getAbility("IceSkates"))) {
            new IceSkates(player);

        }
    }
}