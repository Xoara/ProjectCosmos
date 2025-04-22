package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
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
public class GalacticRipple extends CosmicAbility implements AddonAbility {

    private long cooldown, time;
    private int pstage;
    private double maxRadius, radius, r_inc, interval, damage, knockback;

    public GalacticRipple(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && this.player.isSneaking() && !CoreAbility.hasAbility(player, GalacticRipple.class)) {
            maxRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GalacticRipple.Radius.MaxRadius");
            cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GalacticRipple.Cooldown");
            r_inc = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GalacticRipple.Radius.Increment");
            interval = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GalacticRipple.Radius.Interval");
            damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GalacticRipple.Damage");
            knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GalacticRipple.Knockback");

            time = System.currentTimeMillis();
            radius = 0.5;

            player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 5, 2);
            start();
        }
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() >= time + interval) {
            if (radius < maxRadius) {
                time = System.currentTimeMillis();
                radius += r_inc;
            } else {
                remove();
                bPlayer.addCooldown(this);
                return;
            }
        }
        showRing();
        for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.getLocation(), radius)) {
            if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                DamageHandler.damageEntity(e, damage, this);
                double x = e.getLocation().getX() - player.getLocation().getX();
                double y = e.getLocation().getY() - player.getLocation().getY();
                double z = e.getLocation().getZ() - player.getLocation().getZ();
                Vector v = new Vector(x, y, z).normalize().multiply(knockback);
                e.setVelocity(v);
            }
        }
    }

    private void showRing() {
        Location centre = player.getLocation();
        double increment = (1 * Math.PI) / 360;
        double angle = pstage * increment;
        double x = centre.getX() + (radius * Math.cos(angle));
        double y = centre.getY() + 0.75;
        double z = centre.getZ() + (radius * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, y, z);

        ParticleEffect.END_ROD.display(loc, 2, 0, 0.5, 0, 0f);


        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getLeftSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(loc, 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getRightSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            ParticleEffect.SQUID_INK.display(loc, 3, 0, 0, 0, 0.02);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getLeftSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(loc, 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getRightSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            ParticleEffect.CLOUD.display(loc, 3, 0, 0, 0, 0.02);
        }

        double x2 = centre.getX() + (radius * -Math.cos(angle));
        double y2 = centre.getY() + 0.75;
        double z2 = centre.getZ() + (radius * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, y2, z2);

        ParticleEffect.END_ROD.display(loc2, 2, 0, 0.5, 0, 0);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getLeftSide(loc2, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(loc2, 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getRightSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getRightSide(loc2, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            ParticleEffect.SQUID_INK.display(loc2, 3, 0, 0, 0, 0.02);
            loc2.getWorld().playSound(loc2, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 0.62F);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getLeftSide(loc2, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(loc2, 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getRightSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getRightSide(loc2, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            ParticleEffect.CLOUD.display(loc2, 3, 0, 0, 0, 0.02);
            loc2.getWorld().playSound(loc2, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 1.22F);
        }
        pstage += 15;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return player != null ? player.getLocation() : null;
    }

    @Override
    public String getName() {
        return "GalacticRipple";
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
    public String getDescription() {
        return "This ability allows cosmicbenders to expand and grow their cosmic energy as far as outside their body to keep enemies at a distance.\n" +
                "Struck enemies are knocked back and damaged.";
    }

    @Override
    public String getInstructions() {
        return "*Hold Shift* > *Left Click*";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.GalacticRipple.Enabled");
    }

    @Override
    public void remove() {
        super.remove();
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