package com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class MassManipulation extends GravityAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("SpeedFactor")
    private int speedFactor;
    @Attribute("JumpFactor")
    private int jumpFactor;
    @Attribute("SlowFallingFactor")
    private int fallingFactor;
    @Attribute("KnockReduction")
    private double knockReduction;
    @Attribute(Attribute.KNOCKBACK)
    private double knockback;
    @Attribute("PullPower")
    private double pullPower;
    private AttributeModifier knockMod;

    private ArrayList<UUID> affected = new ArrayList<>();

    private boolean click;
    private boolean cancelsWithDuration = false;
    private int pstage;

    public MassManipulation(Player player, boolean click) {
        super(player);


        if (CoreAbility.hasAbility(player, MassManipulation.class) && click) {
            MassManipulation massManipulation = CoreAbility.getAbility(player, MassManipulation.class);
            massManipulation.remove();
            return;
        }

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, MassManipulation.class)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.MassManipulation.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.MassManipulation.Duration");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MassManipulation.Damage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MassManipulation.PullRange");
            this.speedFactor = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.MassManipulation.SpeedFactor");
            this.jumpFactor = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.MassManipulation.JumpFactor");
            this.fallingFactor = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.MassManipulation.FallingFactor");
            this.knockReduction = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MassManipulation.KnockReduction");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MassManipulation.Knockback");
            this.pullPower = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MassManipulation.PullPower");


            this.click = click;
            if (duration > 0) {
                this.cancelsWithDuration = true;
            }
            if (click) {
                this.knockMod = new AttributeModifier("KnockReduction", knockReduction, AttributeModifier.Operation.ADD_NUMBER);
                player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(knockMod);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

            }

            this.bPlayer.addCooldown(this);
            this.start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if ((cancelsWithDuration) && System.currentTimeMillis() > duration + getStartTime()) {
            remove();
            return;
        }
        if (click) {
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                new ColoredParticle(Color.fromRGB(66, 0, 188), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(45, 0, 130), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(13, 0, 56), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
            } else {
                new ColoredParticle(Color.fromRGB(109, 133, 255), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(80, 78, 196), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(72, 49, 175), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.05F, 0F);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.05F, 1.65F);
            SolarOmens();
            SolarOmens2();
            SolarOmens3();

            if (this.player.isSneaking() && this.bPlayer.getBoundAbilityName().equalsIgnoreCase("MassManipulation")) {
                boolean hit = false;
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), range)) {
                    if (!entity.getUniqueId().equals(player.getUniqueId()) && !affected.contains(entity.getUniqueId())) {
                        Vector vec = PCMethods.createDirectionalVector(entity.getLocation(), player.getLocation());
                        GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(pullPower));

                        if (entity.getLocation().distance(player.getLocation()) < 0.8) {
                            Vector knockDir = PCMethods.createDirectionalVector(player.getLocation(), entity.getLocation());
                            knockDir.setY(0.5);
                            DamageHandler.damageEntity(entity, player, damage, this);
                            GeneralMethods.setVelocity(this, entity, knockDir.normalize().multiply(knockback));
                            affected.add(entity.getUniqueId());

                            Bukkit.getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 20 * 6);
                        }
                    }
                }
                if (hit) {
                    remove();
                }
            }
        } else {
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                new ColoredParticle(Color.fromRGB(66, 0, 188), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(45, 0, 130), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(13, 0, 56), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
            } else {
                new ColoredParticle(Color.fromRGB(109, 133, 255), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(80, 78, 196), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
                new ColoredParticle(Color.fromRGB(72, 49, 175), 1.05F).display(player.getLocation(), 1, 0.3, 0.1, 0.3);
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.05F, 1.8F);
            LunarOmens();
            LunarOmens2();
            LunarOmens3();

            if (speedFactor > 0 && !player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, speedFactor, false, false, false));
            }
            if (jumpFactor > 0 && !player.hasPotionEffect(PotionEffectType.JUMP)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 30, jumpFactor, false, false, false));
            }
            if (fallingFactor > 0 && !player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 30, fallingFactor, false, false, false));
            }
        }
    }

    private void SolarOmens() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (0.8 * Math.cos(angle));
        double z = centre.getZ() + (0.8 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
           }

        double x2 = centre.getX() + (0.8 * -Math.cos(angle));
        double z2 = centre.getZ() + (0.8 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }

        pstage++;
    }

    private void SolarOmens2() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (1 * Math.cos(angle));
        double z = centre.getZ() + (1 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);
        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void SolarOmens3() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (1.25 * Math.cos(angle));
        double z = centre.getZ() + (1.25 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (1.25 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.25 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void LunarOmens() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (0.8 * Math.cos(angle));
        double z = centre.getZ() + (0.8 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (0.8 * -Math.cos(angle));
        double z2 = centre.getZ() + (0.8 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void LunarOmens2() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (1 * Math.cos(angle));
        double z = centre.getZ() + (1 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    private void LunarOmens3() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 15;
        double angle = pstage * increment;
        double x = centre.getX() + (1.25 * Math.cos(angle));
        double z = centre.getZ() + (1.25 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.3, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.5F)).display(loc, 1, 0.05, 0.05, 0.05);
        }

        double x2 = centre.getX() + (1.25 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.25 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.3, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.5F)).display(loc2, 1, 0.05, 0.05, 0.05);
        }
        pstage++;
    }

    @Override
    public void remove() {
        if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        }
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            player.removePotionEffect(PotionEffectType.JUMP);
        }
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        if (click) {
            player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(knockMod);
        }
        this.bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
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

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "MassManipulation";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }


    @Override
    public String getDescription() {
        return "A master Cosmicbender themselves can change their mass, and how they are affected by gravity!\n" +
                "Increased Mass: A Cosmicbender will experience decreased knockback, and nearby entities may feel a \"slight\" pull towards the user, but will be sent flying upon contact!\n" +
                "- Decreased Mass: A Cosmicbender will gain many effects which mimic the low-gravity effects on the moon!";
    }

    @Override
    public String getInstructions() {
        return "\n" +
                "Increased Mass: *Left Click* to activate > *Hold Shift* to pull nearby entities closer to you! -\n" +
                "Decreased Mass: *Tap Shift* to activate! -";
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
