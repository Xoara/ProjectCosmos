package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class LaserFission extends LaserAbility implements AddonAbility {

    private long cooldown;
    private long range;
    private double damage;
    private double collisionRadius;
    private int maxBounces;
    @Attribute("Speed")
    private double speed;
    private double speedMod;

    public float radius = 1F;
    public float grow = 0;
    public double radials = 0.19634954084936207D;
    public int circles = 3;
    public int helixes = 4;
    protected int step = 0;

    private int bounces;
    private int blueCounter;

    private Location origin;
    private Location location;
    private Vector direction;

    private boolean hasReached = false;
    Random rand = new Random();

    public LaserFission(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserFission.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.LaserFission.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserFission.Damage");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserFission.CollisionRadius");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserFission.Speed");
            this.maxBounces = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.LaserFission.MaxBounces");
            this.speedMod = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.LaserFission.SpeedModifier");

            this.bounces = 0;

            this.blueCounter = rand.nextInt(49) / 5;

            this.origin = this.player.getEyeLocation();
            this.location = origin.clone();
            this.direction = this.player.getLocation().getDirection();

            this.speed = (this.speed * (ProjectKorra.time_step / 1000.0));

            this.bPlayer.addCooldown(this);
            lasergrid();
            this.start();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

            lasergrid();
        }
    }

    @Override
    public void progress() {
        if (!PCMethods.generalBendCheck(this, this.player)) {
            this.remove();
            return;
        }

        for (int i = 0; i <= 2; i++) {
            if (this.origin.distance(this.location) >= this.range) {
                this.remove();
                return;
            }

            if (GeneralMethods.isSolid(this.location.getBlock())) {
                if (bounces >= maxBounces) {
                    remove();
                    return;
                }
                bounces++;
                reflect();
            }

            if (isWater(location.getBlock())) {
                if (!WaterSpout.getAffectedBlocks().containsKey(location.getBlock())) {
                    if (bounces >= maxBounces) {
                        remove();
                        return;
                    }
                    bounces++;
                    reflect();
                }
            }

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

            new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 2.0f).display(this.location, 3, 0, 0, 0);

            this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 1.3f);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 1.5f);
            this.location.add(this.direction.clone().multiply(speed / 2));

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.collisionRadius)) {
                if (entity != this.player) {
                    DamageHandler.damageEntity(entity, this.player, this.damage, this);
                    this.remove();
                }
            }
        }
    }

    public void lasergrid() {
        Location location = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(1.2D));
        location.add(0.0D, 0.3D, 0.0D);
        for (int x = 0; x < this.circles; x++) {
            for (int i = 0; i < this.helixes; i++) {
                double angle = this.step * this.radials + 6.283185307179586D * i / this.helixes;
                Vector v = new Vector(Math.cos(angle) * this.radius, (this.step * this.grow),
                        Math.sin(angle) * this.radius);
                rotateAroundAxisX(v, ((location.getPitch() + 90.0F) * 0.017453292F));
                rotateAroundAxisY(v, (-location.getYaw() * 0.017453292F));
                location.add(v);

                new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 1.8f).display(location, 2, 0, 0, 0);
                ParticleEffect.FIREWORKS_SPARK.display(location, 1, 0, 0, 0, 0.07f);

                location.subtract(v);
            }
            this.step++;
        }
    }

    public static final Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static final Vector rotateAroundAxisX(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }


    private void reflect() {
        Vector newDir = direction.clone();

        Block block = this.getLocation().getBlock();
        ArrayList<Block> blocks = new ArrayList<>(0);
        blocks.add(block.getRelative(BlockFace.DOWN));
        blocks.add(block.getRelative(BlockFace.UP));
        blocks.add(block.getRelative(BlockFace.NORTH));
        blocks.add(block.getRelative(BlockFace.SOUTH));
        blocks.add(block.getRelative(BlockFace.EAST));
        blocks.add(block.getRelative(BlockFace.WEST));
        Vector tmpDirection = this.location.getDirection().clone().multiply(0.1D);
        Location tmpLocation = this.location.clone().subtract(tmpDirection);
        while (tmpLocation.getBlock().getLocation().equals(block.getLocation()))
            tmpLocation.subtract(tmpDirection);
        int i = 0;
        for (Block b : blocks) {
            if (tmpLocation.getBlock().getLocation().equals(b.getLocation()))
                break;
            i++;
        }
        if ((((i == 0) ? 1 : 0) | ((i == 1) ? 1 : 0)) != 0) {
            newDir.setY(newDir.getY() * -1.0D);
        } else if ((((i == 2) ? 1 : 0) | ((i == 3) ? 1 : 0)) != 0) {
            newDir.setZ(newDir.getZ() * -1.0D);
        } else {
            newDir.setX(newDir.getX() * -1.0D);
        }

        setDirection(newDir);
        this.speed *= this.speedMod;
        this.origin = this.location.clone();
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
    public double getCollisionRadius() {
        return collisionRadius;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.LaserFission.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "LaserFission";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return "LaserFission is the most basic ability at a Laserbenders's disposal. " +
                "Whenever this move comes into contact with a solid surface, it will reflect, and gain speed and range!";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click! -";
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
