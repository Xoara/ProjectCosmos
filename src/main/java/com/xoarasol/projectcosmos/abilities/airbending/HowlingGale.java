package com.xoarasol.projectcosmos.abilities.airbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class HowlingGale extends AirAbility implements AddonAbility {

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();

    private long cooldown;
    private double range;
    private double damage;

    private boolean controllable;
    private boolean hit;
    private int currPoint;
    private double power;
    private double speed;
    private int bounceLimit;
    private int bounceCounter;

    public HowlingGale(Player player) {
        super(player);

        if (this.bPlayer.isOnCooldown(this)) {
            return;
        }
        setFields();
        start();
        this.bPlayer.addCooldown(this);
    }

    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Air.HowlingGale.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Air.HowlingGale.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Air.HowlingGale.Damage");
        this.location = player.getEyeLocation();
        this.controllable = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.HowlingGale.Controllable");
        this.power = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.HowlingGale.Power");
        this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.HowlingGale.Speed");
        this.bounceLimit = ProjectCosmos.plugin.getConfig().getInt("Abilities.Air.HowlingGale.BounceLimit");
        this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        this.location = this.origin.clone();
        this.direction = this.player.getLocation().getDirection();
        this.bounceCounter = 0;
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }

        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (this.controllable) {
            Vector untouchVector = this.direction.clone().multiply(this.speed);
            Location destinationLocation = this.location.clone().add(this.player.getLocation().getDirection().multiply(this.speed));
            Vector desiredVector = destinationLocation.toVector().subtract(this.location.clone().toVector());
            Vector steeringVector = desiredVector.subtract(untouchVector).multiply(0.25D);
            this.direction = this.direction.add(steeringVector);
        }
        if (GeneralMethods.isSolid(this.location.getBlock())) {
            this.bounceCounter++;
            if (this.bounceCounter > this.bounceLimit) {
                remove();
                return;
            }
            this.direction = findBounceDirection(this.location, this.direction);
            if (this.controllable)
                this.direction.multiply(1.5D);
        }
        this.location.add(this.direction.clone().multiply(this.speed));
        AirParticles();
        ParticleEffect.CLOUD.display(location, 5, 0.05, 0.05, 0.05, 0.09f);
        AirRings(60, 2.25F, 3);
        AirRings(60, 2.5F, 3);
        AirRings(60, 3.0F, 3);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1.3D)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 0.75F);
                entity.setVelocity(this.direction.multiply(this.power));
                return;
            }
        }
    }

    public Vector findBounceDirection(Location projectileLoc, Vector projectileDirection) {
        Block block = projectileLoc.getBlock();
        ArrayList<Block> blocks = new ArrayList<>(0);
        blocks.add(block.getRelative(BlockFace.DOWN));
        blocks.add(block.getRelative(BlockFace.UP));
        blocks.add(block.getRelative(BlockFace.NORTH));
        blocks.add(block.getRelative(BlockFace.SOUTH));
        blocks.add(block.getRelative(BlockFace.EAST));
        blocks.add(block.getRelative(BlockFace.WEST));
        Vector tmpDirection = projectileDirection.clone().multiply(0.1D);
        Location tmpLocation = projectileLoc.clone().subtract(tmpDirection);
        while (tmpLocation.getBlock().getLocation().equals(block.getLocation()))
            tmpLocation.subtract(tmpDirection);
        int i = 0;
        for (Block b : blocks) {
            if (tmpLocation.getBlock().getLocation().equals(b.getLocation()))
                break;
            i++;
        }
        Vector newDirection = projectileDirection.clone();
        if ((((i == 0) ? 1 : 0) | ((i == 1) ? 1 : 0)) != 0) {
            newDirection.setY(newDirection.getY() * -1.0D);
        } else if ((((i == 2) ? 1 : 0) | ((i == 3) ? 1 : 0)) != 0) {
            newDirection.setZ(newDirection.getZ() * -1.0D);
        } else {
            newDirection.setX(newDirection.getX() * -1.0D);
        }
        return newDirection;
    }

    private void AirParticles() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.8D);
                temp.add(dir);
                ParticleEffect.SPELL.display(temp, 1, 0.0D, 0.0D, 0.0D, 0.003000000026077032D);

            }
            if (this.hit) {
                remove();
                return;
            }
            tempLocation.add(this.direction.clone().multiply(0.2D));
        }
        this.location.getWorld().playSound(this.location, Sound.ITEM_TRIDENT_RIPTIDE_3, 0.85F, 1.2F);
    }


    private void AirRings(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, 0.75D, z);
            ParticleEffect.CLOUD.display(loc, 1, 0.0D, 0.0D, 0.0D, 0.04D);
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.HowlingGale.Enabled");
    }

    public String getName() {
        return "HowlingGale";
    }

    public String getDescription() {
        return "Airbenders can send out a surge of wind to knock their enemies further away from themselves! HowlingGale can also bounce off blocks.";
    }

    public String getInstructions() {
        return "- Tap-Shift! -";
    }

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

    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getAuthor() {
        return "Hiro3 & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

}
