package com.xoarasol.projectcosmos.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.Collision;
import com.projectkorra.projectkorra.event.AbilityCollisionEvent;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.abilities.cosmicbending.*;
import com.xoarasol.projectcosmos.abilities.cosmicbending.dimensionbending.CosmicRift;
import com.xoarasol.projectcosmos.abilities.cosmicbending.dimensionbending.SpaceWarp;
import com.xoarasol.projectcosmos.abilities.cosmicbending.dimensionbending.WormHole;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.BlastOff;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.GravityManipulation;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.MassManipulation;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PCAbilityListener implements Listener {

    @EventHandler
    public void onClick(PlayerAnimationEvent event) {
        if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {

            Player player = event.getPlayer();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            String abil = bPlayer.getBoundAbilityName();

            switch (abil.toUpperCase()) {
                //Cosmic
                case ("METEORSHOWER"):
                    MeteorShower.activate(player);
                    break;
                case ("MASSMANIPULATION"):
                    new MassManipulation(player, true);
                    break;
                case ("COSMICBINDING"):
                    new CosmicBinding(player);
                    break;
                case ("WORMHOLE"):
                    new WormHole(player);
                    break;
                case ("GALACTICRIPPLE"):
                    new GalacticRipple(player);
                    break;
                case ("COSMICBLAST"):
                    new CosmicBlast(player);
                    break;
                case ("ASTRALFLIGHT"):
                    AstralFlight.disengage(player);
                    break;
                case ("BLASTOFF"):
                    BlastOff.activate(player);
                    break;
                case ("STARSURGE"):
                    StarSurge.explode(player);
                    break;
                case ("CRACKOFDAWN"):
                    CrackOfDawn.activate(player);
                    break;
                case ("CONSTELLATIONCUTTER"):
                    ConstellationCutter.createStar(player);
                    break;
                case ("GRAVITYMANIPULATION"):
                    GravityManipulation.reverse(player);
                    break;
                case ("ROVER"):
                    Rover.hop(player);
                    break;
                case ("SPACEWARP"):
                    new SpaceWarp(player);
                    break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        String abil = bPlayer.getBoundAbilityName();

        if (!player.isSneaking()) {
            switch (abil.toUpperCase()) {
                //Cosmic
                case ("MASSMANIPULATION"):
                    new MassManipulation(player, false);
                    break;
                case ("EVENTHORIZON"):
                    new EventHorizon(player);
                    break;
                case ("STELLARBREATH"):
                    new StellarBreath(player);
                    break;
                case ("ROVER"):
                    new Rover(player);
                    break;
                case ("STARSURGE"):
                    new StarSurge(player);
                    break;
                case ("ASTRALFLIGHT"):
                    new AstralFlight(player);
                    break;
                case ("SOLARCYCLONE"):
                    new Magnetar(player);
                    break;
                case ("BLASTOFF"):
                    new BlastOff(player);
                    break;
                case ("SUPERNOVA"):
                    new SuperNova(player);
                    break;
                case ("COSMICRIFT"):
                    new CosmicRift(player);
                    break;
                case ("THESKIESDESCEND"):
                    new TheSkiesDescend(player);
                    break;
                case ("COSMICSEEKER"):
                    new CosmicSeeker(player);
                    break;
                case ("CRACKOFDAWN"):
                    new CrackOfDawn(player);
                    break;
                case ("METEORSHOWER"):
                    new MeteorShower(player);
                    break;
                case ("PULSAR"):
                    new Pulsar(player);
                    break;
                case ("CONSTELLATIONCUTTER"):
                    new ConstellationCutter(player);
                    break;
                case ("GRAVITYMANIPULATION"):
                    new GravityManipulation(player);
                    break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) return;

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();

        if (fallingBlock.hasMetadata("MeteorShower")) {
            event.setCancelled(true);
            MeteorShower.Impact(fallingBlock);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause().equals(DamageCause.FALL) && event.getEntity() instanceof Player) {
            final Player player = ((Player) event.getEntity()).getPlayer();
            final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
            if (bPlayer.hasElement(PCElement.COSMIC)) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCollision(AbilityCollisionEvent event) {
        Collision collision = event.getCollision();
        CoreAbility cosmicBlast = CoreAbility.getAbility(CosmicBlast.class);

        System.out.println("Ability first was " + collision.getAbilityFirst().getName());
        System.out.println("Ability second was " + collision.getAbilitySecond().getName());
        if (collision.getAbilityFirst() == cosmicBlast) {
            CosmicBlast cBlast = (CosmicBlast) collision.getAbilityFirst();
            cBlast.collide(collision.getAbilitySecond());
        }
    }
}
