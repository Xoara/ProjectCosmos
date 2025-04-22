package com.xoarasol.projectcosmos;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.xoarasol.projectcosmos.abilities.cosmicbending.CosmicBlast;
import com.xoarasol.projectcosmos.configuration.ConfigManager;
import com.xoarasol.projectcosmos.listeners.PCAbilityListener;
import com.xoarasol.projectcosmos.listeners.PCGeneralListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


public class ProjectCosmos extends JavaPlugin {

    public static ProjectCosmos plugin;

    @Override
    public void onEnable() {
        plugin = this;
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
        getLogger().info("ProjectCosmos has been disabled");
    }

    public static void reload() {
        CoreAbility.registerPluginAbilities(ProjectCosmos.getPlugin(), "com.xoarasol.projectcosmos.abilities");
        ProjectCosmos.plugin.reloadConfig();
        ProjectCosmos.plugin.getLogger().info("ProjectCosmos reloaded!");
        ProjectCosmos.plugin.registerCollisions();

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
    }
}
