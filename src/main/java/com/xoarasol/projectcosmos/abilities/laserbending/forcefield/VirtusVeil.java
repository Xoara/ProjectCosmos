package com.xoarasol.projectcosmos.abilities.laserbending.forcefield;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.ForceFieldAbility;
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

public class VirtusVeil extends ForceFieldAbility implements AddonAbility {

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

    public VirtusVeil(Player player) {
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
        knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.VirtusVeil.Knockback");
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.VirtusVeil.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.VirtusVeil.Range");
        this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.VirtusVeil.ChargeTime");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.VirtusVeil.Damage");
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
            this.direction = GeneralMethods.getTargetedLocation(this.player, 0.3).getDirection();
            this.location.add(this.direction);
            location.getWorld().playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.2f, 0.2F);
            location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1.2F);


            growth += 0.100;
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

            if (GeneralMethods.isRegionProtectedFromBuild(player, "VirtusVeil", player.getLocation())) {
                remove();
                bPlayer.addCooldown(this);
                return;
            }

            double pitch = -location.getPitch();
            Location lastLoc = location.clone();
            for (double i = -90 + pitch; i <= 90 + pitch; i += 8) {
                Location tempLoc = location.clone();
                tempLoc.setPitch(0);
                Vector tempDir = tempLoc.getDirection().clone();
                tempDir.setY(0);
                Vector newDir = tempDir.clone().multiply(growth * Math.cos(Math.toRadians(i)));
                tempLoc.add(newDir);
                tempLoc.setY(tempLoc.getY() + (growth * Math.sin(Math.toRadians(i))));

                ParticleEffect.SPELL_INSTANT.display(tempLoc, 1, (float) Math.random() / 2, (float) Math.random() / 2,(float) Math.random() / 2, 0);
                new ColoredParticle(Color.fromRGB(116, 173, 211), 2.4F).display(tempLoc, 6, 0.05, 0.05, 0.05);

                if (j == 0) {
                    // Only check collisions for each block.
                    if (!lastLoc.getBlock().getLocation().equals(tempLoc.getBlock().getLocation())) {
                        lastLoc = tempLoc;

                        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(tempLoc, 2)) {
                            if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId() && !(entity instanceof ArmorStand)) {
                                DamageHandler.damageEntity(entity, damage, this);
                                ParticleEffect.SPELL_INSTANT.display(entity.getLocation(), 15, (float) Math.random() / 2,(float) Math.random() / 2, (float) Math.random() / 2, 0.3F);
                                GeneralMethods.displayColoredParticle("74ADFF", tempLoc, 1, (float) Math.random() / 2,(float) Math.random() / 2, (float) Math.random());
                                GeneralMethods.displayColoredParticle("74ADFF", tempLoc, 1, (float) Math.random() / 2,(float) Math.random() / 2, (float) Math.random());
                                entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 0.2F);


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
            double increment = (1 * Math.PI) / 36;
            double angle = pstage * increment;
            double x = centre.getX() + (1.3 * Math.cos(angle));
            double z = centre.getZ() + (1.3 * Math.sin(angle));
            location = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            ParticleEffect.SPELL_INSTANT.display(new Location(centre.getWorld(), x, centre.getY(), z), 1,  0F, 0F, 0F, 0);
            double x1 = centre.getX() + (1.3 * -Math.cos(angle));
            double z1 = centre.getZ() + (1.3 * -Math.sin(angle));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            ParticleEffect.SPELL_INSTANT.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1, 0F, 0F, 0F, 0);
            if (pstage >= 360) {
                pstage = 0;
            }
            pstage++;
        }
        if (charged) {
            Location centre = player.getLocation().clone().add(0, 1, 0);
            double increment = (1 * Math.PI) / 36;
            double angle = -pstage * increment;
            double x = centre.getX() + (1.3 * Math.cos(angle));
            double z = centre.getZ() + (1.3 * Math.sin(angle));
            location = GeneralMethods.getTargetedLocation(player, 1);
            origin = GeneralMethods.getTargetedLocation(player, 1);
            direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x, centre.getY(), z));
            ParticleEffect.SPELL_INSTANT.display(new Location(centre.getWorld(), x, centre.getY(), z), 1, 0F, 0F, 0F, 0);
            double x1 = centre.getX() + (1.3 * Math.cos(angle));
            double z1 = centre.getZ() + (1.3 * Math.sin(angle));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("74ADFF", new Location(centre.getWorld(), x1, centre.getY(), z1));
            ParticleEffect.SPELL_INSTANT.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 1, 0F, 0F, 0F, 0);
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
            //GeneralMethods.displayColoredParticle("000000", new Location(centre.getWorld(), x, centre.getY(), z));
            //GeneralMethods.displayColoredParticle("EEEEEE", new Location(centre.getWorld(), x1, centre.getY(), z1));
            GeneralMethods.displayColoredParticle("FFFFFF", centre);
            ParticleEffect.ENCHANTMENT_TABLE.display(new Location(centre.getWorld(), x1, centre.getY(), z1), 2, (float) Math.random() / 2, (float) Math.random() / 2, (float) Math.random() / 2, 0.2F);
            centre.getWorld().playSound(centre, Sound.BLOCK_CHORUS_FLOWER_GROW, 0.1F, 0.1F);
            if (p >= 360) {
                p = 0;
            }
            p++;
        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.ForceField.VirtusVeil.Enabled");
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
        return "This ability allows ForceFieldbenders to channel light into a veil, harming anyone in its path!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift -> Release-Shift when it's charged up! -";
    }

    @Override
    public String getName() {
        return "VirtusVeil";
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
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}