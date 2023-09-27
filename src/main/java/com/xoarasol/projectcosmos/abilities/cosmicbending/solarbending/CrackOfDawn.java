package com.xoarasol.projectcosmos.abilities.cosmicbending.solarbending;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.SolarAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public class CrackOfDawn extends SolarAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute("RiseInterval")
    private long riseInterval;
    @Attribute("MaxShots")
    private int maxShots;
    @Attribute("LevitationSpeed")
    private double riseSpeed;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.RADIUS)
    private double collisionRadius;
    private long threshold;
    private double descendSpeed;

    private int shots;
    private boolean charged;
    private long time;

    private int id;
    private ConcurrentHashMap<Integer, Shot> rays = new ConcurrentHashMap<>();

    private int currPoint;
    private int pstage;

    public CrackOfDawn(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, CrackOfDawn.class)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.CrackOfDawn.Cooldown");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.CrackOfDawn.Damage");
            this.riseInterval = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.CrackOfDawn.RiseInterval");
            this.maxShots = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Solar.CrackOfDawn.MaxShots");
            this.riseSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.CrackOfDawn.LevitationSpeed");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.CrackOfDawn.ShotRange");
            this.speed = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.CrackOfDawn.ShotSpeed") * (ProjectKorra.time_step / 1000F);
            this.threshold = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Solar.CrackOfDawn.DisableThreshold");
            this.collisionRadius = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.CrackOfDawn.CollisionRadius");
            this.descendSpeed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Solar.CrackOfDawn.DescendSpeed");

            this.shots = 0;
            this.charged = false;
            this.time = System.currentTimeMillis();

            this.id = 0;
            this.currPoint = 0;

            start();
        }

    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBend(this)) {
            remove();
            return;
        }

        if (System.currentTimeMillis() > threshold + time) {
            remove();
            return;
        }
        if (charged && shots <= 0 && rays.isEmpty()) {
            remove();
            return;
        }

        if (!charged) {
            if (this.player.isSneaking()) {
                if (System.currentTimeMillis() > time + riseInterval) {
                    time = System.currentTimeMillis();
                    shots++;
                }
                if (shots >= maxShots) {
                    shots = maxShots;
                    charged = true;
                    return;
                }
                Vector vec = new Vector(0, 1, 0);
                GeneralMethods.setVelocity(this, player, vec.normalize().multiply(riseSpeed));
                new ColoredParticle(Color.fromRGB(255, 199, 0), 1.4F).display(player.getLocation(), 3, 0.5, 0.5, 0.5);
                new ColoredParticle(Color.fromRGB(255, 216, 89), 1.4F).display(player.getLocation(), 3, 0.5, 0.5, 0.5);
                new ColoredParticle(Color.fromRGB(255, 230, 147), 1.4F).display(player.getLocation(), 3, 0.5, 0.5, 0.5);
                omens();
                omens2();
                omens3();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2, 1.15f);
            } else {
                if (!(shots > 0)) {
                    remove();
                    return;
                }
                this.charged = true;
            }
        } else {
            progressShots();
            Vector down = new Vector(0, -1, 0);
            GeneralMethods.setVelocity(this, player, down.clone().normalize().multiply(descendSpeed));
            //displays the fully charged ring
            for (int i = 0; i < 5; i++) {
                currPoint -= 1;

                if (currPoint > 360) {
                    currPoint = 0;
                }

                double angle = currPoint * Math.PI / 180.0D;
                double x = 1.5 * Math.cos(angle);
                double z = 1.5 * Math.sin(angle);
                Location loc = player.getLocation().add(x, 1, z);
                new ColoredParticle(Color.fromRGB(255, 221, 0), 0.9F).display(loc, 2, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(255, 179, 0), 0.9F).display(loc, 1, 0.1, 0.1, 0.1);
                new ColoredParticle(Color.fromRGB(255, 123, 0), 0.9F).display(loc, 1, 0.1, 0.1, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.45f, 1.25f);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHORUS_FLOWER_GROW, 0.45f, 0.75f);
            }
        }
    }

    private void omens() {
        Location centre = player.getLocation();
        double increment = (1 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.5 * Math.cos(angle));
        double z = centre.getZ() + (1.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        new ColoredParticle(Color.fromRGB(255, 199, 0), 1.6F).display(loc, 2, 0.1, 0.1, 0.1);
        double x2 = centre.getX() + (1.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        new ColoredParticle(Color.fromRGB(255, 199, 0), 1.6F).display(loc2, 2, 0.1, 0.1, 0.1);

        pstage++;
    }

    private void omens2() {
        Location centre = player.getLocation();
        double increment = (1 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.7 * Math.cos(angle));
        double z = centre.getZ() + (1.7 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        new ColoredParticle(Color.fromRGB(255, 216, 89), 1.6F).display(loc, 2, 0.1, 0.1, 0.1);

        double x2 = centre.getX() + (1.7 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.7 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        new ColoredParticle(Color.fromRGB(255, 216, 89), 1.6F).display(loc2, 2, 0.1, 0.1, 0.1);

        pstage++;
    }

    private void omens3() {
        Location centre = player.getLocation();
        double increment = (1 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1.9 * Math.cos(angle));
        double z = centre.getZ() + (1.9 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        new ColoredParticle(Color.fromRGB(255, 230, 147), 1.6F).display(loc, 2, 0.1, 0.1, 0.1);

        double x2 = centre.getX() + (1.9 * -Math.cos(angle));
        double z2 = centre.getZ() + (1.9 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        new ColoredParticle(Color.fromRGB(255, 230, 147), 1.6F).display(loc2, 2, 0.1, 0.1, 0.1);

        pstage++;
    }

    private void progressShots() {
        if (shots <= 0 && rays.isEmpty()) {
            remove();
            return;
        }
        for (int id : rays.keySet()) {
            rays.get(id).progressShot();
        }
    }


    public void fireShot() {
        if (shots > 0 && charged) {
            rays.put(id, new Shot(id, this, player.getLocation().add(0, 1, 0)));
            id++;
            shots--;

            if (id >= Integer.MAX_VALUE) {
                id = Integer.MIN_VALUE;

            }
            if (shots > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "You have " + ChatColor.GOLD + shots + ChatColor.YELLOW + " shots left!"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "You're all out of shots!"));
            }
        }
    }

    public static void activate(Player player) {
        if (CoreAbility.hasAbility(player, CrackOfDawn.class)) {
            CrackOfDawn crackOfDawn = CoreAbility.getAbility(player, CrackOfDawn.class);
            crackOfDawn.fireShot();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.66f);

        }
    }


    @Override
    public void remove() {
        this.bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public String getDescription() {
        return  "With this ability you can (literally) become the sunrise. Using your solar power, you can soar up into the sky. Once high above the grounds, you can cast solar flares at your foes!";
    }

    @Override
    public String getInstructions() {
        return "Hold Shift!";
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
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "CrackOfDawn";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? player.getLocation() : null;
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

    public class Shot {

        private int id;
        private CrackOfDawn ability;

        private Location origin;
        private Location location;
        private Vector direction;

        public Shot(int id, CrackOfDawn ability, Location location) {
            this.id = id;
            this.ability = ability;
            this.origin = location;
            this.location = origin.clone();
            this.direction = origin.getDirection();
            ParticleEffect.FLASH.display(player.getLocation().add(0, 1, 0), 1, 0, 0, 0);

        }

        public void progressShot() {
            for (int i = 0; i <= 3; i++) {
                if (GeneralMethods.isSolid(location.getBlock()) || isWater(location.getBlock())) {
                    rays.remove(this.id);
                    return;
                }
                if (location.distance(origin) > range) {
                    rays.remove(this.id);
                    return;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, collisionRadius)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())) {
                        ParticleEffect.FLASH.display(entity.getLocation(), 1, 0, 0, 0);
                        DamageHandler.damageEntity(entity, player, damage, ability);
                        rays.remove(this.id);
                    }
                }
                ParticleEffect.END_ROD.display(location, 2, 0, 0, 0, 0.05);
                new ColoredParticle(Color.fromRGB(255, 199, 0), 1F).display(location, 3, 0.3, 0.3, 0.3);
                new ColoredParticle(Color.fromRGB(255, 216, 89), 1F).display(location, 3, 0.3, 0.3, 0.3);
                new ColoredParticle(Color.fromRGB(255, 230, 147), 1F).display(location, 3, 0.05, 0.05, 0.05);
                new ColoredParticle(Color.fromRGB(255, 216, 89), 1).display(location, 4, collisionRadius / 2, collisionRadius / 2, collisionRadius / 2);
                new ColoredParticle(Color.fromRGB(255, 199, 0), 0.5F).display(location, 8, collisionRadius / 2, collisionRadius / 2, collisionRadius / 2);
                location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1, 1f);

                location.add(player.getLocation().getDirection().clone().normalize().multiply(speed / 3));
            }
        }
    }
}
