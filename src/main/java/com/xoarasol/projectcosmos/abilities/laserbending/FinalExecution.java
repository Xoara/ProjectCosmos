package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
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
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FinalExecution extends LaserAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("DamageInterval")
    private long damageInterval;
    @Attribute("StartingRadius")
    private double startingRadius;
    @Attribute("FinalRadius")
    private double finalRadius;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute("RadianceDuration")
    private int radianceDuration;

    private double recoil;
    private double formDistance;
    private boolean damageBlocks;
    private boolean revertBlocks;
    private long revertTime;

    private TempBlock tempBlock;
    private Location stream;
    private Location origin;
    private Location location;
    private Vector direction;
    private Location direction1;

    private double curRadius;
    private double radialIncrement;
    private boolean isCharged;

    private long time;
    private int curPoint;

    private boolean hasReached = false;
    private int blueCounter;
    Random rand = new Random();

    public FinalExecution(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, FinalExecution.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.FinalExecution.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.Damage");
            this.damageInterval = 500;
            this.startingRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.StartingRadius");
            this.finalRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.FinalRadius");
            this.duration = 200;
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.FinalExecution.ChargeTime");
            this.recoil = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.Recoil");
            this.formDistance = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.FinalExecution.FormDistance");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.FinalExecution.RadianceDuration");

            this.blueCounter = rand.nextInt(49) / 5;

            this.location = player.getLocation().add(0, 1, 0);
            this.direction = player.getLocation().getDirection();

            this.curRadius = startingRadius - 3;
            this.radialIncrement = (finalRadius / curRadius) / ((this.duration / 1000F) * 20);
            this.isCharged = false;

            time = System.currentTimeMillis() - damageInterval;

            start();
        }
    }

    static Material[] unbreakables = {Material.BEDROCK, Material.BARRIER, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.ENDER_CHEST, Material.CHEST, Material.TRAPPED_CHEST};

    public static boolean isUnbreakable(Block block) {
        if (block.getState() instanceof InventoryHolder) {
            return true;
        }
        if (Arrays.asList(unbreakables).contains(block.getType())) {
            return true;
        }
        return false;
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBend(this)) {
            if (isCharged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (!this.player.isSneaking() && !isCharged) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime() + chargeTime) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!isCharged) {
            if (System.currentTimeMillis() > chargeTime + getStartTime()) {
                time = System.currentTimeMillis();
                isCharged = true;

                origin = player.getLocation().add(0, 1, 0);
                origin.add(origin.getDirection().normalize().multiply(formDistance));

                ParticleEffect.FLASH.display(player.getLocation().add(0, 1, 0), 2, 0.2D, 0.2D, 0.2D);
                ParticleEffect.FIREWORKS_SPARK.display(player.getLocation().add(0, 1, 0), 16, 0.2D, 0.2D, 0.2D, 0.05f);

                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.ENTITY_ENDER_DRAGON_GROWL, 2F, 1.65F);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.BLOCK_BEACON_POWER_SELECT, 2F, 0.8F);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.55F);

                GeneralMethods.setVelocity(this, player, direction.clone().normalize().multiply(-1 * recoil));

                return;
            }
            warningBeam();
        } else {
            explodeBeam();
            displayOrigin();
        }
    }

    private void explodeBeam() {
        location = origin.clone();
        Location target = GeneralMethods.getTargetedLocation(player, 200);
        direction1 = this.player.getEyeLocation();
        curRadius *= (1 + radialIncrement);

        if (this.curRadius > finalRadius) {
            curRadius = finalRadius;
        }

        for (int i = 0; i <= range; i += 0.5) {
            location.add(direction.multiply(0.5).normalize());

            if (location.distance(origin) > range) {
                return;
            }

            new ColoredParticle(Color.fromRGB(255, 38, this.blueCounter), 2F).display(location, 2, 1.5, 1.5, 1.5);
            new ColoredParticle(Color.fromRGB(255, 38, 109), 2F).display(location, 2, 1.5, 1.5, 1.5);
            ParticleEffect.SMOKE_LARGE.display(location, 6, 0, 0, 0, 0.1);
            BlockData bdata = Bukkit.createBlockData(Material.MAGMA_BLOCK);
            ParticleEffect.BLOCK_CRACK.display(location, 1, 0.2, 0.2, 0.2, bdata);
            ParticleEffect.EXPLOSION_LARGE.display(location, 1, 4, 4, 4);

            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                ParticleEffect.SMOKE_LARGE.display(location, 10, 0.2, 0.2, 0.2, 0.1);
            }


            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 4)) {
                if (entity != this.player) {
                    DamageHandler.damageEntity(entity, this.player, this.damage, this);
                }
            }

            if (GeneralMethods.isSolid(location.getBlock())) {
                createExplosion(location.getBlock().getLocation(), 3, player);
                return;
            }
        }
    }
    private void explode(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_SHULKER_BULLET_HIT, 0.3F, 0.4F);
    }

    private void createExplosion(Location loc, double radius, Entity entity) {
        if (damageBlocks && !isUnbreakable(loc.getBlock()))
            if (!isTransparent(loc.getBlock()) || entity instanceof LivingEntity) {
                for (Location l : GeneralMethods.getCircle(loc, (int) radius, 0, false, true, 0)) {
                    if (!isUnbreakable(l.getBlock())) {
                        tempBlock = new TempBlock(l.getBlock(), Material.AIR);
                        explode(stream);
                        if (revertBlocks)
                            tempBlock.setRevertTime(revertTime);
                    }
                }
            }
    }
    private void warningBeam() {
        this.location = GeneralMethods.getRightSide(this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().multiply(0.36D)), 0.18D);
        this.direction = this.location.getDirection();
        for (double i = 0.0D; i < this.range / 2; i += 0.5D) {
            this.location = this.location.add(this.direction.multiply(1).normalize());
            if (GeneralMethods.isSolid(this.location.getBlock()))
                return;
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FinalExecution", this.location))
                return;
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
            new ColoredParticle(Color.fromRGB(255, 0, this.blueCounter), 1.5f).display(this.location, 1, 0,0,0);

        }
        if ((new Random()).nextInt(1) == 0)
            this.location.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1.2F);
    }


    private void displayOrigin() {
        ParticleEffect.SMOKE_LARGE.display(origin, 10, 0, 0, 0, 0.1);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.FinalExecution.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "FinalExecution";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return "Laserbenders usually use this move as an ultimate move, or as a last resort. They compress extreme amounts of energy into a singular beam," +
                "purposefully overloading themselves to release an explosion that deals immense damage!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift! -";
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
