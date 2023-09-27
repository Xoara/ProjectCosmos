package com.xoarasol.projectcosmos.abilities.laserbending.combos;

import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.RadiationAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Ionize extends RadiationAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute("RadianceDuration")
    private int radianceDuration;
    @Attribute("SelfRadianceDuration")
    private int selfRadianceDuration;

    Random rand = new Random();

    private int id;
    private static int ID = Integer.MIN_VALUE;

    private ConcurrentHashMap<Integer, Ray> rays = new ConcurrentHashMap<>();
    private int pstage;

    public Ionize(Player player) {
        super(player);

        if (!CoreAbility.hasAbility(player, Ionize.class) && this.bPlayer.canBendIgnoreBinds(this)) {
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Laser.Combos.Ionize.Cooldown");
            this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Ionize.Range");
            this.speed = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Laser.Combos.Ionize.Speed");
            this.radianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Ionize.RadianceDuration");
            this.selfRadianceDuration = ProjectCosmos.plugin.getConfig().getInt("Abilities.Laser.Combos.Ionize.SelfRadianceDuration");

            this.speed *= (ProjectKorra.time_step / 1000F);

            spawnRays();
            start();
        }
    }

    private void spawnRays() {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.player.getEyeLocation(), this.range)) {
            if (entity instanceof LivingEntity && !entity.equals(this.player)) {
                id = ID;
                this.rays.put(id, new Ray(this, this.player.getEyeLocation(), (LivingEntity) entity, id));
                if (ID == Integer.MAX_VALUE) {
                    ID = Integer.MIN_VALUE;
                }
                ID++;
            }
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
            this.bPlayer.addCooldown(this);
            remove();
        }
        progressRays();
        omens();
    }

    private void progressRays() {
        if (!rays.isEmpty()) {
            for (int id : rays.keySet()) {
                rays.get(id).progressRay();
            }
        } else {
            this.bPlayer.addCooldown(this);
            remove();
        }
    }

    private void omens() {
        Location centre = player.getLocation();
        double increment = (4 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (1 * Math.cos(angle));
        double z = centre.getZ() + (1 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY() + 1, z);

        new ColoredParticle(Color.fromRGB(56, 255, 157), 2).display(loc, 2, 0, 0, 0);
        ParticleEffect.FIREWORKS_SPARK.display(loc, 1, 0.1, 0.1, 0.1, 0.1);


        double x2 = centre.getX() + (1 * -Math.cos(angle));
        double z2 = centre.getZ() + (1 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY() + 1, z2);

        new ColoredParticle(Color.fromRGB(56, 255, 157), 2).display(loc2, 2, 0, 0, 0);
        ParticleEffect.FIREWORKS_SPARK.display(loc2, 1, 0.1, 0.1, 0.1, 0.1);


        pstage++;
    }
    @Override
    public void remove() {
        if (!rays.isEmpty()) {
            rays.clear();
        }
        setRadiance(this.player, selfRadianceDuration);
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Laser.Combos.Ionize.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Ionize";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public String getDescription() {
        return "Radiationbenders are able to irradiate themselves, and send entity seeking rays out to irradiate others with this ability.";
    }

    @Override
    public String getInstructions() {
        return "- Contaminate (Tap-Shift) > Contaminate (Hold-Shift) > LaserFission (Release-Shift)";
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

    @Override
    public Object createNewComboInstance(Player player) {
        return new Ionize(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> ionize = new ArrayList<>();
        ionize.add(new AbilityInformation("Contaminate", ClickType.SHIFT_DOWN));
        ionize.add(new AbilityInformation("Contaminate", ClickType.SHIFT_UP));
        ionize.add(new AbilityInformation("Contaminate", ClickType.SHIFT_DOWN));
        ionize.add(new AbilityInformation("LaserFission", ClickType.SHIFT_UP));
        return ionize;
    }

    public class Ray {

        private Ionize ability;
        private Location origin;
        private Location location;
        private LivingEntity target;
        private int id;

        private int greenCounter;
        private int blueCounter;

        public Ray(Ionize ability, Location origin, LivingEntity target, int id) {

            this.ability = ability;
            this.origin = origin;
            this.location = origin.clone();
            this.target = target;
            this.id = id;

            this.greenCounter = 242;
            this.blueCounter = 0;
        }

        public void progressRay() {
            for (int i = 0; i < 4; i++) {
                if (GeneralMethods.isSolid(this.location.getBlock())) {
                    rays.remove(id);
                }
                if (target.getLocation().distance(this.origin) > range) {
                    rays.remove(id);
                }

                if (this.blueCounter >= 255) {
                    this.blueCounter = 255;
                } else {
                    this.blueCounter += 5;
                }

                if (this.greenCounter >= 255) {
                    this.greenCounter = 255;
                } else {
                    this.greenCounter += 1;
                }

                new ColoredParticle(Color.fromRGB(0, this.greenCounter, this.blueCounter), 1).display(this.location, 2, 0, 0, 0);
                ParticleEffect.FIREWORKS_SPARK.display(this.location, 1, 0.1, 0.1, 0.1, 0.1);
                this.location.getWorld().playSound(this.location, Sound.BLOCK_CHORUS_FLOWER_GROW, 1F, 2);

                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 1)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        setRadiance((LivingEntity) entity, radianceDuration);
                        rays.remove(id);
                    }
                }

                double x, y, z;
                x = this.target.getEyeLocation().getX() - this.location.getX();
                y = this.target.getEyeLocation().getY() - this.location.getY();
                z = this.target.getEyeLocation().getZ() - this.location.getZ();

                Vector direction = new Vector(x, y, z);
                this.location.add(direction.normalize().multiply(speed / 3));

            }
        }
    }
}