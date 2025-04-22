package com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.LunarAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Orbital extends LunarAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute("PullRadius")
    private double pullRadius;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.KNOCKBACK)
    private double knockback;
    @Attribute("PullFactor")
    private double pullFactor;
    private double explosionRadius;
    private long blockRevertTime;

    private Location location;
    private Location previousLoc;

    private ArmorStand stand;

    private double currPoint;

    private ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();

    public Orbital(Player player) {
        super(player);

        if (CoreAbility.hasAbility(player, Orbital.class)) {
            Orbital orbital = CoreAbility.getAbility(player, Orbital.class);
            orbital.remove();
            return;
        }

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Orbital.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Orbital.Duration");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.Speed");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.Knockback");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.ExplosionRadius");
            this.blockRevertTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Orbital.BlockRevertTime");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.LunarDamage");
            this.radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.LunarRadius");
            this.pullRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.LunarPullRange");
            this.pullFactor = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Orbital.LunarPullFactor");

            this.currPoint = 360;

            this.stand = (ArmorStand) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setInvulnerable(true);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.getEquipment().setHelmet(new ItemStack(Material.END_STONE));

            this.previousLoc = player.getLocation();

            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.3F, 2F);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.3F, 1.75F);
            this.start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime()) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }
        rotateBody();
    }

    private void rotateBody() {
        //rotates the armor stand around the player.
        for (int i = 0; i < speed; i++) {
            currPoint -= 1;

            if (currPoint < 0) {
                currPoint = 360;
            }

            double angle = currPoint * Math.PI / 180.0D;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location loc = player.getLocation().add(x, 0.5, z);
            stand.teleport(loc);
            location = stand.getLocation().add(0, 1, 0);
            new ColoredParticle(Color.fromRGB(191, 207, 255), 1.5f).display(location, 1, 0.1, 0.1, 0.1);
            Vector vec = PCMethods.createDirectionalVector(previousLoc, loc);

            this.previousLoc = loc.clone();

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, player, damage, this);
                    GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(knockback));
                }
            }

            if (GeneralMethods.isSolid(location.getBlock())) {
                for (Block block : GeneralMethods.getBlocksAroundPoint(location, explosionRadius)) {
                    if (GeneralMethods.isSolid(block)) {
                        BlockData bData = block.getBlockData();
                        TempBlock affected = new TempBlock(block, Material.AIR);
                        affected.setRevertTime(blockRevertTime);

                        Vector velocity = PCMethods.createDirectionalVector(location, block.getLocation());

                        FallingBlock fBlock = location.getWorld().spawnFallingBlock(location, bData);
                        fBlock.setVelocity(velocity.normalize().multiply(ThreadLocalRandom.current().nextDouble(5)));
                        fBlock.setDropItem(false);
                        fBlock.setMetadata("orbital", new FixedMetadataValue(ProjectCosmos.plugin, this));

                        fallingBlocks.add(fBlock);
                    }
                }
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, pullRadius)) {
                //'Drags' Waterbenders in towards the moon.
                if (entity instanceof Player) {
                    Player affected = (Player) entity;
                    BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(affected);

                    if (bPlayer.hasElement(Element.WATER)) {
                        Vector dragVec = PCMethods.createDirectionalVector(affected.getEyeLocation(), location);
                        GeneralMethods.setVelocity(this, affected, dragVec.normalize().multiply(pullFactor));
                    }
                }

            }
        }
    }

    public void revertBlockCreation(FallingBlock fBlock, Block block) {
        TempBlock tb = new TempBlock(block, fBlock.getBlockData().getMaterial());
        tb.setRevertTime(blockRevertTime);
        fallingBlocks.remove(fBlock);
        fBlock.remove();

    }

    @Override
    public void remove() {
        if (!fallingBlocks.isEmpty()) {
            for (FallingBlock fblock : fallingBlocks) {
                fblock.remove();
            }
            fallingBlocks.clear();
        }
        stand.remove();
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Lunar.Orbital.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Orbital";
    }

    @Override
    public Location getLocation() {
        return this.location;
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
        return "A Cosmicbender is able to harness the power of the moon, and create a 'false' copy of it, which can be used to smash into things, or damage entities.";
    }

    @Override
    public String getInstructions() {
        return "Activation/Deactivation: *Left Click*";
    }
}
