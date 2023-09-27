package com.xoarasol.projectcosmos.abilities.laserbending.radiation;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
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

public class Contaminate extends RadiationAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute("NauseaDuration")
    private int nauseaDuration;
    @Attribute("WeaknessDuration")
    private int weaknessDuration;

    private boolean isCharged;
    private boolean canAffect;

    private int radius;
    private int waves;

    private long time;
    private long blindTime;
    private long duration;

    Random rand = new Random();

    private ArrayList<LivingEntity> lents = new ArrayList<>();
    private int currPoint;

    public Contaminate(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.radius = 0;
            this.waves = 0;

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.Contaminate.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.Contaminate.Range");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.Contaminate.ChargeTime");
            this.nauseaDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.Contaminate.NauseaDuration");
            this.weaknessDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.Contaminate.WeaknessDuration");

            this.isCharged = false;
            this.canAffect = false;

            this.blindTime = System.currentTimeMillis();
            this.duration = ((this.nauseaDuration / 2) * 100L);

            this.start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            if (this.isCharged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (!this.isCharged && !this.bPlayer.getBoundAbilityName().equalsIgnoreCase("Contaminate")) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > this.chargeTime + this.getStartTime()) {
            this.isCharged = true;
        }
        if (!this.player.isSneaking() && !this.isCharged) {
            remove();
            return;

        }
        if (this.player.isSneaking() && this.isCharged) {
            ParticleEffect.SNEEZE.display(this.player.getEyeLocation(), 3, 0.4, 0.4, 0.4, 0.1);
            player.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_CHORUS_FLOWER_DEATH, 1.4f, 1.1f);
            playParticleRing(120, range, 3);
        }

        if (!this.player.isSneaking() && this.isCharged && !this.canAffect) {
            this.canAffect = true;

            this.time = System.currentTimeMillis();
            this.bPlayer.addCooldown(this);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.player.getLocation(), this.range)) {
                if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                    if (isRadiant((LivingEntity) entity)) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 3, false, false, false));
                        lents.add((LivingEntity) entity);
                        removeRadiance((LivingEntity) entity);
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 0);
                    }
                }
            }
        }
        if (this.canAffect && System.currentTimeMillis() > this.time + this.duration) {
            this.remove();
            return;
        }
        if (this.canAffect) {
            affect();
        }
    }

    private void affect() {
        if (!(lents.isEmpty())) {
            for (LivingEntity lent : lents) {
                if (lent.isDead()) {
                    lents.remove(lent);
                }
                if (lent instanceof Player && !((Player) lent).isOnline()) {
                    lents.remove(lent);
                }
                new ColoredParticle(Color.fromRGB(56, 255, 157), 2).display(lent.getLocation(), 2, 0.5, 0.5, 0.5);
                ParticleEffect.CRIT.display(lent.getLocation(), 1, 0.5, 0.5, 0.5);

                if (rand.nextInt(50) == 0) {
                    lent.getWorld().playSound(lent.getLocation(), Sound.BLOCK_LAVA_AMBIENT, 1, 0);
                }

                if (!(lent.hasPotionEffect(PotionEffectType.BLINDNESS)) && System.currentTimeMillis() > this.blindTime + 1500) {
                    lent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 2, false, false, false));
                    this.blindTime = System.currentTimeMillis();
                }
            }
        } else {
            remove();
        }
    }

    private void playParticleRing(int points, double size, int speed) {
        for (int i = 0; i < speed; ++i) {
            currPoint += 360 / points;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.9D, z);
            new ColoredParticle(Color.fromRGB(56, 255, 157), 1).display(loc, 4, 0, 0, 0);
            ParticleEffect.FIREWORKS_SPARK.display(loc, 2, 0.1, 0.1, 0.1, 0.05);

        }
    }

    @Override
    public void remove() {
        if (!(lents.isEmpty())) {
            for (LivingEntity lent : lents) {
                if (lent.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    lent.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            }
            lents.clear();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Radiation.Contaminate.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Contaminate";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }


    @Override
    public String getDescription() {
        return "Radiationbenders are able to channel their inner radiation outwards, inflicting nearby radiant entities with radiation sickness.";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift > Release-Shift when it's charged up! -";
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
