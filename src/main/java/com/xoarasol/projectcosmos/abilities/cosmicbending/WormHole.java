package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WormHole extends CosmicAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute("PortalRadius")
    private double radius;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute("TeleportCooldown")
    private long teleportCD;

    private Location origin1;
    private Location origin2;
    private Location loc1;
    private Location loc2;
    private Vector dir1;
    private Vector dir2;

    private Location origin;
    private Location location;
    private Vector direction;

    private boolean isOnePlaced;
    private boolean isTwoPlaced;
    private boolean isTwoFired;
    private boolean hasTeleported;

    double currPoint1 = 0;
    double currPoint2 = 0;

    Random rand = new Random();

    private long time;
    private int an;

    public WormHole(Player player) {
        super(player);

        if (CoreAbility.hasAbility(player, WormHole.class)) {
            WormHole wormHole = CoreAbility.getAbility(player, WormHole.class);
            wormHole.fireProjectile();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);
            return;
        }

        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.WormHole.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.WormHole.Duration");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.WormHole.PortalRadius");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.WormHole.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.WormHole.Speed") * (ProjectKorra.time_step / 1000F);
            this.teleportCD = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.WormHole.TeleportCooldown");

            this.origin1 = player.getEyeLocation();
            this.loc1 = origin1.clone();
            this.dir1 = origin1.getDirection();

            this.origin = GeneralMethods.getMainHandLocation(this.player);
            this.location = origin.clone();
            this.direction = origin.getDirection();

            this.isOnePlaced = false;
            this.isTwoPlaced = false;
            this.isTwoFired = false;

            this.time = 0;

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (System.currentTimeMillis() > this.time + teleportCD) {
            this.hasTeleported = false;
        }
        if ((System.currentTimeMillis() > getStartTime() + duration)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        progressPortal1();
        progressPortal2();
    }


    private void progressPortal1() {
        if (!isOnePlaced) {
            for (int i = 0; i <= 2; i++) {
                if (loc1.distance(origin1) > range) {
                    isOnePlaced = true;
                    break;
                }
                if (GeneralMethods.isSolid(loc1.getBlock()) || isWater(loc1.getBlock())) {
                    ParticleEffect.FLASH.display(loc1, 3, 0.5, 0.5, 0.5);
                    loc1.getWorld().playSound(loc1, Sound.ENTITY_IRON_GOLEM_REPAIR, 3, 0);
                    loc1.getWorld().playSound(loc1, Sound.ENTITY_WITHER_HURT, 3, 0);
                    loc1.getWorld().playSound(loc1, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 3, 0.8f);
                    isOnePlaced = true;
                    break;
                }

                new ColoredParticle(Color.fromRGB(255, 255, 255), 2).display(loc1, 2, 0, 0, 0);
                ParticleEffect.SPIT.display(loc1, 1, 0, 0, 0);
                Spirals();

                if (i == 0) {
                    loc1.getWorld().playSound(loc1, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1.2F);
                }

                loc1.add(dir1.normalize().multiply(speed / 2));
            }
        } else {
            Location loc;
            for (double radius = this.radius + 1; radius > 0; radius -= 0.5) {
                for (int i = 0; i < 3; i++) {
                    currPoint1 += 360 / 180D;

                    if (currPoint1 > 360) {
                        currPoint1 = 0;
                    }

                    double angle = currPoint1 * Math.PI / 180.0D;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    loc = loc1.clone().add(x, 0.5, z);


                    if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                        new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                    } else {
                        new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                    }

                    if (rand.nextInt(40) == 0) {
                        ParticleEffect.SPIT.display(loc1.clone().add(0, 0.6, 0), 5, 0.3, 0.3, 0.3);
                    }

                    if ((System.currentTimeMillis() > time + teleportCD) && (isOnePlaced && isTwoPlaced) && !hasTeleported) {
                        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc1, this.radius)) {
                            ParticleEffect.FLASH.display(loc1, 2, 0.3, 0.3, 0.3);
                            loc1.getWorld().playSound(loc1, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.64f);

                            entity.teleport(loc2.clone().add(0, 0.5, 0));

                            ParticleEffect.FLASH.display(loc2, 2, 0.3, 0.3, 0.3);
                            loc2.getWorld().playSound(loc2, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.64f);

                            hasTeleported = true;
                            this.time = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
    }

    private void progressPortal2() {
        if (isTwoFired) {
            if (!isTwoPlaced) {
                for (int i = 0; i <= 2; i++) {
                    if (loc2.distance(origin2) > range) {
                        isTwoPlaced = true;
                        break;
                    }
                    if (GeneralMethods.isSolid(loc2.getBlock()) || isWater(loc2.getBlock())) {
                        ParticleEffect.FLASH.display(loc2, 3, 0.5, 0.5, 0.5);
                        loc2.getWorld().playSound(loc2, Sound.ENTITY_IRON_GOLEM_REPAIR, 3, 0);
                        loc2.getWorld().playSound(loc2, Sound.ENTITY_WITHER_HURT, 3, 0);
                        loc2.getWorld().playSound(loc2, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 3, 0.8f);
                        isTwoPlaced = true;
                        break;
                    }

                    new ColoredParticle(Color.fromRGB(0, 0, 0), 2F).display(loc2, 2, 0, 0, 0);
                    ParticleEffect.SQUID_INK.display(loc2, 1, 0, 0, 0);
                    Spirals2();

                    if (i == 0) {
                        loc2.getWorld().playSound(loc2, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 0.85f);
                    }

                    loc2.add(dir2.normalize().multiply(speed / 2));
                }
            } else {
                Location loc;
                for (double radius = this.radius + 1; radius > 0; radius -= 0.5) {
                    for (int i = 0; i < 3; i++) {
                        currPoint1 += 360 / 180D;

                        if (currPoint1 > 360) {
                            currPoint1 = 0;
                        }

                        double angle = currPoint1 * Math.PI / 180.0D;
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);

                        loc = loc2.clone().add(x, 0.5, z);

                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        } else {
                            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(loc, 1, 0.05, 0.05, 0.05);
                        }

                        if (rand.nextInt(40) == 0) {
                            ParticleEffect.SQUID_INK.display(loc2.clone().add(0,0.6,0), 5, 0.3, 0.3, 0.3);
                        }

                        if ((System.currentTimeMillis() > time + teleportCD) && (isOnePlaced && isTwoPlaced) && !hasTeleported) {
                            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc2, this.radius)) {
                                ParticleEffect.FLASH.display(loc2, 2, 0.3, 0.3, 0.3);
                                loc2.getWorld().playSound(loc2, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.64f);

                                entity.teleport(loc1.clone().add(0, 0.5, 0));

                                ParticleEffect.FLASH.display(loc1, 2, 0.3, 0.3, 0.3);
                                loc1.getWorld().playSound(loc1, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.64f);

                                hasTeleported = true;
                                this.time = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
        }
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 1.0D) {
                Location l = this.loc1.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 0.9D)
                    r = 0.9D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        } else {
                            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        }

                        break;
                    case 1:
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        } else {
                            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        }

                        break;
                }
            }
        }
    }

    private void Spirals2() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 1.0D) {
                Location l = this.loc2.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 0.9D)
                    r = 0.9D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        } else {
                            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        }

                        break;
                    case 1:
                        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                            new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(45, 0, 130), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(66, 0, 188), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        } else {
                            new ColoredParticle(Color.fromRGB(72, 49, 175), 1.2f).display(GeneralMethods.getLeftSide(pl, .70).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(80, 78, 196), 1.2f).display(GeneralMethods.getLeftSide(pl, .35).add(0, 0, 0), 2, 0.05, 0.05, 0.05);
                            new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2f).display(pl, 2, 0.05, 0.05, 0.05);
                        }

                        break;
                }
            }
        }
    }

    public void fireProjectile() {
        if (!isTwoFired) {
            isTwoFired = true;
            origin2 = player.getEyeLocation();
            loc2 = origin2.clone();
            dir2 = origin2.getDirection();

            progressPortal2();

        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.WormHole.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "WormHole";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locs = new ArrayList<>();
        locs.add(loc1);
        locs.add(loc2);
        return locs;
    }

    @Override
    public String getDescription() {
        return "A powerful Cosmicbender can temporarily sever the bounds of time and space to create portals to travel between dimensions.";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click to create a portal, Left-Click again to create the second one! -";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272 & XoarSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
