package com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LunarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
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
import org.bukkit.util.Vector;

import java.util.Random;

public class LunarClap extends LunarAbility implements AddonAbility {

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();

    private long cooldown;
    private double range;
    private double damage;
    private double speed;
    private int pstage;

    public LunarClap(Player player) {
        super(player);
        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.LunarClap.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.LunarClap.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.LunarClap.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.LunarClap.Speed") * (ProjectKorra.time_step / 1000F);

            this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
            this.location = this.origin.clone();
            this.direction = this.player.getLocation().getDirection();

            this.bPlayer.addCooldown(this);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }

        this.direction = this.player.getLocation().getDirection();
        this.location.add(this.direction.multiply(this.speed));

        double increment = (2 + 2) * Math.PI / 36.0D;
        double angle = this.pstage * increment;
        double x = location.getX() + 1 * Math.cos(angle);
        double y = location.getY() + -0.20D * Math.ceil(angle);
        double z = location.getZ() + 1 * Math.sin(angle);
        Location loc = new Location(location.getWorld(), x, y + 0.5D, z);
        ParticleEffect.SPELL_INSTANT.display(loc, 8, 0.5, 0.5, 0.5);
        Plasma();
        Plasma2();
        Plasma3();

        this.pstage++;
        this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 0.3F, 2F);
        this.location.getWorld().playSound(this.location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 1.75F);

        if (GeneralMethods.isSolid(this.location.getBlock())) {
            remove();
            return;
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 0.9D)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                ParticleEffect.SPELL_INSTANT.display(entity.getLocation(), 10, 0, 0, 0, 0.04);
                ParticleEffect.SPELL_INSTANT.display(entity.getLocation(), 10, 1, 1, 1);
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 1));

                return;
            }
        }
    }

    private void Plasma() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.2D);
                temp.add(dir);


                new ColoredParticle(Color.fromRGB(192, 207, 255), 0.8f).display(temp, 1, 0.1, 0.1, 0.1);

            }
            }
            tempLocation.add(this.direction.clone().multiply(0.2D));
        }
    private void Plasma2() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.4D);
                temp.add(dir);


                new ColoredParticle(Color.fromRGB(145, 157, 193), 0.8f).display(temp, 1, 0.1, 0.1, 0.1);

            }
        }
        tempLocation.add(this.direction.clone().multiply(0.2D));
    }

    private void Plasma3() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.6D);
                temp.add(dir);


                new ColoredParticle(Color.fromRGB(103, 111, 137), 0.8f).display(temp, 1, 0.1, 0.1, 0.1);

            }
        }
        tempLocation.add(this.direction.clone().multiply(0.2D));
    }


    private void PlasmaArc2() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.3D);
                temp.add(dir);
                ParticleEffect.SPELL_INSTANT.display(temp, 1, 0, 0, 0);
            }
            tempLocation.add(this.direction.clone().multiply(0.2D));
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return "LunarClap";
    }

    @Override
    public String getDescription() {
        return "This basic Lunar ability allows Lunarbenders to summon a blast of lunar energy by clapping once.";
    }

    @Override
    public String getInstructions() {
        return "*Left Click*";
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
    public boolean isSneakAbility() {
        return false;
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Lunar.LunarClap.Enabled");
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