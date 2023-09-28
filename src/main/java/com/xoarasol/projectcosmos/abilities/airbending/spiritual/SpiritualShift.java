package com.xoarasol.projectcosmos.abilities.airbending.spiritual;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpiritualShift extends SpiritualAbility implements AddonAbility {

    private long duration;
    public long startFog = 1200;
    private long cooldown;
    private double flightSpeed;
    public static boolean onDamaged;
    public static boolean onAttack;
    Location location = player.getLocation().clone().add(0, 0, 0);

    public SpiritualShift(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, SpiritualShift.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Air.Spiritual.SpiritualShift.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Air.Spiritual.SpiritualShift.MaxDuration");
            this.flightSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Air.Spiritual.SpiritualShift.FlightSpeed");
            onAttack = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.Spiritual.SpiritualShift.Remove.onAttack");
            onDamaged = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.Spiritual.SpiritualShift.Remove.onDamaged");

            if (flightSpeed > 10 || flightSpeed < -10) {
                flightSpeed = 0;
            }

            flightSpeed /= 10;

            start();
            location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 0.01F);
        }
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBinds(this)) {
            removeFlight();
            this.bPlayer.addCooldown((Ability) this);
            remove();
            return;

        }
        Block eyeBlock = this.player.getEyeLocation().getBlock();
        if (GeneralMethods.isSolid(eyeBlock)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() - getStartTime() > duration) {
            remove();
            return;
        }
        vanish();
        sneakFog();
        fog();
    }

    public void vanish() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1, 1));
        if (System.currentTimeMillis() > getStartTime() + startFog) {
            Location loc = player.getLocation().clone().add(0, 0, 0);
            ParticleEffect.SPELL_INSTANT.display(loc, 1, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F, 0.02F);
            GeneralMethods.displayColoredParticle("00FFED", loc, 1, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F);
            loc.getWorld().playSound(loc, Sound.BLOCK_CHORUS_FLOWER_GROW, 0.1F, 0.01F);
        }
        allowFlight();
        return;
    }

    private void fog() {
        if (System.currentTimeMillis() < getStartTime() + startFog) {
            Location loc = player.getLocation().clone().add(0, 0, 0);
            ParticleEffect.SPELL_INSTANT.display(loc, 2, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F, 0.02F);
            playAirbendingParticles(loc, 1, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F);
        }
    }

    public void sneakFog() {
        if (player.isSneaking() || player.isSprinting()) {
            Location location = player.getLocation().clone().add(0, 0, 0);
            location.getWorld().playSound(location, Sound.ENTITY_STRAY_AMBIENT, 0.4F, 0.03F);
            GeneralMethods.displayColoredParticle("00FFED", location, 3, (float) Math.random(), (float) Math.random(), (float) Math.random());
            GeneralMethods.displayColoredParticle("00FFED", location, 10, (float) Math.random(), (float) Math.random(), (float) Math.random());
            ParticleEffect.SUSPENDED_DEPTH.display(location, 10, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.5F);
            GeneralMethods.displayColoredParticle("00FFED", location, 5, (float) Math.random(), (float) Math.random(), (float) Math.random());
            ParticleEffect.SUSPENDED_DEPTH.display(location, 5, 0.3F, 0F, 0.3F, 0);
            ParticleEffect.SPELL_INSTANT.display(location, 2, 0.3F, 0F, 0.3F, 0);
        }
    }

    private void allowFlight() {
        player.setFlySpeed((float) flightSpeed);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    private void removeFlight() {
        player.setFlySpeed(0.1F);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public static void disengage(Player player) {
        if (CoreAbility.hasAbility(player, SpiritualShift.class)) {
            SpiritualShift or = CoreAbility.getAbility(player, SpiritualShift.class);
            or.remove();
        }
    }

    @Override
    public void remove() {
        removeFlight();
        location.getWorld().playSound(location, Sound.ENTITY_ALLAY_HURT, 1F, 0.66F);
        Location loc = player.getLocation().clone().add(0, 0, 0);
        ParticleEffect.SMOKE_NORMAL.display(loc, 20, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F, 0.02F);
        bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Air.Spiritual.SpiritualShift.Enabled");
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return "SpiritualShift";
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

    public String getDescription() {
        return "Spiritual AirBenders are able to dissipate themselves using their pure spiritual energy!";
    }

    public String getInstructions() {
        return "- Simply Tap-Shift to SpiritualShift! To Deactivate, Left-Click! -"
                + "\n- While you're vanished, sneaking or sprinting you will release more spiritual essence, which will make you more visible! -"
                + "\n- If you get attacked or you attack someone while vanished, you will automatically disengage SpiritualShift. -";
    }
    @Override
    public boolean isSneakAbility() {
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

}