package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Users may edit, copy, distribute and manage this project.
 * They will not have permission to change this project's
 * credits and claim it as theirs.
 * All rights are reserved to the Author.
 * @author dario (_Hetag1216_)
 *
 */

public class EventHorizon extends CosmicAbility implements AddonAbility {

    private long cooldown;
    private double range;
    private double damage;
    private double growth = 1;
    private double knockback;
    private int pstage;
    private int p;

    private long chargeTime;
    private boolean charged;
    private boolean shoot;

    private Vector direction;
    private Location location;
    private Location origin;
    private double growing;

    public EventHorizon(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
            return;
        }
        setFields();
        if (GeneralMethods.isRegionProtectedFromBuild(this, GeneralMethods.getTargetedLocation(player, range))) {
            return;
        }
        start();
    }

    public void setFields() {
        this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.EventHorizon.Knockback");
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.EventHorizon.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.EventHorizon.Range");
        this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.EventHorizon.ChargeTime");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.EventHorizon.Damage");
        this.growing = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.EventHorizon.Growth");
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }
        if (System.currentTimeMillis() >= getStartTime() + chargeTime) {
            charged = true;
        }
        if ((player.isSneaking()) && (!shoot)) {
            displayYingYang();
            displayPoints();
        } else {
            if (!charged) {
                remove();
                return;
            }
            if (!shoot) {
                shoot = true;
            }
            Energy();
        }
    }

    private void Energy() {
        for (int j = 0; j < 2; j++) {
            location = location.add(direction.multiply(1));

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                location.getWorld().playSound(location, Sound.ITEM_TRIDENT_THUNDER, 0.8F, 1.20F);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.8F, 0.75F);
                location.getWorld().playSound(location, Sound.AMBIENT_BASALT_DELTAS_MOOD, 0.8F, 2F);
            } else {
                location.getWorld().playSound(location, Sound.ITEM_TRIDENT_THUNDER, 0.8F, 1.90F);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.8F, 0.75F);
                location.getWorld().playSound(location, Sound.AMBIENT_BASALT_DELTAS_MOOD, 0.8F, 2F);
            }

            growth += this.growing;
            if (origin.distance(location) >= range) {
                remove();
                bPlayer.addCooldown(this);
                return;
            }

            if (!isTransparent(location.getBlock())) {
                remove();
                bPlayer.addCooldown(this);
                return;
            }

            if (GeneralMethods.isRegionProtectedFromBuild(player, "EventHorizon", player.getLocation())) {
                remove();
                bPlayer.addCooldown(this);
                return;
            }

            double pitch = -location.getPitch();
            Location lastLoc = location.clone();
            for (double i = -90 + pitch; i <= 90 + pitch; i += 9) {
                Location tempLoc = location.clone();
                tempLoc.setPitch(0);
                Vector tempDir = tempLoc.getDirection().clone();
                tempDir.setY(0);
                Vector newDir = tempDir.clone().multiply(growth * Math.cos(Math.toRadians(i)));
                tempLoc.add(newDir);
                tempLoc.setY(tempLoc.getY() + (growth * Math.sin(Math.toRadians(i))));

                ParticleEffect.END_ROD.display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2,(float) Math.random() / 2, 0);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    new ColoredParticle(Color.fromRGB(66, 0, 188), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    new ColoredParticle(Color.fromRGB(45, 0, 130), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    new ColoredParticle(Color.fromRGB(13, 0, 56), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    ParticleEffect.SQUID_INK.display(this.location, 1, 0.1, 0.1, 0.1);

                } else {
                    new ColoredParticle(Color.fromRGB(109, 133, 255), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    new ColoredParticle(Color.fromRGB(80, 78, 196), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    new ColoredParticle(Color.fromRGB(72, 49, 175), 2.45F).display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2);
                    ParticleEffect.CLOUD.display(this.location, 1, 0.1, 0.1, 0.1);

                }

                if (j == 0) {
                    // Only check collisions for each block.
                    if (!lastLoc.getBlock().getLocation().equals(tempLoc.getBlock().getLocation())) {
                        lastLoc = tempLoc;

                        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(tempLoc, 1.2)) {
                            if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId() && !(entity instanceof ArmorStand)) {
                                DamageHandler.damageEntity(entity, damage, this);

                                GeneralMethods.setVelocity(this, entity, this.location.getDirection().normalize().multiply(knockback));
                            }
                        }
                    }
                }
            }
        }
    }

    private void displayYingYang() {
        if (!charged) {
            Location centre = player.getLocation().clone().add(0, 1, 0);
            double increment = (1 * Math.PI) / 20;
            double angle = pstage * increment;
            double x = centre.getX() + (1.3 * Math.cos(angle));
            double z = centre.getZ() + (1.3 * Math.sin(angle));
            location = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
            horizon();
            horizon2();
            horizon3();

            ParticleEffect.END_ROD.display(new Location(centre.getWorld(), x, centre.getY(), z), 1,  0F, 0F, 0F, 0);

            double x1 = centre.getX() + (1.3 * -Math.cos(angle));
            double z1 = centre.getZ() + (1.3 * -Math.sin(angle));

            ParticleEffect.END_ROD.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1, 0F, 0F, 0F, 0);

            if (pstage >= 360) {
                pstage = 0;
            }
            pstage++;
        }
        if (charged) {
            Location centre = player.getEyeLocation().clone().add(0, 1, 0);
            double increment = (1 * Math.PI) / 20;
            double angle = pstage * increment;
            double x = centre.getX() + (0.3 * Math.cos(angle));
            double z = centre.getZ() + (0.3 * Math.sin(angle));
            location = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
            horizon();
            horizon2();
            horizon3();

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                new ColoredParticle(Color.fromRGB(66, 0, 188), 2.45F).display(new Location(centre.getWorld(), x, centre.getY(), z), 1, 0F, 0F, 0F);
            } else {
                new ColoredParticle(Color.fromRGB(109, 133, 255), 2.45F).display(new Location(centre.getWorld(), x, centre.getY(), z), 1, 0F, 0F, 0F);
            };

            double x1 = centre.getX() + (0.3 * -Math.cos(angle));
            double z1 = centre.getZ() + (0.3 * -Math.sin(angle));

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                new ColoredParticle(Color.fromRGB(66, 0, 188), 2.45F).display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1, 0F, 0F, 0F);
            } else {
                new ColoredParticle(Color.fromRGB(109, 133, 255), 2.45F).display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1, 0F, 0F, 0F);
            };

            if (pstage >= 360) {
                pstage = 0;
            }
            pstage++;
        }
    }

    private void displayPoints() {
        if (charged) {
            Location centre = GeneralMethods.getTargetedLocation(player, 2);
            /*
             * Location centre =
             * l.toVector().add(player.getEyeLocation().getDirection().clone().multiply(.75D
             * )) .toLocation(player.getWorld());
             */
            double increment = (/* speed */ 2 * Math.PI) / 36;
            double angle = p * increment;
            double x = centre.getX() + (/* radius */ 0.2 * Math.cos(angle));
            double z = centre.getZ() + (/* radius */ 0.6 * Math.sin(angle));
            double x1 = centre.getX() + (/* radius */ 0.6 * -Math.cos(angle));
            double z1 = centre.getZ() + (/* radius */ 0.2 * -Math.sin(angle));
            ParticleEffect.ELECTRIC_SPARK.display(new Location(centre.getWorld(), x, centre.getY(), z), 1);
            ParticleEffect.ELECTRIC_SPARK.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1);
            GeneralMethods.displayColoredParticle("000000", centre);
            //ParticleEffect.END_ROD.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 2, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2, 0.2F);
            centre.getWorld().playSound(centre, Sound.BLOCK_BEACON_AMBIENT, 0.87F, 0.7F);
            if (p >= 360) {
                p = 0;
            }
            p++;
        }
    }

    private void horizon() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (2.5 * Math.cos(angle));
        double z = centre.getZ() + (2.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.4, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (2.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (2.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.4, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void horizon2() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (3.0 * Math.cos(angle));
        double z = centre.getZ() + (3.0 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (3.0 * -Math.cos(angle));
        double z2 = centre.getZ() + (3.0 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void horizon3() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (3.5 * Math.cos(angle));
        double z = centre.getZ() + (3.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.2, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 2.25F)).display(loc, 2, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (3.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (3.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.2, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 2.25F)).display(loc2, 2, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.EventHorizon.Enabled");
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Location getOrigin() {
        return origin;
    }

    @Override
    public String getDescription() {
        return "EventHorizon is an advanced cosmicbending ability. It allows them to harness the power of a black hole and " +
                "summon a vertical event horizon, damaging and disengaging enemies.";
    }

    @Override
    public String getInstructions() {
        return "*Hold Shift* to charge then *Release Shift* to fire! -";
    }

    @Override
    public String getName() {
        return "EventHorizon";
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