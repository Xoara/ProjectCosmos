package com.xoarasol.projectcosmos.abilities.laserbending.radiation;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.Random;

public class HotSpot extends RadiationAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute("ExplosionRadius")
    private double explosionRadius;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    private long blockRevertTime;

    private boolean isExploded;
    private Location origin;

    public HotSpot(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(this.player, HotSpot.class)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.HotSpot.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.HotSpot.Damage");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.HotSpot.Radius");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Radiation.HotSpot.ExplosionRadius");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.HotSpot.Duration");
            this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Radiation.HotSpot.BlockRevertTime");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Radiation.HotSpot.RadianceDuration");

            this.isExploded = false;
            this.origin = this.player.getLocation().add(0, 0.1, 0);


            start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            this.bPlayer.addCooldown(this);
            return;
        }
        if (this.player.getWorld() != this.origin.getWorld()) {
            remove();
            this.bPlayer.addCooldown(this);
            return;
        }
        if (System.currentTimeMillis() > this.duration + this.getStartTime()) {
            explode();
            return;
        }
        if (!this.isExploded) {
            displayCircle();
            displayInnerParticles();
            detectEntities();
        }
    }

    static Material[] unbreakables = {Material.BEDROCK, Material.BARRIER, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.ENDER_CHEST, Material.CHEST, Material.CHEST_MINECART};

    public static boolean isUnbreakable(Block block) {
        if (block.getState() instanceof InventoryHolder) {
            return true;
        }
        if (Arrays.asList(unbreakables).contains(block.getType())) {
            return true;
        }
        return false;
    }

    private void explode() {
        this.isExploded = true;
        this.bPlayer.addCooldown(this);

        ParticleEffect.EXPLOSION_HUGE.display(this.origin, 3, this.explosionRadius / 2, this.explosionRadius / 2, this.explosionRadius / 2);
        ParticleEffect.FIREWORKS_SPARK.display(this.origin, 10, this.explosionRadius / 2, this.explosionRadius / 2, this.explosionRadius / 2, 1);
        ParticleEffect.SMOKE_LARGE.display(this.origin, 10, this.explosionRadius / 2, this.explosionRadius / 2, this.explosionRadius / 2, 1);
        ParticleEffect.SMOKE_LARGE.display(this.origin, 10, 0.5, 0.5, 0.5, 0.04);

        this.origin.getWorld().playSound(this.origin, Sound.ENTITY_GENERIC_EXPLODE, 2, 0.55f);
        this.origin.getWorld().playSound(this.origin, Sound.BLOCK_BEACON_POWER_SELECT, 2, 0.55f);
        this.origin.getWorld().playSound(this.origin, Sound.BLOCK_CHORUS_FLOWER_DEATH, 2, 1.55f);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.origin, this.explosionRadius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(this.player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, this.player, this.damage, this);
                setRadiance((LivingEntity) entity, this.radianceDuration);
                }
            }


        Random rand = new Random();
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.origin, this.explosionRadius)) {
            if (GeneralMethods.isSolid(block) || isWater(block) || isUnbreakable(block)) {
                TempBlock affected = new TempBlock(block, Material.AIR);
                affected.setRevertTime(this.blockRevertTime);

                if (GeneralMethods.isSolid(affected.getBlock().getRelative(BlockFace.DOWN)) && rand.nextInt(4) == 1) {
                    TempBlock affected2 = new TempBlock(affected.getBlock(), Material.FIRE);
                    affected2.setRevertTime(this.blockRevertTime >= 201 ? blockRevertTime - 200 : 0);
                }
            }
        }
        remove();
    }

    private void displayCircle() {
        for (double i = 0; i < 2 * Math.PI; i += 0.5) {
            Location loc = this.origin.clone();
            double x = this.radius * Math.cos(i);
            double z = this.radius * Math.sin(i);

            new ColoredParticle(Color.fromRGB(0, 255, 255), 1.5f).display(loc, 1, x, 0, z);
        }
    }
    private void displayInnerParticles() {
        for (double i = 0; i < this.radius; i += 0.5) {
            Location loc = this.origin.clone();
            ParticleEffect.END_ROD.display(loc, 2, 0.2, 0.2, 0.2);
            new ColoredParticle(Color.fromRGB(56, 255, 178), 2).display(loc, 2, 0.3, 0.3, 0.3);
            loc.add(0, 1, 0);
        }
    }

    private void detectEntities() {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.origin, this.radius)) {
            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(this.player.getUniqueId())) {
                explode();
                break;
            }
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, HotSpot.class)) {
            HotSpot hotspot = CoreAbility.getAbility(player, HotSpot.class);
            hotspot.explode();
        }
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
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Radiation.HotSpot.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "HotSpot";
    }

    @Override
    public Location getLocation() {
        return this.origin;
    }

    @Override
    public String getDescription() {
        return "Master Radiationbenders can leave behind small patches of supercharged energy." +
                "The patches can explode if entites come into contact with it" +
                " - it can also be detonated manually.";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift! -\n" +
                "- Detonate: Left-Click! -";
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
