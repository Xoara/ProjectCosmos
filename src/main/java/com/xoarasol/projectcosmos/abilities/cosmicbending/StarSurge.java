package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class StarSurge extends CosmicAbility implements AddonAbility {

    private Location location;
    private Vector direction;
    public boolean clicked;

    private long cooldown;
    private long duration;
    private double range;
    private double radius;
    private double damage;
    private double knockback;
    private double speed;
    private double explosionRadius;
    private double spread;
    private int amplifier;

    private double distance;
    private long blockRevertTime;

    public StarSurge(Player player) {
        super(player);

        if (bPlayer.isOnCooldown(this) || !bPlayer.canBend(this) || player.isSneaking()) {
            return;
        }

        setFields();

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.6f, 0f);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 0.75f);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 0.6f, 0.5f);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1.0f, 1.55f);
        } else {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.6f, 0f);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.35f);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 0.6f, 1.1f);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1.0f, 1.8f);
        }

        start();
        this.bPlayer.addCooldown(this);
    }


    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Range");
        this.radius = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Radius");
        this.explosionRadius = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.ExplosionRadius");
        this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.RevertTime");
        this.damage = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Damage");
        this.knockback = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Knockback");
        this.speed = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Speed");
        this.distance = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Particles.Distance");
        this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.StarSurge.Slow.Duration");
        amplifier = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.StarSurge.Slow.Amplifier");

        spread = 0;
        location = player.getEyeLocation();
        direction = location.getDirection();
        clicked = false;
    }

    private boolean unbreakable(Block block) {
        return getConfig().getStringList("Abilities.Generic.Unbreakables").contains(block.getType().toString());
    }

    @Override
    public void progress() {

        if (GeneralMethods.isRegionProtectedFromBuild(this, location)) {
            remove();
            return;
        }

        showLightDisc();

        if (player.getLocation().distance(location) >= range) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (this.clicked) {
            super.remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (GeneralMethods.isSolid(this.location.getBlock())) {
            explosion();
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 15f, 1.25f);


            remove();
            bPlayer.addCooldown(this);
            return;
        }
        for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.getLocation(), radius)) {
            if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                DamageHandler.damageEntity(e, damage, this);
                double x = e.getLocation().getX() - player.getLocation().getX();
                double y = e.getLocation().getY() - player.getLocation().getY();
                double z = e.getLocation().getZ() - player.getLocation().getZ();
                Vector v = new Vector(x, y, z).normalize().multiply(knockback);
                e.setVelocity(v);
                LivingEntity le = (LivingEntity) e;
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (duration * 20 / 1000), amplifier - 1));
            }
        }
    }

    private void showLightDisc() {
        Location loc = location.add(direction.normalize().multiply(speed));

        ParticleEffect.END_ROD.display(loc, 3, 0.2, 0.2, 0.2, 0);


        Vector normal = GeneralMethods.getOrthogonalVector(direction, 0, 1);
        for (double r = 0; r < 2; r += 0.2) {
            for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / (r * 2)) { // r × amount
                Vector ortho = GeneralMethods.getOrthogonalVector(normal, Math.toDegrees(theta), r);

                //ParticleEffect.SQUID_INK.display(loc.clone().add(ortho), 1,  0.02, 0.005, 0.02);
               if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1F)).display(GeneralMethods.getLeftSide(loc.clone().add(ortho), 0.50).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1F)).display(GeneralMethods.getLeftSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(66, 0, 188), 1F)).display(loc.clone().add(ortho), 1, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(45, 0, 130), 1F)).display(GeneralMethods.getRightSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(13, 0, 56), 1F)).display(GeneralMethods.getRightSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                   ParticleEffect.SQUID_INK.display(loc, 3, 0.05, 0.05, 0.05, 0);
                } else {
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1F)).display(GeneralMethods.getLeftSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1F)).display(GeneralMethods.getLeftSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(109, 133, 255), 1F)).display(loc.clone().add(ortho), 1, 0.05, 0.05, 0.05);

                    (new ColoredParticle(Color.fromRGB(80, 78, 196), 1F)).display(GeneralMethods.getRightSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                    (new ColoredParticle(Color.fromRGB(72, 49, 175), 1F)).display(GeneralMethods.getRightSide(loc.clone().add(ortho), 0.25).add(0, 0, 0), 1, 0.05, 0.05, 0.05);
                   ParticleEffect.CLOUD.display(loc, 3, 0.05, 0.05, 0.05, 0);
                }

            }
        }
    }


    public static void explode(Player player) {
        if (CoreAbility.hasAbility(player, StarSurge.class)) {
            StarSurge cs = CoreAbility.getAbility(player, StarSurge.class);
            cs.explosion();
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 15f, 1.45f);
        }
    }

    public void explosion() {
        for (Block blocks : GeneralMethods.getBlocksAroundPoint(location, explosionRadius)) {
            for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.getLocation(), explosionRadius)) {
                if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
                    DamageHandler.damageEntity(e, damage, this);
                }
            }
            if (this.unbreakable(blocks)) {
                return;
            } else {
                ParticleEffect.FIREWORKS_SPARK.display(location, 1, 0.2, 0.2, 0.2, 0.2f);

                TempBlock affected = new TempBlock(blocks, Material.AIR);
                affected.setRevertTime(blockRevertTime);
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return player != null ? this.location : null;
    }

    @Override
    public String getName() {
        return "StarSurge";
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.StarSurge.Enabled");
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return true;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    public String getDescription() {
        return "This is a powerful Cosmicbending ability. It allows the user to create a protoplanetary disk that surges towards enemies dealing damage and slowing them." +
                "The disk will explode on impact with terrain. \n" +
                "Additionally, cosmicbenders can release bursts of energy from the core of the disk, unleashing destruction in its path.";
    }

    public String getInstructions() {
        return "Activation: *Tap Shift*\n" +
                "Bursts of Energy: *Left Click multiple times*";
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
