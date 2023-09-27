package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class StellarBreath extends CosmicAbility implements AddonAbility {

    private long time;

    private Vector direction;

    private long cooldown;
    private long duration;
    private int particles;

    private boolean coolLava;
    private boolean extinguishFire;
    private boolean extinguishMobs;

    private boolean damageEnabled;
    private double playerDamage;
    private double mobDamage;

    private double knockback;
    private int range;
    private LivingEntity target;
    private double launch;

    private boolean regenOxygen;
    private int an;
    private int pstage;


    public StellarBreath(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
            return;
        }

        setFields();
        time = System.currentTimeMillis();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.8f, 1.8f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.8f, 1.8f);
        start();
    }

    public void setFields() {

        cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StellarBreath.Cooldown");
        duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StellarBreath.Duration");
        particles = 2;
        coolLava = false;
        extinguishFire = false;
        extinguishMobs = false;
        damageEnabled = true;
        playerDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.StellarBreath.Damage");
        mobDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.StellarBreath.Damage");
        knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.StellarBreath.Knockback");
        range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.StellarBreath.Range");
        launch = 1.3;
        this.direction = this.player.getLocation().getDirection();

    }

    @Override
    public void progress() {
        if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }
        if (!(bPlayer.getBoundAbility() instanceof StellarBreath)) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!player.isSneaking()) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (System.currentTimeMillis() < time + duration) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1.3f);
            createBeam();
        } else {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        return;
    }

    private boolean isLocationSafe(Location loc) {
        Block block = loc.getBlock();
        if (GeneralMethods.isRegionProtectedFromBuild(player, "StellarBreath", loc)) {
            return false;
        }
        if (!isTransparent(block)) {
            return false;
        }
        return true;
    }

    private void createBeam() {
        Location loc = player.getEyeLocation();
        Vector dir = player.getLocation().getDirection();
        double step = 1;
        double size = 0;
        double damageregion = 1.5;

        for (double i = 0; i < range; i += step) {
            loc = loc.add(dir.clone().multiply(step));
            size += 0.005;
            damageregion += 0.01;

            if (!isLocationSafe(loc)) {
                if (!isTransparent(loc.getBlock())) {
                    if (player.getLocation().getPitch() > 30) {
                        //player.setVelocity(player.getLocation().getDirection().multiply(-launch));
                    }
                }
                return;
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, damageregion)) {
                if (entity.getEntityId() != player.getEntityId() && !(entity instanceof ArmorStand)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this, entity.getLocation()) || ((entity instanceof Player) && Commands.invincible.contains(((Player) entity).getName()))){
                        continue;
                    }
                    if (entity instanceof LivingEntity) {
                        if (damageEnabled) {
                            if (entity instanceof Player)
                                DamageHandler.damageEntity(entity, playerDamage, this);
                            else
                                DamageHandler.damageEntity(entity, mobDamage, this);
                        }

                        if (extinguishMobs)
                            entity.setFireTicks(0);
                    }

                    dir.multiply(knockback);
                    entity.setVelocity(dir);
                }
            }

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                (new ColoredParticle(Color.fromRGB(13, 0, 56), 0.9F)).display(GeneralMethods.getLeftSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(45, 0, 130), 0.9F)).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(66, 0, 188), 0.9F)).display(loc, 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(45, 0, 130), 0.9F)).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(13, 0, 56), 0.9F)).display(GeneralMethods.getRightSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            } else {
                (new ColoredParticle(Color.fromRGB(72, 49, 175), 0.9F)).display(GeneralMethods.getLeftSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(80, 78, 196), 0.9F)).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(109, 133, 255), 0.9F)).display(loc, 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(80, 78, 196), 0.9F)).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(72, 49, 175), 0.9F)).display(GeneralMethods.getRightSide(loc, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            }

            ParticleEffect.WHITE_ASH.display(loc, 4, 0, 0, 0, 0f);
            }
        }


    /*
     * @Override public void remove() { if (player.isOnline()) {
     * bPlayer.addCooldown("AirBreath", cooldown); } super.remove(); }
     */

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getName() {
        return "StellarBreath";
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
        return "With this ability, you can breathe out a stream of cosmic light to damage your targets!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift! -";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.StellarBreath.Enabled");
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