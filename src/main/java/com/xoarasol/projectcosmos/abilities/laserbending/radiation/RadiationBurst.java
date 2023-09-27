package com.xoarasol.projectcosmos.abilities.laserbending.radiation;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class RadiationBurst extends RadiationAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.DURATION)
    private double range;


    private Location origin;
    private Location location;
    private Vector direction;

    private boolean charged;
    private boolean isExploded;
    private boolean hasDetonated;

    ArrayList<UUID> affected = new ArrayList<>();

    Random rand = new Random();

    private int id;
    private static int ID = Integer.MIN_VALUE;

    private ConcurrentHashMap<Integer, Wave> waves = new ConcurrentHashMap<>();

    public RadiationBurst(Player player) {
        super(player);
        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.RadiationBurst.Cooldown");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.RadiationBurst.ChargeTime");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.RadiationBurst.Damage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.RadiationBurst.Steps");;

            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();
            this.direction = origin.getDirection();


            this.charged = false;
            this.isExploded = false;
            this.hasDetonated = false;


            start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            if (this.charged) {
                this.bPlayer.addCooldown(this);
            }
            this.remove();
            return;
        }
        if (!this.bPlayer.getBoundAbilityName().equalsIgnoreCase("RadiationBurst") && !this.isExploded) {
            remove();
            return;
        }

        if (System.currentTimeMillis() > this.getStartTime() + this.chargeTime) {
            this.charged = true;

            this.origin = this.player.getEyeLocation();
            this.location = this.player.getEyeLocation();
            this.direction = this.player.getEyeLocation().getDirection();
        }
        if (!this.player.isSneaking() && !this.isExploded) {
            this.isExploded = true;
        }
        if (this.isExploded && !this.charged) {
            this.remove();
            return;
        }
        if (this.isExploded && !this.hasDetonated) {
            this.hasDetonated = true;
            detonate();
        } else if (this.isExploded) {
            progressWaves();
        }
        if (this.charged && this.player.isSneaking()) {
            Location loc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(2));
            new ColoredParticle(Color.fromRGB(0, 255, 180), 1).display(loc, 1, 0,0,0);
        }
    }

    private void progressWaves() {
        if (waves.isEmpty()) {
            remove();
        }

        for (int id : waves.keySet()) {
            waves.get(id).progressWave();
        }
    }

    public void detonate() {
        this.bPlayer.addCooldown(this);
        this.location.getWorld().playSound(this.location, Sound.BLOCK_CHORUS_FLOWER_DEATH, 1.4f, 1.65f);
        this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 0);
        this.location.getWorld().playSound(this.location, Sound.ENTITY_GENERIC_EXPLODE, 0.86f, 0.55f);
        Location fake = this.player.getLocation().add(0, -1, 0);
        fake.setPitch(0);
        for (int i = -180; i < 180; i += 45) {
            fake.setYaw(i);
            for (double j = -180; j <= 180; j += 55) {
                Location temp = fake.clone();
                Vector dir = fake.getDirection().normalize().clone().multiply(2 * Math.cos(Math.toRadians(j)));
                temp.add(dir);
                temp.setY(temp.getY() + 2 + (2 * Math.sin(Math.toRadians(j))));
                dir = GeneralMethods.getDirection(this.player.getLocation().add(0, 0, 0), temp);
                spawnWave(this.player.getLocation().clone().add(0, 0, 0).setDirection(dir), range, 0.5);
            }
        }
    }

    private void spawnWave(Location location, double range, double gap) {
        id = ID;
        waves.put(id, new Wave(this, location, range, gap, id));
        if (ID == Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        ID++;
    }

    @Override
    public void remove() {
        if (!this.waves.isEmpty()) {
            waves.clear();
        }
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Radiation.RadiationBurst.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "RadiationBurst";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return "This Ability allows Radiationbenders to cause an outburst of radiation to shoot out from a point.";
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

    @Override
    public String getInstructions() {
        return "- Hold-Shift > Release-Shift when charged! -";
    }

    public class Wave {

        private RadiationBurst ability;
        private Location location;
        private Location origin;
        private double range;
        private double gap;
        private int id;
        private int colorCounter = 0;
        private double step;
        private double initPitch;
        private int count;

        @Attribute("RadianceDuration")
        private int radianceDuration;

        public Wave(RadiationBurst ability, Location location, Double range, double gap, int id) {

            this.ability = ability;
            this.origin = location;
            this.location = location;
            this.range = range;
            this.gap = gap;
            this.id = id;
            this.initPitch = this.location.getPitch();
            this.count = 0;
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiationBurst.RadianceDuration");
        }

        public void progressWave() {
            if (step > this.range) {
                waves.remove(id);
                return;
            }
            double step = 0.1;
            for (double i = 0; i < gap; i += step) {
                this.step += step;
                location = location.add(location.getDirection().clone().multiply(step));
                float pitchVal = this.location.getPitch();

                if (count >= 7 && count <= 20) {
                    pitchVal += 1.2;

                    if (pitchVal > 90) {
                        pitchVal = 90;
                    }
                    this.location.setPitch(pitchVal);
                }


                if (!GeneralMethods.isTransparent(this.location.getBlock()) || GeneralMethods.isRegionProtectedFromBuild(player, "RadiationBurst", this.location)) {
                    waves.remove(id);
                }


                new ColoredParticle(Color.fromRGB(0, 255, this.colorCounter), 1).display(this.location, 1, 0, 0, 0);
                this.colorCounter += 4;
                if (this.colorCounter > 255) {
                    this.colorCounter = 255;
                }


                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId()) && !affected.contains(entity.getUniqueId())) {
                        DamageHandler.damageEntity(entity, player, damage, ability);
                        setRadiance((LivingEntity) entity, this.radianceDuration);
                        affected.add(entity.getUniqueId());
                    }
                }

            }
            if (!(count > 21)) {
                count++;
            }
        }
    }
}
