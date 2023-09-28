package com.xoarasol.projectcosmos.abilities.laserbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BurstRays extends LaserAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.KNOCKBACK)
    private double knockback;
    private double angleTheta;
    private double anglePhi;

    private int id;
    private static int ID = Integer.MIN_VALUE;

    private boolean created;

    Random rand = new Random();
    private ConcurrentHashMap<Integer, Bolt> bolts = new ConcurrentHashMap<>();

    public BurstRays(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.BurstRays.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.BurstRays.BoltRange");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.BurstRays.Damage");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.BurstRays.Knockback");
            this.angleTheta = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.BurstRays.AngleTheta");
            this.anglePhi = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.BurstRays.AnglePhi");

            this.created = false;

            this.bPlayer.addCooldown(this);
            this.player.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 1.8f);
            start();

        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.remove();
            return;
        }
        if (!created) {
            createCone();
            this.created = true;
        } else {
            if (bolts.isEmpty()) {
                this.remove();
                return;
            } else {
                progressBolts();
            }
        }
    }

    private void createCone() {
        final Location loc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().multiply(1));
        final Vector vector = loc.getDirection();

        final double angle = Math.toRadians(30);
        double x, y, z;
        final double r = 1;

        for (double theta = 0; theta <= 180; theta += this.angleTheta) {
            final double dPhi = this.anglePhi / Math.sin(Math.toRadians(theta));
            for (double phi = 0; phi < 360; phi += dPhi) {
                final double rPhi = Math.toRadians(phi);
                final double rTheta = Math.toRadians(theta);

                x = r * Math.cos(rPhi) * Math.sin(rTheta);
                y = r * Math.sin(rPhi) * Math.sin(rTheta);
                z = r * Math.cos(rTheta);
                final Vector direction = new Vector(x, z, y);
                Location temp = loc.clone().setDirection(direction);

                if (direction.angle(vector) <= angle) {
                    spawnBolt(temp, this.range, 1, 20);
                }
            }
        }
    }

    private void spawnBolt(Location location, double max, double gap, int arc) {
        id = ID;
        bolts.put(id, new Bolt(this, location, id, max, gap, arc));
        if (ID == Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        ID++;
    }

    private void progressBolts() {
        for (int id : bolts.keySet()) {
            bolts.get(id).progressBolt();
        }
    }

    @Override
    public void remove() {
        if (!bolts.isEmpty()) {
            bolts.clear();
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
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.BurstRays.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "BurstRays";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public String getAuthor() {
        return "XoaraSol & KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


    @Override
    public String getDescription() {
        return "By focusing their energy, Laserbenders are able to create a cone-shaped burst of lasers out from a singular point.";
    }

    @Override
    public String getInstructions() {
        return "To use this move, left-click";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    public class Bolt {

        private BurstRays ability;
        private Location location;
        private float initYaw;
        private float initPitch;
        private double step;
        private double max;
        private double gap;
        private int id;
        private int arc;

        private int greenHex;
        private int blueHex;


        public Bolt(BurstRays ability, Location location, int id, double max, double gap, int arc) {
            this.ability = ability;
            this.location = location;
            this.id = id;
            this.max = max;
            this.gap = gap;
            this.arc = arc;
            this.initPitch = this.location.getPitch();
            this.initYaw = this.location.getYaw();

            this.greenHex = 0;
            this.blueHex = 255;
        }

        public void progressBolt() {

            if (this.step >= this.max) {
                bolts.remove(id);
                return;
            }
            double step = 0.2;
            for (double i = 0; i < gap; i += step) {
                this.step += step;
                location = location.add(location.getDirection().clone().multiply(step));

                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    bolts.remove(id);
                }

                new ColoredParticle(Color.fromRGB(225, greenHex, blueHex), 1).display(this.location, 1, 0, 0, 0);

                this.greenHex -= 5;
                this.blueHex -= 2;
                if (this.greenHex < 0) {
                    this.greenHex = 0;
                }
                if (this.blueHex < 0) {
                    this.blueHex = 0;
                }

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1.2)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        DamageHandler.damageEntity(entity, player, damage, ability);
                        GeneralMethods.setVelocity(ability, entity, this.location.getDirection().normalize().multiply(knockback));
                    }
                }

                switch (rand.nextInt(3)) {
                    case 0:
                        location.setYaw(initYaw - arc);
                        break;
                    case 1:
                        location.setYaw(initYaw + arc);
                        break;
                    default:
                        location.setYaw(initYaw);
                        break;
                }
                switch (rand.nextInt(3)) {
                    case 0:
                        location.setPitch(initPitch - arc);
                        break;
                    case 1:
                        location.setPitch(initPitch + arc);
                        break;
                    default:
                        location.setPitch(initPitch);
                        break;
                }
                if (rand.nextInt(55) == 0) {
                    this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1, 0.86f);
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.7f, 1.5f);

                }
            }
        }
    }
}