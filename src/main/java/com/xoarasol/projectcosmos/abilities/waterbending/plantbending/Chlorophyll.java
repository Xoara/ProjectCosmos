package com.xoarasol.projectcosmos.abilities.waterbending.plantbending;


import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Chlorophyll extends PlantAbility implements AddonAbility {

    private long cooldown, duration;
    private int amplifier;

    public Chlorophyll(Player player) {
        super(player);
        cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Water.Plant.Chlorophyll.Cooldown");
        duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Water.Plant.Chlorophyll.Duration");
        amplifier = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.Chlorophyll.Amplifier");

        if (bPlayer.isOnCooldown(this) || !bPlayer.canBend(this)) {
            return;
        }
        start();
    }

    @Override
    public void progress() {
        ParticleEffect.BLOCK_CRACK.display(player.getLocation(), 18, 0.5, 1.5, 0.5, 0.05, Material.MOSS_BLOCK.createBlockData());
        ParticleEffect.BLOCK_DUST.display(player.getLocation(), 18, 0.5, 1.5, 0.5, 0.05, Material.FLOWERING_AZALEA.createBlockData());
        ParticleEffect.BLOCK_CRACK.display(player.getLocation(), 18, 0.5, 1.5, 0.5, 0.05, Material.RAW_GOLD_BLOCK.createBlockData());
        ParticleEffect.BLOCK_DUST.display(player.getLocation(), 18, 0.5, 1.5, 0.5, 0.05, Material.MOSS_BLOCK.createBlockData());
        ParticleEffect.END_ROD.display(player.getLocation(), 18, 0.5, 1.5, 0.5, 0.05);


        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.7f, 0.86f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 0.8f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ALLAY_HURT, 1f, 1.4f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (duration / 1000 * 20), amplifier - 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (int) (duration / 1000 * 20), amplifier - 1));
        remove();
        bPlayer.addCooldown(this);
        return;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return player != null ? player.getLocation() : null;
    }

    @Override
    public String getName() {
        return "Chlorophyll";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Water.Plant.Chlorophyll.Enabled");
    }

    public String getDescription() {
        return "Chloroplasts can absorb light, healing Plantbenders and speeding them up in the meantime!";
    }

    public String getInstructions() {
        return "- Left-Click! -";
    }

    public boolean isHarmlessAbility() {
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
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    public boolean isSneakAbility() {
        return false;
    }

}
