package com.xoarasol.projectcosmos.abilities.cosmicbending.combos;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.GravityAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TectonicDisruption extends GravityAbility implements AddonAbility, ComboAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.KNOCKUP)
    private double knockup;
    @Attribute(Attribute.RADIUS)
    private double blockCollisionRadius;


    private Location origin;
    private Location location;
    private Vector direction;

    private List<FallingBlock> tracker;

    public TectonicDisruption(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {
            //check for the source block before instantiating any other variable
            int selectRange = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Combos.TectonicDisruption.SelectRange");
            Block block = player.getTargetBlockExact(selectRange, FluidCollisionMode.NEVER);

            if (block != null && GeneralMethods.isSolid(block) && GeneralMethods.isSolid(block.getRelative(BlockFace.DOWN).getType())) {
                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.TectonicDisruption.Cooldown");
                this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.TectonicDisruption.Duration");
                this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.TectonicDisruption.Damage");
                this.knockup = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.TectonicDisruption.Knockup");
                this.blockCollisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.TectonicDisruption.BlockCollisionRadius");

                this.origin = block.getLocation();
                this.location = origin.clone();
                this.direction = player.getLocation().getDirection();
                this.direction.setY(0);

                this.tracker = new ArrayList<>();

                this.bPlayer.addCooldown(this);

                location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 0.56F);
                location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 0.66F);
                start();
            }
        }
    }
    public boolean isHiddenAbility() {
        return true;
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > duration + getStartTime()) {
            remove();
            return;
        }

        location.add(direction.normalize());
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, 2)) {
            if (GeneralMethods.isSolid(block) && isAir(block.getRelative(BlockFace.UP).getType())) {

                if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                    BlockData blockData = Bukkit.createBlockData(Material.CRYING_OBSIDIAN);
                    FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0, 1, 0), blockData);
                    fallingBlock.setMetadata("tectonicdisruption", new FixedMetadataValue(ProjectCosmos.plugin, this));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(0, 1, 0).normalize().multiply(0.2));

                    TempBlock tempBlock = new TempBlock(block, Material.AIR);
                    block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 0.56F);
                    tempBlock.setRevertTime(1000);

                    tracker.add(fallingBlock);
                }
                } else {
                    BlockData blockData = Bukkit.createBlockData(Material.SCULK);
                    FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0, 1, 0), blockData);

                    fallingBlock.setMetadata("tectonicdisruption", new FixedMetadataValue(ProjectCosmos.plugin, this));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(0, 1, 0).normalize().multiply(0.2));

                    TempBlock tempBlock = new TempBlock(block, Material.AIR);
                    block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 0.56F);
                    tempBlock.setRevertTime(1000);

                    tracker.add(fallingBlock);
                }
            }

        ArrayList<FallingBlock> toRemove = new ArrayList<>();
        for (Iterator<FallingBlock> blockIterator = tracker.iterator(); blockIterator.hasNext(); ) {
            FallingBlock fBlock = blockIterator.next();

            if (fBlock == null || fBlock.isDead()) {
                toRemove.add(fBlock);
                fBlock.remove();
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fBlock.getLocation(), blockCollisionRadius)) {
                if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                    DamageHandler.damageEntity(entity, player, damage, this);
                    Vector knock = direction.clone().setY(0.5);
                    GeneralMethods.setVelocity(this, entity, knock.normalize());
                    toRemove.add(fBlock);
                    fBlock.remove();
                }
            }
        }

        if (!toRemove.isEmpty()) {
            for (FallingBlock fallingBlock : toRemove) {
                if (tracker.contains(fallingBlock)) {
                    tracker.remove(fallingBlock);
                }
            }
            toRemove.clear();
        }
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Combos.TectonicDisruption.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "TectonicDisruption";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locs = new ArrayList<>();
        for (FallingBlock fallingBlock : tracker) {
            locs.add(fallingBlock.getLocation());
        }
        return locs;
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
    public Object createNewComboInstance(Player player) {
        return new TectonicDisruption(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> gravityWave = new ArrayList<>();
        gravityWave.add(new AbilityInformation("GravityManipulation", ClickType.LEFT_CLICK));
        gravityWave.add(new AbilityInformation("GravityManipulation", ClickType.LEFT_CLICK));
        gravityWave.add(new AbilityInformation("CosmicBlast", ClickType.SHIFT_DOWN));
        gravityWave.add(new AbilityInformation("GravityManipulation", ClickType.LEFT_CLICK));
        return gravityWave;
    }

    @Override
    public String getDescription() {
        return "By focusing their energy, a cosmicbender can bend the space around them, and cause a wave of decreased gravity to eject from a specific point.";
    }

    @Override
    public String getInstructions() {
        return "GravityManipulation (Left-Click Twice) > CosmicBlast (Hold-Sneak) > GravityManipulation (Left-Click while looking at a block)";
    }
}

