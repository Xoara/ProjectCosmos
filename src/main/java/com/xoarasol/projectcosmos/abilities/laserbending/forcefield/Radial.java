package com.xoarasol.projectcosmos.abilities.laserbending.forcefield;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.ForceFieldAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Radial extends ForceFieldAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldowm;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    private double radius;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.DURATION)
    private double duration;

    private int count;
    private int currPoint;

    private boolean click;

    private Location curloc;
    private Location origin;
    private ArrayList<Location> circleLocs = new ArrayList<>();

    public Radial(Player player, boolean click) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, Radial.class)) {

            this.cooldowm = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.Radial.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.Radial.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.Radial.Speed");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.Radial.Duration");

            if (click) {
                this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.Radial.ClickRadius");
                this.click = true;
            } else {
                this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.Radial.SneakRadius");
                this.click = false;
            }

            this.speed *= (ProjectKorra.time_step / 1000F);
            this.origin = this.player.getEyeLocation();

            this.count = 0;
            this.currPoint = 0;

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

            start();
        }
    }

    private void plasmaRing() {

        for (int i = 0; i < this.speed; i++) {
            currPoint += 360 / 60;

            if (currPoint > 360) {
                currPoint = 0;
            }

            double angle = currPoint * 3.141592653589793D / 180.0D;
            double x = this.radius * Math.cos(angle);
            double z = this.radius * Math.sin(angle);

            this.curloc = this.origin.clone().add(x, 0, z);
            (new ColoredParticle(Color.fromRGB(116, 173, 211), 2.5F)).display(curloc, 1, 0, 0, 0);
            ParticleEffect.FIREWORKS_SPARK.display(this.curloc, 1, 0, 0, 0, 0.1);
            this.curloc.getWorld().playSound(this.curloc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.1f, 0.75f);
            this.curloc.getWorld().playSound(this.curloc, Sound.BLOCK_BEACON_POWER_SELECT, 0.1f, 1.1f);

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(curloc, 0.7)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(this.player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, this.player, this.damage, this);
                }
            }
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            this.bPlayer.addCooldown(this);
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.getStartTime() + this.duration) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (click) {
            this.origin = this.player.getLocation().add(0, 0.5, 0);
        }
        plasmaRing();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.ForceField.Radial.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldowm;
    }

    @Override
    public String getName() {
        return "Radial";
    }

    @Override
    public Location getLocation() {
        return this.curloc;
    }

    @Override
    public String getDescription() {
        return  "Forcefieldbenders can create a temporary rotating energy field, damaging anything it comes into contact with!";
    }

    @Override
    public String getInstructions() {
        return "(Sneak): Creates a larger, grounded ring around the user.\n" +
                "(Click): Creates a smaller ring that moves with the user.";
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
