package com.xoarasol.projectcosmos.abilities.cosmicbending.darkcosmicbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.api.DarkCosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Users may edit, copy, distribute and manage this project. They will not have
 * permission to change this project's credits and claim it as theirs. All
 * rights are reserved to the Author.
 *
 * @author dario (_Hetag1216_)
 */
public class NullSphere extends DarkCosmicAbility implements AddonAbility {
    // pre set vars
    private Location location;
    private Location origin;
    private Vector direction;
    // settable vars
    private long cooldown;
    private float range, radius, damage, knockback, speed, spread;
    // effects
    private float distance;
    public static Config config;
    private boolean controllable;
    private boolean hit;
    private Random rand;

    public NullSphere(Player player) {
        super(player);

        if (this.bPlayer.canBend(this)) {
            cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dark.NullSphere.Cooldown");
            range = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dark.NullSphere.Range");
            damage = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dark.NullSphere.Damage");
            speed = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dark.NullSphere.Speed") * (ProjectKorra.time_step / 1000F);
            this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
            distance = 15;
            spread = 0;
            controllable = true;

            location = player.getEyeLocation();
            direction = location.getDirection();

            byte b;
            int i;
            ItemStack[] arrayOfItemStack;
            for (i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i; ) {
                ItemStack item = arrayOfItemStack[b];
                if (item != null && item.getType() == Material.NETHERITE_SWORD && item.containsEnchantment(Enchantment.MENDING) && item.containsEnchantment(Enchantment.KNOCKBACK) && item.containsEnchantment(Enchantment.LOOT_BONUS_MOBS) && item.containsEnchantment(Enchantment.DAMAGE_ALL) && item.containsEnchantment(Enchantment.SWEEPING_EDGE) && item.containsEnchantment(Enchantment.DURABILITY) && item.containsEnchantment(Enchantment.DAMAGE_UNDEAD))
                    if (item.getAmount() >= 1) {
                        item.setAmount(item.getAmount() - 0);


                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.75f);
                        ActionBar.sendActionBar(PCElement.COSMIC.getColor() + "* CONSUME! *", new Player[]{this.player});
                        start();
                        this.bPlayer.addCooldown((Ability) this);

                    } else {
                        return;
                    }
                b++;
            }
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }
        if (this.origin.distance(this.location) > this.range) {
            remove();
            return;
        }
        if (this.controllable) {
            Vector untouchVector = this.direction.clone().multiply(this.speed);
            Location destinationLocation = this.location.clone().add(this.player.getLocation().getDirection().multiply(this.speed));
            Vector desiredVector = destinationLocation.toVector().subtract(this.location.clone().toVector());
            Vector steeringVector = desiredVector.subtract(untouchVector).multiply(0.25D);
            this.direction = this.direction.add(steeringVector);
        }
        if (GeneralMethods.isSolid(this.location.getBlock())) {
            remove();
            return;
        }

        this.location.add(this.direction.clone().multiply(this.speed));
        Sphere();
        ParticleEffect.SQUID_INK.display(this.location, 4, 2, 2, 2, 0.05);

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.75D)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, this.damage, (Ability) this);
                entity.setVelocity(this.direction.multiply(1));
                return;
            }
        }
    }

    private void Sphere() {
        if (this.location == null) {
            Location origin = this.player.getEyeLocation().clone();
            this.location = origin.clone();
        }
        Location tempLocation = this.location.clone();
        for (int i = 0; i < 5; i++) {
            for (int angle = 0; angle < 360; angle += 20) {
                Location temp = tempLocation.clone();
                Vector dir = GeneralMethods.getOrthogonalVector(this.direction.clone(), angle, 2.75D);
                temp.add(dir);
                ParticleEffect.SQUID_INK.display(temp, 2, 0.0D, 0.0D, 0.0D, 0.003000000026077032D);

            }
            if (this.hit) {
                remove();
                return;
            }
            tempLocation.add(this.direction.clone().multiply(0.2D));
        }
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
        return "NullSphere";
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this, this.cooldown);
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Dark.NullSphere.Enabled");
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
        return true;
    }

    @Override
    public String getAuthor() {
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
    }

    public String getDescription() {
        return "This Ability can only the used with the Sword of The Dark Star.\n" +
        "Dark Cosmicbenders are able to create spheres of absolute Zero and manipulate it towards their enemies to deal immense damage.";
    }

    public String getInstructions() {
        return "LeftClick!";
    }

    @Override
    public void load() {
    }


    @Override
    public void stop() {

    }
}