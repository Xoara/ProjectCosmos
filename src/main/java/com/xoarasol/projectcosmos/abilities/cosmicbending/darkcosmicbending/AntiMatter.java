package com.xoarasol.projectcosmos.abilities.cosmicbending.darkcosmicbending;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.DarkCosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class AntiMatter extends DarkCosmicAbility implements AddonAbility {

    private Location location;
    private Vector direction;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    private boolean avatarOnly;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    private boolean damagesBlocks;
    private long regen;
    @Attribute(Attribute.RADIUS)
    private double radius;
    private int an;

    public AntiMatter(Player player) {
        super(player);
        if (bPlayer.isOnCooldown(this)) {
            return;
        }

        setFields();
        byte b;
        int i;
        ItemStack[] arrayOfItemStack;
        for (i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i; ) {
            ItemStack item = arrayOfItemStack[b];
            if (item != null && item.getType() == Material.NETHERITE_SWORD && item.containsEnchantment(Enchantment.MENDING) && item.containsEnchantment(Enchantment.KNOCKBACK) && item.containsEnchantment(Enchantment.LOOT_BONUS_MOBS) && item.containsEnchantment(Enchantment.DAMAGE_ALL) && item.containsEnchantment(Enchantment.SWEEPING_EDGE) && item.containsEnchantment(Enchantment.DURABILITY) && item.containsEnchantment(Enchantment.DAMAGE_UNDEAD))
                if (item.getAmount() >= 1) {
                    item.setAmount(item.getAmount() - 0);


                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.75f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 0.75f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1.65f);
                    ActionBar.sendActionBar(PCElement.COSMIC.getColor() + "* DARKNESS DEVOURS! *", new Player[]{this.player});
                    start();

                } else {
                    return;
                }
            b++;
        }
    }

    public void setFields() {

        duration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Dark.AntiMatter.Duration");
        cooldown = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Dark.AntiMatter.Cooldown");
        damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Dark.AntiMatter.Damage");
        range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Dark.AntiMatter.Range");
        avatarOnly = false;
        damagesBlocks = ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Enabled");
        regen = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Regen");
        radius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Radius");
    }
    public boolean isHiddenAbility() {
        return true;
    }

    @Override
    public void progress() {
        if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }
        if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (!player.isSneaking()) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        if (avatarOnly && !bPlayer.isAvatarState()) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }
        createBeam();
    }

    private void createBeam() {
        location = player.getLocation().add(0, 1.2, 0);
        direction = location.getDirection();
        for (double i = 0; i < range; i += 0.5) {
            location = location.add(direction.multiply(0.5).normalize());

            if (RegionProtection.isRegionProtected(player, location, this)) {
                return;
            }

            ParticleEffect.SQUID_INK.display(location, 1, 0.5f, 0.5f, 0.5f, 0f);
            Spirals();
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2)) {
                if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId() && !(entity instanceof ArmorStand)) {
                    entity.setFireTicks(100);
                    DamageHandler.damageEntity(entity, damage, this);
                }
            }

            if (location.getBlock().getType().isSolid()) {
                location.getWorld().createExplosion(location, 0F);
                if (damagesBlocks) {
                    //new TempExplosion(player, location.getBlock(), "AntiMatter", radius, regen, damage, false);
                    for (Location loc : GeneralMethods.getCircle(location, (int) radius, 0, false, true, 0)) {
                        TempBlock tb = new TempBlock(loc.getBlock(), Material.AIR);
                        tb.setRevertTime(regen);
                    }
                }
                return;
            }
        }
    }

    private void Spirals() {
        this.an += 20;
        if (this.an > 360)
            this.an = 0;
        for (int i = 0; i < 1; i++) {
            for (double d = -1.0D; d <= 0.0D;

                 d += 1.0D) {
                Location l = this.location.clone();
                double r = d * 2.0 / 1.0D;
                if (r > 1.0D)
                    r = 1.0D;
                Vector ov = GeneralMethods.getOrthogonalVector(this.direction, (this.an + 180 * i) + d, r);
                Location pl = l.clone().add(ov.clone());


                switch (i) {
                    case 0:

                        ParticleEffect.SPELL_WITCH.display(pl, 1, 0, 0, 0, 0f);
                        break;
                    case 1:

                        ParticleEffect.SPELL_WITCH.display(pl, 1, 0, 0, 0, 0f);
                        break;
                }
            }
        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return "AntiMatter";
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
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public String getAuthor() {
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    @Override
    public String getDescription() {
        return "This Ability can only the used with the Sword of The Dark Star.\n" +
                "THIS ABILITY ALLOWS DARK COSMICBENDERS TO CONSUME THE GROUND AROUND THEM USING A BEAM OF ANTI MATTER!";
    }

    @Override
    public String getInstructions() {
        return "- Hold-Shift! -";
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public boolean isAvatarOnly() {
        return avatarOnly;
    }

    public void setAvatarOnly(boolean avatarOnly) {
        this.avatarOnly = avatarOnly;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean damagesBlocks() {
        return damagesBlocks;
    }

    public void setDamagesBlocks(boolean blockdamage) {
        this.damagesBlocks = blockdamage;
    }

    public long getRegen() {
        return regen;
    }

    public void setRegen(long regen) {
        this.regen = regen;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Dark.AntiMatter.Enabled");
    }
}