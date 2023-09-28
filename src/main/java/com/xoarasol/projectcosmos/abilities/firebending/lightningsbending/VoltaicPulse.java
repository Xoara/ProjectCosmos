package com.xoarasol.projectcosmos.abilities.firebending.lightningsbending;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class VoltaicPulse extends LightningAbility implements AddonAbility {

    private double t = 0.7853981633974483D;
    private double particleRotation;
    private double range;
    private double damage;
    private double stunChance;
    private double push;
    private long stunDuration;
    private long cooldown;
    private long chargeTime;
    private boolean isCharged;


    private Location loc;
    private ArrayList<Entity> electrocuted;

    private boolean isSAEnabled;
    private int resPower;

    public VoltaicPulse(Player player) {
        super(player);
        if (!this.bPlayer.canBend((CoreAbility)this))
            return;
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5F, 1F);
        ParticleEffect.FLASH.display(player.getLocation(), 1);
        start();
        setFields();
    }

    private void setFields() {
        this.loc = this.player.getLocation().add(0.0D, -1.0D, 0.0D).clone();
        this.isCharged = false;
        this.electrocuted = new ArrayList<>();
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.VoltaicPulse.Range");
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Lightning.VoltaicPulse.Cooldown");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.VoltaicPulse.Damage");
        this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Lightning.VoltaicPulse.ChargeTime");
        this.stunChance = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.VoltaicPulse.Electrocute.StunChances");
        this.stunDuration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Fire.Lightning.VoltaicPulse.Electrocute.StunDuration");
        this.push = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Fire.Lightning.VoltaicPulse.Push");

        this.isSAEnabled = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Fire.Lightning.VoltaicPulse.SuperArmor.Enabled");
        this.resPower = ProjectCosmos.plugin.getConfig().getInt("Abilities.Fire.Lightning.VoltaicPulse.SuperArmor.Resistance.Power");
    }

    private void electrocute(LivingEntity le) {
        double i = Math.random() * 100.0D;
        if (i <= this.stunChance && !this.electrocuted.contains(le)) {
            this.electrocuted.add(le);
            MovementHandler mh = new MovementHandler(le, (CoreAbility)this);
            mh.stopWithDuration(this.stunDuration / 1000L * 20L, Element.LIGHTNING.getColor() + "* Electrocuted *");
            return;
        }
    }

    public void progress() {
        if (this.t >= this.range) {
            remove();
            this.bPlayer.addCooldown((Ability)this);
            return;
        }
        if (this.player.isDead() || !this.player.isOnline() || this.player == null || !this.player.isSneaking()) {
            this.bPlayer.addCooldown((Ability)this);
            remove();
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild((Ability)this, this.loc)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + this.chargeTime &&
                !this.isCharged)
            this.isCharged = true;
        if (!this.isCharged) {
            absorbtion();
            absorbtion2();
            superArmor();
        } else {
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_BEEHIVE_WORK, 0.5F, 0F);
            this.loc.getWorld().playSound(this.loc, Sound.ENTITY_CREEPER_HURT, 0.5F, 0F);
            this.loc.getWorld().playSound(this.loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5F, 0.66F);
            pulse();
        }
    }

    private void absorbtion() {
        Location localLocation1 = this.player.getLocation();
        double d1 = 0.1570796326794897D;
        double d2 = 0.06283185307179587D;
        double d3 = 1.0D;
        double d4 = 1.0D;
        double d5 = 0.1570796326794897D * this.particleRotation;
        double d6 = 0.06283185307179587D * this.particleRotation;
        double d7 = localLocation1.getX() + 1.0D * Math.cos(d5);
        double d8 = localLocation1.getZ() + 1.0D * Math.sin(d5);
        double newY = localLocation1.getY() + 1.0D + 1.0D * Math.cos(d6);
        Location localLocation2 = new Location(this.player.getWorld(), d7, newY, d8);
        playLightningbendingParticle(localLocation2, 0.1, 0.1, 0.1);
        double xd7 = localLocation1.getX() + 1.0D * -Math.cos(d5);
        double xd8 = localLocation1.getZ() + 1.0D * -Math.sin(d5);
        double xnewY = localLocation1.getY() + 1.0D + 1.0D * Math.cos(d6);
        Location localLocation3 = new Location(this.player.getWorld(), xd7, xnewY, xd8);
        playLightningbendingParticle(localLocation3, 0.1, 0.1, 0.1);
        localLocation3.getWorld().playSound(localLocation3, Sound.ENTITY_CREEPER_HURT, 0.5F, 0F);
        localLocation3.getWorld().playSound(localLocation3, Sound.BLOCK_BEEHIVE_WORK, 0.5F, 0F);
        this.particleRotation++;
        this.loc = this.player.getLocation().add(0.0D, -1.0D, 0.0D).clone();
        ActionBar.sendActionBar(Element.LIGHTNING.getColor() + "* Absorbing *", new Player[] { this.player });
    }


    private void absorbtion2() {
        Location localLocation1 = this.player.getLocation();
        double d1 = 0.1570796326794897D;
        double d2 = 0.06283185307179587D;
        double d3 = 3.0D;
        double d4 = 3.0D;
        double d5 = 0.1570796326794897D * this.particleRotation;
        double d6 = 0.06283185307179587D * this.particleRotation;
        double d7 = localLocation1.getX() + 1.0D * Math.cos(d5);
        double d8 = localLocation1.getZ() + 1.0D * Math.sin(d5);
        double newY = localLocation1.getY() + 4.0D + 4.0D * Math.cos(d6);
        Location localLocation2 = new Location(this.player.getWorld(), d7, newY, d8);
        playLightningbendingParticle(localLocation2, 0.2, 0.2, 0.2);
        double xd7 = localLocation1.getX() + 1.0D * -Math.cos(d5);
        double xd8 = localLocation1.getZ() + 1.0D * -Math.sin(d5);
        double xnewY = localLocation1.getY() + 4.0D + 4.0D * Math.cos(d6);
        Location localLocation3 = new Location(this.player.getWorld(), xd7, xnewY, xd8);

        playLightningbendingParticle(localLocation3, 0.2, 0.2, 0.2);
        playLightningbendingParticle(localLocation3, 0.2, 0.2, 0.1);
        playLightningbendingParticle(localLocation3, 0.1, 0.1, 0.1);
        localLocation3.getWorld().playSound(localLocation3, Sound.ENTITY_CREEPER_HURT, 0.5F, 0F);
        localLocation3.getWorld().playSound(localLocation3, Sound.BLOCK_BEEHIVE_WORK, 0.5F, 0F);

        this.particleRotation++;
        this.loc = this.player.getLocation().add(0.0D, -1.0D, 0.0D).clone();
    }


    private void pulse() {
        this.t += 0.3141592653589793D;
        for (double theta = 0.0D; theta <= 6.283185307179586D; theta += 0.04908738521234052D) {
            double x = this.t * Math.cos(theta);
            double y = 0.3D * Math.exp(-0.1D * this.t) * Math.sin(this.t) + 1.5D;
            double z = this.t * Math.sin(theta);
            this.loc.add(x, y, z);
           playLightningbendingParticle(this.loc, 0.1F, 0.1F, 0.1F);
            this.loc.subtract(x, y, z);
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.loc, this.t)) {
                if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                    DamageHandler.damageEntity(entity, this.damage, (Ability)this);
                    LivingEntity le = (LivingEntity)entity;
                    electrocute(le);
                    entity.setVelocity(this.player.getEyeLocation().getDirection().normalize().multiply(this.push));
                }
            }
        }
    }

    private void superArmor() {
        if (this.isSAEnabled)
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int)(this.chargeTime / 1000L * 20L), this.resPower - 1));
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getName() {
        return "VoltaicPulse";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getAuthor() {
        return "_Hetag1216_ & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return true;
    }

    public String getDescription() {
        return "Lightningbenders are able to absorb the power of the true lightning to the last speck of it and discharge all the power at once.";
    }

    public String getInstructions() {
        return "- Hold-Shift! -";
    }
    public void remove() {
        super.remove();
        this.electrocuted.clear();
    }

}
