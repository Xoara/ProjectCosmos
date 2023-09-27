package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class SpaceWarp extends CosmicAbility implements AddonAbility {
    private int timer;
    private boolean shot;
    private int cooldown;


    public SpaceWarp(Player player) {
        super(player);
        
        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.SpaceWarp.Cooldown");
            this.timer = 0;
            shot = false;

            bPlayer.addCooldown(this, this.cooldown);
            start();
            ActionBar.sendActionBar(PCElement.COSMIC.getColor() + "* Warping through the Cosmos. *", new Player[]{this.player});
        }
    }


    private Location findLocations() {
        Location x = player.getEyeLocation().clone();
        Vector y = x.getDirection();
        for (int i = 0; i < 20; i++) {
            if (x.getBlock().getType() == Material.AIR || x.getBlock().getType() == Material.WATER) {
                x.add(y);
            } else {
                break;
            }
        }
        return x;
    }

    @Override
    public void remove() {
        bPlayer.addCooldown(this, 12000);
        super.remove();
    }

    private void go() {
        Location origin = player.getLocation().clone();
        Location destination = findLocations();
        player.teleport(destination);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, .5f, 1f);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (origin.distance(destination) > 8) {
                    origin.add(GeneralMethods.getDirection(origin, destination).normalize().multiply(1.5));

                    ParticleEffect.SQUID_INK.display(origin, 7, 1.2, 1.2, 1.2, 0.1);
                    ParticleEffect.END_ROD.display(origin, 7, 1.2, 1.2, 1.2, 0.05);

                } else {
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(ProjectKorra.plugin, 1, 1);
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBindsCooldowns(this) || !player.isValid()) {
            remove();
            return;
        }
        timer++;
        if (timer > 10 && !shot) {
            shot = true;
            go();
            remove();
        } else {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, .5f, 1f);
            ParticleEffect.SQUID_INK.display(player.getLocation(), 7, 1.2, 1.2, 1.2, 0.1);
            ParticleEffect.END_ROD.display(player.getLocation(), 7, 1.2, 1.2, 1.2, 0.05);
             if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getLeftSide(player.getLocation(), .1).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(player.getLocation(), .5).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(player.getLocation(), 2,0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getRightSide(player.getLocation(), .5).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(GeneralMethods.getRightSide(player.getLocation(), .1).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
            } else {
                (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getLeftSide(player.getLocation(), .1).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(player.getLocation(), .5).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(player.getLocation(), 2,0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getRightSide(player.getLocation(), .5).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(GeneralMethods.getRightSide(player.getLocation(), .1).add(0, 0, 0), 2, 0.05, 0.05, 0.05);

            }

        }
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
    public String getDescription() {
        return "Advanced Cosmicbenders are able to warp through space and time itself by channeling the power of a wormhole around themselves.";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click! -";
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "SpaceWarp";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.SpaceWarp.Enabled");
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Lubdan and Xoara";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
