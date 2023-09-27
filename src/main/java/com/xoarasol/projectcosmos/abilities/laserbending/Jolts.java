package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class Jolts extends LaserAbility implements AddonAbility {

    private long cooldown;
    private double damage;
    private double range;
    private double radius;
    private double speed = 1;

    private Location origin;
    private Location loc;
    private Vector dir;
    private Entity e;
    private limb chosen;

    private enum limb {
        RP, LP, RK, LK;
    }

    public Jolts(Player player) {
        super(player);
        if (!this.bPlayer.canBend((CoreAbility)this) || this.bPlayer.isOnCooldown((Ability)this) || GeneralMethods.isRegionProtectedFromBuild((Ability)this, player.getLocation()))
            return;
        setFields();
        start();
    }

    public void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserJolts.Cooldown");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserJolts.Damage");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserJolts.Range");
        this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserJolts.Radius");
        this.origin = this.player.getEyeLocation();
        this.chosen = limb.values()[(new Random()).nextInt((limb.values()).length)];
        if (this.chosen == limb.LK)
            this.origin = GeneralMethods.getLeftSide(this.origin, 0.6D).subtract(0.0D, 0.4D, 0.0D);
        if (this.chosen == limb.LP)
            this.origin = GeneralMethods.getLeftSide(this.origin, 0.6D);
        if (this.chosen == limb.RK)
            this.origin = GeneralMethods.getRightSide(this.origin, 0.6D).subtract(0.0D, 0.4D, 0.0D);
        if (this.chosen == limb.RP)
            this.origin = GeneralMethods.getRightSide(this.origin, 0.6D);
        this.loc = this.origin.clone();
        this.dir = this.origin.getDirection();
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
        if (this.loc.distance(this.origin) > this.range)
            remove();
        if (GeneralMethods.isSolid(this.loc.getBlock()))
            remove();
        this.dir = this.player.getEyeLocation().getDirection();
        this.loc.add(this.dir.clone().multiply(this.speed));
        if (this.e == null) {
            new ColoredParticle(Color.fromRGB(255, 33, 92), 3f).display(this.loc, 3, 0.1, 0, 0.1);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 1.3f);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 1.2f);
            this.e = (Entity)getAffected(this.loc, this.radius, this.player);
        } else {
            DamageHandler.damageEntity(this.e, this.damage, (Ability)this);
            remove();
        }
    }

    public static LivingEntity getAffected(Location loc, double radii, Player player) {
        LivingEntity e = null;
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, radii)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId())
                e = (LivingEntity)entity;
        }
        return e;
    }
    public void remove() {
        super.remove();
    }

    public String getDescription() {
        return "This spell allows Laserbenders to throw up to 4 Jolts of pure Laser at your enemies.";
    }

    public String getInstructions() {
        return "- Left-Click! -";
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
