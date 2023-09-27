package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
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
import java.util.UUID;


public class Fallout extends RadiationAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute(Attribute.SPEED)
    private double speed;
    private int radianceDuration;
    private double radianceDamage;
    private double radiantSpeed;

    private Location location;
    private Vector direction;

    private ArrayList<UUID> affected = new ArrayList<>();

    public Fallout(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Fallout.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Fallout.Duration");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Fallout.Damage");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Fallout.Radius");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Fallout.Speed");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Fallout.RadianceDuration");
            this.radianceDamage = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Fallout.RadianceDamage");
            this.radiantSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Fallout.RadiantSpeed");

            this.location = this.player.getLocation();
            this.direction = this.player.getEyeLocation().getDirection();

            this.flightHandler.createInstance(this.player, this.getName());
            this.player.setAllowFlight(true);
            this.player.setFallDistance(0);

            this.bPlayer.addCooldown("Boost", ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Boost.Cooldown"));

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
            ParticleEffect.EXPLOSION_HUGE.display(player.getLocation(), 1, 0, 0, 0);
            start();
        }
    }


    @Override
    public void progress() {
        if (this.bPlayer.isOnCooldown(this)) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + this.duration) {
            setRadiance(this.player, radianceDuration);
            this.remove();
            return;
        }
        if (this.player.isSneaking() && this.bPlayer.getBoundAbilityName().equalsIgnoreCase("Boost")) {
            this.location = this.player.getEyeLocation();
            ParticleEffect.FIREWORKS_SPARK.display(this.location, 5, 0.5, 0.5, 0.5, 0.1);
            new ColoredParticle(Color.fromRGB(0, 249, 255), 3).display(this.location.add(this.direction.normalize().multiply(0.5)), 6, this.radius / 2, this.radius / 2, this.radius / 2);
            new ColoredParticle(Color.fromRGB(0, 255, 212), 3).display(this.location.add(this.direction.normalize().multiply(0.5)), 2, this.radius / 2, this.radius / 2, this.radius / 2);
            new ColoredParticle(Color.fromRGB(0, 163, 255), 3).display(this.location.add(this.direction.normalize().multiply(0.5)), 2, this.radius / 2, this.radius / 2, this.radius / 2);

            this.location.getWorld().playSound(this.location, Sound.ENTITY_BEE_POLLINATE, 1, 1);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_CHORUS_FLOWER_GROW, 1, 1);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.radius)) {
                if (entity instanceof LivingEntity && !entity.equals(this.player) && !affected.contains(entity.getUniqueId())) {

                    if (isRadiant((LivingEntity) entity)) {
                        DamageHandler.damageEntity(entity, this.player, this.radianceDamage, this);
                        removeRadiance((LivingEntity) entity);
                    } else {
                        DamageHandler.damageEntity(entity, this.player, this.damage, this);
                        setRadiance((LivingEntity) entity, (this.radianceDuration));
                    }
                    affected.add(entity.getUniqueId());
                }
            }

            if (isRadiant(this.player)) {
                this.speed = this.radiantSpeed;
            }

            GeneralMethods.setVelocity(this, this.player, this.direction.normalize().multiply(this.speed));

        } else {
            setRadiance(this.player, radianceDuration);
            remove();
        }
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        this.flightHandler.removeInstance(this.player, this.getName());
        this.player.setAllowFlight(false);
        this.player.setFallDistance(0);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.Fallout.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getDescription() {
        return  "For a small amount of time, a Radiationbender is able to shoot themselves forward at immense" +
                " speeds. This will damage surrounding entities, and do additional damage if the victim is" +
                " radiant! If the user is radiant, their speed will greatly increase!";
    }

    @Override
    public String getInstructions() {
        return "- RadiationBurst (Tap-Shift 2x) > Boost (Hold-Shift) -";
    }

    @Override
    public String getName() {
        return "Fallout";
    }

    @Override
    public Location getLocation() {
        return this.location != null ? this.location : this.player.getLocation();
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Fallout(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> fallout = new ArrayList<>();
        fallout.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_DOWN));
        fallout.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_UP));
        fallout.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_DOWN));
        fallout.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_UP));
        fallout.add(new AbilityInformation("Boost", ClickType.SHIFT_DOWN));
        return fallout;
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
