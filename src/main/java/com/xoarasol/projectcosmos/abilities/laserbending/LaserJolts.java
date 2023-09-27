package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LaserJolts extends LaserAbility implements AddonAbility {
    private long cooldown;

    private int shots;

    private Location origin;

    private Location loc;

    private Vector dir;

    private int currentshots;

    public LaserJolts(Player player) {
        super(player);
        if (!this.bPlayer.canBend((CoreAbility)this) || CoreAbility.hasAbility(player, getClass()) || this.bPlayer.isOnCooldown((Ability)this) || GeneralMethods.isRegionProtectedFromBuild((Ability)this, player.getLocation()))
            return;
        setFields();
        start();
        onClick();
    }

    public void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserJolts.Cooldown");
        this.shots = 4;
        this.origin = this.player.getEyeLocation();
        this.loc = this.origin.clone();
        this.dir = this.origin.getDirection();
        this.currentshots = 0;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getName() {
        return "LaserJolts";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return false;
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline() || this.bPlayer.isChiBlocked() || GeneralMethods.isRegionProtectedFromBuild((Ability)this, this.player.getLocation()))
            remove();
        if (this.currentshots == this.shots)
            remove();
    }

    public void onClick() {
        new Jolts(this.player);
        this.currentshots++;
    }

    public void remove() {
        this.bPlayer.addCooldown((Ability)this);
        super.remove();
    }

    public String getDescription() {
        return "This ability allows Laserbenders to throw up to 4 jolts of laser at their foes!";
    }

    public String getInstructions() {
        return "- Left-Click multiple times! -";
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getAuthor() {
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
