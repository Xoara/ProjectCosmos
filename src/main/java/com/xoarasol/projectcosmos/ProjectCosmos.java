package com.xoarasol.projectcosmos;

import com.xoarasol.projectcosmos.abilities.cosmicbending.CosmicBlast;
import com.xoarasol.projectcosmos.abilities.laserbending.LaserFission;
import com.xoarasol.projectcosmos.abilities.laserbending.combos.Dispersion;
import com.xoarasol.projectcosmos.abilities.laserbending.forcefield.Radial;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.xoarasol.projectcosmos.configuration.ConfigManager;
import com.xoarasol.projectcosmos.listeners.PCAbilityListener;
import com.xoarasol.projectcosmos.listeners.PCGeneralListener;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ProjectCosmos extends JavaPlugin {

    public static ProjectCosmos plugin;

    public ArrayList<LivingEntity> radiantEntities;
    public HashMap<UUID, String> plasmaList;

    @Override
    public void onEnable() {
        plugin = this;
        this.radiantEntities = new ArrayList<>();
        this.plasmaList = new HashMap<>();

        new ConfigManager();
        new PCElement();


        ProjectCosmos.plugin.getServer().getPluginManager().registerEvents(new PCAbilityListener(), ProjectCosmos.plugin);
        ProjectCosmos.plugin.getServer().getPluginManager().registerEvents(new PCGeneralListener(), ProjectCosmos.plugin);

        CoreAbility.registerPluginAbilities(ProjectCosmos.plugin, "com.xoarasol.projectcosmos.abilities");
        ProjectCosmos.plugin.reloadConfig();

        registerCollisions();

        getLogger().info("ProjectCosmos has been enabled!");
    }

    @Override
    public void onDisable() {
        for (LivingEntity lent : plugin.radiantEntities) {
            LaserAbility.removeRadiance(lent);
        }
        getLogger().info("ProjectCosmos has been disabled");
    }

    public static void reload() {
        CoreAbility.registerPluginAbilities(ProjectCosmos.getPlugin(), "com.xoarasol.projectcosmos.abilities");
        ProjectCosmos.plugin.reloadConfig();
        ProjectCosmos.plugin.getLogger().info("ProjectCosmos reloaded!");
        ProjectCosmos.plugin.registerCollisions();

        for (LivingEntity lent : plugin.radiantEntities) {
            LaserAbility.removeRadiance(lent);
        }
    }

    public static ProjectCosmos getPlugin() {
        return plugin;
    }

    public static String getVersion() {
        return ChatColor.BLACK + "[" + ChatColor.BLUE + "ProjectCosmos" + ChatColor.BLACK + "] " + ChatColor.RESET + plugin.getDescription().getVersion();
    }

    public void registerCollisions() {
        //Cosmic
        ProjectKorra.getCollisionInitializer().addLargeAbility(CoreAbility.getAbility(CosmicBlast.class));
        ProjectKorra.getCollisionInitializer().addRemoveSpoutAbility(CoreAbility.getAbility(CosmicBlast.class));

        //Laser
        ProjectKorra.getCollisionInitializer().addRemoveSpoutAbility(CoreAbility.getAbility(LaserFission.class));
        ProjectKorra.getCollisionInitializer().addRemoveSpoutAbility(CoreAbility.getAbility(Radial.class));
        ProjectKorra.getCollisionInitializer().addRemoveSpoutAbility(CoreAbility.getAbility(Dispersion.class));
    }
}
