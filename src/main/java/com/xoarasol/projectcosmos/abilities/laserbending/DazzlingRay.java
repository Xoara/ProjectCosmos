package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
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

import java.util.Random;


public class DazzlingRay extends LaserAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private int cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.SPEED)
    private double speed;
    
    private boolean hasReached = true;
    private boolean launched;
    private boolean charged;
    private double angle;
    
    private Vector direction;
    private Location origin;
    private Location location;
    private double rotation;
    private int counter;

    private int currPoint;
    private double knockback;
    private int an;

    public DazzlingRay(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.DazzlingRay.Cooldown");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.DazzlingRay.Speed");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.DazzlingRay.Damage");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.DazzlingRay.Knockback");
            this.direction = this.player.getLocation().getDirection();
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.DazzlingRay.Range");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.DazzlingRay.ChargeTime");
            }

            this.speed *= (ProjectKorra.time_step / 1000F);

            start();

            rotation = 0;
            counter = 0;
        }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            if (charged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + chargeTime) {
            charged = true;
        }
        if (player.isSneaking() && (charged)) {
            if (new Random().nextInt(8) == 0) {
                location.getWorld().playSound(location, Sound.ENTITY_HORSE_BREATHE, 0.5F, 0F);
            }
        }
        if ((player.isSneaking()) && (!launched)) {
            inhale();
            focusEffect();
        } else {
            if (!charged) {
                remove();
                return;
            }
            if (!launched) {
                bPlayer.addCooldown(this);
                launched = true;
            }
            if (GeneralMethods.isSolid(location.getBlock())) {
                remove();
                bPlayer.addCooldown(this);
                return;
            }
            if (GeneralMethods.isRegionProtectedFromBuild(player, "DazzlingRay", location)) {
                remove();
                return;
            }
            Ray();
        }
    }

    private void Ray() {
        if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "DazzlingRay", this.location)) {
            this.origin = this.player.getEyeLocation().clone().add(0, 1, 0);
            this.direction =  this.player.getEyeLocation().getDirection();
            this.location.add(this.direction.clone().multiply(this.speed));

            (new ColoredParticle(Color.fromRGB(255, 0, 140), 4.0F)).display(this.location, 2, 0.3D, 0.0D, 0.3D);
            (new ColoredParticle(Color.fromRGB(255, 20, 0), 4.0F)).display(this.location, 2, 0.3D, 0.1D, 0.3D);

            Spirals();

            ParticleEffect.FIREWORKS_SPARK.display(this.location, 16, 0.2D, 0.2D, 0.2D, 0.05f);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.6F, 1.4F);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.6F, 1.75F);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_GENERIC_EXPLODE, 0.6F, 1.3F);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6F, 1.89F);
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1.5D)) {
                if (entity instanceof LivingEntity && entity.getEntityId() != this.player.getEntityId() && !(entity instanceof org.bukkit.entity.ArmorStand)) {
                    DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                    entity.setVelocity(this.direction.multiply(this.knockback));

                }
            }
        }


        if (this.location.getBlock().isLiquid()) {
            ParticleEffect.FLASH.display(this.location.getBlock().getLocation(), 2, 0.2D, 0.2D, 0.2D);
            this.location.getBlock().getWorld().playSound(this.location.getBlock().getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 2.0F, 0.75F);
            remove();
            this.bPlayer.addCooldown((Ability)this);
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            this.bPlayer.addCooldown((Ability)this);
            return;
        }
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 1.0D) {
                Location l = this.location.clone();
                double r = d * -1.5D / 1.0D;
                if (r > 2D)
                    r = 2D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        ParticleEffect.FIREWORKS_SPARK.display(pl, 4, 0.1, 0.1, 0.1, 0.03);

                        break;
                    case 1:

                        ParticleEffect.FIREWORKS_SPARK.display(pl, 4, 0.1, 0.1, 0.1, 0.03);
                        break;
                }
            }
        }
    }


    public void inhale () {
        if (charged) {
            lightRing(60, 1.5f, 3);
            lightRing(60, 1.3f, 3);
            angle -= 3;

        }
    }

    public void focusEffect() {
        if (hasReached) {
            location = GeneralMethods.getTargetedLocation(player, 0.5);
            origin = GeneralMethods.getTargetedLocation(player, 0.5);


        }
    }

    private void lightRing(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 1D, z);
            new ColoredParticle(Color.fromRGB(255, 0, 20), 1.8f).display(loc, 1, 0, 0, 0);
            ParticleEffect.FIREWORKS_SPARK.display(loc, 1, 0.1, 0.1, 0.1, 0.03);

        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }


    public Location getOrigin() {
        return origin;
    }

    @Override
    public String getName() {
        return "DazzlingRay";
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Laserbenders are able to channel their energy using their vocals! After inhaling, they scream, with the scream they send out a blast of dazzling lasers! Enemies struck will be knocked back far!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift -> Release-Shift when it's charged up! -";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.DazzlingRay.Enabled");
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
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
