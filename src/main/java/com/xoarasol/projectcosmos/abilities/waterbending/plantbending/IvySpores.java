package com.xoarasol.projectcosmos.abilities.waterbending.plantbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class IvySpores extends PlantAbility implements AddonAbility {
    private long cooldown;

    private double damage;

    private double collisionRadius;

    private double range;

    private double speed;

    private double sourcerange;

    private double an;

    private Location origin;

    private Location loc;

    private Vector direction;

    private Block sourceblock;

    private TempBlock sourcetb;

    private Boolean hasreleasedshift;

    private ArrayList<Entity> bound;

    private Random rand = new Random();

    private int slowDuration;

    private int slowPower;

    private int poisonDuration;

    private int poisonPower;

    public IvySpores(Player player) {
        super(player);
        if (!this.bPlayer.canBend((CoreAbility)this) || CoreAbility.hasAbility(player, getClass()) || this.bPlayer.isOnCooldown((Ability)this) || GeneralMethods.isRegionProtectedFromBuild((Ability)this, player.getLocation()))
            return;
        prepare();
        if (this.sourceblock == null)
            return;
        if (isPlant (this.sourceblock)) {
            this.origin = player.getEyeLocation();
            this.loc = this.origin.clone();
            this.sourcetb = new TempBlock(this.sourceblock, Material.valueOf("MANGROVE_ROOTS"));
            setFields();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2.0F, 1.6F);
            start();
        }
    }

    public void setFields() {
        this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Water.Plant.IvySpores.Cooldown");
        this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Water.Plant.IvySpores.Damage");
        this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Water.Plant.IvySpores.CollisionRadius");
        this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Water.Plant.IvySpores.Range");
        this.poisonDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.IvySpores.Poison.Duration");
        this.poisonPower = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.IvySpores.Poison.Power");
        this.slowDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.IvySpores.Slow.Duration");
        this.slowPower = ProjectCosmos.plugin.getConfig().getInt("Abilities.Water.Plant.IvySpores.Slow.Power");
        this.speed = 1.0D;
        this.hasreleasedshift = Boolean.valueOf(false);
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Water.Plant.IvySpores.Enabled");
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getName() {
        return "IvySpores";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return false;
    }

    Location loca = GeneralMethods.getTargetedLocation(player, 2);

    public void progress() {
        if (!this.hasreleasedshift.booleanValue() && !this.player.isSneaking())
            this.hasreleasedshift = Boolean.valueOf(true);
        if (!this.hasreleasedshift.booleanValue() && this.player.isSneaking())
            ParticleEffect.BLOCK_CRACK.display(loca, 5, 0.0D, 0.0D, 0.0D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
        if (this.hasreleasedshift.booleanValue()) {
            this.direction = this.player.getLocation().getDirection();
            this.loc.add(this.direction.clone().multiply(this.speed));
            ParticleEffect.BLOCK_CRACK.display(this.loc, 20, 0.5D, 0.5D, 0.5D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
            LightSpirals();
            if (this.rand.nextInt(2) == 0)
                this.loc.getWorld().playSound(this.loc, Sound.BLOCK_GRASS_STEP, 2.0F, 1.5F);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_WET_GRASS_BREAK, 2.0F, 0.5F);
        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.loc, this.collisionRadius)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.player, this.damage, (Ability)this);
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.slowDuration, this.slowPower));
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, this.poisonDuration, this.poisonPower));
                remove();
                return;
            }
        }
        if (this.player.isDead() || !this.player.isOnline() || this.bPlayer.isChiBlocked() || GeneralMethods.isRegionProtectedFromBuild((Ability)this, this.player.getLocation()))
            remove();
        if (this.loc.distance(this.origin) > this.range)
            remove();
        if (this.loc.getBlock().getType().isSolid() && this.loc.distance(this.origin) > 1.0D)
            remove();
    }

    private void LightSpirals() {
        this.loc = this.loc.add(this.direction.normalize().multiply(0.4D));
        this.an += 20.0D;
        if (this.an > 360.0D)
            this.an = 0.0D;
        for (int i = 0; i < 4; i++) {
            for (double d = -4.0D; d <= 0.0D;

                 d += 5.0D) {
                Location l = this.loc.clone();
                double r = d * -1.0D / 5.0D;
                if (r > 1.1D)
                    r = 1.1D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, this.an + (90 * i) + d, r);
                Location pl = l.clone().add(ov.clone());
                switch (i) {
                    case 0:
                        (new ColoredParticle(Color.fromRGB(78, 114, 57), 2.3F)).display(pl, 2, 0.0D, 0.0D, 0.0D);
                        ParticleEffect.BLOCK_CRACK.display(pl, 2, 0.0D, 0.0D, 0.0D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
                        break;
                    case 1:
                        (new ColoredParticle(Color.fromRGB(78, 114, 57), 2.3F)).display(pl, 2, 0.0D, 0.0D, 0.0D);
                        ParticleEffect.BLOCK_CRACK.display(pl, 2, 0.0D, 0.0D, 0.0D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
                        break;
                    case 2:
                        (new ColoredParticle(Color.fromRGB(78, 114, 57), 2.3F)).display(pl, 2, 0.0D, 0.0D, 0.0D);
                        ParticleEffect.BLOCK_CRACK.display(pl, 2, 0.0D, 0.0D, 0.0D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
                        break;
                    case 3:
                        (new ColoredParticle(Color.fromRGB(78, 114, 57), 2.3F)).display(pl, 2, 0.0D, 0.0D, 0.0D);
                        ParticleEffect.BLOCK_CRACK.display(pl, 2, 0.0D, 0.0D, 0.0D, 0.0D, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
                        break;
                }
            }
        }
    }

    public void prepare() {
        this.sourcerange = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Water.Plant.IvySpores.SourceRange");
        this.sourceblock = selectSource(this.player, Double.valueOf(this.sourcerange));
    }
    public static Block selectSource(Player player, Double sourcerange) {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection();
        RayTraceResult rayresult = loc.getWorld().rayTraceBlocks(loc, dir, sourcerange, FluidCollisionMode.ALWAYS);
        if (rayresult != null) {
            Block source = rayresult.getHitBlock();
            if (source != null) {
                return source;
            }
        }
        return null;
    }
    public void remove() {
        this.sourcetb.revertBlock();
        this.bPlayer.addCooldown((Ability)this);
        super.remove();
    }

    public String getDescription() {
        return "Plantbenders sometimes also work with poison. They are able to pull up the roots of poisonous plants and shoot it at their enemeies!";
    }

    public String getInstructions() {
        return "- Hold-Shift while looking at a pkabt block > Release-Shift at an enemy! -";
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
