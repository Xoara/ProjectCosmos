package com.xoarasol.projectcosmos.abilities.laserbending.forcefield;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.ForceFieldAbility;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnergyBolt extends ForceFieldAbility implements AddonAbility {

    public static enum PlasmaBoltType {
        FORCE, CYRO, PYRO, NECRO;
    }

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute("BoltDelay")
    private long boltDelay;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("EffectDuration")
    private int effectDuration;
    private double collisionRadius;
    private double effectRadius;
    private long blockRegenDelay;

    private boolean firing;
    private int count;

    private String hexVal1;
    private String hexVal2;
    private String hexVal3;
    private ChatColor chatColor;
    private String effectMessage;
    private PlasmaBoltType pType;

    private Location location;
    private Location origin;
    private Vector direction;

    private List<String> toDeadBushes = new ArrayList<>();
    private List<String> toCrackedStoneBricks = new ArrayList<>();


    public EnergyBolt(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, EnergyBolt.class)) {

            String plasmaType = "Force";
            if (ProjectCosmos.plugin.plasmaList.containsKey(player.getUniqueId())) {
                plasmaType = ProjectCosmos.plugin.plasmaList.get(player.getUniqueId());
            }

            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.EnergyBolt.Cooldown");
            this.boltDelay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.EnergyBolt.Delay");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.EnergyBolt.Damage");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.EnergyBolt.Speed");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.EnergyBolt.Range");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.EnergyBolt.CollisionRadius");
            this.effectRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.ForceField.EnergyBolt.EffectRadius");
            this.blockRegenDelay = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.ForceField.EnergyBolt.BlockRegenDelay");

            this.toDeadBushes = ProjectCosmos.plugin.getConfig().getStringList("Abilities.Laser.ForceField.EnergyBolt.ToDeadBushes");
            this.toCrackedStoneBricks = ProjectCosmos.plugin.getConfig().getStringList("Abilities.Laser.ForceField.EnergyBolt.ToCrackedBricks");

            this.count = 0;

            this.firing = false;
            this.speed = (this.speed * (ProjectKorra.time_step / 1000.0F));
            this.speed /= 3;

            setBoltFields(plasmaType);
            start();
        }
    }

    private void setBoltFields(String plasmaType) {
        switch (plasmaType.toUpperCase()) {
            case ("CYRO"):
                this.effectDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.EnergyBolt.FrostDuration");
                this.effectMessage = (ChatColor.AQUA + "* Frozen *");
                this.hexVal1 = "#03ffff";
                this.hexVal2 = "#dcfcfc";
                this.hexVal3 = "#fcfcfc";
                chatColor = ChatColor.AQUA;
                pType = PlasmaBoltType.CYRO;
                break;
            case ("PYRO"):
                this.effectDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.EnergyBolt.FireTicks");
                this.effectMessage = (ChatColor.RED + "* Burning *");
                this.hexVal1 = "#ff4800";
                this.hexVal2 = "#ffb300";
                this.hexVal3 = "#ffe482";
                chatColor = ChatColor.RED;
                pType = PlasmaBoltType.PYRO;
                break;
            case ("NECRO"):
                this.effectDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.EnergyBolt.NecrosisDuration");
                this.effectMessage = (ChatColor.DARK_PURPLE + "* Necrotic *");
                this.hexVal1 = "#19002b";
                this.hexVal2 = "#3c018f";
                this.hexVal3 = "#9c1ec9";
                chatColor = ChatColor.DARK_PURPLE;
                pType = PlasmaBoltType.NECRO;
                break;
            default:
                this.effectDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.ForceField.EnergyBolt.PlasmaDuration");
                this.effectMessage = (PCElement.FORCEFIELD.getColor() + "* Struck by EnergyBolt*");
                this.hexVal1 = "#74ADD3";
                this.hexVal2 = "#6496B7";
                this.hexVal3 = "#4C4C4C";
                chatColor = ChatColor.GRAY;
                pType = PlasmaBoltType.FORCE;
                break;
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (!this.firing && !this.bPlayer.getBoundAbilityName().equalsIgnoreCase("EnergyBolt")) {
            remove();
            return;
        }
        if (!this.firing) {
            this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chatColor + "*Powering up*"));
        }

        if (System.currentTimeMillis() > this.getStartTime() + this.boltDelay && !this.firing) {
            this.firing = true;
            this.bPlayer.addCooldown(this);

            this.origin = GeneralMethods.getRightSide(this.player.getEyeLocation(), 0.36);
            this.location = this.origin.clone();
            this.direction = this.origin.getDirection();
            this.location.getWorld().playSound(this.location, Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1.2F);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_ENDERMAN_HURT, 1, 0F);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_CREEPER_HURT, 1, 0F);
        }
        if (this.firing) {
            for (int i = 0; i < 4; i++) {
                if (this.location.distance(this.origin) > this.range) {
                    this.remove();
                    break;
                }
                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    boltEffect();
                    this.remove();
                    break;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.collisionRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                        affect((LivingEntity) entity);
                        this.remove();
                        break;
                    }
                }
                GeneralMethods.displayColoredParticle(hexVal1, this.location, 2, 0.1, 0.1, 0.1);
                GeneralMethods.displayColoredParticle(hexVal3, this.location, 1, 0.3, 0.3, 0.3);

                if (count == 4) {
                    drawCircle();
                    count = 0;

                    if (this.pType == PlasmaBoltType.NECRO) {
                        ParticleEffect.SOUL.display(this.location, 2, 0, 0, 0, 0.1);
                    }
                }
                this.location.add(this.direction.normalize().multiply(this.speed));
                count++;
            }
        }
    }

    private void affect(LivingEntity lent) {
        DamageHandler.damageEntity(lent, this.player, this.damage, this);
        GeneralMethods.setVelocity(this, lent, this.direction.normalize().multiply(0.3));

        if (lent instanceof Player) {
            if (CoreAbility.hasAbility((Player) lent, WaterSpout.class)) {
                WaterSpout spout = CoreAbility.getAbility((Player) lent, WaterSpout.class);
                spout.remove();
            }
            if (CoreAbility.hasAbility((Player) lent, AirSpout.class)) {
                AirSpout spout = CoreAbility.getAbility((Player) lent, AirSpout.class);
                spout.remove();
            }
        }
        boltEffect();
    }

    private void boltEffect() {
        this.location.getWorld().playSound(this.location, Sound.ITEM_TOTEM_USE, 1.2F, 1.2F);
        this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 1.2F, 0.56F);

        switch (pType) {
            case CYRO:
                GeneralMethods.displayColoredParticle("#d6feff", this.location, 10, this.effectRadius / 2, this.effectRadius / 2, this.effectRadius / 2);
                BlockData blockData = Bukkit.createBlockData(Material.BLUE_ICE);
                ParticleEffect.BLOCK_CRACK.display(this.location, 6, 2, 2, 2, 0.04f, blockData);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.effectRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                        if (isRadiant((LivingEntity) entity)) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.effectDuration, 2, true, true));
                            removeRadiance((LivingEntity) entity);

                            if (entity instanceof Player) {
                                Player player1 = (Player) entity;
                                player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(effectMessage));
                            }
                        }
                    }
                }
                for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, this.effectRadius)) {
                    if (isWater(block)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.ICE);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    }
                    if (isLava(block) || block.getType().equals(Material.MAGMA_BLOCK)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.OBSIDIAN);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    }
                    if (GeneralMethods.isSolid(block)) {
                        Block above = block.getRelative(BlockFace.UP);
                        if (above.getType().isAir() && !block.getType().equals(Material.ICE) && !block.getType().equals(Material.OBSIDIAN)) {
                            TempBlock snowBlock = new TempBlock(above, Material.SNOW);
                            snowBlock.setRevertTime(this.blockRegenDelay);
                        }
                    }
                }
                break;
            case PYRO:
                ParticleEffect.EXPLOSION_HUGE.display(this.location, 2, this.effectRadius / 2, this.effectRadius / 2, this.effectRadius / 2);
                ParticleEffect.FLAME.display(this.location, 5, this.effectRadius / 2, this.effectRadius / 2, this.effectRadius / 2, 1);
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.effectRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                        if (isRadiant((LivingEntity) entity)) {
                            entity.setFireTicks(this.effectDuration);
                            removeRadiance((LivingEntity) entity);

                            if (entity instanceof Player) {
                                Player player1 = (Player) entity;
                                player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(effectMessage));
                            }
                        }
                    }
                }
                for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, effectRadius)) {
                    if (isWater(block)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.AIR);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                        ParticleEffect.CLOUD.display(block.getLocation(), 1, 0.1, 0.1, 0.1, 0.3);
                    } else if (isIce(block)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.WATER);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    }
                }
                break;
            case NECRO:
                ParticleEffect.SOUL.display(this.location, 6, effectRadius / 2, effectRadius / 2, effectRadius / 2, 0.1);
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.effectRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                        if (isRadiant((LivingEntity) entity)) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, this.effectDuration, 1, true, true));
                            removeRadiance((LivingEntity) entity);

                            if (entity instanceof Player) {
                                Player player1 = (Player) entity;
                                player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(effectMessage));
                            }
                        }
                    }
                }

                for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, effectRadius)) {
                    Material matType = block.getType();
                    if (matType.equals(Material.GRASS_BLOCK) || matType.equals(Material.MYCELIUM) || matType.equals(Material.PODZOL)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.DIRT_PATH);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    } else if (matType.equals(Material.MOSSY_COBBLESTONE)) {
                        TempBlock affectedBlock = new TempBlock(block, Material.COBBLESTONE);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    } else if (toCrackedStoneBricks.contains(matType.toString())) {
                        TempBlock affectedBlock = new TempBlock(block, Material.CRACKED_STONE_BRICKS);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    } else if (toDeadBushes.contains(matType.toString())) {
                        TempBlock affectedBlock = new TempBlock(block, Material.DEAD_BUSH);
                        affectedBlock.setRevertTime(this.blockRegenDelay);
                    }
                }
                break;
            default:
                List<Location> particleLocs = PCMethods.drawSpherePoints(this.location, 5, 5, true, true, 0);
                Random rand = new Random();

                for (int i = 0; i < 30; i++) {
                    Location temp = particleLocs.get(rand.nextInt(particleLocs.size()));
                    double x, y, z;

                    x = this.location.getX() - temp.getX();
                    y = this.location.getY() - temp.getY();
                    z = this.location.getZ() - temp.getZ();

                    ParticleEffect.END_ROD.display(this.location, 0, x, y, z);
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.effectRadius)) {
                    if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                        if (isRadiant((LivingEntity) entity)) {
                            MovementHandler movementHandler = new MovementHandler((LivingEntity) entity, this);
                            movementHandler.stopWithDuration(this.effectDuration, this.effectMessage);
                            removeRadiance((LivingEntity) entity);
                        }
                    }
                }
                break;
        }
    }


    private void drawCircle() {
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = this.location.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 0.5);
                temp.add(dir);
                GeneralMethods.displayColoredParticle(hexVal2, temp, 1, 0, 0, 0);
            }
        }
    }

    @Override
    public void remove() {
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.ForceField.EnergyBolt.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "EnergyBolt";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getAuthor() {
        return "KWilson272";
    }

    @Override
    public String getDescription() {
        return "EnergyBolt is an extremely versatile move in any ForcefieldBender's arsenal, and can embody Fire, Ice, Electricity, and even Death!";
    }

    @Override
    public String getInstructions() {
        return "- Left-Click! - \n" +
                "- To see the different bolt types, type '@EnergyBolt Types -";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }


    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }
}
