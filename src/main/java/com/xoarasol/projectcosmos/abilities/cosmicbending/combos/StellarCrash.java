package com.xoarasol.projectcosmos.abilities.cosmicbending.combos;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StellarCrash extends CosmicAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.SPEED)
    private double downForce;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute("MaxDamage")
    private double maxDamage;
    @Attribute("BaseDamage")
    private double baseDamage;
    @Attribute("HeightScale")
    private double heightScale;
    @Attribute(Attribute.RANGE)
    private double damageRange;
    @Attribute("PullRange")
    private double pullRange;
    @Attribute("PullForce")
    private double pullForce;

    private Location origin;

    private int currPoint1;
    private int currPoint2;

    public StellarCrash(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.StellarCrash.Cooldown");
            this.downForce = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.DownForce");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.StellarCrash.Duration");
            this.maxDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.MaxDamage");
            this.baseDamage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.BaseDamage");
            this.heightScale = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.StellarCrash.HeightScale");
            this.damageRange = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.DamageRange");
            this.pullRange = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.PullRange");
            this.pullForce = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.StellarCrash.PullForce");

            this.origin = player.getLocation();

            this.flightHandler.createInstance(this.player, this.getName());
            player.setAllowFlight(true);

            this.bPlayer.addCooldown(this);
            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this) || !this.bPlayer.getBoundAbilityName().equalsIgnoreCase("BlastOff")) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime()) {
            remove();
            return;
        }
        if (GeneralMethods.isSolid(this.player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            detonate();
            return;
        }

        Vector vec = new Vector(0, -1, 0);
        this.player.setVelocity(vec.normalize().multiply(downForce));

        for (int i = 0; i < 15; i++) {
            currPoint1 += 360 / 60;

            if (currPoint1 > 360) {
                currPoint1 = 0;
            }

            double angle = currPoint1 * Math.PI / 180.0D;
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location loc = player.getLocation().add(x, 0, z);
            ParticleEffect.END_ROD.display(loc, 1, 0, 0, 0, 0.03);
            ParticleEffect.SQUID_INK.display(loc, 1, 0, 0, 0, 0);
            loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 2f);
        }

        for (int i = 0; i < 15; i++) {
            currPoint2 -= 360 / 60;

            if (currPoint2 < 0) {
                currPoint2 = 360;
            }

            double angle = currPoint2 * Math.PI / 180.0D;
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location loc = player.getLocation().add(x, 0, z);
            new ColoredParticle(Color.fromRGB(20, 0, 60), 2).display(loc, 1, 0, 0, 0);
        }
    }

    private void detonate() {
        double distance = origin.distance(this.player.getLocation());

        if (distance >= heightScale) {
            for (double radius = 0.5; radius <= damageRange; radius += 0.5) {
                for (int i = 0; i <= 360; i++) {
                    double x, z;

                    x = radius * Math.cos(i);
                    z = radius * Math.sin(i);

                    Location loc = player.getLocation().add(x, 0.1, z);
                    if (ThreadLocalRandom.current().nextInt(15) == 0) {
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            (new ColoredParticle(Color.fromRGB(66, 0, 188), 2.0F)).display(loc, 1, 0, 0, 0);
                        } else {
                            (new ColoredParticle(Color.fromRGB(109, 133, 255), 2.0F)).display(loc, 1, 0, 0, 0);
                        }
                    }
                    if (ThreadLocalRandom.current().nextInt(15) == 0) {
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            (new ColoredParticle(Color.fromRGB(13, 0, 56), 2.0F)).display(loc, 1, 0, 0, 0);
                        } else {
                            (new ColoredParticle(Color.fromRGB(80, 78, 196), 2.0F)).display(loc, 1, 0, 0, 0);
                        }
                    }
                    if (ThreadLocalRandom.current().nextInt(15) == 0) {
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            (new ColoredParticle(Color.fromRGB(45, 0, 130), 2.0F)).display(loc, 1, 0, 0, 0);
                        } else {
                            (new ColoredParticle(Color.fromRGB(72, 49, 175), 2.0F)).display(loc, 1, 0, 0, 0);
                        }
                    }
                    if (ThreadLocalRandom.current().nextInt(20) == 0) {
                        ParticleEffect.END_ROD.display(loc, 1, 0.1, 0.1, 0.1);
                        loc.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.45f, 0);
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_HURT, 0.45f, 0);
                        } else {
                            loc.getWorld().playSound(loc, Sound.ITEM_TRIDENT_RETURN, 0.45f, 0.6f);
                        }

                    }
                }
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), pullRange)) {
                if (!entity.getUniqueId().equals(player.getUniqueId())) {
                    Vector vec = PCMethods.createDirectionalVector(entity.getLocation(), player.getLocation());
                    GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(pullForce));
                }
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), damageRange)) {
                if (!entity.getUniqueId().equals(player.getUniqueId())) {
                    double damage = baseDamage * Math.round(distance / heightScale);
                    if (damage > maxDamage) {
                        damage = maxDamage;
                    }

                    DamageHandler.damageEntity(entity, player, damage, this);
                }
            }
        }
        remove();
    }


    @Override
    public void remove() {
        this.flightHandler.removeInstance(player, this.getName());
        player.setAllowFlight(false);
        player.setFallDistance(0);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Combos.StellarCrash.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "StellarCrash";
    }

    @Override
    public Location getLocation() {
        return this.player.getLocation();
    }

    @Override
    public String getDescription() {
        return "Cosmicbenders can increase their mass exponentially, and send themselves rocketing towards the ground. \n" +
                "-\n" +
                "\"The element of creation heeds my— WHOOPS DAMMIT! ...Alright, are my kneecaps still where they're supposed to be? Thank the stars.\"";
    }

    @Override
    public String getInstructions() {
        return "- CosmicBlast (Tap-Shift 2x) > BlastOff (Left-Click) -";
    }


    @Override
    public Object createNewComboInstance(Player player) {
        return new StellarCrash(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> gravityCrash = new ArrayList<>();
        gravityCrash.add(new AbilityInformation("CosmicBlast", ClickType.SHIFT_DOWN));
        gravityCrash.add(new AbilityInformation("CosmicBlast", ClickType.SHIFT_UP));
        gravityCrash.add(new AbilityInformation("CosmicBlast", ClickType.SHIFT_DOWN));
        gravityCrash.add(new AbilityInformation("CosmicBlast", ClickType.SHIFT_UP));
        gravityCrash.add(new AbilityInformation("BlastOff", ClickType.LEFT_CLICK));
        return gravityCrash;
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