package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class TheSkiesDescend extends CosmicAbility implements AddonAbility {

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

    private double t = 0.7853981633974483D;

    private Location origin;
    private Location location;
    private Location target;
    private Vector direction;

    private boolean firing;
    private int knockUp;

    public TheSkiesDescend(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, TheSkiesDescend.class)) {
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.TheSkiesDescend.Range");
            Block block = player.getTargetBlockExact(range, FluidCollisionMode.NEVER);
            if (block != null) {
                this.target = block.getLocation();

                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.TheSkiesDescend.Cooldown");
                this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.TheSkiesDescend.Damage");
                this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.TheSkiesDescend.Speed") * (ProjectKorra.time_step / 1000F);
                this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.TheSkiesDescend.CollisionRadius");
                this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.TheSkiesDescend.ExplosionRadius");
                this.delay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.TheSkiesDescend.Delay");
                this.knockUp = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.TheSkiesDescend.KnockUp");
                this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.TheSkiesDescend.BlockRevertTime");
                this.slowDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.TheSkiesDescend.LevitationDuration");
                this.slowPower = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.TheSkiesDescend.LevitationPower");

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
                origin = player.getLocation().add(0, range, 0);
                location = origin.clone();
                direction = PCMethods.createDirectionalVector(origin, target);
                ParticleEffect.EXPLOSION_HUGE.display(origin, 3, 1, 1, 1);
                ParticleEffect.FLASH.display(origin, 3, 1, 1,1);
                return;
            }
            //new sounds
            target.getWorld().playSound(target, Sound.ENTITY_HORSE_BREATHE, 0.25f, 0.8F);
            target.getWorld().playSound(target, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1.25f);
            for (int i = 0; i <= 360; i++) {
                double x,z;
                x = explosionRadius * Math.cos(i);
                z = explosionRadius * Math.sin(i);

                Location loc = target.clone().add(x, 1, z);

                if (ThreadLocalRandom.current().nextInt(10) == 0) {
                    ParticleEffect.END_ROD.display(loc, 1, 0, 0, 0, 0.02);
                    ParticleEffect.SMOKE_NORMAL.display(loc, 1, 0.2, 0.2, 0.2, 0);
                }

            }
        } else {
            //new sounds
            location.getWorld().playSound(location, Sound.ENTITY_VEX_HURT, 5, 0.7f);
            location.getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 2, 0);
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

                BlockData bData = Bukkit.createBlockData(Material.CRYING_OBSIDIAN);
                ParticleEffect.BLOCK_DUST.display(location, 2, radius/2, radius/2, radius/2, bData);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    new ColoredParticle(Color.fromRGB(13, 0, 56), 2.4f).display(location, 4, radius/2,radius/2,radius/2);
                    new ColoredParticle(Color.fromRGB(45, 0, 130), 2.4f).display(location, 4, radius/3,radius/3,radius/3);
                    new ColoredParticle(Color.fromRGB(66, 0, 188), 2.4f).display(location, 4, radius/2,radius/2,radius/2);
                } else {
                    new ColoredParticle(Color.fromRGB(72, 49, 175), 2.4f).display(location, 4, radius/2,radius/2,radius/2);
                    new ColoredParticle(Color.fromRGB(80, 78, 196), 2.4f).display(location, 4, radius/3,radius/3,radius/3);
                    new ColoredParticle(Color.fromRGB(109, 133, 255), 2.4f).display(location, 4, radius/2,radius/2,radius/2);
                }

                    ParticleEffect.END_ROD.display(location, 6, 0,0,0, 0.04f);
                bData = Bukkit.createBlockData(Material.CRYING_OBSIDIAN);
                ParticleEffect.BLOCK_DUST.display(location, 3, 0.5, 0.5, 0.5, bData);
                ParticleEffect.SQUID_INK.display(location, 3, 0.1, 0.1, 0.1);
                omens();
                ParticleEffect.FLASH.display(location, 1, 0, 0, 0);

                location.add(direction.normalize().multiply(speed/3));
            }
        }
    }

    private void omens() {
        Location centre = location;
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (4.5 * Math.cos(angle));
        double z = centre.getZ() + (4.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        ParticleEffect.END_ROD.display(loc, 2, 0, 0, 0, 0.04);

        double x2 = centre.getX() + (4.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (4.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        ParticleEffect.END_ROD.display(loc2, 2, 0, 0, 0, 0.04);

        pstage++;
    }

    private void explode() {
        ParticleEffect.EXPLOSION_HUGE.display(location, 3, 1, 1, 1);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10.3F, 0.65f);
            location.getWorld().playSound(location, Sound.ITEM_TRIDENT_THUNDER, 10.3F, 1.15f);
            location.getWorld().playSound(location, Sound.ENTITY_WITHER_HURT, 10.3F, 0f);
        } else {
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10.3F, 0.65f);
            location.getWorld().playSound(location, Sound.ITEM_TRIDENT_THUNDER, 10.3F, 1.55f);
            location.getWorld().playSound(location, Sound.ITEM_TRIDENT_RETURN, 10.3F, 0.76f);
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, explosionRadius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, player, damage, this);
                GeneralMethods.setVelocity(this, entity, new Vector(0, knockUp, 0));

                if (!((LivingEntity) entity).hasPotionEffect(PotionEffectType.LEVITATION)) {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, slowDuration, slowPower, false, false, false));
                }
            }
        }
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, explosionRadius * 1.5)) {
            if (GeneralMethods.isSolid(block) && isAir(block.getRelative(BlockFace.UP).getType())) {
                TempBlock tb = new TempBlock(block.getRelative(BlockFace.SELF), Material.MAGMA_BLOCK);
                tb.setRevertTime(blockRevertTime);

                if (ThreadLocalRandom.current().nextInt(4) == 0) {
                    ParticleEffect.SMOKE_LARGE.display(block.getRelative(BlockFace.UP).getLocation(), 18, 0,0,0,0.17);
                    ParticleEffect.END_ROD.display(block.getRelative(BlockFace.UP).getLocation(), 18, 0,0,0,0.17);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.TheSkiesDescend.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "TheSkiesDescend";
    }

    @Override
    public Location getLocation() {
        return location != null ? location : null;
    }

    public String getDescription() {
        return "TheSkiesDescent is a master level cosmicbending ability. It allows cosmicbenders to summon a constellation's worth of fury " +
                "accelerating down to the ground, which deals damage to enemies, knocks them up and melts the ground beneath!";
    }

    @Override
    public String getInstructions() {
        return "*Tap Shift* while aiming at the ground";
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
