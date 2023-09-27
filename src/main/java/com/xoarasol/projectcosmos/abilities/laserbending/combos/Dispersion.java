package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Dispersion extends LaserAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RADIUS)
    private double collisionRadius;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    private double angle;
    @Attribute("ProjectileDamage")
    private double projDamage;
    private double fanRange;

    private Location origin;
    private Location location;
    private Vector direction;

    private int id;

    private boolean isFan;

    private ConcurrentHashMap<Integer, LightStream> streams = new ConcurrentHashMap<>();

    public Dispersion(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Dispersion.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.Speed");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.CollisionRadius");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.Range");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Dispersion.RadianceDuration");
            this.angle = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.Angle");
            this.fanRange = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Dispersion.FanRange");
            this.projDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Dispersion.ProjectileDamage");

            this.origin = GeneralMethods.getMainHandLocation(this.player);
            this.location = this.origin.clone();
            this.direction = this.origin.getDirection();

            this.isFan = false;
            this.speed *= (ProjectKorra.time_step / 1000F);

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
        if (!this.isFan) {
            progressProjectile();
        } else {
            progressFan();
        }
    }

    public void progressProjectile() {
        for (int i = 0; i <= 3; i++) {
            if (GeneralMethods.isSolid(this.location.getBlock()) || isWater(this.location.getBlock()) || (this.location.distance(this.origin) > this.range)) {
                remove();
                return;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.collisionRadius)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(this.player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, this.player, this.projDamage, this);
                    remove();
                }
            }

            ParticleEffect.FIREWORKS_SPARK.display(this.location, 1, 0, 0, 0, 0.1);
            ParticleEffect.END_ROD.display(this.location, 2, 0.1, 0.1, 0.1);
            new ColoredParticle(Color.fromRGB(255, 255, 255), 1).display(this.location, 2, 0.3, 0.3, 0.3);

            this.location.setDirection(this.player.getEyeLocation().getDirection().normalize());
            this.location.add(this.location.getDirection().clone().normalize().multiply(this.speed / 3));

        }
        this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0.86f);
    }

    public void progressFan() {
        if (!streams.isEmpty()) {
            for (int i : streams.keySet()) {
                streams.get(i).progressStream();
            }
        } else {
            remove();
        }
    }

    public void startFan() {
        if (!this.isFan) {
            this.isFan = true;
            this.angle = Math.toRadians(angle);
            this.angle /= 7;

            ParticleEffect.FLASH.display(this.location, 2, 0.1, 0.1, 0.1);
            ParticleEffect.END_ROD.display(this.location, 5, 0.3, 0.3, 0.3, 0.5);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2, 0.56f);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1, 1.25f);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2, 1.2f);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 0);

            for (id = 1; id <= 7; id++) {
                Location loc = this.location.clone();
                Vector dir = this.location.getDirection();
                switch (id) {
                    case 1:
                        dir = setVector(dir.clone(), angle * 3);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 212, 0, 255));
                        break;
                    case 2:
                        dir = setVector(dir.clone(), angle * 2);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 111, 0, 255));
                        break;
                    case 3:
                        dir = setVector(dir.clone(), angle);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 0, 115, 255));
                        break;
                    case 4:
                        //Location's direction should stay the same as this would be the center stream
                        streams.put(id, new LightStream(this, id, loc, 0, 255, 30));
                        break;
                    case 5:
                        dir = setVector(dir.clone(), angle * -1);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 255, 255, 0));
                        break;
                    case 6:
                        dir = setVector(dir.clone(), angle * -2);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 255, 150, 0));
                        break;
                    default:
                        dir = setVector(dir.clone(), angle * -3);
                        loc.setDirection(dir);
                        streams.put(id, new LightStream(this, id, loc, 255, 0, 0));
                        break;
                }
            }
        }
    }

    private Vector setVector(Vector vector, double angle) {

        double x, z, vx, vz;
        x = vector.getX();
        z = vector.getZ();

        vx = x * Math.cos(angle) - z * Math.sin(angle);
        vz = x * Math.sin(angle) + z * Math.cos(angle);

        vector.setX(vx);
        vector.setZ(vz);

        return vector;
    }

    public static void disperse(Player player) {
        if (CoreAbility.hasAbility(player, Dispersion.class)) {
            Dispersion dispersion = CoreAbility.getAbility(player, Dispersion.class);
            dispersion.startFan();
        }
    }


    public boolean isFan() {
        return this.isFan;
    }

    @Override
    public void remove() {
        if (!streams.isEmpty()) {
            streams.clear();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.Dispersion.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getDescription() {
        return "Laserbenders can fire pure white-light energy from their hands, and split it into 7 streams.";
    }

    @Override
    public String getInstructions() {
        return "- LaserFission (Tap-Shift) > DazzlingRay (Left-Click 2x) -\n" +
                "- Rainbow Fan: DazzlingRay (Left-Click) -";
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
    public Object createNewComboInstance(Player player) {
        return new Dispersion(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> dispersion = new ArrayList<>();
        dispersion.add(new AbilityInformation("LaserFission", ClickType.SHIFT_DOWN));
        dispersion.add(new AbilityInformation("LaserFission", ClickType.SHIFT_UP));
        dispersion.add(new AbilityInformation("DazzlingRay", ClickType.LEFT_CLICK));
        dispersion.add(new AbilityInformation("DazzlingRay", ClickType.LEFT_CLICK));
        return dispersion;
    }

    @Override
    public String getName() {
        return "Dispersion";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public class LightStream {

        private Dispersion ability;
        private int id;
        private Location origin;
        private Location location;
        private Vector direction;

        private int redValue;
        private int greenValue;
        private int blueValue;

        public LightStream(Dispersion ability, int id, Location origin, int redValue, int greenValue, int blueValue) {
            this.ability = ability;
            this.id = id;
            this.origin = origin;
            this.location = origin.clone();
            this.direction = this.origin.getDirection();

            this.redValue = redValue;
            this.greenValue = greenValue;
            this.blueValue = blueValue;
        }

        public void progressStream() {
            for (int i = 0; i <= 3; i++) {
                if (GeneralMethods.isSolid(this.location.getBlock()) || isWater(this.location.getBlock()) || (this.location.distance(this.origin) > fanRange)) {
                    streams.remove(this.id);
                    return;
                }
                new ColoredParticle(Color.fromRGB(this.redValue, this.greenValue, this.blueValue), 2).display(this.location, 1, 0, 0, 0);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 0.5)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        DamageHandler.damageEntity(entity, player, damage, this.ability);
                        streams.remove(this.id);
                    }
                }
                this.location.add(this.direction.clone().normalize().multiply(speed / 3));
            }
        }
    }
}
