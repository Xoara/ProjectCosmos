package com.xoarasol.projectcosmos.abilities.waterbending.plantbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.xoarasol.projectcosmos.ProjectCosmos;
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

import java.util.Random;

public class VineTangle extends PlantAbility implements AddonAbility {

    private Location location;
    private Location origin;
    private Vector direction;
    private Random rand = new Random();
    public static Config config;

    private long cooldown;
    private double range;
    private double damage;
    private int rotation;
    private int rootDuration;

    private Location center;
    private TempBlock source;
    private int pstage;
    private double collisionRadius;

    public VineTangle(Player player) {
        super(player);
        if (this.bPlayer.isOnCooldown((Ability)this))
            return;
        Block topBlock = GeneralMethods.getTopBlock(player.getLocation(), 0, -50);
        if (!isPlant(topBlock.getType())) {
            if (topBlock == null)
                topBlock = player.getLocation().getBlock();
            Material mat = topBlock.getType();
            if (mat != Material.GRASS_BLOCK && mat != Material.DIRT_PATH && mat != Material.PODZOL && mat != Material.FARMLAND && mat != Material.MOSS_BLOCK && mat != Material.MOSS_CARPET)
                if (isPlantbendable(topBlock))
                return;
        }
        setFields();

        if (bPlayer.isOnCooldown(this) || !bPlayer.canBend(this) || !player.isSneaking()) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 0);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
        start();
        this.bPlayer.addCooldown((Ability)this);
    }

    private void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.VineTangle.Cooldown");
        this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.VineTangle.Range");
        this.damage = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.VineTangle.Damage");
        this.rootDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.VineTangle.RootDuration");
        this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Water.Plant.VineTangle.CollisionRadius");
        this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        this.location = this.origin.clone();
        this.direction = this.player.getLocation().getDirection();
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Water.Plant.VineTangle.Enabled");
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }
        this.location.add(this.direction.multiply(1));

        omens();

        this.rotation++;
        for (int i = -180; i < 180; i += 20) {
            double angle = i * Math.PI / 180.0D;
            double x = 0.8D * Math.cos(angle + this.rotation);
            double z = 0.8D * Math.sin(angle + this.rotation);
            Location loc = this.location.clone();
            loc.add(x, 0.0D, z);
            ParticleEffect.BLOCK_CRACK.display(loc, 2, 0.0D, -0.8, 0.0D, 0.1, Material.MANGROVE_ROOTS.createBlockData());
            GeneralMethods.displayColoredParticle("4D632D", loc, 2, 0.25, -0.8, 0.25);
            location.getWorld().playSound(location, Sound.BLOCK_GRASS_STEP, 1, 0.75F);

        }

        if (GeneralMethods.isSolid(this.location.getBlock())) {
            remove();
            return;
        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, collisionRadius)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability)this);

                ParticleEffect.BLOCK_CRACK.display(entity.getLocation(), 7, 1, 0.1, 1, 0.1, Material.MANGROVE_ROOTS.createBlockData());
                GeneralMethods.displayColoredParticle("4D632D", entity.getLocation(), 4, 1, 0.1, 1);
                entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 0.75F);
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.rootDuration, 5));
                return;
            }
        }
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    private void omens() {
        Location centre = player.getLocation();
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.5 * Math.cos(angle));
        double z = centre.getZ() + (1.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        GeneralMethods.displayColoredParticle("4D632D", loc, 5, 0.1, 0.1, 0.1);
        ParticleEffect.BLOCK_CRACK.display(loc, 5, 0, 0, 0, 0.03, Material.MANGROVE_ROOTS.createBlockData());

        double x2 = centre.getX() + (1.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        GeneralMethods.displayColoredParticle("4D632D", loc2, 5, 0, 0, 0);
        ParticleEffect.BLOCK_CRACK.display(loc2, 5, 0.1, 0.1, 0.1, 0.03, Material.MANGROVE_ROOTS.createBlockData());

        pstage++;
    }


    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return null;
    }

    public String getName() {
        return "VineTangle";
    }

    public String getDescription() {
        return "Plantbenders are able to entangle their enemies inside thorny vines, damaging and rooting anyone they hit!";
    }

    public String getInstructions() {
        return "- Hold-Shift > LeftClick while standing on a plant block! -";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
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
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


}
