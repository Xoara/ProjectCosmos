package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ElectronLance extends LaserAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    private double secondHalfDamage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.KNOCKBACK)
    private double knockback;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.SPEED)
    private double speed;

    private double startingRadius;
    private double finalRadius;
    private double currentRadius;
    private double decayMod;

    private Location origin;
    private Location location;
    private Vector direction;

    private boolean isCharged;
    private boolean isFired;

    private int blueCounter;
    private boolean hasRedReached;

    public ElectronLance(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ElectronLance.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.Damage");
            this.secondHalfDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.SecondHalfDamage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.Range");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.Knockback");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ElectronLance.ChargeTime");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.Speed");

            this.startingRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ElectronLance.StartingRadius");
            this.finalRadius = 0.6;
            this.currentRadius = this.startingRadius;

            //Creates the cone shape
            this.decayMod = 1 - Math.abs(Math.sin(this.startingRadius / this.range));

            this.isCharged = false;
            this.isFired = false;

            this.speed = (this.speed * (ProjectKorra.time_step / 1000.0));

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            if (this.isCharged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (this.bPlayer.isOnCooldown(this) && !this.isFired) {
            remove();
            return;
        }
        if (!this.isFired && !this.bPlayer.getBoundAbilityName().equalsIgnoreCase("ElectronLance")) {
            this.remove();
            return;
        }
        if (!this.player.isSneaking() && !this.isCharged) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.chargeTime) {
            this.isCharged = true;
        }
        if (!this.player.isSneaking() && !this.isFired && this.isCharged) {
            this.isFired = true;
            this.bPlayer.addCooldown(this);

            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();
            this.direction = this.origin.getDirection();
        }
        if (this.isFired && !this.isCharged) {
            this.remove();
            return;
        }
        if (this.isCharged && !this.isFired) {
            ParticleEffect.FIREWORKS_SPARK.display(this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(3)), 1,0,0, 0);
            player.getWorld().playSound(this.player.getEyeLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1.25f);
        }

        if (this.hasRedReached) {
            this.blueCounter -= 5;
            if (this.blueCounter <= 0) {
                this.blueCounter = 0;
                this.hasRedReached = false;
            }
        } else {
            this.blueCounter += 4;
            if (this.blueCounter >= 255) {
                this.blueCounter = 255;
                this.hasRedReached = true;
            }
        }
        progressLance();
    }

    private void drawCircle() {
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 30) {
                Location temp = this.location.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, this.currentRadius - 0.3);
                temp.add(dir);
                new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 1.5f).display(temp, 2, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 1.2f).display(temp, 2, 0.1, 0.1, 0.1);
            }
        }
    }

    public void progressLance() {
        if (this.isFired) {
            for (int i = 0; i <= 2; i++) {
                if (this.location.distance(this.origin) >= this.range) {
                    this.remove();
                    return;
                }

                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    this.remove();
                    return;
                }

                if (i % 2 == 0) {
                    drawCircle();
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.6F, 0.86f);
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_DEACTIVATE, 0.6F, 1.26f);
                }

                ParticleEffect.FIREWORKS_SPARK.display(this.location, 4, 0.1, 0.1, 0.1, 0.02f);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.5)) {
                    if (entity != this.player) {
                        DamageHandler.damageEntity(entity, this.player, this.damage, this);
                        entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 4F, 0.7f);
                        ParticleEffect.EXPLOSION_HUGE.display(entity.getLocation(), 1, 1, 1, 1);
                        this.remove();
                        return;
                    }
                }
                this.location.add(this.direction.normalize().multiply(this.speed / 2));
            }
            if (this.currentRadius <= finalRadius) {
                this.currentRadius = this.finalRadius;
            } else {
                this.currentRadius *= this.decayMod;
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.ElectronLance.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public double getCollisionRadius() {
        return this.currentRadius;
    }

    @Override
    public String getName() {
        return "ElectronLance";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return "This ability allows Laserbenders to shoot out a lance of electrons, which decreases in size as it progresses, " +
                "and can even pierce through armor!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift > Release-Shift when it's charged up! -";
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
