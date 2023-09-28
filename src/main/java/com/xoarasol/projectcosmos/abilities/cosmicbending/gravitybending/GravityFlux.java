package com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.GravityAbility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class GravityFlux extends GravityAbility implements AddonAbility {

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
    private int knockUp;

    private double t = 0.7853981633974483D;

    private Location origin;
    private Location location;
    private Location target;
    private Vector direction;

    private boolean firing;

    public GravityFlux(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, GravityFlux.class)) {
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityFlux.Range");
            Block block = player.getTargetBlockExact(range, FluidCollisionMode.NEVER);
            if (block != null) {
                this.target = block.getLocation();

                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GravityFlux.Cooldown");
                this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityFlux.Damage");
                this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.TheSkiesDescend.Speed") * (ProjectKorra.time_step / 1000F);
                this.knockUp = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityFlux.KnockUp");
                this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityFlux.CollisionRadius");
                this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityFlux.ExplosionRadius");
                this.delay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GravityFlux.Delay");
                this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GravityFlux.BlockRevertTime");
                this.slowDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityFlux.LevitationDuration");
                this.slowPower = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityFlux.LevitationPower");

                this.firing = false;

                start();
            }
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (!firing) {
            if (System.currentTimeMillis() > delay + getStartTime()) {
                this.bPlayer.addCooldown(this);
                firing = true;
                origin = player.getLocation().add(0, 4, 0);
                location = origin.clone();
                direction = PCMethods.createDirectionalVector(origin, target);
                return;
            }
            //new sounds
            target.getWorld().playSound(target, Sound.ENTITY_WITHER_AMBIENT, 0.25f, 0.8F);
            for (int i = 0; i <= 360; i++) {
                double x,z;
                x = explosionRadius * Math.cos(i);
                z = explosionRadius * Math.sin(i);

                Location loc = target.clone().add(x, 1, z);

                if (ThreadLocalRandom.current().nextInt(30) == 0) {
                    ParticleEffect.END_ROD.display(loc, 3, 0, 5, 0, 0.02);
                    ParticleEffect.SMOKE_LARGE.display(loc, 3, 0, 5, 0, 0, 0.02);
                    if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                        new ColoredParticle(Color.fromRGB(13, 0, 56), 2.4f).display(loc, 3, 0, 5, 0);
                        new ColoredParticle(Color.fromRGB(45, 0, 130), 2.4f).display(loc, 3, 0, 5, 0);
                        new ColoredParticle(Color.fromRGB(66, 0, 188), 2.4f).display(loc, 3, 0, 5, 0);
                    } else {
                        new ColoredParticle(Color.fromRGB(72, 49, 175), 2.4f).display(loc, 3, 0, 5, 0);
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 2.4f).display(loc, 3, 0, 5, 0);
                        new ColoredParticle(Color.fromRGB(109, 133, 255), 2.4f).display(loc, 3, 0, 5, 0);
                    }
                }

            }
        } else {
            //new sounds
            location.getWorld().playSound(location, Sound.ENTITY_WITHER_HURT, 5, 0.7f);
            for (int i = 0; i <= 3; i++) {
                if (GeneralMethods.isSolid(location.getBlock()) || isWater(location.getBlock())) {
                    explode();
                    return;
                }

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, radius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        explode();
                        break;
                    }
                }
                location.add(direction.normalize().multiply(speed/3));
            }
        }
    }


    private void explode() {
        // new sounds
        ParticleEffect.EXPLOSION_HUGE.display(location, 3, 1, 1, 1);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10.3F, 0.65f);
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 10.3F, 0.0f);
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, explosionRadius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, player, damage, this);
                GeneralMethods.setVelocity(this, entity, new Vector(0, knockUp, 0));

                if (!((LivingEntity) entity).hasPotionEffect(PotionEffectType.LEVITATION)) {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, slowDuration, slowPower, false, false, false));
                }
            }
        }
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, explosionRadius * 1.0)) {
            if (GeneralMethods.isSolid(block) && isAir(block.getRelative(BlockFace.UP).getType())) {
                TempBlock tb = new TempBlock(block.getRelative(BlockFace.SELF), Material.SCULK);
                tb.setRevertTime(blockRevertTime);

                if (ThreadLocalRandom.current().nextInt(4) == 0) {
                    ParticleEffect.SMOKE_LARGE.display(block.getRelative(BlockFace.UP).getLocation(), 18, 0,0,0,0.17);
                    ParticleEffect.END_ROD.display(block.getRelative(BlockFace.UP).getLocation(), 18, 0,0,0,0.17);
                    ParticleEffect.SONIC_BOOM.display(block.getRelative(BlockFace.UP).getLocation(), 4, 0,0,0,0.17);
                }
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.GravityFlux.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "GravityFlux";
    }

    @Override
    public Location getLocation() {
        return location != null ? location : null;
    }

    public String getDescription() {
        return "A simple but potent skill at a Cosmicbender's diasposal, the ability to rip foes from the ground and let them crash back down!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift > Left-Click while looking at a block! -";
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
