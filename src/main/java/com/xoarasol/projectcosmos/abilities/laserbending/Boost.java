package com.xoarasol.projectcosmos.abilities.laserbending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Boost extends LaserAbility implements AddonAbility {

    private long cooldown;
    private long time;
    private long duration;
    private double speed;

    public Boost(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, Boost.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Boost.Cooldown");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Boost.Power");
            this.duration = 200;
            this.time = System.currentTimeMillis();

            start();
        }
    }

    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.bPlayer.addCooldown(this);
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.time + this.duration) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }
        this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8F, 0.83F);
        this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 0.8F, 0.55F);

        ParticleEffect.FIREWORKS_SPARK.display(GeneralMethods.getRightSide(this.player.getLocation(), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0, 0.04f);
        ParticleEffect.FIREWORKS_SPARK.display(GeneralMethods.getLeftSide(this.player.getLocation(), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0, 0.04f);

        (new ColoredParticle(Color.fromRGB(255, 0, 140), 3.5F)).display(GeneralMethods.getRightSide(this.player.getLocation(), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0);
        (new ColoredParticle(Color.fromRGB(255, 0, 140), 3.5F)).display(GeneralMethods.getLeftSide(this.player.getLocation(), 0.55).add(this.player.getVelocity().clone()), 2, 0, 0, 0);

        if (GeneralMethods.isSolid(this.player.getLocation().getBlock())) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }

        Vector velocity = this.player.getEyeLocation().getDirection().clone().normalize().multiply(this.speed);
        extinguish(this.player);
        this.player.setVelocity(velocity);
        this.player.setFallDistance(0.0F);
        this.player.setFireTicks(0);
    }

    public static boolean extinguish(final Player player) {
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) {
            return true;
        } else if (bPlayer.getBoundAbilityName().equals("Boost") || hasAbility(player, Boost.class)) {
            player.setFireTicks(-1);
            return false;
        }
        return true;
    }

//    public static void disengage (Player player) {
//        if (CoreAbility.hasAbility(player, Boost.class)) {
//            Boost boost = CoreAbility.getAbility(player, Boost.class);
//            boost.remove();
//        }
//    }

    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public void remove() {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
        this.flightHandler.removeInstance(this.player, this.getName());
        this.player.setFallDistance(0);
        super.remove();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Boost.Enabled");
    }

    public Location getLocation() {
        return (this.player != null) ? this.player.getLocation() : null;
    }

    public String getName() {
        return "Boost";
    }

    public boolean isHarmlessAbility() {
        return true;
    }

    public boolean isSneakAbility() {
        return false;
    }

    public String getDescription() {
        return "This simple Laser Ability allows Laserbenders to dash in a desired direction using small Lasers as boosters!";
    }

    public String getInstructions() {
        return "- LeftClick! -";
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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
