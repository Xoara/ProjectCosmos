package com.xoarasol.projectcosmos.abilities.cosmicbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class AccretionDisk extends GravityAbility implements ComboAbility, AddonAbility {

    private long cooldown;
    private long interval;
    private double maxRadius;
    private double maxSpreads;
    private double damage;
    private double knockup;
    private double speed;
    private long duration;
    private long duration1;
    private int amplifier;
    private int amplifier1;

    private long time;
    private float radius, spreads, fake_radius;

    public AccretionDisk(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.AccretionDisk.Cooldown");
            interval = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.AccretionDisk.DamageInterval");
            maxRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.AccretionDisk.MaxRadius");
            maxSpreads = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.AccretionDisk.MaxSpreads");
            damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.AccretionDisk.Damage");
            speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.AccretionDisk.Speed");
            knockup = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.AccretionDisk.Knockup");
            duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.AccretionDisk.Levitation.Duration");
            amplifier = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Combos.AccretionDisk.Levitation.Amplifier");
            duration1 = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.AccretionDisk.SlowFalling.Duration");
            amplifier1 = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Combos.AccretionDisk.SlowFalling.Amplifier");
            start();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.7f, 0.7f);
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (radius < maxRadius) {
            radius += speed;
            display(Mode.started);
        } else {
            if (spreads < maxSpreads) {
                display(Mode.finished);
                if (System.currentTimeMillis() >= time + interval) {
                    spread();
                    affect();
                    spreads++;
                    time = System.currentTimeMillis();
                }
            } else {
                remove();
                bPlayer.addCooldown(this);
                return;
            }
        }
    }

    private void spread() {
        for (int d = 0; d < 360; d += 10) {
            double radians = Math.toRadians(d);
            double x = radius * Math.cos(radians);
            double z = radius * Math.sin(radians);
            final Location loc = this.getLocation().add(x, 1, z);

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                (new ColoredParticle(Color.fromRGB(0, 0, 0), 1.0F)).display(loc, 1, 0, 0.1, 0);
                ParticleEffect.SQUID_INK.display(loc, 4, 0, 0, 0, 0.02);
                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_HURT, 0.7f, 0f);
            } else {
                (new ColoredParticle(Color.fromRGB(178, 191, 255), 1.75F)).display(loc, 1, 0, 0.1, 0);
                ParticleEffect.CLOUD.display(loc, 4, 0, 0, 0, 0.02);
                loc.getWorld().playSound(loc, Sound.ITEM_TRIDENT_RETURN, 0.7f, 0.7f);
            }


            ParticleEffect.END_ROD.display(loc, 4, 0, 0, 0, 0.02);

            loc.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_REPAIR, 0.7f, 0f);
            loc.subtract(x, 0, z);
        }
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

    enum Mode {
        started, finished;
    }

    private void display(Mode mode) {
        if (mode == Mode.started) {
            for (int d = 0; d < 360; d += 20) {
                double radians = Math.toRadians(d);
                double x = radius * Math.cos(radians);
                double z = radius * Math.sin(radians);
                final Location loc = this.getLocation().add(x, 1, z);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(loc, 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(GeneralMethods.getRightSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                } else {
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(loc, 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(GeneralMethods.getRightSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                }

                loc.subtract(x, 0, z);
            }
        } else if (mode == Mode.finished) {
            for (int d = 0; d < 360; d += 10) {
                double radians = Math.toRadians(d);
                double x = fake_radius * Math.cos(radians);
                double z = fake_radius * Math.sin(radians);
                final Location loc = this.getLocation().add(x, 1, z);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(loc, 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(GeneralMethods.getLeftSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(GeneralMethods.getRightSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                } else {
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(loc, 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(GeneralMethods.getLeftSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(GeneralMethods.getRightSide(loc, .25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                }

                loc.subtract(x, 0, z);
                fake_radius -= 0.1;
                if (fake_radius <= 0) {
                    fake_radius = radius - 1;
                }
            }
        }
    }
    private void affect() {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.getLocation(), this.radius)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                DamageHandler.damageEntity(entity, damage, this);
                Vector v = new Vector(0, 0, 0);
                v.setY(knockup);
                entity.setVelocity(v);
                LivingEntity le = (LivingEntity) entity;
                le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (duration * 20 / 1000), amplifier - 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, (int) (duration1 * 20 / 1000), amplifier1 - 1));
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Combos.AccretionDisk.Enabled");
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
        return "AccretionDisk";
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
    public boolean isSneakAbility() {
        return true;
    }

    public String getDescription() {
        return "This is a very dangerous technique and is very difficult to use! Advanced Cosmicbenders can disrupt gravity in a field around" +
                " them. People and entities entering this field will take damage while floating in different directions.";
    }

    public String getInstructions() {
        return "- GalacticRipple (Tap-Shift 2x) > BlastOff (Left-Click) > GalacticRipple (Tap-Shift) -";
    }

    @Override
    public Object createNewComboInstance(Player arg0) {
        return new AccretionDisk(arg0);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        ArrayList<AbilityInformation> gf = new ArrayList<>();
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_DOWN));
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_UP));
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_DOWN));
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_UP));
        gf.add(new AbilityInformation("BlastOff", ClickType.LEFT_CLICK));
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_DOWN));
        gf.add(new AbilityInformation("GalacticRipple", ClickType.SHIFT_UP));

        return gf;
    }
}
