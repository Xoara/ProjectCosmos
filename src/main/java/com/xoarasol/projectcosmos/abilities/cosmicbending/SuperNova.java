package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.firebending.BlazeArc;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class SuperNova extends CosmicAbility implements AddonAbility {

    private int cooldown;
    private double range;
    private double damage;
    public static Config config;
    private long chargeTime;

    private boolean hasReached = true;
    private boolean launched;
    private boolean charged;

    private boolean fire;
    private int fireRadius;

    private double angle;

    private Vector direction;
    private Location origin;
    private Location location;
    private double rotation;
    private int counter;

    Random rand = new Random();
    private int pstage;
    private int an;

    public SuperNova(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
            return;
        }
        setFields();

        start();
    }

    private void setFields() {

        this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.SuperNova.Cooldown");
        this.chargeTime = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.SuperNova.ChargeTime");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.SuperNova.Damage");

        rotation = 0;
        counter = 0;

    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this) && (bPlayer.canBendIgnoreBinds(this) && (!bPlayer.canBendIgnoreCooldowns(this)))) {
            remove();
            return;
        }
        if (player.isDead() || !player.isOnline()) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + chargeTime) {
            charged = true;
        }
        if (player.isSneaking() && (charged)) {
            if (new Random().nextInt(8) == 0) {
                location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 0F);
            }
        }
        if ((player.isSneaking()) && (!launched)) {
            charge();
            displayFireStar();
        } else {
            if (!charged) {
                remove();
                return;
            }
            if (!launched) {
                bPlayer.addCooldown(this);
                launched = true;
            }
            if (GeneralMethods.isSolid(location.getBlock())) {
                if (BlazeArc.isIgnitable(location.getBlock().getRelative(BlockFace.UP))) {
                }
                remove();
                bPlayer.addCooldown(this);
                return;
            }
            Star();
        }
    }


    private void Star() {
        if (!GeneralMethods.isRegionProtectedFromBuild(player, "SuperNova", location)) {

            direction = GeneralMethods.getTargetedLocation(player, 5).getDirection();
            if (player.isSneaking()) {
                returnStar();
            }
            origin = player.getLocation().clone().add(0, 5, 0);
            location.add(direction);

            (new ColoredParticle(Color.fromRGB(192, 207, 255), 5.0F)).display(location, 1, 0.1D, 0.1D, 0.1D);
            (new ColoredParticle(Color.fromRGB(103, 111, 137), 5.0F)).display(location, 1, 0.1D, 0.1D, 0.1D);
            Spirals();
            Spirals2();

            if (rand.nextInt(6) == 0) {
                ParticleEffect.FLASH.display(location, 2, 0.2, 0.2, 0.2);
                location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.5F, 0.75F);
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1.75)) {
                if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId()
                        && !(entity instanceof ArmorStand)) {
                    DamageHandler.damageEntity(entity, damage, this);
                    remove();
                }
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    bPlayer.addCooldown(this);
                    return;
                }
            }
        }
        if (location.getBlock().isLiquid()) {
            remove();
            ParticleEffect.FLASH.display(location.getBlock().getLocation(), 2, 0.2, 0.2, 0.2);
            location.getBlock().getWorld().playSound(location.getBlock().getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 2F, 0.75F);
            bPlayer.addCooldown(this);
            return;
        }

        if (origin.distance(location) > range) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (new Random().nextInt(1) == 0) {
            location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 0.53F);
            location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 1.65f);
            location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 0.5F, 0.0F);
        }
    }

    private void returnStar() {
        Location loc = this.player.getEyeLocation();
        Vector dV = loc.getDirection().normalize();
        loc.add(new Vector(dV.getX() * 6.0D, dV.getY() * 6.0D, dV.getZ() * 6.0D));

        Vector vector = loc.toVector().subtract(location.toVector());
        this.direction = loc.setDirection(vector).getDirection().normalize();
    }

    public void charge() {
        if (charged) {
            Location loc = GeneralMethods.getTargetedLocation(player, 5);
            grid();
            angle -= 3;
            GeneralMethods.displayColoredParticle("C0CFFF", loc, 4, 0.15D, 0.15D, 0.15D);

        }
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 2; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 0.9D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 0.9D)
                    r = 0.9D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        ParticleEffect.SPELL_INSTANT.display(pl, 4, 0.09, 0.09, 0.09, 0.04);

                        break;
                    case 1:

                        ParticleEffect.SPELL_INSTANT.display(pl, 4, 0.09, 0, 0.09, 0.04);
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

                 d += 1.9D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 1.0D;
                if (r > 1.0D)
                    r = 1.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        new ColoredParticle(Color.fromRGB(145, 157, 193), 1.8f).display(pl, 3, 0, 0, 0);
                        break;
                    case 1:

                        new ColoredParticle(Color.fromRGB(145, 157, 193), 1.8f).display(pl, 3, 0, 0, 0);
                        break;
                }
            }
        }
    }

    public void displayFireStar() {
        if (hasReached) {
            location = GeneralMethods.getTargetedLocation(player, 5);
            origin = GeneralMethods.getTargetedLocation(player, 5);
            (new ColoredParticle(Color.fromRGB(192, 207, 255), 1.0F)).display(location, 1, 0.1D, 0.1D, 0.1D);
            (new ColoredParticle(Color.fromRGB(103, 111, 137), 1.0F)).display(location, 1, 0.1D, 0.1D, 0.1D);

        }
    }

    private void grid() {
        Location centre = GeneralMethods.getTargetedLocation(player, 5);
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.0 * Math.cos(angle));
        double z = centre.getZ() + (1.0 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0, z);

        ParticleEffect.SPELL_INSTANT.display(loc, 4, 0.5, 0.1, 0.5, 0f);

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0, z2);

        ParticleEffect.SPELL_INSTANT.display(loc2, 4, 0.5, 0.1, 0.5, 0f);

        pstage++;
    }


    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.SuperNova.Enabled");
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Location getOrigin() {
        return origin;
    }

    @Override
    public String getName() {
        return "SuperNova";
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

    @Override
    public String getDescription() {
        return "MoonFall in an extremely advanced Lunarbending ability and is very difficult to use. Advanced Lunarbenders are able to access pure moon energy, creating falling lunar blasts.";
    }

    @Override
    public String getInstructions() {
        return "*Hold Shift* to charge > Release-Shift\n" +
                "Recall: *Hold Shift* again to recall the stream! -";
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
}
