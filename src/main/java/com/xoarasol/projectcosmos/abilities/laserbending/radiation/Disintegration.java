package com.xoarasol.projectcosmos.abilities.laserbending.radiation;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.*;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Disintegration extends RadiationAbility implements AddonAbility {

    private double t = 0.7853981633974483D;
    private double particleRotation;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    private double stunChance;
    private double push;
    @Attribute("StunDuration")
    private long stunDuration;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    private boolean isCharged;

    private Location loc;
    private ArrayList<Entity> electrocuted;

    private ArrayList<UUID> affected;

    public Disintegration(Player player) {
        super(player);
        if (this.bPlayer.canBend((CoreAbility) this)) {

            this.loc = this.player.getLocation().add(0.0D, -1.0D, 0.0D).clone();
            this.isCharged = false;
            this.electrocuted = new ArrayList<>();
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.Disintegration.Range");
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.Disintegration.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.Disintegration.Damage");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.Disintegration.ChargeTime");
            this.stunChance = 0;
            this.stunDuration = 0;
            this.push = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.Disintegration.Push");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.Disintegration.RadianceDuration");

            this.affected = new ArrayList<>();

            start();
        }
    }

    private void electrocute(LivingEntity le) {
        double i = Math.random() * 100.0D;
        if (i <= this.stunChance && !this.electrocuted.contains(le)) {
            this.electrocuted.add(le);
            MovementHandler mh = new MovementHandler(le, (CoreAbility) this);
            //mh.stopWithDuration(this.stunDuration, Element.PUSSY.getColor() + "*Hi What´s up?*");
            return;
        }
    }

    public void progress() {
        if (this.t >= this.range) {
            remove();
            this.bPlayer.addCooldown((Ability) this);
            return;
        }
        if (!PCMethods.generalBendCheck(this, this.player)) {
            this.remove();
            return;
        }
        if (!this.player.isSneaking() && !this.isCharged) {
            this.remove();
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild((Ability) this, this.loc)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + this.chargeTime && !this.isCharged)
            this.isCharged = true;
        if (!this.isCharged) {
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_CHORUS_FLOWER_GROW, 1.4F, 0.5F);
            absorbtion();
        } else {
            this.bPlayer.addCooldown(this);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 0.53F);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_CHORUS_FLOWER_DEATH, 1.4F, 1.53F);
            this.loc.getWorld().playSound(this.loc, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 0.5F, 0.0F);
            pulse();
        }
    }

    private void absorbtion() {
        Location localLocation1 = this.player.getLocation();
        double d1 = 0.1570796326794897D;
        double d2 = 0.06283185307179587D;
        double d3 = 1.0D;
        double d4 = 1.0D;
        double d5 = 0.1570796326794897D * this.particleRotation;
        double d6 = 0.06283185307179587D * this.particleRotation;
        double d7 = localLocation1.getX() + 1.5D * Math.cos(d5);
        double d8 = localLocation1.getZ() + 1.5D * Math.sin(d5);
        double newY = localLocation1.getY() + 1.0D + 1.0D * Math.cos(d6);
        Location localLocation2 = new Location(this.player.getWorld(), d7, newY, d8);
        (new ColoredParticle(Color.fromRGB(56, 255, 157), 1.5F)).display(localLocation2, 2, 0.25D, 0.0D, 0.25D);

        double xd7 = localLocation1.getX() + 1.0D * -Math.cos(d5);
        double xd8 = localLocation1.getZ() + 1.0D * -Math.sin(d5);
        double xnewY = localLocation1.getY() + 1.0D + 1.0D * Math.cos(d6);
        Location localLocation3 = new Location(this.player.getWorld(), xd7, xnewY, xd8);
        (new ColoredParticle(Color.fromRGB(56, 255, 255), 1.5F)).display(localLocation3, 2, 0.25D, 0.0D, 0.25D);

        this.particleRotation++;
        this.loc = this.player.getLocation().add(0.0D, -1.0D, 0.0D).clone();
        ActionBar.sendActionBar(PCElement.RADIATION.getColor() + "* Channeling *", new Player[]{this.player});
    }

    private void pulse() {
        this.t += 1.5D;
        for (double theta = 0.0D; theta <= 6.283185307179586D; theta += 0.04908738521234052D) {
            double x = this.t * Math.cos(theta);
            double y = 0.6D * Math.exp(-0.1D * this.t) * Math.sin(this.t) + 1.5D;
            double z = this.t * Math.sin(theta);
            this.loc.add(x, y, z);

            (new ColoredParticle(Color.fromRGB(56, 255, 157), 1.5F)).display(this.loc, 2, 0.35D, 0.0D, 0.35D);
            (new ColoredParticle(Color.fromRGB(56, 255, 255), 1.5F)).display(this.loc, 2, 0.35D, 0.0D, 0.35D);
            ParticleEffect.FIREWORKS_SPARK.display(this.loc, 1, 0, 0, 0, 0.08f);

            this.loc.subtract(x, y, z);
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.loc, this.t)) {
                if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId() && !this.affected.contains(entity.getUniqueId())) {
                    DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                    LivingEntity le = (LivingEntity) entity;
                    if (isRadiant(le)) {
                        electrocute(le);
                        removeRadiance(le);
                    } else {
                        setRadiance(le, this.radianceDuration);
                    }
                    entity.setVelocity(this.player.getEyeLocation().getDirection().normalize().multiply(this.push));
                    this.affected.add(le.getUniqueId());
                }
            }
        }

    }

    public void remove() {
        super.remove();
        this.electrocuted.clear();
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.loc;
    }

    @Override
    public String getName() {
        return "Disintegration";
    }

    @Override
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

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Radiation.Disintegration.Enabled");
    }

    public String getDescription() {
        return "Advanced Radiationbenders are able to channel all their energy to release a very deadly wave of rays to " +
                "disintegrate anyone in its path.";
    }

    public String getInstructions() {
        return "- Hold-Shift! -";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "_Hetag1216_ & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

}
