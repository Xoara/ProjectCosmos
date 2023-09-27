package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.BendingPlayer;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class SystemicShock extends RadiationAbility implements AddonAbility, ComboAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RADIUS)
    private int radius;
    @Attribute(Attribute.DURATION)
    private int effectDuration;
    private long abilityCooldown;
    private long holdTime;

    private Location origin;
    private Location location;

    private boolean firing;
    private int blueCounter;
    private boolean hasReached;

    private  int currPoint = 0;
    private int currPoint2 = 360;

    Random rand = new Random();

    public SystemicShock(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.SystemicShock.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.SystemicShock.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.SystemicShock.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.SystemicShock.Speed");
            this.radius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.SystemicShock.Radius");
            this.abilityCooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.SystemicShock.AbilityCooldown");
            this.effectDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.SystemicShock.EffectDuration");
            this.holdTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.SystemicShock.HoldTime");

            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();

            this.speed *= (ProjectKorra.time_step / 1000F);

            this.firing = false;
            this.blueCounter = rand.nextInt(49) / 5;
            this.hasReached = false;

            start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!this.firing) {
            if (System.currentTimeMillis() > this.holdTime + getStartTime()) {
                this.bPlayer.addCooldown(this);
                remove();
                return;
            }
            this.origin = this.player.getEyeLocation();
            this.location = this.player.getEyeLocation();

            rotatingRing(1, 0.5);
            reverseRotatingRing(1.5, 1);
            rotatingRing(1.5, 1.5);
            reverseRotatingRing(1, 2);
            rotatingRing(0.5, 2.5);
        } else {
            progressBeam();
        }

        if (this.hasReached) {
            this.blueCounter -= 5;
            if (this.blueCounter <= 0) {
                this.hasReached = false;
                this.blueCounter = 0;
            }
        } else {
            this.blueCounter +=5;
            if (this.blueCounter >= 255) {
                this.hasReached = true;
                this.blueCounter = 255;
            }
        }
    }

    private void rotatingRing(double size, double addition) {
        for (int i = 0; i < 4; i++) {
            currPoint += 360 / 180;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, addition, z);
            (new ColoredParticle(Color.fromRGB(0, 255, this.blueCounter), 0.8F)).display(loc, 1, 0, 0, 0);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHORUS_FLOWER_GROW, 0.7f, 2f);
        }
    }

    private void reverseRotatingRing(double size, double addition) {
        for (int i = 4; i > 0; i--) {
            currPoint2 -= 360 / 180;

            if (currPoint2 < 0) {
                currPoint2 = 360;
            }

            double angle = currPoint2 * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, addition, z);
            (new ColoredParticle(Color.fromRGB(0, 255, this.blueCounter), 0.8F)).display(loc, 1, 0, 0, 0);
        }
    }

    public void progressBeam() {
        for (int i = 0; i < 3; i++) {
            if (GeneralMethods.isSolid(this.location.getBlock()) || isWater(this.location.getBlock()) || this.location.distance(this.origin) > this.range) {
                remove();
                return;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, radius)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(this.player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, this.player, this.damage, this);

                    if (entity instanceof Player && isRadiant((LivingEntity) entity)) {
                        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) entity);
                        String abil = bPlayer.getBoundAbilityName();
                        if (abil != null) {
                            bPlayer.addCooldown(abil, this.abilityCooldown);
                        }
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.effectDuration, 2,false, false, true));
                        removeRadiance((LivingEntity) entity);
                    }
                    this.remove();
                    return;
                }
            }
            ParticleEffect.END_ROD.display(this.location, 2, 0.1,0.1,0.1,0.01);
            ParticleEffect.FIREWORKS_SPARK.display(this.location, 2, 0,0,0, 0.1);
            new ColoredParticle(Color.fromRGB(0, 255, this.blueCounter), 4).display(this.location, 1, this.radius/2F, this.radius/2F, this.radius/2F);
            new ColoredParticle(Color.fromRGB(0, 255, 0), 1).display(this.location, 1, this.radius, this.radius, this.radius);
            new ColoredParticle(Color.fromRGB(0, 255, 255), 1).display(this.location, 1, this.radius, this.radius, this.radius);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 0);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 0.75f);
            this.location.getWorld().playSound(this.location, Sound.BLOCK_ANVIL_LAND, 1, 1.7f);

            this.location.add(this.player.getEyeLocation().getDirection().normalize().multiply(this.speed /3));
        }
    }

    public void fire() {
        if (!this.firing) {
            this.firing = true;
            this.bPlayer.addCooldown(this);
            progressBeam();
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, SystemicShock.class)) {
            SystemicShock shock = CoreAbility.getAbility(player, SystemicShock.class);
            shock.fire();
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
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.SystemicShock.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "SystemicShock";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Xoara & KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


    @Override
    public String getDescription() {
        return "Laserbenders can focus large amounts of energy into a fireable ball, and upon contact with this ball, " +
                "radiant players will become exhausted, and ";
    }

    @Override
    public String getInstructions() {
        return "Usage: Contaminate (Left-Click) > Contaminate (Left-Click) > RadiationBurst (Tap-Sneak)\n" +
                "(to Fire): RadiationBurst (Left-Click)";
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new SystemicShock(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> systemicShock = new ArrayList<>();
        systemicShock.add(new AbilityInformation("Contaminate", ClickType.LEFT_CLICK));
        systemicShock.add(new AbilityInformation("Contaminate", ClickType.LEFT_CLICK));
        systemicShock.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_DOWN));
        systemicShock.add(new AbilityInformation("RadiationBurst", ClickType.SHIFT_UP));
        return systemicShock;
    }
}
