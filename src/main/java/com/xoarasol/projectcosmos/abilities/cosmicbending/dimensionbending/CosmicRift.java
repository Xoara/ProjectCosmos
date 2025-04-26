package com.xoarasol.projectcosmos.abilities.cosmicbending.dimensionbending;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.xoarasol.projectcosmos.ProjectCosmos;
import com.xoarasol.projectcosmos.api.CosmicAbility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CosmicRift extends CosmicAbility implements AddonAbility {

    private long startTime;
    private boolean isCharging;
    private Location portalCenter;
    private BukkitRunnable particleSwirlTask;

    private long chargeTime;
    private long duration;
    private long cooldown;
    private int portalRadius;
    private int range;

    public CosmicRift(Player player) {
        super(player);

        if (this.bPlayer.canBend(this) && !CoreAbility.hasAbility(player, CosmicRift.class)) {
            this.chargeTime = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dimension.CosmicRift.ChargeTime", 2500);
            this.duration = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dimension.CosmicRift.Portal.Duration", 15000);
            this.cooldown = ProjectCosmos.plugin.getConfig().getLong("Abilities.Cosmic.Dimension.CosmicRift.Cooldown", 30000);
            this.range = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Dimension.CosmicRift.Range", 10);
            this.portalRadius = ProjectCosmos.plugin.getConfig().getInt("Abilities.Cosmic.Dimension.CosmicRift.Portal.Radius", 4);
        }

        start();
        startTime = System.currentTimeMillis();
        isCharging = true;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }

        if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }

        if (!player.isSneaking() && isCharging) {
            long heldTime = System.currentTimeMillis() - startTime;
            if (heldTime >= chargeTime) {
                summonPortal();
                bPlayer.addCooldown(this);
            }
            remove();
            return;
        }

        if (isCharging) {
            ParticleEffect.END_ROD.display(player.getLocation().add(0, 1.5, 0), 5, 0.3F, 0.3F, 0.3F, 0);
        }
    }

    private void summonPortal() {
        portalCenter = getTargetLocation();

        if (portalCenter == null) {
            portalCenter = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(range));
        }

        spawnPortal(portalCenter);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);

        startSwirlingParticles();

        new BukkitRunnable() {
            @Override
            public void run() {
                removePortal(portalCenter);
            }
        }.runTaskLater(ProjectCosmos.getPlugin(), duration / 50); // 20 ticks = 1 second
    }

    private Location getTargetLocation() {
        Block target = player.getTargetBlockExact(range);
        if (target != null && target.getType() != Material.AIR) {
            return target.getLocation().add(0, 1, 0);
        }
        return null;
    }

    private void spawnPortal(Location center) {
        int radius = portalRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if (loc.distanceSquared(center) <= radius * radius) {
                        Block block = loc.getBlock();
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.END_GATEWAY);

                            // BLOCK THE BEAM
                            Block blockAbove = block.getRelative(BlockFace.UP);
                            if (blockAbove.getType() == Material.AIR) {
                                blockAbove.setType(Material.BARRIER);
                            }
                        }
                    }
                }
            }
        }
    }

    private void removePortal(Location center) {
        if (particleSwirlTask != null) {
            particleSwirlTask.cancel();
        }

        int radius = portalRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    if (block.getType() == Material.END_GATEWAY) {
                        block.setType(Material.AIR);
                    }
                }
            }
            spawnCollapseExplosion(center);
        }
    }

    private void startSwirlingParticles() {
        particleSwirlTask = new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (portalCenter == null) {
                    cancel();
                    return;
                }

                t += 0.05;

                double radius = portalRadius + 2;
                for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 8) {
                    double x = radius * Math.cos(theta + t);
                    double z = radius * Math.sin(theta + t);
                    Location particleLoc = portalCenter.clone().add(x, 0, z);

                    portalCenter.getWorld().spawnParticle(Particle.REVERSE_PORTAL, particleLoc, 2, 0.1, 0.1, 0.1, 0);
                    portalCenter.getWorld().spawnParticle(Particle.SONIC_BOOM, particleLoc, 2, 0.1, 0.1, 0.1, 0);
                    portalCenter.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                }
            }
        };
        particleSwirlTask.runTaskTimer(ProjectCosmos.getPlugin(), 0L, 2L);
    }

    private void spawnCollapseExplosion(Location center) {
        new BukkitRunnable() {
            double radius = portalRadius;

            @Override
            public void run() {
                if (radius <= 0) {
                    center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.5F);
                    cancel();
                    return;
                }

                for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 8) {
                    for (double phi = 0; phi < Math.PI; phi += Math.PI / 8) {
                        double x = radius * Math.cos(theta) * Math.sin(phi);
                        double y = radius * Math.cos(phi);
                        double z = radius * Math.sin(theta) * Math.sin(phi);

                        Location particleLoc = center.clone().add(x, y, z);
                        center.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 2, 0, 0, 0, 0);
                        center.getWorld().spawnParticle(Particle.ASH, particleLoc, 1, 0, 0, 0, 0);
                    }
                }

                radius -= 0.3;
            }
        }.runTaskTimer(ProjectCosmos.getPlugin(), 0L, 3L); // Shrink every 3 ticks
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public Location getLocation() {
        return portalCenter;
    }

    @Override
    public String getName() {
        return "CosmicRift";
    }

    @Override
    public boolean isEnabled() {
        return ProjectCosmos.plugin.getConfig().getBoolean("Abilities.Cosmic.CosmicRift.Enabled", true);
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Open a mysterious cosmic rift between worlds by charging your energy and releasing it!";
    }

    @Override
    public String getInstructions() {
        return "Hold-Shift until charged > Release-Shift to create the portal!";
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public String getAuthor() {
        return "XoaraSol";
    }

    @Override
    public String getVersion() {
        return ProjectCosmos.getVersion();
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
}