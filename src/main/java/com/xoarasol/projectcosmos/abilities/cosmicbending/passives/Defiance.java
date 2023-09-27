package com.xoarasol.projectcosmos.abilities.cosmicbending.passives;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Defiance extends CosmicAbility implements AddonAbility, PassiveAbility {

    public Defiance(final Player player) {
        super(player);
    }

    @Override
    public void progress() {

    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Cosmicbenders are able to defy gravity, therefore negating all kinds of Fall-Damage!";
    }

    @Override
    public String getName() {
        return "Defiance";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isInstantiable() {
        return false;
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    @Override
    public String getAuthor() {
        return "Xoara";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Passives.Cosmic.Defiance.Enabled");
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }
}
