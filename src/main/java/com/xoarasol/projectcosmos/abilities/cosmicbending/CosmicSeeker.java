package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CosmicSeeker extends CosmicAbility implements AddonAbility {

    private double t = 0.7853981633974483D;
    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();

    private float radius, fake_radius;

    private long cooldown;
    private double range;
    private double damage;
    private int glowDuration;
    private int pstage;
    private int phase;

    public CosmicSeeker(Player player) {
        super(player);

        if (this.bPlayer.isOnCooldown(this)) {
            return;
        }
        setFields();
        start();
        location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 0.56F);
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_CHARGE, 1, 0.66F);
        this.bPlayer.addCooldown(this);
    }

    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmicSeeker.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmicSeeker.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmicSeeker.Damage");
        this.glowDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmicSeeker.GlowDuration");

        this.radius = 6;

        this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        this.location = this.origin.clone();
        this.direction = this.player.getLocation().getDirection();
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }
        this.direction = this.player.getLocation().getDirection();
        this.location.add(direction.normalize().multiply(1));

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.68F)).display(location, 3, 0.05, 0.05, 0.05);

        } else {

            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.68F)).display(location, 3, 0.05, 0.05, 0.05);

        }

        Arcs();

        for (int i = 0; i < 1; i++) {
            double x = 0.3 * Math.sin(Math.toRadians(this.phase));
            double y = 0.3 * Math.cos(Math.toRadians(this.phase));
            double z = 0.0D;
            Vector vector = new Vector(x, y, z);
            vector = vector.multiply(4.0D);
            double yaw = Math.toRadians(-this.location.getYaw());
            double pitch = Math.toRadians(this.location.getPitch());
            double oldX = vector.getX();
            double oldY = vector.getY();
            double oldZ = vector.getZ();
            vector.setY(oldY * Math.cos(pitch) - oldZ * Math.sin(pitch));
            vector.setZ(oldY * Math.sin(pitch) + oldZ * Math.cos(pitch));
            oldZ = vector.getZ();
            vector.setX(oldX * Math.cos(yaw) + oldZ * Math.sin(yaw));
            vector.setZ(-oldX * Math.sin(yaw) + oldZ * Math.cos(yaw));
            this.location.add(vector);

            ParticleEffect.END_ROD.display(this.location, 3, 0, 0, 0, 0.02);

            this.location.subtract(vector);
            if (ThreadLocalRandom.current().nextInt(6) == 0)

                this.location.add(this.direction.normalize().multiply(3).multiply(0.5D / 3));
            this.phase += 11;


            this.pstage++;


            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                this.location.getWorld().playSound(this.location, Sound.ENTITY_WITHER_AMBIENT, 0.2F, 0.76F);
            } else {
                this.location.getWorld().playSound(this.location, Sound.ENTITY_WITHER_AMBIENT, 0.2F, 1.76F);
            }
            if (GeneralMethods.isSolid(this.location.getBlock())) {
                remove();
                return;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1.5)) {
                if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                    DamageHandler.damageEntity(entity, this.damage, (Ability) this);

                    if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 2.0F, 0F);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 2.0F, 0F);
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITHER_HURT, 2.0F, 0F);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 2.0F, 0F);
                    } else {
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 2.0F, 0F);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 2.0F, 0F);
                        entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_TRIDENT_RETURN, 2.0F, 0.6F);
                        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 2.0F, 0.6F);
                    }
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, this.glowDuration, 1));
                    shockwave(entity.getLocation());
                    remove();
                    return;
                }
            }
        }
    }

    private void shockwave(Location center) {
        final World world = center.getWorld();
        final int maxRadius = 5;
        final int stepsPerRadius = 50; // smoothness of each ring
        final int delayPerStep = 2; // ticks between rings (~0.1s)

        new BukkitRunnable() {
            int currentRadius = 1;

            @Override
            public void run() {
                if (currentRadius > maxRadius) {
                    cancel();
                    return;
                }

                for (int i = 0; i < stepsPerRadius; i++) {
                    double angle = 2 * Math.PI * i / stepsPerRadius;
                    double x = Math.cos(angle) * currentRadius;
                    double z = Math.sin(angle) * currentRadius;

                    Location particleLoc = center.clone().add(x, 1, z);
                    world.spawnParticle(Particle.END_ROD, particleLoc, 2, 0.1, 0, 0.1, 0.03);

                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(particleLoc, 0.75)) {
                        if (entity instanceof LivingEntity &&
                                !entity.getUniqueId().equals(player.getUniqueId())) {

                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(
                                    PotionEffectType.GLOWING,
                                    glowDuration,
                                    1
                            ));
                        }
                    }
                }

                currentRadius++;
            }
        }.runTaskTimer(ProjectCosmos.plugin, 0L, delayPerStep);
    }

    private void Arcs() {
        Location centre = location;
        double increment = (1.3 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.3 * Math.cos(angle));
        double z = centre.getZ() + (1.3 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + -0.4, z);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.205F).display(loc, 2, 0.2, 0.2, 0.2);
            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.205F).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.205F).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.205F).display(loc, 2, 0.2, 0.2, 0.2);
            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.205F).display(GeneralMethods.getLeftSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.205F).display(GeneralMethods.getRightSide(loc, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

        }

        double x2 = centre.getX() + (1.3 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.3 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.4, z2);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.205F).display(loc2, 2, 0.2, 0.2, 0.2);
            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.205F).display(GeneralMethods.getLeftSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.205F).display(GeneralMethods.getRightSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.205F).display(loc2, 2, 0.2, 0.2, 0.2);
            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.205F).display(GeneralMethods.getLeftSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.205F).display(GeneralMethods.getRightSide(loc2, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
        }

        pstage++;
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.CosmicSeeker.Enabled");
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return null;
    }

    public String getName() {
        return "CosmicSeeker";
    }

    public String getDescription() {
        return "This ability allows cosmicbenders to fire a long ranged cosmic spiral to deal damage and mark an enemy. \n" +
                "Marked enemies will glow, revealing their location for a duration.";
    }

    public String getInstructions() {
        return "*Tap Shift* to fire!";
    }

    public String getAuthor() {
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
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

}
