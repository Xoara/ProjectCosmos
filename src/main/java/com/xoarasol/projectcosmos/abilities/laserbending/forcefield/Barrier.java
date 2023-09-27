package com.xoarasol.projectcosmos.abilities.laserbending.forcefield;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.ForceFieldAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Barrier extends ForceFieldAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SELECT_RANGE)
    private double selectRange;
    @Attribute(Attribute.RANGE)
    private int range;
    @Attribute("Radius")
    private int radius;

    private Location location;
    private List<Location> locations = new ArrayList<>();
    private List<TempBlock> blocks = new ArrayList<>();

    Random rand = new Random();

    private boolean isPlaced;

    public Barrier(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.Barrier.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.Barrier.Duration");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.Barrier.Damage");
            this.selectRange = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.Barrier.SelectRange");
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.Barrier.PlaceRange");
            this.radius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.Barrier.Radius");

            this.isPlaced = false;

            this.bPlayer.addCooldown(this);
            setLocations();
            player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2f, 0.8f);
            player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 0f);
            start();
        }
    }

    @Override
    public void progress() {
        if (this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            if (System.currentTimeMillis() > this.getStartTime() + this.duration) {
                shatter(null);
            }
            if (!isPlaced) {
                this.isPlaced = true;
                placeBarrier();
            }
        } else {
            remove();
        }
    }

    public void shatter(Player player) {
        int count = 1;
        for (TempBlock tb : blocks) {

            if (tb != null) {
                tb.revertBlock();

                BlockData blockData = Bukkit.createBlockData(Material.LIGHT_BLUE_STAINED_GLASS);
                ParticleEffect.BLOCK_CRACK.display(tb.getLocation(), 4, 1, 1, 1, 0.04f, blockData);

                if (count % 2 == 0) {
                    int pitchval = rand.nextInt(2);
                    tb.getLocation().getWorld().playSound(tb.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.45f, pitchval);
                    tb.getLocation().getWorld().playSound(tb.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.45f, pitchval);
                }

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(tb.getLocation(), 2)) {
                    DamageHandler.damageEntity(entity, this.player, damage, this);
                    if (entity instanceof LivingEntity) {
                        if (player == null) {
                                DamageHandler.damageEntity(entity, this.player, damage, this);
                            }
                        } else if (!entity.equals(player)) {
                                DamageHandler.damageEntity(entity, this.player, damage, this);
                            }
                }
                count++;
            }
        }
        blocks.clear();
        remove();
    }

    public static void shatterBarrier(Player player) {
        for (Barrier barrier : CoreAbility.getAbilities(Barrier.class)) {
           Block block = player.getTargetBlockExact(ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.Barrier.SelectRange"));
           if (block != null) {
               if (barrier.getLocations().contains(block.getLocation())) {
                   barrier.shatter(player);
                   break;
               }
           }
        }
    }

    public void placeBarrier() {
        for (Location location : locations) {
            TempBlock barrierBlock = new TempBlock(location.getBlock(), Material.LIGHT_BLUE_STAINED_GLASS);
            blocks.add(barrierBlock);
        }
    }

    private void setLocations() {
        final Location tempLoc = this.player.getLocation().add(this.player.getLocation().getDirection().normalize().multiply(this.range));
        final Vector dir = this.player.getEyeLocation().getDirection();
        this.location = tempLoc.clone().add(dir.normalize().multiply(this.range));
        Vector vector;
        Block block;

        for (double i = 0; i <= this.radius; i += 0.5) {
            for (double angle = 0; angle < 360; angle += 10) {
                vector = GeneralMethods.getOrthogonalVector(dir.clone(), angle, i);
                vector.add(dir.clone());
                block = tempLoc.clone().add(vector).getBlock();

                if (!this.blocks.contains(block) && isAir(block.getType()) || FireAbility.isFire(block.getType())) {
                    this.locations.add(block.getLocation());
                }
            }
        }
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public void remove() {
        for (TempBlock tb : blocks) {
            if (tb != null) {
                tb.revertBlock();
            }
        }
        blocks.clear();
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
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.ForceField.Barrier.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Barrier";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return "Advanced ForceFieldBenders are able to cast fields of pure force! These force shields do not last forever, and upon dispersion, may damage entities around them.";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click!\n" +
                "- Disperse: Tap-Shift! -";
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Lubdan, KWilson272 & XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }
}
