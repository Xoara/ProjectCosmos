package com.xoarasol.projectcosmos.abilities.firebending.combustionbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class CombustingBeam extends CombustionAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private int range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute("ExplosionRadius")
    private double explosionRadius;
    private long delay;
    private long blockRevertTime;
    private int slowPower;
    private int slowDuration;
    private int pstage;
    private int casts, maxCasts;

    private double t = 0.7853981633974483D;

    private Location origin;
    private Location location;
    private Location target;
    private Vector direction;

    private boolean firing;
    private int an;

    public CombustingBeam(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, CombustingBeam.class)) {
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Fire.Combustion.CombustingBeam.Range");
            Block block = player.getTargetBlockExact(range, FluidCollisionMode.NEVER);
            if (block != null) {
                this.target = block.getLocation();

                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Combustion.CombustingBeam.Cooldown");
                this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combustion.CombustingBeam.Damage");
                this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combustion.CombustingBeam.Speed") * (ProjectKorra.time_step / 1000F);
                this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combustion.CombustingBeam.CollisionRadius");
                this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Combustion.CombustingBeam.ExplosionRadius");
                this.delay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Combustion.CombustingBeam.Delay");
                this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Combustion.CombustingBeam.BlockRevertTime");
                this.maxCasts = 2;
                this.slowDuration = 0;
                this.slowPower = 0;

                this.firing = false;

                start();
            }
        }
    }

    private boolean unbreakable(Block block) {
        return getConfig().getStringList("Abilities.Generic.Unbreakables").contains(block.getType().toString());
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (casts >= maxCasts + 1) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (!firing) {
            if (System.currentTimeMillis() > delay + getStartTime()) {
                this.bPlayer.addCooldown(this);
                firing = true;
                origin = player.getLocation().add(0, 10, 0);
                location = origin.clone();
                direction = PCMethods.createDirectionalVector(origin, target);
                ParticleEffect.EXPLOSION_LARGE.display(origin, 3, 1, 1, 1);
                ParticleEffect.FLASH.display(origin, 3, 1, 1,1);
                return;
            }
            //new sounds
            target.getWorld().playSound(target, Sound.ENTITY_HORSE_BREATHE, 0.25f, 0.8F);
            for (int i = 0; i <= 360; i++) {
                double x,z;
                x = explosionRadius * Math.cos(i);
                z = explosionRadius * Math.sin(i);

                Location loc = target.clone().add(x, 1, z);

                if (ThreadLocalRandom.current().nextInt(10) == 0) {
                    ParticleEffect.SMOKE_LARGE.display(loc, 1, 0.2, 0.2, 0.2, 0);
                }

            }
        } else {
            location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 5, 0);
            location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 5, 0);
            for (int i = 0; i <= 3; i++) {
                if (GeneralMethods.isSolid(location.getBlock()) || isWater(location.getBlock())) {
                    explode();
                    location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5, 0);
                    location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 5, 0);
                    ParticleEffect.EXPLOSION_HUGE.display(location, 1, 0, 0, 0, 0);
                    return;
                }

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, radius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        explode();
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5, 0);
                        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 5, 0);
                        ParticleEffect.EXPLOSION_HUGE.display(location, 1, 0, 0, 0, 0);
                        break;
                    }
                }
                if (this.getBendingPlayer().canUseSubElement(Element.BLUE_FIRE)) {
                    ParticleEffect.SOUL_FIRE_FLAME.display(location, 2, radius/2, radius/2, radius/2);
                } else {
                    ParticleEffect.FLAME.display(location, 2, radius / 2, radius / 2, radius / 2);
                }

                ParticleEffect.FIREWORKS_SPARK.display(location, 6, 0,0,0, 0f);
                Spirals();

                ParticleEffect.FLASH.display(location, 1, 0, 0, 0);

                location.add(direction.normalize().multiply(speed/3));
            }
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
                double r = d * -this.explosionRadius / 1.0D;
                if (r > 1.0D)
                    r = 1.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        ParticleEffect.SMOKE_LARGE.display(pl, 2, 0, 0, 0, 0.04f);
                        break;
                    case 1:

                        ParticleEffect.SMOKE_LARGE.display(pl, 2, 0, 0, 0, 0.04f);
                        break;
                }
            }
        }
    }

    private void explode() {
        for (Block blocks : GeneralMethods.getBlocksAroundPoint(location, explosionRadius)) {
            for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.getLocation(), explosionRadius)) {
                if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                    DamageHandler.damageEntity(e, damage, this);
                }
            }
            if (this.unbreakable(blocks)) {
                return;
            } else {
                if (this.getBendingPlayer().canUseSubElement(Element.BLUE_FIRE)) {
                    ParticleEffect.SOUL_FIRE_FLAME.display(location, 1, 2, 2, 2, 0.05f);
                } else {
                    ParticleEffect.FLAME.display(location, 1, 2, 2, 2, 0.05f);
                }
                ParticleEffect.SMOKE_LARGE.display(location, 1, 2, 2, 2, 0.05f);

                TempBlock affected = new TempBlock(blocks, Material.AIR);
                affected.setRevertTime(blockRevertTime);
            }
        }
        remove();
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Fire.Combustion.CombustingBeam.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "CombustingBeam";
    }

    @Override
    public Location getLocation() {
        return location != null ? location : null;
    }

    public String getDescription() {
        return "A Master Combustionbender is able to to summon a beam from above, which explode on impact!";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click on the ground!- ";
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
