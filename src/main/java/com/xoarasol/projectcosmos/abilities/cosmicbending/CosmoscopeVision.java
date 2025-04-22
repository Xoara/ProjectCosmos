package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.event.PlayerStanceChangeEvent;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CosmoscopeVision extends CosmicAbility implements AddonAbility {

    protected UUID uuid;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.SPEED)
    private int vision;

    private CosmicAbility cosmicvision;

    public CosmoscopeVision(final Player player) {
        super(player);
        if (!this.bPlayer.canBend(this)) {
            return;
        }
        this.cooldown = 2000; //ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmoscopeVision.Cooldown");
        this.duration = 60000; //ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmoscopeVision.Duration");
        this.vision = 1; //ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmoscopeVision.NightVisionPower") - 1;
        this.uuid = player.getUniqueId();

        final CosmicAbility vision = getCosmicVision();
        if (vision != null) {
            vision.remove();
            if (vision instanceof CosmoscopeVision) {
                setCosmicvision(null);
                return;
            }
        }
        this.bPlayer.addCooldown(this);
        this.start();
        ParticleEffect.END_ROD.display(GeneralMethods.getLeftSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        ParticleEffect.END_ROD.display(GeneralMethods.getRightSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        ParticleEffect.ELECTRIC_SPARK.display(GeneralMethods.getLeftSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        ParticleEffect.ELECTRIC_SPARK.display(GeneralMethods.getRightSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        setCosmicvision(this);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.5F, 0.65F);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.5F, 1.65F);
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this) || !this.bPlayer.hasElement(PCElement.COSMIC)) {
            this.remove();
            return;
        } else if (this.duration != 0 && System.currentTimeMillis() > this.getStartTime() + this.duration) {
            this.remove();
            return;
        }

        if (!this.player.hasPotionEffect(PotionEffectType.NIGHT_VISION) || this.player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() < this.vision || (this.player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() == this.vision && this.player.getPotionEffect(PotionEffectType.NIGHT_VISION).getDuration() == 1)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, this.vision, true, false), true);
        }
    }

    public static void removeVision(Player player) {
        if (CoreAbility.hasAbility(player, CosmoscopeVision.class)) {
            CosmoscopeVision cs = CoreAbility.getAbility(player, CosmoscopeVision.class);
            cs.remove();
        }
    }

    @Override
    public void remove() {
        super.remove();
        setCosmicvision(null);
        ParticleEffect.SQUID_INK.display(GeneralMethods.getLeftSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        ParticleEffect.SQUID_INK.display(GeneralMethods.getRightSide(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.36D)), 0.35D), 4, 0, 0, 0, 0.02f);
        this.player.playSound(this.player.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1F, 0F);
        this.player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public void setCosmicvision(final CosmicAbility stance) {
        final String oldStance = (this.cosmicvision == null) ? "" : this.cosmicvision.getName();
        final String newStance = (stance == null) ? "" : stance.getName();
        this.cosmicvision = stance;
        ChatUtil.displayMovePreview(this.player);
        this.bPlayer.removeCooldown(this);
        final PlayerStanceChangeEvent event = new PlayerStanceChangeEvent(Bukkit.getPlayer(this.uuid), oldStance, newStance);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public String getName() {
        return "CosmoscopeVision";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getDescription() {
        return "This ability allows Cosmicbenders to see through the darkness of space, which makes it easier to map through the vastness of the cosmos.";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click to activate | Tap-Shift to deactivate! -";
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
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

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUUIDString() {
        return this.uuid.toString();
    }

    public int getVision() {
        return this.vision;
    }

    public void getVision(final int vision) {
        this.vision = vision;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public CosmicAbility getCosmicVision() {
        return this.cosmicvision;
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
