package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class PhotonPunch extends LaserAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    private double entityKnockback;
    private double selfKnockback;

    private boolean hasPunched;

    private int blueCounter;
    private boolean hasReached;

    private Location origin;
    private Location location;
    private Vector direction;

    public PhotonPunch(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, PhotonPunch.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.PhotonPunch.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.PhotonPunch.Damage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.PhotonPunch.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.PhotonPunch.Speed");
            this.entityKnockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.PhotonPunch.EntityKnockback");
            this.selfKnockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.PhotonPunch.SelfKnockback");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.PhotonPunch.RadianceDuration");

            this.hasPunched = false;

            this.speed *= (ProjectKorra.time_step / 1000F);

            Random rand = new Random();
            this.blueCounter = rand.nextInt(49) / 5;
            this.hasReached = false;
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }
        if (!this.hasPunched) {
            new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 1).display(GeneralMethods.getMainHandLocation(this.player).add(this.player.getLocation().getDirection().normalize().multiply(0.8)), 1, 0, 0, 0);

            if (this.hasReached) {
                this.blueCounter -= 5;
                if (this.blueCounter <= 0) {
                    this.blueCounter = 0;
                    this.hasReached = false;
                }
            } else {
                this.blueCounter += 5;
                if (this.blueCounter >= 255) {
                    this.blueCounter = 255;
                    this.hasReached = true;
                }
            }
        } else {
            for (int i = 0; i <= 5; i++) {
                if (GeneralMethods.isSolid(this.location.getBlock()) || isWater(this.location.getBlock()) || this.location.distance(this.origin) > this.range) {
                    remove();
                    return;
                }
                new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 3).display(this.location, 2, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(255, this.blueCounter, this.blueCounter), 1).display(this.location, 2, 0.1, 0.1, 0.1);
                ParticleEffect.FIREWORKS_SPARK.display(this.location, 3, 0, 0, 0, 0.1, new Particle.DustOptions(Color.fromRGB(255, 10, 10), 2));

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1)) {
                    if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                        GeneralMethods.setVelocity(this, entity, this.direction.clone().normalize().multiply(this.entityKnockback));
                        DamageHandler.damageEntity(entity, this.player, this.damage, this);
                        setRadiance((LivingEntity) entity, this.radianceDuration);
                    }
                }
                this.location.add(this.direction.clone().normalize().multiply(this.speed / 5));

            }
        }
    }

    public void punch() {
        if (!this.hasPunched) {
            this.hasPunched = true;
            this.bPlayer.addCooldown(this);
            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();
            this.direction = this.player.getEyeLocation().getDirection();

            GeneralMethods.setVelocity(this, this.player, this.direction.clone().normalize().multiply(-1 * this.selfKnockback));
            player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);
            player.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 0.92f);
            player.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1.71f);
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, PhotonPunch.class)) {
            PhotonPunch lp = CoreAbility.getAbility(player, PhotonPunch.class);
            lp.punch();
        }
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
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.PhotonPunch.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "PhotonPunch";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public String getDescription() {
        return "Laserbenders can focus large amounts of energy into their fists, and unleash the stored power on living things. " +
                "The raw energy output will send both the user and the victim flying back.";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift > Left-Click! -";
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
