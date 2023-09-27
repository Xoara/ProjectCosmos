package com.xoarasol.projectcosmos.abilities.laserbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reversal extends LaserAbility implements AddonAbility {

    @Attribute("Cooldown")
    private long cooldown;
    @Attribute("ChargeTime")
    private long chargeTime;
    @Attribute("Damage")
    private double damage;
    @Attribute("RadiantDamage")
    private double radiantDamage;
    @Attribute("Range")
    private double range;
    @Attribute("Speed")
    private double speed;
    @Attribute("Knockback")
    private double knockback;
    private long blockRegenDelay;
    private double collisionRadius;
    private double explosionRadius;

    private Location origin;
    private Location curLoc;
    private Vector direction;

    private boolean isCharged;
    private boolean isFired;

    private int currPoint;
    private int count;
    Random rand = new Random();

    public Reversal(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(this.player, Reversal.class)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Reversal.Cooldown");
            this.chargeTime = 1;
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.Damage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.Speed");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.Knockback");
            this.blockRegenDelay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Reversal.BlockRegenDelay");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.CollisionRadius");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Reversal.ExplosionRadius");

            this.isFired = false;
            this.isCharged = false;

            this.count = 0;

            this.speed = (this.speed * (ProjectKorra.time_step / 1000.0));


            this.start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            if (this.isCharged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (!this.isFired && !this.bPlayer.getBoundAbilityName().equalsIgnoreCase("Reversal")) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.chargeTime) {
            this.isCharged = true;
        }
        if (!this.player.isSneaking() && !this.isFired) {
            this.isFired = true;

            this.origin = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().multiply(this.range));
            this.curLoc = this.origin.clone();
        }
        if (this.isFired && !this.isCharged) {
            this.remove();
            return;
        }

        // chargeAnimation();
        progressBeam();
    }

    public void chargeAnimation() {
        List<Location> particleLocs = new ArrayList<>();
        particleLocs = PCMethods.drawSpherePoints(this.player.getEyeLocation(), 5, 5, false, true, 0);

        if (!this.isCharged) {

            //While loop gets a number of locations on/in a sphere to begin particle effects from
            int count = 0;
            while (count < 10) {

                int particleRand = rand.nextInt(particleLocs.size());
                Location particleLoc = particleLocs.get(particleRand);

                //gets the distances from the particle location to the players location to use in a pseudo-vector.
                double x = this.player.getLocation().getX() - particleLoc.getX();
                double y = this.player.getLocation().getY() - particleLoc.getY();
                double z = this.player.getLocation().getZ() - particleLoc.getZ();

                //some particles can have movement added to them by setting their count/amount to 0, and will use
                // the XYZ as a pseudo vector
                ParticleEffect.FIREWORKS_SPARK.display(particleLoc, 0, x, y, z, 0.1);
                particleLoc.getWorld().playSound(particleLoc, Sound.BLOCK_BEACON_ACTIVATE, 0.56f, 1.45f);
                count++;
            }
            return;
        }
        if (!this.isFired) {
            chargeRing();
        }
    }


    private void chargeRing() {
        for (int i = 0; i < 6; ++i) {
            currPoint += 360 / 60;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * Math.PI / 180.0D;
            double x = 1.5 * Math.cos(angle);
            double z = 1.5 * Math.sin(angle);

            Location loc = player.getLocation().add(x, 1D, z);

            double particleX, particleY, particleZ;
            particleX = loc.clone().add(0, 0.6, 0).getX() - loc.getX();
            particleY = loc.clone().add(0, 0.6, 0).getY() - loc.getY();
            particleZ = loc.clone().add(0, 0.6, 0).getZ() - loc.getZ();

            if (currPoint % 4 == 0) {
                ParticleEffect.FIREWORKS_SPARK.display(loc, 0, particleX, particleY, particleZ, 1);
            }

            (new ColoredParticle(Color.fromRGB(255, 61, 61), 1.3F)).display(loc, 1, 0, 0, 0);
            loc.getWorld().playSound(loc, Sound.BLOCK_BELL_RESONATE, 0.55f, 1.85f);
        }
    }

    public void progressBeam() {
        if (this.isFired) {

            //To get the reverse direction, we will subtract the XYZ values of our players location, from the current location
            double x = this.player.getEyeLocation().getX() - this.curLoc.getX();
            double y = this.player.getEyeLocation().getY() - this.curLoc.getY();
            double z = this.player.getEyeLocation().getZ() - this.curLoc.getZ();

            //create a new vector based off of the subtraction, which will be facing towards the player, instead of away
            this.direction = new Vector(x, y, z);

            ParticleEffect.FIREWORKS_SPARK.display(this.curLoc, 5, 0, 0, 0, 0.2);
            ParticleEffect.WHITE_ASH.display(this.curLoc, 4, 0.1, 0.1, 0.1, 1);

            new ColoredParticle(Color.fromRGB(255, 61,61), 3).display(this.curLoc, 4, 0, 0, 0);
            new ColoredParticle(Color.fromRGB(255, 61,61), 1).display(this.curLoc, 2, 0.3, 0.3, 0.3);
            this.curLoc.getWorld().playSound(this.curLoc, Sound.BLOCK_ANVIL_LAND, 1, 1.68f);
            this.curLoc.getWorld().playSound(this.curLoc, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1.45f);

            if (count % 4 == 0) {
                drawCircle();
            }
            count++;

            if (GeneralMethods.isSolid(this.curLoc.getBlock())) {
                explode();
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.curLoc, this.collisionRadius)) {
                if (!entity.equals(this.player)) {
                    explode();
                }
            }

            this.curLoc.add(this.direction.normalize().multiply(this.speed));
            if (this.curLoc.distance(this.player.getEyeLocation()) <= 0.8) {
                this.bPlayer.addCooldown(this);
                this.remove();
            }
        }

    }

    private void drawCircle() {
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = this.curLoc.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 1.4);
                temp.add(dir);
                ParticleEffect.FIREWORKS_SPARK.display(temp, 1, 0, 0, 0, 0.06f);
                new ColoredParticle(Color.fromRGB(255, 61,61), 1).display(temp, 1, 0.1, 0.1, 0.1);
            }
        }
    }

    public void explode() {
        if (isFired) {
            ParticleEffect.EXPLOSION_HUGE.display(this.curLoc, 2, this.explosionRadius / 2, this.explosionRadius / 2, this.explosionRadius / 2, 1);
            this.curLoc.getWorld().playSound(this.curLoc, Sound.ENTITY_GENERIC_EXPLODE, 10, 0);
            this.curLoc.getWorld().playSound(this.curLoc, Sound.BLOCK_BEACON_POWER_SELECT, 10, 1.25f);
            ParticleEffect.FIREWORKS_SPARK.display(this.curLoc, 10, 2, 2, 2, 0.3f);
            ParticleEffect.SMOKE_LARGE.display(this.curLoc, 20, 2, 2, 2, 0.3f);
            ParticleEffect.SQUID_INK.display(this.curLoc, 10, 2, 2, 2, 0.3f);
            ParticleEffect.FIREWORKS_SPARK.display(this.curLoc, 10, 2, 2, 2, 0.3f);
            ParticleEffect.SMOKE_LARGE.display(this.curLoc, 20, 2, 2, 2, 0.3f);
            ParticleEffect.SQUID_INK.display(this.curLoc, 10, 2, 2, 2, 0.3f);

            for (Block block : GeneralMethods.getBlocksAroundPoint(this.curLoc.add(0, -1, 0), this.explosionRadius + 1)) {
                if (GeneralMethods.isSolid(block) || isWater(block)) {
                    TempBlock affectedBlock = new TempBlock(block, Material.AIR);
                    affectedBlock.setRevertTime(this.blockRegenDelay);
                }
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.curLoc, this.explosionRadius)) {
                if (entity instanceof LivingEntity) {
                    if (!entity.equals(this.player)) {

                        //gets distance between the entity and the explosion location, to calculate knockback
                        double x = entity.getLocation().getX() - curLoc.getX();
                        double y = entity.getLocation().getY() - curLoc.getY();
                        double z = entity.getLocation().getZ() - curLoc.getZ();

                        //creates vector based on subtraction
                        Vector knockDir = new Vector(x, y, z);
                        GeneralMethods.setVelocity(this, entity, knockDir.normalize().multiply(this.knockback));
                        DamageHandler.damageEntity(entity, this.damage, this);
                        }
                    }
                }
            this.bPlayer.addCooldown(this);
            this.remove();
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, Reversal.class)) {
            Reversal reversal = CoreAbility.getAbility(player, Reversal.class);
            reversal.explode();
        }
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
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Reversal.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Reversal";
    }

    @Override
    public Location getLocation() {
        return this.curLoc != null ? this.curLoc : this.player.getLocation();
    }

    @Override
    public String getDescription() {
        return "Advanced Laserbenders are able to focus and channel energy from far away from their body, and send it racing " +
                "towards them. This energy is so unstable that it will destroy anything and anyone in its path.";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift! - \n" +
                "- Detonate: Left-Click! -";
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
