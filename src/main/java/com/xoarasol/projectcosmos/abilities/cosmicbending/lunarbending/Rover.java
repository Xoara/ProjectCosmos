package com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.LunarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Rover extends LunarAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private int duration;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    private int ticksTillExplosion;
    private double explosionRadius;
    private double hopPower;

    private Minecart cart;
    private BossBar bossBar;
    private Vector direction;

    private int progressCounter;
    private int detonationCounter;
    private double knockback;
    private int pstage;

    public Rover(Player player) {
        super(player);


        if (CoreAbility.hasAbility(player, Rover.class)) {
            Rover rover = CoreAbility.getAbility(player, Rover.class);
            rover.hopa();
            return;
        }

        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Rover.Cooldown");
            this.duration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.Rover.Duration");
            this.speed = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Rover.Speed");
            this.damage = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Lunar.Rover.Damage");
            this.knockback = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Rover.Knockback");
            this.ticksTillExplosion = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Lunar.Rover.ExplosionTicks");
            this.explosionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Rover.ExplosionRadius");
            this.hopPower = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Lunar.Rover.HopPower");

            this.cart = (Minecart) player.getWorld().spawnEntity(player.getLocation(), EntityType.MINECART);
            cart.setInvulnerable(true);
            cart.setMaxSpeed(speed);

            this.bossBar = player.getServer().createBossBar("Battery Power", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBar.setProgress(1);

            progressCounter = 0;
            detonationCounter = 0;

            start();
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        if (duration <= progressCounter) {
            detonate();
            return;
        }

        if (isWater(cart.getLocation().getBlock())) {
            detonate();
        }
        List<Entity> inCart = new ArrayList<>();
        inCart = cart.getPassengers();

        progressCounter++;
        double progress = 1 - ((double) progressCounter / duration);
        bossBar.setProgress(progress);

        if (!inCart.isEmpty()) {
            if (this.cart.getPassengers().contains(player)) {
                Vector dir = player.getEyeLocation().getDirection();

                if (cart.isOnGround()) {
                    dir.setY(0);

                    LunarGround();
                    LunarGround2();
                    LunarGround3();
                    LunarGround4();
                    LunarGround5();
                    LunarGround6();

                } else {
                    dir.setY(-0.1);
                }
                cart.setVelocity(dir.normalize().multiply(speed));

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 1.0)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        DamageHandler.damageEntity(entity, player, damage, this);
                        remove();
                        return;
                    }
                }
            } else {
                for (Entity entity : inCart) {
                    if (entity instanceof Player) {
                        detonationCounter++;

                        int ticksLeft = ticksTillExplosion - detonationCounter;

                        Player cartPlayer = Bukkit.getPlayer(entity.getUniqueId());
                        cartPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "* UNAUTHORIZED DRIVER! INNITATING SELF-DESTRUCTION IN " + ticksLeft + "! *"));
                        cartPlayer.getWorld().playSound(cartPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 0);

                        Vector dir = cartPlayer.getEyeLocation().getDirection();

                        if (cart.isOnGround()) {
                            dir.setY(0);
                        } else {
                            dir.setY(-1);
                        }
                        cart.setVelocity(dir.normalize().multiply(speed));
                    }
                }

                if (detonationCounter == ticksTillExplosion) {
                    detonate();
                }
            }
            cart.getWorld().playSound(cart.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 0.5F, 0.0F);
        }
    }

    public Location getRightHandPos() {
        return GeneralMethods.getRightSide(player.getLocation(), .5).add(0, 1, 0);
    }

    private void LunarGround() {
        new ColoredParticle(Color.fromRGB(192, 207, 255), 1.2f).display(getRightHandPos().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }

    public Location getRightHandPos2() {
        return GeneralMethods.getRightSide(player.getLocation(), .7).add(0, 1, 0);
    }

    private void LunarGround2() {
        new ColoredParticle(Color.fromRGB(145, 157, 193),1.2f).display(getRightHandPos2().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }

    public Location getRightHandPos3() {
        return GeneralMethods.getRightSide(player.getLocation(), .9).add(0, 1, 0);
    }

    private void LunarGround3() {
        new ColoredParticle(Color.fromRGB(103, 111, 137),1.2f).display(getRightHandPos3().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }


    public Location getLeftHandPos() {
        return GeneralMethods.getLeftSide(player.getLocation(), .5).add(0, 1, 0);
    }

    private void LunarGround4() {
        new ColoredParticle(Color.fromRGB(192, 207, 255), 1.2f).display(getLeftHandPos().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }

    public Location getLeftHandPos2() {
        return GeneralMethods.getLeftSide(player.getLocation(), .7).add(0, 1, 0);
    }

    private void LunarGround5() {
        new ColoredParticle(Color.fromRGB(145, 157, 193),1.2f).display(getLeftHandPos2().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }

    public Location getLeftHandPos3() {
        return GeneralMethods.getLeftSide(player.getLocation(), .9).add(0, 1, 0);
    }

    private void LunarGround6() {
        new ColoredParticle(Color.fromRGB(103, 111, 137),1.2f).display(getLeftHandPos3().toVector().add(player.getEyeLocation().getDirection().clone().multiply(.8D)).toLocation(player.getWorld()), 3, 0.08, 0.08, 0.08);
    }


    private void detonate() {
        Location loc = cart.getLocation();
        ParticleEffect.EXPLOSION_HUGE.display(loc, 2, 0.2, 0.2, 0.2);
        ParticleEffect.CLOUD.display(loc, 15, 0.2, 0.2, 0.2, 0.08);
        ParticleEffect.SMOKE_LARGE.display(loc, 15, 0.2, 0.2, 0.2, 0.08);
        ParticleEffect.FLASH.display(loc, 1, 1, 1, 1);
        cart.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5F, 2);
        cart.getWorld().playSound(loc, Sound.ITEM_TOTEM_USE, 0.5F, 1.2f);
        cart.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
        cart.remove();

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(cart.getLocation(), explosionRadius)) {
            if (entity instanceof LivingEntity) {
                DamageHandler.damageEntity(entity, player, damage, this);
                Vector vec = PCMethods.createDirectionalVector(loc, entity.getLocation());
                GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(2));
            }
        }
        remove();
    }

    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        bossBar.removeAll();

        if (!cart.isDead()) {
            cart.remove();
        }

        super.remove();
    }

    public static void hop(Player player) {
        if (CoreAbility.hasAbility(player, Rover.class)) {
            Rover cs = CoreAbility.getAbility(player, Rover.class);
            cs.hopa();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 15f, 0.66f);
            return;
        }
    }

    public void hopa() {
        if (cart.isOnGround() && cart.getPassengers().contains(player)) {
            Vector vec = new Vector(0, 1, 0);
            cart.setVelocity(vec.normalize().multiply(hopPower));
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Lunar.Rover.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Rover";
    }

    @Override
    public Location getLocation() {
        return cart != null ? cart.getLocation() : null;
    }

    @Override
    public String getDescription() {
        return "Master Lunarbenders can summon a moon-rover and use it to trek the terrain around themselves! VROOOOOOOOOOOOM!";
    }

    @Override
    public String getInstructions() {
        return "- Tap-Shift > Right-Click the Rover to hop in!";
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
