package com.xoarasol.projectcosmos.abilities.laserbending.passives;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.LaserAbility;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PhotonBooster extends LaserAbility implements AddonAbility, PassiveAbility {

    // Configurable variables.
    @Attribute(Attribute.SPEED)
    private int speedPower;

    public PhotonBooster(final Player player) {
        super(player);
        this.setFields();
    }

    public void setFields() {
        speedPower = ProjectCosmos.plugin.getConfig().getInt("Passives.Laser.PhotonBooster.SpeedFactor") - 1;
    }

    @Override
    public void progress() {
        if (!this.player.isSprinting() || !this.bPlayer.canUsePassive(this) || !this.bPlayer.canBendPassive(this)) {
            return;
        }

        // Speed Buff.
        if (!this.player.hasPotionEffect(PotionEffectType.SPEED) || this.player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() < this.speedPower || (this.player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == this.speedPower && this.player.getPotionEffect(PotionEffectType.SPEED).getDuration() == 1)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, this.speedPower, true, false), true);
        }
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Passives.Laser.PhotonBooster.Enabled");
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
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
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "PhotonBooster";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public boolean isInstantiable() {
        return true;
    }

    @Override
    public boolean isProgressable() {
        return true;
    }

    public int getSpeedPower() {
        return this.speedPower;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Xoara";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
