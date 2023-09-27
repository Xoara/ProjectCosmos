package com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GravityManipulation extends GravityAbility implements AddonAbility {

    private enum BlockEffect {
        DAMAGE,
        FIRE,
        SLOW,
        NORMAL;
    }

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SELECT_RANGE)
    private int sourceRange;
    @Attribute("LaunchPower")
    private double launchPower;
    @Attribute(Attribute.RADIUS)
    private double collisionRadius;
    @Attribute(Attribute.KNOCKBACK)
    private double knockback;
    private int revertTime;
    private double animationSpeed;
    private int fireTicks;
    private int slownessDuration;

    private Location origin;
    private Location targetLoc;
    private Location location;

    private double distance;

    private Block sourceBlock;
    private FallingBlock fBlock;

    private boolean isFired;

    private BlockEffect blockEffect;

    public GravityManipulation(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !this.player.isSneaking()) {

            //Run the block check before initializing any other variables to prevent initializing what would ultimately end up unused
            this.sourceRange = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityManipulation.SourceRange");
            this.sourceBlock = player.getTargetBlockExact(sourceRange, FluidCollisionMode.NEVER);

            if (sourceBlock == null) {
                return;
            }

            //Only will continue if the source block is solid.
            if (GeneralMethods.isSolid(sourceBlock)) {
                this.blockEffect = getEffect();
                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.GravityManipulation.Cooldown");
                this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.Range");
                this.launchPower = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.LaunchPower");
                this.revertTime = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityManipulation.BlockRevertTime");
                this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.CollisionRadius");
                this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.Knockback");
                this.animationSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.AnimationSpeed");
                this.slownessDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityManipulation.SlownessDuration");
                this.fireTicks = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.GravityManipulation.FireTicks");
                this.distance = sourceBlock.getLocation().distance(player.getLocation());

                fBlock = player.getWorld().spawnFallingBlock(sourceBlock.getLocation().add(0, 0.5, 0), sourceBlock.getBlockData());
                fBlock.setDropItem(false);
                fBlock.setMetadata("gravitymanipulation", new FixedMetadataValue(ProjectCosmos.plugin, this));
                fBlock.setHurtEntities(false);
                fBlock.setGravity(true);

                TempBlock tb = new TempBlock(sourceBlock, Material.AIR);
                tb.setRevertTime(revertTime);

                location = fBlock.getLocation();
                this.targetLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(distance));

                this.isFired = false;

                if (blockEffect.equals(BlockEffect.DAMAGE)) {
                    this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.ExtraDamage");
                } else {
                    this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.GravityManipulation.Damage");
                }

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
        if (System.currentTimeMillis() > 8000 + getStartTime()) {
            remove();
            return;
        }
        if (!isFired) {
            if (!this.bPlayer.getBoundAbilityName().equals(getName())) {
                remove();
                return;
            }
            if (!this.player.isSneaking()) {
                isFired = true;
                origin = fBlock.getLocation();
                this.bPlayer.addCooldown(this);
                player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0);
                fBlock.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(launchPower));
                return;
            }

            if (fBlock.isDead()) {
                respawnFallingBlock();
            }
            targetLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(distance));
            location = fBlock.getLocation();

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                new ColoredParticle(Color.fromRGB(66, 0, 188), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                new ColoredParticle(Color.fromRGB(45, 0, 130), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                new ColoredParticle(Color.fromRGB(13, 0, 56), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
            } else {
                new ColoredParticle(Color.fromRGB(109, 133, 255), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                new ColoredParticle(Color.fromRGB(80, 78, 196), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                new ColoredParticle(Color.fromRGB(72, 49, 175), 1.05F).display(location, 2, 0.2, 0.2, 0.2);

            }

            Vector vec = targetLoc.clone().subtract(location).toVector().multiply(animationSpeed);

            if (vec.getY() < 0 && aboveGroundCheck()) {
                vec.setY(0);
            }
            fBlock.setVelocity(vec);

        } else {
            for (int i = 0; i <= 5; i++) {
                location = fBlock.getLocation();

                if (location.distance(origin) > range) {
                    ParticleEffect.BLOCK_DUST.display(location, 10, 0.1, 0.1, 0.1, fBlock.getBlockData());
                    remove();
                    return;
                }
                if (fBlock.isDead()) {
                    remove();
                    return;
                }

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    new ColoredParticle(Color.fromRGB(66, 0, 188), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(45, 0, 130), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(13, 0, 56), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                } else {
                    new ColoredParticle(Color.fromRGB(109, 133, 255), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(80, 78, 196), 1.05F).display(location, 2, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(72, 49, 175), 1.05F).display(location, 2, 0.2, 0.2, 0.2);

                }


                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, collisionRadius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        DamageHandler.damageEntity(entity, player, damage, this);
                        GeneralMethods.setVelocity(this, entity, fBlock.getLocation().getDirection().normalize().multiply(knockback));
                        ParticleEffect.BLOCK_DUST.display(location, 10, 0.1, 0.1, 0.1, fBlock.getBlockData());
                        affect((LivingEntity) entity);
                        remove();
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void remove() {
        fBlock.remove();
        super.remove();
    }

    private boolean aboveGroundCheck() {
        if (location.getY() % 1 > 0.1) {
            return false;
        }

        List<Vector> relativeBlockEdges = new ArrayList<>(Arrays.asList(
                new Vector(-0.5, 0, -0.5),
                new Vector(0.5, 0, -0.5),
                new Vector(0.5, 0, 0.5),
                new Vector(-0.5, 0, 0.5)
        ));

        for (Vector relativeEdge : relativeBlockEdges) {

            Location blockEdge = location.clone().add(relativeEdge);
            blockEdge.subtract(0, 0.1, 0);

            if (blockEdge.getBlock().getType().isSolid())
                return true;
        }

        return false;
    }

    private void respawnFallingBlock() {
        Location loc = fBlock.getLocation();
        fBlock = player.getWorld().spawnFallingBlock(loc, sourceBlock.getBlockData());
        fBlock.setDropItem(false);
        fBlock.setMetadata("gravitymanipulation", new FixedMetadataValue(ProjectCosmos.plugin, this));
        fBlock.setHurtEntities(false);
        fBlock.setGravity(true);
    }

    private BlockEffect getEffect() {
        if (ProjectCosmos.plugin.getConfig().getList("Abilities.Cosmic.GravityManipulation.AffectingBlocks.DamageBlocks").contains(sourceBlock.getType().toString())) {
            return BlockEffect.DAMAGE;
        } else if (ProjectCosmos.plugin.getConfig().getList("Abilities.Cosmic.GravityManipulation.AffectingBlocks.FireBlocks").contains(sourceBlock.getType().toString())) {
            return BlockEffect.FIRE;
        } else if (ProjectCosmos.plugin.getConfig().getList("Abilities.Cosmic.GravityManipulation.AffectingBlocks.SlowBlocks").contains(sourceBlock.getType().toString())) {
            return BlockEffect.SLOW;
        } else {
            return BlockEffect.NORMAL;
        }
    }

    private void affect(LivingEntity entity) {
        if (blockEffect == BlockEffect.FIRE) {
            entity.setFireTicks(fireTicks);
        } else if (blockEffect == BlockEffect.SLOW) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration, 1, false, false, false));
        } else {
            return;
        }
    }

    public void reverse() {
        Vector vec = PCMethods.createDirectionalVector(fBlock.getLocation(), player.getEyeLocation());

        if (!isFired) {
            isFired = true;
            origin = fBlock.getLocation();
            this.bPlayer.addCooldown(this);
            player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

        }

        fBlock.setVelocity(vec.normalize().multiply(launchPower));
    }

    public static void reverse(Player player) {
        if (CoreAbility.hasAbility(player, GravityManipulation.class)) {
            GravityManipulation gManip = CoreAbility.getAbility(player, GravityManipulation.class);
            gManip.reverse();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.GravityManipulation.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "GravityManipulation";
    }

    @Override
    public Location getLocation() {
        return fBlock != null ? fBlock.getLocation() : null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public String getDescription() {
        return "GravityManipulation is one of Cosmicbending's most primitive moves. At will, Cosmicbenders are able to manipulate the gravity of the blocks around them, and toss them at great speeds.\n" +
                "Left-Click to send the GravityManipulation backwards!";
    }


    @Override
    public String getInstructions() {
        return "- Hold-Shift at a block to source, and Release-Shift to launch the block. -";
    }
}
