package com.xoarasol.projectcosmos.abilities.cosmicbending.solarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.SolarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class Pulsar extends SolarAbility implements AddonAbility {
    //similar to spiritbeam, a slightly longer lasting blast that grows and shrinks as it goes

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("DamageInterval")
    private long damageInterval;
    @Attribute("StartingRadius")
    private double startingRadius;
    @Attribute("FinalRadius")
    private double finalRadius;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    private double recoil;
    private double formDistance;

    private Location origin;
    private Location location;
    private Vector direction;

    private double curRadius;
    private double radialIncrement;
    private boolean isCharged;

    private long time;

    private int curPoint;

    public Pulsar(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, Pulsar.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.Pulsar.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.Range");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.Damage");
            this.damageInterval = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.Pulsar.DamageInterval");
            this.startingRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.StartingRadius");
            this.finalRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.FinalRadius");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.Pulsar.Duration");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.Pulsar.ChargeTime");
            this.recoil = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.Recoil");
            this.formDistance = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.Pulsar.FormDistance");

            this.location = player.getLocation().add(0, 1, 0);
            this.direction = player.getLocation().getDirection();

            this.curRadius = startingRadius - 0.3;
            this.radialIncrement = (finalRadius / curRadius) / ((this.duration / 1000F) * 20);
            this.isCharged = false;

            //Since we aren't using a boolean to check for if the move has done damage, we must ensure that time is less than the damage interval when we first start the move up.
            time = System.currentTimeMillis() - damageInterval;

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBend(this)) {
            if (isCharged) {
                this.bPlayer.addCooldown(this);
            }
            remove();
            return;
        }
        if (!this.player.isSneaking() && !isCharged) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime() + chargeTime) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!isCharged) {
            if (System.currentTimeMillis() > chargeTime + getStartTime()) {
                time = System.currentTimeMillis();
                isCharged = true;

                origin = player.getLocation().add(0, 1, 0);
                origin.add(origin.getDirection().normalize().multiply(formDistance));

                ParticleEffect.FLASH.display(player.getLocation().add(0, 1, 0), 2, 0.2D, 0.2D, 0.2D);
                ParticleEffect.END_ROD.display(player.getLocation().add(0, 1, 0), 3, 0.2D, 0.2D, 0.2D, 0.05f);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.ITEM_TRIDENT_THUNDER, 2F, 1.25F);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.ENTITY_ENDER_DRAGON_GROWL, 2F, 1.85F);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.BLOCK_BEACON_POWER_SELECT, 2F, 0.8F);
                player.getLocation().add(0, 1, 0).getWorld().playSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.55F);

                GeneralMethods.setVelocity(this, player, direction.clone().normalize().multiply(-1 * recoil));

                return;
            }
            chargeAnimation();
        } else {
            fireBeam();
            displayOrigin();
        }
    }

    private void chargeAnimation() {
        for (double i = 0; i < 3; i += 0.5) {
            for (int j = 0; j < 5; j++) {
                curPoint += 360 / 60;

                if (curPoint > 360) {
                    curPoint = 0;
                }

                double angle = curPoint * Math.PI / 180.0D;
                double x = i * Math.cos(angle);
                double z = i * Math.sin(angle);

                Location loc = player.getLocation().add(x, 0.1, z);
                new ColoredParticle(Color.fromRGB(255, 230, 147), 1.1F).display(loc, 1, 0, 0, 0);
                new ColoredParticle(Color.fromRGB(255, 199, 0), 1.1F).display(loc, 5, 0.1, 0.1, 0.1);
            }
        }
    }

    private void fireBeam() {
        location = origin.clone();
        Location target = GeneralMethods.getTargetedLocation(player, 200);
        direction = PCMethods.createDirectionalVector(origin, target);
        curRadius *= (1 + radialIncrement);

        if (this.curRadius > finalRadius) {
            curRadius = finalRadius;
        }

        for (int i = 0; i <= range; i += 0.5) {
            location.add(direction.multiply(0.5).normalize());

            if (location.distance(origin) > range) {
                return;
            }

            new ColoredParticle(Color.fromRGB(255, 199, 0), 1.0F).display(location, 2, curRadius / 4, curRadius / 4, curRadius / 4);
            new ColoredParticle(Color.fromRGB(255, 216, 89), 1.0F).display(location, 2, curRadius / 4, curRadius / 4, curRadius / 4);
            new ColoredParticle(Color.fromRGB(255, 230, 147), 1.0F).display(location, 2, curRadius / 4, curRadius / 4, curRadius / 4);
            BlockData bdata = Bukkit.createBlockData(Material.WHITE_STAINED_GLASS);
            ParticleEffect.BLOCK_CRACK.display(location, 1, 0.2, 0.2, 0.2, bdata);

            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                ParticleEffect.END_ROD.display(location, 5, 0.5, 0.5, 0.5);

                location.getWorld().playSound(location, Sound.BLOCK_CHORUS_FLOWER_GROW, 1, 1);
            }


            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, curRadius + 0.3)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    if (System.currentTimeMillis() > time + damageInterval) {
                        DamageHandler.damageEntity(entity, player, damage, this);
                        GeneralMethods.setVelocity(this, entity, direction.normalize().multiply(1));
                        time = System.currentTimeMillis();
                    }
                }
            }

            if (GeneralMethods.isSolid(location.getBlock())) {
                return;
            }
        }
    }

    private void displayOrigin() {
        new ColoredParticle(Color.fromRGB(255, 199, 0), 1.5F).display(origin, 2, 0.3, 0.3, 0.3);
        new ColoredParticle(Color.fromRGB(255, 216, 89), 1.5F).display(origin, 2, 0.3, 0.3, 0.3);
        new ColoredParticle(Color.fromRGB(255, 230, 147), 1.5F).display(origin, 2, 0.3, 0.3, 0.3);
        ParticleEffect.END_ROD.display(origin, 1, 0, 0, 0, 0.1);
        ParticleEffect.END_ROD.display(origin, 4, 0.3,0,0.3);
    }

    @Override
    public void remove() {
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Solar.Pulsar.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Pulsar";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }


    @Override
    public String getDescription() {
        return "Master Solarbenders can store up solar energy into extremely fine points, and discharge it as a large blast.";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift! -";
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
