package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ConstellationCutter extends CosmicAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SELECT_RANGE)
    private int selectRange;
    @Attribute("MaxStars")
    private int maxStars;
    private long damageInterval;
    private int blastInterval;
    private double blastRadius;
    private double blastDamage;

    private long time;

    private ConcurrentHashMap<Integer, Location> stars = new ConcurrentHashMap<Integer, Location>();

    private int curStars;

    public ConstellationCutter(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, ConstellationCutter.class)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.ConstellationCutter.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.ConstellationCutter.Duration");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.ConstellationCutter.Damage");
            this.selectRange = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.ConstellationCutter.SelectRange");
            this.maxStars = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.ConstellationCutter.MaxStars");
            this.damageInterval = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.ConstellationCutter.DamageInterval");
            this.blastInterval = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.ConstellationCutter.BlastInterval");
            this.blastRadius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.ConstellationCutter.BlastRadius");
            this.blastDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.ConstellationCutter.BlastDamage");

            this.curStars = 0;
            this.time = System.currentTimeMillis() - damageInterval;

            createStar();
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }
        if (!stars.isEmpty()) {
            for (int id : stars.keySet()) {
                ParticleEffect.END_ROD.display(stars.get(id), 2, 0.1, 0.1, 0.1);
                ParticleEffect.CLOUD.display(stars.get(id), 3, 0.0,0.0,0.0);

                if (ThreadLocalRandom.current().nextInt(blastInterval) == 0) {
                    ParticleEffect.FLASH.display(stars.get(id), 1, 0, 0, 0);
                    stars.get(id).getWorld().playSound(stars.get(id), Sound.ENTITY_VEX_HURT, 1, 0);
                    stars.get(id).getWorld().playSound(stars.get(id), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.75f);

                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(stars.get(id), blastRadius)) {
                        if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                            DamageHandler.damageEntity(entity, player, blastDamage, this);
                        }
                    }
                }


                if (stars.size() > 1) {
                    Location loc = stars.get(id).clone();

                    if ((stars.size() - 1) >= id + 1) {
                        Vector direction = PCMethods.createDirectionalVector(loc, stars.get(id + 1));
                        double distance = loc.distance(stars.get(id + 1));

                        for (double i = 0; i <= distance; i += 0.7) {
                            ParticleEffect.END_ROD.display(loc, 1, 0,0,0);

                            if (loc.distance(stars.get(id)) > distance) {
                                continue;
                            }

                            if (System.currentTimeMillis() > time + damageInterval) {
                                for (Entity enity : GeneralMethods.getEntitiesAroundPoint(loc, 1)) {
                                    if (enity instanceof LivingEntity && !enity.getUniqueId().equals(player.getUniqueId())) {
                                        DamageHandler.damageEntity(enity, player, damage, this);
                                        time = System.currentTimeMillis();
                                        break;
                                    }
                                }
                            }

                            loc.add(direction.clone().normalize().multiply(0.7));
                        }
                    }
                }
            }
        }
    }

    public static void createStar(Player player) {
        if (CoreAbility.hasAbility(player, ConstellationCutter.class)) {
            ConstellationCutter cutter = CoreAbility.getAbility(player, ConstellationCutter.class);
            cutter.createStar();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

        }
    }

    public void createStar() {
        if (curStars <= maxStars) {
            Location loc = player.getLocation().add(0, 1, 0);
            loc.add(loc.getDirection().normalize().multiply(selectRange));
            stars.put(curStars, loc);
            curStars++;

            ParticleEffect.FLASH.display(loc, 1, 0, 0, 0);
        }

        int starsLeft = maxStars - curStars + 1;
        if (starsLeft == 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + "You " + ChatColor.DARK_PURPLE + "can't " + ChatColor.LIGHT_PURPLE + "create any more stars!"));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + "You can create " + ChatColor.DARK_PURPLE + starsLeft + ChatColor.LIGHT_PURPLE + " more stars!"));
        }
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        if (!stars.isEmpty()) {
            stars.clear();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.ConstellationCutter.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "ConstellationCutter";
    }

    @Override
    public Location getLocation() {
        return null;
    }


    @Override
    public String getDescription() {
        return "Cosmicbenders can create small stellar objects, which will fire beams towards eachother, damaging anything in their path.";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift > Left-Click multiple times in different directions! -";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
