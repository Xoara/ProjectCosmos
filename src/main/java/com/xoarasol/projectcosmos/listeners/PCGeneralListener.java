package com.xoarasol.projectcosmos.listeners;

import com.projectkorra.projectkorra.event.AbilityCollisionEvent;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.abilities.cosmicbending.MeteorShower;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class PCGeneralListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBendingReload (BendingReloadEvent event) {
        Bukkit.getScheduler().runTaskLater(ProjectCosmos.plugin, ProjectCosmos::reload, 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockChange(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock fBlock = (FallingBlock) event.getEntity();

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
            if (fBlock.hasMetadata("meteorshower")) {
                MeteorShower.Impact(fBlock);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void abilityCollideEvent(AbilityCollisionEvent event) {

    }
}
