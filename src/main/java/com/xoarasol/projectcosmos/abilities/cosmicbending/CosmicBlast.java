package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.*;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CosmicBlast extends CosmicAbility implements AddonAbility {

    private enum CollisionType {
        AIR,
        EARTH,
        WATER;
    }

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.DAMAGE)
    private double speed;
    @Attribute(Attribute.RADIUS)
    private double collisionRadius;

    private double knockback;
    private double explosionRadius;


    private boolean isCollision;
    private CollisionType collisionType;

    private ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();
    private ArrayList<UUID> affected = new ArrayList<>();
    private ArrayList<Location> streams = new ArrayList<>();
    private ArrayList<TempBlock> tempBlocks = new ArrayList<>();

    public CosmicBlast(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmicBlast.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.Speed") * (ProjectKorra.time_step / 1000F);
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.CollisionRadius");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.CollisionKnockback");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBlast.ExplosionRadius");


            this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
            this.location = this.origin.clone();
            this.direction = this.player.getLocation().getDirection();

            this.isCollision = false;

            this.bPlayer.addCooldown(this);
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }

        direction = this.player.getLocation().getDirection();
        if (!isCollision) {
            if (this.rand.nextInt(2) == 0) {

                this.location.getWorld().playSound(this.location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.6F, 1.45F);

            }
            for (int i = 0; i <= 2; i++) {

                if (this.origin.distance(this.location) > this.range) {
                    remove();
                    return;
                }
                this.location.add(direction.normalize().multiply(speed / 2));

                ParticleEffect.END_ROD.display(this.location, 1, 0.5, 0.5, 0.5, 0f);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getLeftSide(this.location, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(this.location, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(this.location, 2, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getRightSide(this.location, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getRightSide(this.location, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

                } else {
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getLeftSide(this.location, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(this.location, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(this.location, 2, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getRightSide(this.location, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getRightSide(this.location, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

                }

                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    remove();
                    return;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.collisionRadius)) {
                    if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                        DamageHandler.damageEntity(entity, player, this.damage, this);
                        remove();
                        return;
                    }
                }
            }
        } else {
            if (collisionType == CollisionType.AIR) {
                for (int i = 0; i <= 2; i++) {
                    if (this.origin.distance(this.location) > this.range) {
                        remove();
                        return;
                    }
                    this.location.add(direction.normalize().multiply(speed / 2));
                    ParticleEffect.SPELL.display(location, 2, 0,0,0);
                    ParticleEffect.CLOUD.display(location, 5, collisionRadius/2,collisionRadius/2,collisionRadius/2);
                    AirAbility.playAirbendingSound(location);

                    if (GeneralMethods.isSolid(this.location.getBlock())) {
                        remove();
                        return;
                    }
                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.collisionRadius)) {
                        if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                            DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                            GeneralMethods.setVelocity(this, entity, direction.normalize().multiply(knockback));
                            remove();
                            return;
                        }
                    }
                }
            } else if (collisionType == CollisionType.EARTH) {
                if (fallingBlocks.isEmpty()) {
                    remove();
                    return;
                }

                ArrayList<FallingBlock> fBlocks = fallingBlocks;
                for (FallingBlock fallingBlock : fBlocks) {
                    new ColoredParticle(Color.fromRGB(0, 0, 0), 0.6F).display(fallingBlock.getLocation(), 1, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(20, 30, 80), 0.6F).display(fallingBlock.getLocation(), 1, 0.2, 0.2, 0.2);
                    new ColoredParticle(Color.fromRGB(20, 30, 140), 0.6F).display(fallingBlock.getLocation(), 1, 0.2, 0.2, 0.2);

                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fallingBlock.getLocation(), 1.5)) {
                        if (entity instanceof  LivingEntity && !entity.getUniqueId().equals(player.getUniqueId()) && !affected.contains(entity.getUniqueId())) {
                            DamageHandler.damageEntity(entity, player, damage, this);
                            fallingBlocks.remove(fallingBlock);
                        }
                    }
                }
            } else if (collisionType == CollisionType.WATER) {
                ArrayList<Location> toRemove = new ArrayList<>();
                for (Location loc : streams) {
                    loc.add(direction.normalize().multiply(speed));

                    if (!isAir(loc.getBlock().getType())) {
                        toRemove.add(loc);
                        return;
                    }

                    TempBlock tb = new TempBlock(loc.getBlock(), Material.WATER);
                    tb.setRevertTime(300);
                    tempBlocks.add(tb);

                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1.5)) {
                        if (entity instanceof  LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                            DamageHandler.damageEntity(entity, player, damage, this);
                            remove();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void collide(CoreAbility ability) {
        player.sendMessage("CosmicBlast Collision called");
        if (ability instanceof AirAbility) {
            collisionType = collisionType.AIR;

            AirAbility.playAirbendingSound(player.getLocation());
            ParticleEffect.SPELL.display(player.getLocation(), 5, 0.2,0.2,0.2, 0.005);
            AirAbility.playAirbendingSound(player.getLocation());

        } else if (ability instanceof EarthAbility) {
            collisionType = CollisionType.EARTH;
            Location temp = location.clone();
            int yaw = Math.round(temp.clone().getYaw());

            for (int i = 0; i < 10; i++) {
                temp.setYaw(yaw + (ThreadLocalRandom.current().nextInt(50) - 30));
                temp.setPitch(ThreadLocalRandom.current().nextInt(40) - 20);

                Vector v = temp.clone().add(0, 1, 0).getDirection().normalize();
                Location temp2 = temp.clone().add(new Vector(v.getX() * 2, v.getY(), v.getZ() * 2));
                temp2.setDirection(location.getDirection()).getDirection().normalize().multiply(1);

                FallingBlock fallingBlock = temp2.getWorld().spawnFallingBlock(temp2, Bukkit.createBlockData(Material.STONE));
                fallingBlock.setMetadata("cosmicblast", new FixedMetadataValue(ProjectCosmos.plugin, this));
                fallingBlock.setDropItem(false);
                fallingBlocks.add(fallingBlock);
            }
        } else if (ability instanceof FireAbility) {
            ParticleEffect.EXPLOSION_LARGE.display(location, 3, explosionRadius/2, explosionRadius/2, explosionRadius/2);
            ParticleEffect.FLAME.display(location, 10, explosionRadius/2, explosionRadius/2, explosionRadius/2, 0.05);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, explosionRadius)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, player, damage, this);

                    Vector knock = PCMethods.createDirectionalVector(location, ((LivingEntity) entity).getEyeLocation());
                    GeneralMethods.setVelocity(this, entity, knock.normalize().multiply(knockback));
                }
            }
            for (Block block : GeneralMethods.getBlocksAroundPoint(location, explosionRadius)) {
                if (GeneralMethods.isSolid(block) && isAir(block.getRelative(BlockFace.UP).getType())) {
                    if (ThreadLocalRandom.current().nextInt(10) == 0) {
                        Block up = block.getRelative(BlockFace.UP);
                        TempBlock fire = new TempBlock(up, Material.FIRE);
                        fire.setRevertTime(1000);
                    }
                }
            }
            remove();
        } else if (ability instanceof WaterAbility) {
            collisionType = CollisionType.WATER;

            WaterAbility.playWaterbendingSound(player.getLocation());
            ParticleEffect.WATER_SPLASH.display(player.getLocation(), 5, 0.2,0.2,0.2, 0.005);
            WaterAbility.playWaterbendingSound(player.getLocation());

            for (Block block : GeneralMethods.getBlocksAroundPoint(location, collisionRadius * 1.5)) {
                if (isAir(block.getType()) && ThreadLocalRandom.current().nextInt(3) == 0) {
                    streams.add(block.getLocation());
                }
            }
        }
    }

    @Override
    public void remove() {
        for (TempBlock tb : tempBlocks) {
            tb.revertBlock();
        }
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
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.CosmicBlast.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getName() {
        return "CosmicBlast";
    }

    @Override
    public String getDescription() {
        return "A basic Cosmic ability! Shoot a cosmic blast at your enemies. This cosmic ability has great defensive capabilities, and reacts differently when colliding with abilities from other elements.";
    }

    @Override
    public String getInstructions() {
        return "- Use Left-Click! -";
    }

    @Override
    public String getAuthor() {
        return "KWilson272 & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }
}
