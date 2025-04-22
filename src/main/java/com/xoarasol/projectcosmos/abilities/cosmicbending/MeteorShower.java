package com.xoarasol.projectcosmos.abilities.cosmicbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public class MeteorShower extends CosmicAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SELECT_RANGE)
    private int selectRange;
    @Attribute("ExplosionRadius")
    private double explosionRadius;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute("Meteorites")
    private int maxMeteorites;
    private long revertTime;
    private long duration;

    private int meteorites;
    private ConcurrentHashMap<Integer, FallingBlock> meteors = new ConcurrentHashMap<>();

    public MeteorShower(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, MeteorShower.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.MeteorShower.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MeteorShower.Damage");
            this.selectRange = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.MeteorShower.SelectRange");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MeteorShower.ExplosionRadius");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.MeteorShower.Speed");
            this.maxMeteorites = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.MeteorShower.Meteorites");
            this.revertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.MeteorShower.RevertTime");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.MeteorShower.Duration");

            this.meteorites = 1;

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (meteorites >= maxMeteorites && meteors.isEmpty()) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }
        for (int id : meteors.keySet()) {

            if ( meteors.get(id) != null && !meteors.get(id).isDead()) {
                Location loc = meteors.get(id).getLocation();

                BlockData bData = Bukkit.createBlockData(Material.MAGMA_BLOCK);
                ParticleEffect.BLOCK_DUST.display(loc, 5, 0.1, 0.1, 0.1, bData);
                bData = Bukkit.createBlockData(Material.ANCIENT_DEBRIS);
                ParticleEffect.BLOCK_DUST.display(loc, 5, 0.1, 0.1, 0.1, bData);
                ParticleEffect.FLASH.display(loc, 1, 0, 0, 0);

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    ParticleEffect.BLOCK_DUST.display(loc, 5, 0.1, 0.1, 0.1, bData);
                    ParticleEffect.END_ROD.display(loc, 5, 0.5, 0.5, 0.5, 0.05);
                    new ColoredParticle(Color.fromRGB(66, 0, 188), 2).display(loc, 6, 0.5, 0.5, 0.5);
                    new ColoredParticle(Color.fromRGB(45, 0, 130), 1.6F).display(loc, 6, 0.5, 0.5, 0.5);
                    new ColoredParticle(Color.fromRGB(13, 0, 56), 1.2F).display(loc, 6, 0.5, 0.5, 0.5);
                } else {
                    ParticleEffect.BLOCK_DUST.display(loc, 5, 0.1, 0.1, 0.1, bData);
                    ParticleEffect.END_ROD.display(loc, 5, 0.5, 0.5, 0.5, 0.05);
                    new ColoredParticle(Color.fromRGB(72, 49, 175), 2).display(loc, 6, 0.5, 0.5, 0.5);
                    new ColoredParticle(Color.fromRGB(80, 78, 196), 1.6F).display(loc, 6, 0.5, 0.5, 0.5);
                    new ColoredParticle(Color.fromRGB(109, 133, 255), 1.2F).display(loc, 6, 0.5, 0.5, 0.5);
                }

                loc.getWorld().playSound(loc, Sound.ENTITY_VEX_HURT, 2, 0);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, 1.5)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        impact(meteors.get(id));
                    }
                }
            } else {
                meteors.get(id).remove();
            }
        }
    }

    public void createMeteor() {
        Block block = player.getTargetBlockExact(selectRange, FluidCollisionMode.NEVER);

        if (block != null && (meteorites <= maxMeteorites)) {
            Location formLoc = player.getLocation().add(0, selectRange, 0);
            Vector dir = PCMethods.createDirectionalVector(formLoc, block.getLocation());

            ParticleEffect.EXPLOSION_HUGE.display(formLoc, 3, 0.5, 0.5, 0.5);
            ParticleEffect.LAVA.display(formLoc, 10, 0.5, 0.5, 0.5, 1);
            ParticleEffect.FLASH.display(formLoc, 3, 0.5, 0.5, 0.5);

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                formLoc.getWorld().playSound(formLoc, Sound.ENTITY_WITHER_HURT, 3, 0);
                formLoc.getWorld().playSound(formLoc, Sound.ENTITY_IRON_GOLEM_HURT, 3, 0);
            } else {
                formLoc.getWorld().playSound(formLoc, Sound.ITEM_TRIDENT_RETURN, 3, 0);
                formLoc.getWorld().playSound(formLoc, Sound.ENTITY_IRON_GOLEM_HURT, 3, 0);
            }

            BlockData bData = Bukkit.createBlockData(Material.MAGMA_BLOCK);
            FallingBlock fBlock = formLoc.getWorld().spawnFallingBlock(formLoc, bData);
            fBlock.setInvulnerable(true);
            fBlock.setDropItem(false);
            fBlock.setVelocity(dir.normalize().multiply(speed));
            fBlock.setGlowing(true);
            fBlock.setMetadata("MeteorShower", new FixedMetadataValue(ProjectCosmos.plugin, this));
            meteors.put(meteorites, fBlock);
            meteorites++;

            if (meteorites == Integer.MAX_VALUE) {
                meteorites = Integer.MIN_VALUE;
            }
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, MeteorShower.class)) {
            MeteorShower shower = CoreAbility.getAbility(player, MeteorShower.class);
            shower.createMeteor();
        }
    }

    public void impact(FallingBlock fBlock) {
        Location impactLoc = fBlock.getLocation();
        fBlock.remove();

        ParticleEffect.END_ROD.display(impactLoc, 1, 0.3 , 0.3, 0.3, 0.09);
        ParticleEffect.SMOKE_LARGE.display(impactLoc, 1, 0.3, 0.3, 0.09);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_WITHER_HURT, 3, 0);
        } else {
            impactLoc.getWorld().playSound(impactLoc, Sound.ITEM_TRIDENT_RETURN, 6, 0);
        }
        impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 0);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(impactLoc, explosionRadius)) {
            if (entity instanceof  LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                DamageHandler.damageEntity(entity, player, damage, this);
            }
        }

        for (Block block : GeneralMethods.getBlocksAroundPoint(impactLoc, explosionRadius)) {
            if (EarthAbility.isEarthbendable(block.getType(), true, true, true) && !WaterAbility.isWater(block.getRelative(BlockFace.UP))) {
                TempBlock tb = new TempBlock(block, Material.MAGMA_BLOCK);
                tb.setRevertTime(revertTime);
            }
        }

        for (int id : meteors.keySet()) {
            if (meteors.get(id).equals(fBlock)) {
                meteors.remove(id);
            }
        }
    }

    public static void Impact(FallingBlock fallingBlock) {
        for (MeteorShower shower : CoreAbility.getAbilities(MeteorShower.class)) {
            if (shower.getMeteors().containsValue(fallingBlock)) {
                shower.impact(fallingBlock);
            }
        }
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        super.remove();
    }

    public ConcurrentHashMap<Integer, FallingBlock> getMeteors() {
        return meteors;
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.MeteorShower.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "MeteorShower";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "KWilson272";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public String getDescription() {
        return "Cosmicbenders can teleport meteors from outer space into earths atmosphere, and strike down entities with great force!";
    }

    @Override
    public String getInstructions() {
        return "Activation: *Tap Shift* \n" +
                "Summon Meteors: *Left-Click* multile times while aiming at the ground!";
    }
}
