package com.xoarasol.projectcosmos.abilities.laserbending.radiation;

import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class RadiantTrail extends RadiationAbility implements AddonAbility {

    private ArrayList<Location> fires;
    Random rand = new Random();

    int timer;
    private int radianceDuration;
    private int sizes;
    private int duration;
    private int particleamount;
    private int particledensity;

    private double damage;
    private double radius;

    private long time;
    private long cooldown;

    public RadiantTrail(Player player) {
        super(player);
        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, RadiantTrail.class)) {
            this.fires = new ArrayList<>();
            damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.RadiantTrail.Damage");
            radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.RadiantTrail.Radius");
            cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.RadiantTrail.Cooldown");
            radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiantTrail.RadianceDuration");
            sizes = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiantTrail.Length");
            duration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiantTrail.AbilityDuration");
            particleamount = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiantTrail.ParticleAmount");
            particledensity = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.RadiantTrail.RandomizerTicks");
            timer = 0;
            time = System.currentTimeMillis();
            start();
        }
    }

    @Override
    public void progress() {
        timer++;
        if(!player.isValid() || bPlayer.isChiBlocked() || !bPlayer.canBendIgnoreBindsCooldowns(this) || timer > 8*27){
            remove();
            return;
        }
        if (System.currentTimeMillis() > time + duration) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!PCMethods.generalBendCheck(this, this.player)) {
            this.remove();
            return;
        }
        if (!this.bPlayer.canBend((CoreAbility) this)) {
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;
        }
        while (this.fires.size() > sizes){
            this.fires.remove(0);
        }
        this.fires.add(player.getLocation());
        for(Location x : fires){
            if (rand.nextInt(particledensity) == 0) {
                ParticleEffect.FIREWORKS_SPARK.display(x, particleamount, radius, 0.3D, radius, .08);
                (new ColoredParticle(Color.fromRGB(56, 255, 157), 1.5F)).display(x, particleamount, radius, 0.3D, radius);
                (new ColoredParticle(Color.fromRGB(56, 255, 255), 1.5F)).display(x, particleamount, radius, 0.3D, radius);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHORUS_FLOWER_DEATH, 0.4F, 1.53F);
            }
            for (Entity entity : x.getWorld().getNearbyEntities(x ,radius, 1, radius)){
                if (entity instanceof LivingEntity){
                    if(entity.getUniqueId() != player.getUniqueId()){
                        DamageHandler.damageEntity(entity, damage, this);
                        setRadiance((LivingEntity)entity, this.radianceDuration);
                    }
                }
            }
        }
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
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Radiation.RadiantTrail.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "RadiantTrail";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
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
