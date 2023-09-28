package com.xoarasol.projectcosmos.abilities.earthbending.sandbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class AridEruption extends SandAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.DURATION)
    private double range;


    private Location origin;
    private Location location;
    private Vector direction;

    private boolean charged;
    private boolean isExploded;
    private boolean hasDetonated;

    ArrayList<UUID> affected = new ArrayList<>();

    Random rand = new Random();

    private int id;
    private static int ID = Integer.MIN_VALUE;

    private ConcurrentHashMap<Integer, Wave> waves = new ConcurrentHashMap<>();

    public AridEruption(Player player) {
        super(player);
        if (this.bPlayer.canBend(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Earth.Sand.AridEruption.Cooldown");
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Earth.Sand.AridEruption.ChargeTime");
            this.damage = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Earth.Sand.AridEruption.Damage");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Earth.Sand.AridEruption.Steps");


            this.origin = this.player.getEyeLocation();
            this.location = this.origin.clone();
            this.direction = origin.getDirection();


            this.charged = false;
            this.isExploded = false;
            this.hasDetonated = false;

            Block topBlock = GeneralMethods.getTopBlock(player.getLocation(), 0, -50);
            if (!isSand(topBlock.getType())) {
                if (topBlock == null)
                    topBlock = player.getLocation().getBlock();
                Material mat = topBlock.getType();
                if (isSandbendable(topBlock))
                    return;
            }
            if (GeneralMethods.isRegionProtectedFromBuild((Ability) this, player.getLocation()))
                return;

            start();
        }
    }


    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            if (this.charged) {
                this.bPlayer.addCooldown(this);
            }
            this.remove();
            return;
        }
        if (!this.bPlayer.getBoundAbilityName().equalsIgnoreCase("AridEruption") && !this.isExploded) {
            remove();
            return;
        }

        if (System.currentTimeMillis() > this.getStartTime() + this.chargeTime) {
            this.charged = true;

            this.origin = this.player.getEyeLocation();
            this.location = this.player.getEyeLocation();
            this.direction = this.player.getEyeLocation().getDirection();
        }
        if (!this.player.isSneaking() && !this.isExploded) {
            this.isExploded = true;
        }
        if (this.isExploded && !this.charged) {
            this.remove();
            return;
        }
        if (this.isExploded && !this.hasDetonated) {
            this.hasDetonated = true;
            detonate();
        } else if (this.isExploded) {
            progressWaves();
        }
        if (this.charged && this.player.isSneaking()) {
            Location loc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(2));

            new ColoredParticle(Color.fromRGB(205, 188, 145), 1.5f).display(loc, 2, 0.5,0.5,0.5);
            ParticleEffect.BLOCK_CRACK.display(loc, 3, 0.5D, 0.5D, 0.5D, 0, Material.SAND.createBlockData());

        }
    }

    private void progressWaves() {
        if (waves.isEmpty()) {
            remove();
        }

        for (int id : waves.keySet()) {
            waves.get(id).progressWave();
        }
    }

    public void detonate() {
        this.bPlayer.addCooldown(this);
        this.location.getWorld().playSound(this.location, Sound.BLOCK_SAND_BREAK, 2f, 0.8f);
        this.location.getWorld().playSound(this.location, Sound.BLOCK_GRASS_STEP, 2f, 0.85f);

        Location fake = this.player.getLocation().add(0, -1, 0);
        fake.setPitch(0);
        for (int i = -180; i < 180; i += 45) {
            fake.setYaw(i);
            for (double j = -180; j <= 180; j += 55) {
                Location temp = fake.clone();
                Vector dir = fake.getDirection().normalize().clone().multiply(1 * Math.cos(Math.toRadians(j)));
                temp.add(dir);
                temp.setY(temp.getY() + 2 + (2 * Math.sin(Math.toRadians(j))));
                dir = GeneralMethods.getDirection(this.player.getLocation().add(0, 0, 0), temp);
                spawnWave(this.player.getLocation().clone().add(0, 0, 0).setDirection(dir), range, 0.5);
            }
        }
    }

    private void spawnWave(Location location, double range, double gap) {
        id = ID;
        waves.put(id, new Wave(this, location, range, gap, id));
        if (ID == Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        ID++;
    }

    @Override
    public void remove() {
        if (!this.waves.isEmpty()) {
            waves.clear();
        }
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
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Earth.Sand.AridEruption.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "AridEruption";
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return "Sandbenders are able to gather dust and sand from below them, and release an eruption of sand, blinding enemies!!";
    }


    @Override
    public void load() {
    }

    @Override
    public void stop() {
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
    public String getInstructions() {
        return "- Hold Shift > Release when it's charged! -";
    }

    public class Wave {

        private AridEruption ability;
        private Location location;
        private Location origin;
        private double range;
        private double gap;
        private int id;
        private double step;
        private double initPitch;
        private int count;
        private Random rand = new Random();
        private int blindDuration;
        private int blindPower;

        public Wave(AridEruption ability, Location location, Double range, double gap, int id) {

            this.ability = ability;
            this.origin = location;
            this.location = location;
            this.blindDuration = getConfig().getInt("Abilities.Earth.Sand.AridEruption.Blindness.Duration");
            this.blindPower = getConfig().getInt("Abilities.Earth.Sand.AridEruption.Blindness.Power");
            this.range = range;
            this.gap = gap;
            this.id = id;
            this.initPitch = this.location.getPitch();
            this.count = 0;
        }

        public void progressWave() {
            if (step > this.range) {
                waves.remove(id);
                return;
            }
            double step = 0.1;
            for (double i = 0; i < gap; i += step) {
                this.step += step;
                location = location.add(location.getDirection().clone().multiply(step));
                float pitchVal = this.location.getPitch();

                if (count >= 7 && count <= 20) {
                    pitchVal += 1.2;

                    if (pitchVal > 90) {
                        pitchVal = 90;
                    }
                    this.location.setPitch(pitchVal);
                }


                if (!GeneralMethods.isTransparent(this.location.getBlock()) || GeneralMethods.isRegionProtectedFromBuild(player, "AridEruption", this.location)) {
                    waves.remove(id);
                }


                ParticleEffect.BLOCK_CRACK.display(this.location, 1, 0.07, 0.07, 0.07, 0.09, Material.SAND.createBlockData());

                if (this.rand.nextInt(100) == 0)
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_SAND_PLACE, 0.6f, 0.85f);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2)) {
                    if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId()) && !affected.contains(entity.getUniqueId())) {
                        DamageHandler.damageEntity(entity, player, damage, ability);
                        ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindDuration, this.blindPower));
                        affected.add(entity.getUniqueId());
                    }
                }

            }
            if (!(count > 21)) {
                count++;
            }
        }
    }
}
