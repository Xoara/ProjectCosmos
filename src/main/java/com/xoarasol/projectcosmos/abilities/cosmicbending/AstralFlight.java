package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AstralFlight extends CosmicAbility implements AddonAbility {

    private long duration;
    public long startFog = 1200;
    private long cooldown;
    private int currPoint;
    private double flightSpeed;
    Location location = player.getLocation().clone().add(0, 0, 0);

    public AstralFlight(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, AstralFlight.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.AstralFlight.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.AstralFlight.MaxDuration");
            this.flightSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.AstralFlight.FlightSpeed");

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
        defy();
        sneakFog();
        fog();
    }

    public void defy() {

        if (System.currentTimeMillis() > getStartTime() + startFog) {
            Location loc = player.getLocation().clone().add(0, 0, 0);
            VoidRing(60, 0.8F, 4);
            VoidRing2(60, 1.0F, 4);
            VoidRing3(60, 1.2F, 4);

            VoidRings(60, 0.8F, 4);
            VoidRings2(60, 1.0F, 4);
            VoidRings3(60, 1.2F, 4);

            loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.1F, 1.15F);
        }
        allowFlight();
        return;
    }

    private void fog() {
        if (System.currentTimeMillis() < getStartTime() + startFog) {
            Location loc = player.getLocation().clone().add(0, 0, 0);
            ParticleEffect.WHITE_ASH.display(loc, 10, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F, 0.02);

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.8F)).display(loc, 3, 1.1, 1.1, 1.1);

            } else {

                (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.8F)).display(loc, 3, 1.1, 1.1, 1.1);

            }

        }
    }

    public void sneakFog() {
        if (player.isSneaking() || player.isSprinting()) {
            Location location = player.getLocation().clone().add(0, 0, 0);
            ParticleEffect.WHITE_ASH.display(location, 10, 0.3F, 0F, 0.3F, 0);
        }
    }

    private void VoidRing2(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, -0.25D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
        }
    }

    private void VoidRing(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, -0.25D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
        }
    }

    private void VoidRing3(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, -0.25D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
        }
    }

    private void VoidRings2(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, 0.05D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
        }
    }

    private void VoidRings(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, 0.05D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
        }
    }

    private void VoidRings3(int points, float size, int speed) {
        for (int i = 0; i < speed; i++) {
            this.currPoint += 360 / points;
            if (this.currPoint > 360)
                this.currPoint = 0;
            double angle = this.currPoint * Math.PI / 180.0D;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            Location loc = this.player.getLocation().add(x, 0.05D, z);
            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {

                (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            } else {

                (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.3F)).display(loc, 2, 0.05, 0.05, 0.05);

            }
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
        if (CoreAbility.hasAbility(player, AstralFlight.class)) {
            AstralFlight or = CoreAbility.getAbility(player, AstralFlight.class);
            or.remove();
        }
    }

    @Override
    public void remove() {
        removeFlight();
        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1F, 0.76F);
        Location loc = player.getLocation().clone().add(0, 0, 0);
        ParticleEffect.SMOKE_NORMAL.display(loc, 20, (float) Math.random() / 1F, (float) 0.5F, (float) Math.random() / 1F, 0.02F);
        bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.AstralFlight.Enabled");
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
        return "AstralFlight";
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
        return "Cosmicbenders are able to defy gravity, making them able to levitate in the air or fly really quickly!";
    }

    public String getInstructions() {
        return "- Tap-Shift! - \n" +
                "- Deactivation: Left-Click! -";
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
