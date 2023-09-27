package com.xoarasol.projectcosmos.listeners;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending.Orbital;
import com.projectkorra.projectkorra.event.AbilityCollisionEvent;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PCGeneralListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBendingReload (BendingReloadEvent event) {
        Bukkit.getScheduler().runTaskLater(ProjectCosmos.plugin, ProjectCosmos::reload, 1);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (msg.equalsIgnoreCase("@EnergyBolt Types")) {
            player.sendMessage(ChatColor.AQUA + "@EnergyBolt Force (Default)");
            player.sendMessage(ChatColor.DARK_AQUA + "@EnergyBolt Cyro");
            player.sendMessage(ChatColor.RED + "@EnergyBolt Pyro");
            player.sendMessage(ChatColor.DARK_PURPLE + "@EnergyBolt Necro");

            if (ProjectCosmos.plugin.plasmaList.containsKey(uuid)) {
                String boltType = ProjectCosmos.plugin.plasmaList.get(uuid);
                player.sendMessage(PCElement.FORCEFIELD.getColor() + "Your EnergyBolt type is " + boltType + "!");
            } else {
                player.sendMessage(PCElement.FORCEFIELD.getColor() + "Your EnergyBolt type is Force!");
            }
            event.setCancelled(true);

        } else if (msg.equalsIgnoreCase("@EnergyBolt Cyro")) {
            if (ProjectCosmos.plugin.plasmaList.containsKey(uuid)) {
                ProjectCosmos.plugin.plasmaList.remove(uuid);
            }
            ProjectCosmos.plugin.plasmaList.put(uuid, "Cyro");
            player.sendMessage(ChatColor.DARK_AQUA + "Your EnergyBolt now radiates an icy energy!");
            event.setCancelled(true);

        } else if (msg.equalsIgnoreCase("@EnergyBolt Pyro")) {
            if (ProjectCosmos.plugin.plasmaList.containsKey(uuid)) {
                ProjectCosmos.plugin.plasmaList.remove(uuid);
            }
            ProjectCosmos.plugin.plasmaList.put(uuid, "Pyro");
            player.sendMessage(ChatColor.RED + "Your EnergyBolt now radiates a fiery energy!");
            event.setCancelled(true);

        } else if (msg.equalsIgnoreCase("@EnergyBolt Necro")) {
            if (ProjectCosmos.plugin.plasmaList.containsKey(uuid)) {
                ProjectCosmos.plugin.plasmaList.remove(uuid);
            }
            ProjectCosmos.plugin.plasmaList.put(uuid, "Necro");
            player.sendMessage(ChatColor.DARK_PURPLE + "Your EnergyBolt now radiates a draining energy!");
            event.setCancelled(true);

        }else if (msg.equalsIgnoreCase("@EnergyBolt Force")) {
            if (ProjectCosmos.plugin.plasmaList.containsKey(uuid)) {
                ProjectCosmos.plugin.plasmaList.remove(uuid);
            }
            player.sendMessage(PCElement.FORCEFIELD.getColor() + "Your EnergyBolt now radiates regular energy!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockChange(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock fBlock = (FallingBlock) event.getEntity();

            if (fBlock.hasMetadata("orbital")) {
                event.setCancelled(true);
                ((Orbital) fBlock.getMetadata("orbital").get(0).value()).revertBlockCreation(fBlock, event.getBlock());
            }
            if (fBlock.hasMetadata("gravitymanipulation")) {
                event.setCancelled(true);
            }
            if (fBlock.hasMetadata("stellarcrash")) {
                event.setCancelled(true);
            }
            if (fBlock.hasMetadata("tectonicdisruption")) {
                event.setCancelled(true);
            }
            if (fBlock.hasMetadata("cosmicblast")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void abilityCollideEvent(AbilityCollisionEvent event) {

    }
}
