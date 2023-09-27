package com.xoarasol.projectcosmos.api;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class LaserAbility extends ElementalAbility {

    public LaserAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return PCElement.LASER;
    }

    public boolean isIgniteAbility() {
        return false;
    }

    public boolean isExplosiveAbility() {
        return false;
    }

    public static void setRadiance(LivingEntity entity, int duration) {
        if (isRadiant(entity)) {
            removeRadiance(entity);
        }
        if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 1, false, false, false));
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_BELL_RESONATE, 0.5F, 2);

            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "*Irradiated*"));
            }
            ProjectCosmos.plugin.radiantEntities.add(entity);

            Bukkit.getServer().getScheduler().runTaskLater(ProjectCosmos.plugin, new Runnable() {
                @Override
                public void run() {
                    if (isRadiant(entity)) {
                        removeRadiance(entity);
                    }
                }
            }, duration);
        }
    }

    public static int getRadianceDuration(LivingEntity entity) {
        if (isRadiant(entity)) {
            if (entity.hasPotionEffect(PotionEffectType.GLOWING)) {
                return entity.getPotionEffect(PotionEffectType.GLOWING).getDuration();
            }
        }
        return 0;
    }

    public static void removeRadiance(LivingEntity entity) {
        if (isRadiant(entity)) {
            entity.removePotionEffect(PotionEffectType.GLOWING);
            ProjectCosmos.plugin.radiantEntities.remove(entity);
        }
    }

    public static boolean isRadiant(LivingEntity entity) {
        return ProjectCosmos.plugin.radiantEntities.contains(entity);
    }

}
