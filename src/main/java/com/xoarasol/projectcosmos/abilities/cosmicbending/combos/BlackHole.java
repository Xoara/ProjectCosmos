package com.xoarasol.projectcosmos.abilities.cosmicbending.combos;

import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.PCMethods;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ColoredParticle;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlackHole extends CosmicAbility implements ComboAbility, AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.SELECT_RANGE)
    private int selectRange;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("PullForce")
    private double force;
    private long duration;
    private boolean charged;
    private Location origin;
    private Vector direction;

    private int increment = 20;

    private Location location;
    private int pstage;
    private int phase = 0;

    public BlackHole(Player player) {
        super(player);

        if (this.bPlayer.canBendIgnoreBinds(this)) {

            //get the target block first and check if its null before continuing.
            this.selectRange = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Combos.BlackHole.SelectRange");
            Block target = player.getTargetBlockExact(selectRange, FluidCollisionMode.NEVER);

            if (target != null) {
                this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.BlackHole.Cooldown");
                this.range = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.BlackHole.PullRange");
                this.force = ProjectCosmos.plugin.getConfig().getDouble("Abilities.Cosmic.Combos.BlackHole.PullForce");
                this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Combos.BlackHole.Duration");

                this.origin = this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
                this.direction = this.player.getLocation().getDirection();
                this.location = target.getLocation().add(0, 1, 0);
                start();
            }
        }
    }

    @Override
    public void progress() {
        if (!this.bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            remove();
            return;
        }

        //Creates the sphere effect
        for (double theta = 0; theta < 180; theta += this.increment) {
            for (double phi = 0; phi < 360; phi += this.increment) {
                final double rphi = Math.toRadians(phi);
                final double rtheta = Math.toRadians(theta);

                final Location loc = this.location.clone().add(2 / 1.5 * Math.cos(rphi) * Math.sin(rtheta), 2 / 1.5 * Math.cos(rtheta), 2 / 1.5 * Math.sin(rphi) * Math.sin(rtheta));
                if (ThreadLocalRandom.current().nextInt(3) == 0) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2, 0);
                    (new ColoredParticle(Color.fromRGB(0, 0, 0), 10.0F)).display(loc, 1, 0, 0, 0);
                }
            }
        }
        this.increment += 20;
        if (this.increment >= 70) {
            this.increment = 20;
        }

        //Displays the inner colors
        BlockData bData = Bukkit.createBlockData(Material.CRYING_OBSIDIAN);
        ParticleEffect.SMOKE_LARGE.display(location, 4, 0.5, 0.5, 0.5);

        for (int i = 0; i < 1; i++) {
            double x = 1 * Math.sin(Math.toRadians(this.phase));
            double y = 1 * Math.cos(Math.toRadians(this.phase));
            double z = 0.0D;
            Vector vector = new Vector(x, y, z);
            vector = vector.multiply(3.0D);
            double yaw = Math.toRadians(-this.location.getYaw());
            double pitch = Math.toRadians(this.location.getPitch());
            double oldX = vector.getX();
            double oldY = vector.getY();
            double oldZ = vector.getZ();
            vector.setY(oldY * Math.cos(pitch) - oldZ * Math.sin(pitch));
            vector.setZ(oldY * Math.sin(pitch) + oldZ * Math.cos(pitch));
            oldZ = vector.getZ();
            vector.setX(oldX * Math.cos(yaw) + oldZ * Math.sin(yaw));
            vector.setZ(-oldX * Math.sin(yaw) + oldZ * Math.cos(yaw));
            this.location.add(vector);

            if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
                (new ColoredParticle(Color.fromRGB(45, 0, 130), 2.0F)).display(this.location, 5, 0.2, 0.1, 0.2);
            } else {
                (new ColoredParticle(Color.fromRGB(109, 133, 255), 2.0F)).display(this.location, 5, 0.2, 0.1, 0.2);
            }

            this.location.subtract(vector);
            if (ThreadLocalRandom.current().nextInt(6) == 0)

                this.location.add(this.direction.normalize().multiply(3).multiply(0.3D / 3));
            this.phase += 11;


            this.pstage++;
        }
        eventHorizon();

        //Creates the particles being drawn in effect
        List<Location> particleLocs = new ArrayList<>();
        particleLocs = PCMethods.drawSpherePoints(location, (int) range, (int) range, false, true, 0);

        //While loop gets a number of locations on/in a sphere to begin particle effects from
        int count = 0;
        while (count < 10) {

            int particleRand = ThreadLocalRandom.current().nextInt(particleLocs.size());
            Location particleLoc = particleLocs.get(particleRand);

            //gets the distances from the particle location to the players location to use in a pseudo-vector.
            double x = location.getX() - particleLoc.getX();
            double y = location.getY() - particleLoc.getY();
            double z = location.getZ() - particleLoc.getZ();

            //some particles can have movement added to them by setting their count/amount to 0, and will use
            // the XYZ as a pseudo vector
            ParticleEffect.END_ROD.display(particleLoc, 0, x, y , z , 0.1);
            ParticleEffect.SPELL_INSTANT.display(particleLoc, 0, x, y , z , 0.1);
            count++;
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, range)) {
            Vector vec = PCMethods.createDirectionalVector(entity.getLocation(), location);
            GeneralMethods.setVelocity(this, entity, vec.normalize().multiply(force));
        }
    }

    private void eventHorizon() {
        Location centre = location;
        double increment = (2 * Math.PI) / 36;
        double angle = pstage * increment;
        double x = centre.getX() + (2.5 * Math.cos(angle));
        double z = centre.getZ() + (2.5 * Math.sin(angle));
        Location loc = new Location(centre.getWorld(), x, centre.getY()-0.5 + 1, z);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(loc, 2, 0.5, 0.1, 0.5);
        }

        double x2 = centre.getX() + (2.5 * -Math.cos(angle));
        double z2 = centre.getZ() + (2.5 * -Math.sin(angle));
        Location loc2 = new Location(centre.getWorld(), x2, centre.getY()-0.4 + 1, z2);

        if (this.getBendingPlayer().canUseSubElement(PCElement.DARK_COSMIC)) {
            (new ColoredParticle(Color.fromRGB(13, 0, 56), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(45, 0, 130), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(66, 0, 188), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
        } else {
            (new ColoredParticle(Color.fromRGB(72, 49, 175), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(80, 78, 196), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
            (new ColoredParticle(Color.fromRGB(109, 133, 255), 1.45F)).display(loc2, 2, 0.5, 0.1, 0.5);
        }

        pstage++;
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
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.Combos.BlackHole.Enabled");
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "BlackHole";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return "Grandmaster Cosmicbenders able to generate supermassive blackholes, similar to what's at the center of most galaxies. This spell requires ascended skills!";
    }

    @Override
    public String getInstructions() {
        return "- EventHorizon (Tap-Shift) > CosmicBlast (Left-Click) > EventHorizon (Tap-Shift while looking at a block) -";
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new BlackHole(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        final ArrayList<AbilityInformation> blackHole = new ArrayList<>();
        blackHole.add(new AbilityInformation("EventHorizon", ClickType.SHIFT_DOWN));
        blackHole.add(new AbilityInformation("EventHorizon", ClickType.SHIFT_UP));
        blackHole.add(new AbilityInformation("CosmicBlast", ClickType.LEFT_CLICK));
        blackHole.add(new AbilityInformation("EventHorizon", ClickType.SHIFT_DOWN));
        blackHole.add(new AbilityInformation("EventHorizon", ClickType.SHIFT_UP));
        return blackHole;
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
