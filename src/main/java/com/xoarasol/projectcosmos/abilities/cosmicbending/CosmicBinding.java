package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CosmicBinding extends CosmicAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute("Pull")
    private double pull;
    private double impactRadius;
    private double knockback;
    private double damage;
    private long onMissCooldown;


    private Location origin;
    private Location location;
    private Vector direction;

    private LivingEntity target;
    private boolean latched;
    private long time;
    private int an;
    private int pstage;

    public CosmicBinding(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, CosmicBinding.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmicBinding.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.ProjectileSpeed") * (ProjectKorra.time_step / 1000F);
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmicBinding.Duration");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.CollisionRadius");
            this.pull = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.Pull");
            this.impactRadius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.CosmicBinding.ImpactRadius");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.Knockback");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.CosmicBinding.Damage");
            this.onMissCooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.CosmicBinding.OnMissCooldown");

            this.origin = GeneralMethods.getMainHandLocation(this.player);
            this.location = origin.clone();
            this.direction = origin.getDirection();

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 0.8f, 0.55f);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1.2f, 0f);
            }

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
        if (!latched) {
            for (int i = 0; i <= 2; i++) {
                if (GeneralMethods.isSolid(location.getBlock()) || isWater(location.getBlock()) || this.location.distance(origin) > range) {
                    this.bPlayer.addCooldown(this, onMissCooldown);
                    remove();
                    return;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, radius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        target = (LivingEntity) entity;
                        latched = true;
                        time = System.currentTimeMillis();
                        break;
                    }
                }

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    DarkSpirals();
                    location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.1f, 1);
                } else {
                    LightSpirals();
                    location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.1f, 1.85f);
                }

                location.add(direction.clone().normalize().multiply(speed / 2));

            }
        } else {
            if (System.currentTimeMillis() > time + duration) {
                this.bPlayer.addCooldown(this);
                remove();
                return;
            }

            grid();
            grid2();

            if (this.player.isSneaking() && this.bPlayer.getBoundAbilityName().equalsIgnoreCase("CosmicBinding")) {
                Vector vec = PCMethods.createDirectionalVector(player.getEyeLocation(), target.getEyeLocation());
                GeneralMethods.setVelocity(this, player, vec.normalize().multiply(pull));

                BlockData bData = Bukkit.createBlockData(Material.END_STONE);
                ParticleEffect.BLOCK_DUST.display(player.getLocation(), 2, 0, 0, 0, bData);
                ParticleEffect.BLOCK_DUST.display(player.getLocation(), 2, 0.3, 0.3, 0.3, bData);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    (new ColoredParticle(Color.fromRGB(66, 0, 188), 2.45F)).display(player.getLocation(), 2, 0.05, 0.05, 0.05);
                } else {
                    (new ColoredParticle(Color.fromRGB(109, 133, 255), 2.45F)).display(player.getLocation(), 2, 0.05, 0.05, 0.05);
                }

                if (player.getLocation().distance(target.getLocation()) < 1.5) {
                    impact();

                }
            }
        }
    }

    private void grid() {
        Location centre = target.getLocation();
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.0 * Math.cos(angle));
        double z = centre.getZ() + (1.0 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1.2, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 1).display(loc, 3, 0, 0, 0);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 1).display(loc, 3, 0, 0, 0);
        }

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(45, 0, 130), 1).display(loc2, 3, 0, 0, 0);
        } else {
            new ColoredParticle(Color.fromRGB(80, 78, 196), 1).display(loc2, 3, 0, 0, 0);
        }

        pstage++;
    }

    private void grid2() {
        Location centre = target.getLocation();
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1 * Math.cos(angle));
        double z = centre.getZ() + (1 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 0.8, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(66, 0, 188), 1).display(loc, 3, 0, 0, 0);
        } else {
            new ColoredParticle(Color.fromRGB(109, 133, 255), 1).display(loc, 3, 0, 0, 0);
        }

        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 0.6, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            new ColoredParticle(Color.fromRGB(45, 0, 130), 1).display(loc2, 3, 0, 0, 0);
        } else {
            new ColoredParticle(Color.fromRGB(80, 78, 196), 1).display(loc2, 3, 0, 0, 0);
        }

        pstage++;
    }

    private void DarkSpirals() {
        this.location = this.location.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 4; i++) {
            for (double d = -4.0D; d <= 0.0D;

                 d += 5.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 1.1D)
                    r = 1.1D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        new ColoredParticle(Color.fromRGB(66, 0, 188), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(45, 0, 130), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(13, 0, 56), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        ParticleEffect.SQUID_INK.display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }

    private void LightSpirals() {
        this.location = this.location.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0;
        for (int i = 0; i < 4; i++) {
            for (double d = -4.0D; d <= 0.0D;

                 d += 5.0D) {
                Location l = this.location.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 1.1D)
                    r = 1.1D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        ParticleEffect.CLOUD.display(pl, 2, 0, 0, 0);
                        break;
                    case 1:
                        new ColoredParticle(Color.fromRGB(109, 133, 255), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 2:
                        new ColoredParticle(Color.fromRGB(80, 78, 196), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                    case 3:
                        new ColoredParticle(Color.fromRGB(72, 49, 175), 1.4f).display(pl, 2, 0, 0, 0);
                        break;
                }
            }
        }
    }


    private void impact() {
        ParticleEffect.EXPLOSION_HUGE.display(player.getLocation(), 3, 0, 0, 0);
        ParticleEffect.FLASH.display(player.getLocation(), 3, 0.1, 0.1, 0.1);

        BlockData bData = Bukkit.createBlockData(Material.END_STONE);
        ParticleEffect.BLOCK_DUST.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, bData);
        ParticleEffect.SMOKE_LARGE.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, 0.07);
        ParticleEffect.FIREWORKS_SPARK.display(player.getLocation(), 15, impactRadius / 2, impactRadius / 2, impactRadius / 2, 0.07);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1.25f);

        Vector playerKnock = PCMethods.createDirectionalVector(target.getLocation(), player.getLocation());

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), impactRadius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, player, damage, this);

                Vector vec = PCMethods.createDirectionalVector(player.getLocation(), entity.getLocation());
                GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(knockback));
            }
        }
        GeneralMethods.setVelocity(this, player, playerKnock.normalize().multiply(knockback));
        this.bPlayer.addCooldown(this);
        remove();
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.CosmicBinding.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "CosmicBinding";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return "CosmicBinding is an advanced level ability that allows cosmicbenders to bind themselves onto enemies. After the enemy has been bound and marked," +
                "the cosmicbender pulls themselves towards them, crashing directly into them!";
    }

    @Override
    public String getInstructions() {
        return "Bind: Left-Click \n" +
                "Crash: Hold-Shift to crash into your bound enemy!";
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
