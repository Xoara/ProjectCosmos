package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.SolarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Users may edit, copy, distribute and manage this project. They will not have
 * permission to change this project's credits and claim it as theirs. All
 * rights are reserved to the Author.
 *
 * @author dario (_Hetag1216_)
 */
public class Magnetar extends SolarAbility implements AddonAbility {

    private double currentHeight;
    private double currentRadius;
    private long cooldown, chargeTime;
    private double damage, push, radius, fakepstage;
    private int pstage;
    private boolean respect_particles, isCharged, hasReached;
    private double charge_radius, charge_height;
    private int currPoint;
    private Location tempo;

    private Location origin;
    private Map<Integer, Integer> angles;
    private double maxHeight;

    public Magnetar(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {
            cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.SolarCyclone.Cooldown");
            //
            this.respect_particles = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Solar.SolarCyclone.ChargeAnimation.RespectParticles");
            this.charge_height = 3;
            this.charge_radius = 2;
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.SolarCyclone.ChargeTime");
            //
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.SolarCyclone.Radius");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.SolarCyclone.Damage");
            this.push = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.SolarCyclone.Knockback");

            this.currentHeight = 2;
            this.maxHeight = 35;
            this.currentRadius = this.currentHeight / 20 * 8;
            this.origin = player.getTargetBlock((HashSet<Material>) null, (int) 20).getLocation();
            this.origin.setY(this.origin.getY() - 1.0 / 10.0 * this.currentHeight);
            fakepstage = -1;

            this.angles = new ConcurrentHashMap<>();
            this.tempo = this.origin.clone();

            int angle = 0;
            for (int i = 0; i <= this.maxHeight; i += (int) this.maxHeight / 13) { //stream count 8
                this.angles.put(i, angle);
                angle += 90;
                if (angle == 360) {
                    angle = 0;
                }
            }
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (this.player.getEyeLocation().getBlock().isLiquid() || !player.isSneaking() || !this.bPlayer.canBend(this)) {
            this.bPlayer.addCooldown(this);
            this.remove();
            return;
        }
        if (4000 != 0) {
            if (this.getStartTime() + 4000 <= System.currentTimeMillis()) {
                this.bPlayer.addCooldown(this);
                this.remove();
                return;
            }
        }
        this.rotateTornado();
        if (!this.isCharged) {
            if (!this.player.isSneaking()) {
                remove();
                return;
            }
            if (respect_particles) {
                if (this.hasReached && System.currentTimeMillis() >= this.getStartTime() + chargeTime) {
                    this.isCharged = true;
                }
            } else {
                if (System.currentTimeMillis() >= this.getStartTime() + chargeTime) {
                    this.isCharged = true;
                }
            }
            if (charge_radius > 0) {
                this.charge_radius -= 0.05;
            }
            if (charge_height > 0) {
                this.charge_height -= 0.05;
            }
            if (charge_radius <= 0) {
                if (charge_height > 0) {
                    charge_height -= 0.1;
                }
            } else if (charge_height <= 0) {
                if (charge_radius > 0) {
                    charge_height -= 0.1;
                }
            }
            if (charge_height <= 0 && charge_radius <= 0) {
                this.hasReached = true;
            }
            this.displayCharge();
        } else {
            this.displayCharged();

        }
    }

