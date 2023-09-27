package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class LaserGaze extends LaserAbility implements AddonAbility {

    private Location location;
    private Vector direction;
    private long duration;
    private long cooldown;
    private double range;
    private double damage;
    private long time;
    private long revertTime;
    private long damageInterval;
    private long damageTime;
    private int fireticks;

    private boolean hasHit;

    private int colorCounter;
    private boolean hasColorReached;

    public LaserGaze(Player player) {
        super(player);
        if (this.bPlayer.canBend(this)) {
            setFields();
            start();
        }
    }

    public void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserGaze.Cooldown");
        this.duration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.LaserGaze.Duration");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserGaze.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserGaze.Damage");
        this.fireticks = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.LaserGaze.FireTicks");
        this.revertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserGaze.BlockRevertTime");
        this.damageInterval = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserGaze.DamageInterval");
        this.time = System.currentTimeMillis();
        this.damageTime = System.currentTimeMillis();

        Random rand = new Random();
        this.colorCounter = rand.nextInt(32) *5;
        this.hasColorReached = false;

        this.hasHit = false;
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }

        if (!this.bPlayer.canBend((CoreAbility) this)) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }
        if (System.currentTimeMillis() > this.time + this.duration) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }
        if (!this.player.isSneaking()) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }
        if (this.hasColorReached) {
            this.colorCounter -= 5;
            if (this.colorCounter <= 0) {
                this.colorCounter = 0;
                this.hasColorReached = false;
            }
        } else {
            this.colorCounter += 5;
            if (this.colorCounter >= 165) {
                this.colorCounter = 165;
                this.hasColorReached = true;
            }
        }
        Gaze1();
        Gaze2();
    }

    private void Gaze1() {
        this.location = GeneralMethods.getRightSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D);
        this.direction = this.location.getDirection();
        for (double i = 0.0D; i < this.range; i += 0.5D) {
            this.location = this.location.add(this.direction.normalize());
            if (this.location.distance(this.player.getEyeLocation()) > this.range) {
                return;
            }
            if (GeneralMethods.isSolid(this.location.getBlock())) {
                if (isEarth(this.location.getBlock()) && isSand(this.location.getBlock())) {
                    TempBlock tb = new TempBlock(this.location.getBlock(), Material.MAGMA_BLOCK);
                    tb.setRevertTime(this.revertTime);
                    ParticleEffect.LAVA.display(this.location, 1, 0.5, 0.5,0.5);
                    ParticleEffect.SMOKE_LARGE.display(this.location, 5, 0.15, 0.15,0.15, 0.04f);
                }
                return;
            }

            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "LaserGaze", this.location))
                return;

            new ColoredParticle(Color.fromRGB(255, this.colorCounter, 0), 1.5f).display(this.location, 1, 0,0,0);
            if ((new Random()).nextInt(5) == 0)
                this.location.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 1);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 0.7D)) {
                if (entity instanceof org.bukkit.entity.LivingEntity && entity.getEntityId() != this.player.getEntityId() &&
                        !(entity instanceof org.bukkit.entity.ArmorStand)) {
                    if (this.hasHit && System.currentTimeMillis() > this.damageTime + this.damageInterval) {
                        DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                        this.damageTime = System.currentTimeMillis();
                    } else if (!this.hasHit) {
                        this.hasHit = true;
                        DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                        this.damageTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    private void Gaze2() {
        this.location = GeneralMethods.getLeftSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D);
        this.direction = this.location.getDirection();
        for (double i = 0.0D; i < this.range; i += 0.5D) {
            this.location = this.location.add(this.direction.normalize());
            if (this.location.distance(this.player.getEyeLocation()) > this.range) {
                return;
            }
            if (GeneralMethods.isSolid(this.location.getBlock())) {
                if (isEarth(this.location.getBlock())) {
                    TempBlock tb = new TempBlock(this.location.getBlock(), Material.MAGMA_BLOCK);
                    tb.setRevertTime(this.revertTime);
                    ParticleEffect.LAVA.display(this.location, 1, 0.5, 0.5,0.5);
                    ParticleEffect.SMOKE_LARGE.display(this.location, 5, 0.15, 0.15,0.15, 0.04f);
                }
                return;
            }
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "LaserGaze", this.location))
                return;

            new ColoredParticle(Color.fromRGB(255, this.colorCounter, 0), 1.5f).display(this.location, 1, 0,0,0);
            if ((new Random()).nextInt(5) == 0)
                this.location.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1);


            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 0.7D)) {
                if (entity instanceof org.bukkit.entity.LivingEntity && entity.getEntityId() != this.player.getEntityId() &&
                        !(entity instanceof org.bukkit.entity.ArmorStand)) {
                    if (this.hasHit && System.currentTimeMillis() > this.damageTime + this.damageInterval) {
                        DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                        this.damageTime = System.currentTimeMillis();
                    } else if (!this.hasHit) {
                        this.hasHit = true;
                        DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                        this.damageTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getDescription() {
        return "This ability is used in combat amongst Laserbenders often." +
                "LaserGaze allows you to shoot out two continuous beams of dangerous rays out of your eyes!";
    }

    public String getInstructions() {
        return "- Hold Shift! -";
    }

    public String getName() {
        return "LaserGaze";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.LaserGaze.Enabled");
    }

    @Override
    public void remove() {
        super.remove();
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
        return true;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272 & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}