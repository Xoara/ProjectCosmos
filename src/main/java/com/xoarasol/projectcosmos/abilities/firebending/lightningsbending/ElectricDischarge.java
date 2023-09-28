package com.xoarasol.projectcosmos.abilities.firebending.lightningsbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ElectricDischarge extends LightningAbility implements AddonAbility {

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
    private double collisionRadius;

    public ElectricDischarge(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Lightning.ElectricDischarge.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.ElectricDischarge.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.ElectricDischarge.Damage");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.ElectricDischarge.SingleBoltCollisionRadius");
            this.angleTheta = 5;
            this.anglePhi = 5;

            this.created = false;

            this.bPlayer.addCooldown(this);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

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

        final double angle = Math.toRadians(7);
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
                    spawnBolt(temp, this.range, 1, 30);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Fire.Lightning.ElectricDischarge.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "ElectricDischarge";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
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

    @Override
    public String getDescription() {
        return "This is a basic Lightning ability which allows the user to release an electrical current from their fingertips!";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click! -";
    }

    public class Bolt {

        private ElectricDischarge ability;
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


        public Bolt(ElectricDischarge ability, Location location, int id, double max, double gap, int arc) {
            this.ability = ability;
            this.location = location;
            this.id = id;
            this.max = max;
            this.gap = gap;
            this.arc = arc;
            this.initPitch = this.location.getPitch();
            this.initYaw = this.location.getYaw();
        }

        public void progressBolt() {

            if (this.step >= this.max) {
                bolts.remove(id);
                return;
            }
            double step = 0.4;
            for (double i = 0; i < gap; i += step) {
                this.step += step;
                location = location.add(location.getDirection().clone().multiply(step));

                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    bolts.remove(id);
                }

                playLightningbendingParticle(this.location, 0, 0, 0);

                this.greenHex -= 5;
                this.blueHex -= 2;
                if (this.greenHex < 0) {
                    this.greenHex = 0;
                }
                if (this.blueHex < 0) {
                    this.blueHex = 0;
                }

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, collisionRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        DamageHandler.damageEntity(entity, player, damage, ability);
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
                if (rand.nextInt(5) == 0) {

                    this.location.getWorld().playSound(this.location, Sound.ENTITY_CREEPER_HURT, 0.7f, 0);


                }
            }
        }
    }
}
