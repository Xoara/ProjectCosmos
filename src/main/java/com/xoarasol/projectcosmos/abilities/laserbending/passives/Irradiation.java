package com.xoarasol.projectcosmos.abilities.laserbending.passives;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Irradiation extends RadiationAbility implements AddonAbility, PassiveAbility {

    public Irradiation(final Player player) {
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
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "This passive allows Radiationbenders to irradiate either themselves and/or their opponents. " +
                "If a radiant entity is hit with an ability, it deals extra damage!";
    }

    @Override
    public String getName() {
        return "Irradiation";
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
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Passives.Laser.Irradiation.Enabled");
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }
}