    private void displayCharge() {
        Location centre = player.getLocation();
        double increment = 3 * Math.PI / 360;
        double angle = fakepstage * increment;
        double x = centre.getX() + (charge_radius * Math.cos(angle));
        double y = centre.getY() + (charge_height * Math.ceil(-angle));
        double z = centre.getZ() + (charge_radius * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, y, z);

        (new ColoredParticle(Color.fromRGB(255, 230, 147),  1.4F)).display(loc, 2, 0.05, 0.05, 0.05);

        chargeRing(60, 1.3f, 3);
        chargeRing2(60, 1.5f, 4);
        chargeRing3(60, 1.7f, 5);

        double x2 = centre.getX() + (charge_radius * -Math.cos(angle));
        double y2 = centre.getY() + (charge_height * Math.ceil(-angle));
        double z2 = centre.getZ() + (charge_radius * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, y2, z2);

        (new ColoredParticle(Color.fromRGB(255, 230, 147),  1.4F)).display(loc2, 2, 0.05, 0.05, 0.05);

        loc2.getWorld().playSound(loc2, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 0.86F);
        fakepstage--;
    }
    private void displayCharged() {
        for (int i = 2; i < 4; i++) {
            Location centre = player.getLocation();
            double increment = ((Math.random() * 3) * Math.PI) / 360;
            double angle = pstage * increment;
            double x = centre.getX() + (radius * Math.cos(angle));
            double y = centre.getY() + Math.ceil(angle);
            double z = centre.getZ() + (radius * Math.sin(angle));
            Location loc = new Location(centre.getWorld(), x, y, z);

            ParticleEffect.END_ROD.display(loc, 1, 0.3f, 0.3f, 0.3f, 0.09f);
            ParticleEffect.END_ROD.display(loc, 1, 0.3f, 0.3f, 0.3f, 0.08f);
            storm(60, (float) this.radius, 6);
            storm2(60, (float) this.radius, 6);
            storm3(60, (float) this.radius, 6);

            double x2 = centre.getX() + (radius * -Math.cos(angle));
            double y2 = centre.getY() + Math.ceil(angle);
            double z2 = centre.getZ() + (radius * -Math.sin(angle));
            Location loc2 = new Location(centre.getWorld(), x2, y2, z2);

            ParticleEffect.END_ROD.display(loc2, 1, 0.3f, 0.3f, 0.3f, 0.08f);
            ParticleEffect.END_ROD.display(loc2, 1, 0.3f, 0.3f, 0.3f, 0.08f);

            loc2.getWorld().playSound(loc2, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 0.6F);
            loc2.getWorld().playSound(loc2, Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 0.01F);
            loc2.getWorld().playSound(loc2, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.6F, 0.85F);
            affect(this.getLocation());
            pstage += i * i;
            if (pstage >= 300) {
                bPlayer.addCooldown(this);
                return;
            }
        }
    }
    private void chargeRing(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.4D, z);
            (new ColoredParticle(Color.fromRGB(255, 199, 0), 1.4F)).display(loc, 2, 0.4, 0.1, 0.4);

        }
    }

    private void chargeRing2(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.4D, z);
            (new ColoredParticle(Color.fromRGB(255, 216, 89), 1.4F)).display(loc, 2, 0.4, 0.1, 0.4);

        }
    }
    private void chargeRing3(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.4D, z);
            (new ColoredParticle(Color.fromRGB(255, 230, 147),  1.4F)).display(loc, 2, 0.4, 0.1, 0.4);

        }
    }

    private void rotateTornado() {
        this.origin = player.getLocation().add(0,-1,0);
        final double timefactor = this.currentHeight / this.maxHeight;
        this.currentRadius = timefactor * 8;

        //if (!ElementalAbility.isAir(this.origin.getBlock().getType()) && this.origin.getBlock().getType() != Material.BARRIER) {

        for (final Entity entity : origin.getWorld().getNearbyEntities(origin,25,25,25)) {
            if (GeneralMethods.isRegionProtectedFromBuild(this, entity.getLocation())) {
                continue;
            }
            if(entity.getUniqueId() == player.getUniqueId()){
                continue;
            }
            final double y = entity.getLocation().getY();
            double factor;
            if (y > this.origin.getY() && y < this.origin.getY() + this.currentHeight) {
                factor = (y - this.origin.getY()) / this.currentHeight;
                final Location testloc = new Location(this.origin.getWorld(), this.origin.getX(), y, this.origin.getZ());
                if (testloc.getWorld().equals(entity.getWorld()) && testloc.distance(entity.getLocation()) < this.currentRadius * factor) {
                    double x, z, vx, vz, mag;
                    double angle = 100;
                    double vy = 0.7 * 1; //pushfactor
                    angle = Math.toRadians(angle);

                    x = entity.getLocation().getX() - this.origin.getX();
                    z = entity.getLocation().getZ() - this.origin.getZ();

                    mag = Math.sqrt(x * x + z * z);

                    if (mag == 0.0) {
                        vx = 0.0;
                        vz = 0.0;
                    } else {
                        vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
                        vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;
                    }

                    if (entity instanceof Player) {
                        vy = 0.05 * 1; //pushfactor
                    }

                    if (entity.getEntityId() == this.player.getEntityId()) {
                        final Vector direction = this.player.getEyeLocation().getDirection().clone().normalize();
                        vx = direction.getX();
                        vz = direction.getZ();
                        final Location playerloc = this.player.getLocation();
                        final double py = playerloc.getY();
                        final double oy = this.origin.getY();
                        final double dy = py - oy;

                        if (dy >= this.currentHeight * .95) {
                            vy = 0;
                        } else if (dy >= this.currentHeight * .85) {
                            vy = 6.0 * (.95 - dy / this.currentHeight);
                        } else {
                            vy = .6;
                        }
                    }

                    if (entity instanceof Player) {
                        if (Commands.invincible.contains(((Player) entity).getName())) {
                            continue;
                        }
                    }

                    final Vector velocity = entity.getVelocity().clone();
                    velocity.setX(vx);
                    velocity.setZ(vz);
                    velocity.setY(1);
                    velocity.multiply(timefactor);
                    GeneralMethods.setVelocity(entity, velocity);
                    entity.setFallDistance(0);
                    DamageHandler.damageEntity(entity,player,2,this);
                    entity.setFireTicks(5);

                }
            }
        }

        for (final int i : this.angles.keySet()) {
            double x, y, z, factor;
            double angle = this.angles.get(i);
            angle = Math.toRadians(angle);

            y = this.origin.getY() + timefactor * i;
            factor = i / this.currentHeight;

            x = this.origin.getX() + timefactor * factor * this.currentRadius * Math.cos(angle);
            z = this.origin.getZ() + timefactor * factor * this.currentRadius * Math.sin(angle);

            final Location effect = new Location(this.origin.getWorld(), x, y, z);
            if (!GeneralMethods.isRegionProtectedFromBuild(this, effect)) {

                // ParticleEffect.FLAME.display(effect, ThreadLocalRandom.current().nextInt(0,50),.125,.125,.125,.1);
                (new ColoredParticle(Color.fromRGB(255, 199, 0), 1.0F)).display(effect, 3, 0.1, 0.1, 0.1);
                (new ColoredParticle(Color.fromRGB(255, 216, 89), 1.0F)).display(effect, 3, 0.1, 0.1, 0.1);
                (new ColoredParticle(Color.fromRGB(255, 230, 147), 1.0F)).display(effect, 3, 0.1, 0.1, 0.1);
                ParticleEffect.END_ROD.display(effect, 0, 1, 1, 1, 0.07f);

                for (Entity e : GeneralMethods.getEntitiesAroundPoint(effect, radius)) {
                    if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                        ((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 1));
                    }
                }
            }
            this.angles.put(i, this.angles.get(i) + 25 * (int) 1); //speed 1
        }
        this.currentHeight = this.currentHeight > this.maxHeight ? this.maxHeight : this.currentHeight + 1;
    }


    private void storm(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 1.8D, z);
            (new ColoredParticle(Color.fromRGB(255, 199, 0), 2.0F)).display(loc, 2, 0.1, 1, 0.1);

        }
    }

    private void storm2(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.3D, z);
            (new ColoredParticle(Color.fromRGB(255, 199, 0), 2.0F)).display(loc, 3, 0.1, 1, 0.1);

        }
    }

    private void storm3(int points, float size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 3.5D, z);
            (new ColoredParticle(Color.fromRGB(255, 199, 0), 2.0F)).display(loc, 2, 0.1, 1, 0.1);

        }
    }

    private void affect(Location location) {
        for (Entity e : GeneralMethods.getEntitiesAroundPoint(location, radius)) {
            if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                DamageHandler.damageEntity(e, damage, this);
                double x = e.getLocation().getX() - player.getLocation().getX();
                double y = e.getLocation().getY() - player.getLocation().getY();
                double z = e.getLocation().getZ() - player.getLocation().getZ();
                Vector v = new Vector(x, y, z).normalize().multiply(this.push);
                e.setVelocity(v);
            }
        }
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
        return "SolarCyclone";
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Solar.SolarCyclone.Enabled");
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public String getDescription() {
        return "Using this Ability, Solarbenders are able to summon dangerous flares of solar energy, forming a cyclone around them!";
    }

    @Override
    public String getInstructions() {
        return "*Hold Shift*";
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