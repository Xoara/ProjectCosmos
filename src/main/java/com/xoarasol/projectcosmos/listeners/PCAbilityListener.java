package com.xoarasol.projectcosmos.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.Collision;
import com.projectkorra.projectkorra.ability.util.PassiveManager;
import com.projectkorra.projectkorra.event.AbilityCollisionEvent;
import com.xoarasol.projectcosmos.PCElement;
import com.xoarasol.projectcosmos.abilities.airbending.HowlingGale;
import com.xoarasol.projectcosmos.abilities.airbending.spiritual.SpiritualShift;
import com.xoarasol.projectcosmos.abilities.airbending.spiritual.TemperedFate;
import com.xoarasol.projectcosmos.abilities.cosmicbending.*;
import com.xoarasol.projectcosmos.abilities.cosmicbending.darkcosmicbending.AntiMatter;
import com.xoarasol.projectcosmos.abilities.cosmicbending.darkcosmicbending.NullSphere;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.BlastOff;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.GravityBinding;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.GravityManipulation;
import com.xoarasol.projectcosmos.abilities.cosmicbending.gravitybending.MassManipulation;
import com.xoarasol.projectcosmos.abilities.cosmicbending.lunarbending.*;
import com.xoarasol.projectcosmos.abilities.cosmicbending.solarbending.CrackOfDawn;
import com.xoarasol.projectcosmos.abilities.cosmicbending.solarbending.Pulsar;
import com.xoarasol.projectcosmos.abilities.cosmicbending.solarbending.SolarCyclone;
import com.xoarasol.projectcosmos.abilities.earthbending.sandbending.AridEruption;
import com.xoarasol.projectcosmos.abilities.firebending.combustionbending.CombustingBeam;
import com.xoarasol.projectcosmos.abilities.firebending.lightningsbending.ElectricDischarge;
import com.xoarasol.projectcosmos.abilities.firebending.lightningsbending.VoltaicPulse;
import com.xoarasol.projectcosmos.abilities.laserbending.*;
import com.xoarasol.projectcosmos.abilities.laserbending.combos.Dispersion;
import com.xoarasol.projectcosmos.abilities.laserbending.combos.SystemicShock;
import com.xoarasol.projectcosmos.abilities.laserbending.forcefield.Barrier;
import com.xoarasol.projectcosmos.abilities.laserbending.forcefield.EnergyBolt;
import com.xoarasol.projectcosmos.abilities.laserbending.forcefield.Radial;
import com.xoarasol.projectcosmos.abilities.laserbending.forcefield.VirtusVeil;
import com.xoarasol.projectcosmos.abilities.laserbending.passives.PhotonBooster;
import com.xoarasol.projectcosmos.abilities.laserbending.radiation.*;
import com.xoarasol.projectcosmos.abilities.waterbending.plantbending.Chlorophyll;
import com.xoarasol.projectcosmos.abilities.waterbending.plantbending.IvySpores;
import com.xoarasol.projectcosmos.abilities.waterbending.plantbending.VineTangle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class PCAbilityListener implements Listener {

    @EventHandler
    public void onClick(PlayerAnimationEvent event) {
        if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {

            Player player = event.getPlayer();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            String abil = bPlayer.getBoundAbilityName();

            switch (abil.toUpperCase()) {
                //Air
                case ("HOWLINGGALE"):
                    new HowlingGale(player);
                    break;
                case ("SPIRITUALSHIFT"):
                    SpiritualShift.disengage(player);
                    break;
                case ("TEMPEREDFATE"):
                    new TemperedFate(player);
                    break;
                //Water
                case ("VINETANGLE"):
                    new VineTangle(player);
                    break;
                case ("CHLOROPHYLL"):
                    new Chlorophyll(player);
                    break;
                //Fire
                case ("COMBUSTINGBEAM"):
                    new CombustingBeam(player);
                    break;
                case ("ELECTRICDISCHARGE"):
                    new ElectricDischarge(player);
                    break;
                //Laser
                case ("REVERSAL"):
                    Reversal.activate(player);
                    break;
                case ("BURSTRAYS"):
                    new BurstRays(player);
                    break;
                case ("PHOTONPUNCH"):
                    PhotonPunch.activate(player);
                    break;
                case ("LASERFISSION"):
                    new LaserFission(player);
                    break;
                case ("BOOST"):
                    new Boost(player);
                    break;
                case ("HOTSPOT"):
                    HotSpot.activate(player);
                    break;
                case ("BARRIER"):
                    new Barrier(player);
                    break;
                case ("RADIANTTRAIL"):
                    new RadiantTrail(player);
                    break;
                case ("RADIAL"):
                    new Radial(player, true);
                    break;
                case ("RADIATIONBURST"):
                    SystemicShock.activate(player);
                    break;
                case ("DAZZLINGRAY"):
                    Dispersion.disperse(player);
                    break;
                case ("ENERGYBOLT"):
                    new EnergyBolt(player);
                    break;
                //Cosmic
                case ("ORBITAL"):
                    new Orbital(player);
                    break;
                case ("METEORSHOWER"):
                    MeteorShower.activate(player);
                    break;
                case ("MASSMANIPULATION"):
                    new MassManipulation(player, true);
                    break;
                case ("COSMOSCOPEVISION"):
                    new CosmoscopeVision(player);
                    break;
                case ("LUNARCLAP"):
                    new LunarClap(player);
                    break;
                case ("GRAVITYBINDING"):
                    new GravityBinding(player);
                    break;
                case ("LUNARGLINT"):
                    new LunarGlint(player);
                    break;
                case ("WORMHOLE"):
                    new WormHole(player);
                    break;
                case ("PRIMORDIALBURST"):
                    new GravityBinding(player);
                    break;
                case ("SUPERNOVA"):
                    new SuperNova(player);
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
                    new Rover(player);
                    break;
                case ("SPACEWARP"):
                    new SpaceWarp(player);
                    break;
                case ("NULLSPHERE"):
                    new NullSphere(player);
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
                //Air
                case ("SPIRITUALSHIFT"):
                    new SpiritualShift(player);
                    break;
                //Water
                case ("IVYSPORES"):
                    new IvySpores(player);
                    break;
                //Earth
                case ("ARIDERUPTION"):
                    new AridEruption(player);
                    break;
                //Fire
                case ("VOLTAICPULSE"):
                    new VoltaicPulse(player);
                    break;
                //Laser
                case ("PHOTONPUNCH"):
                    new PhotonPunch(player);
                    break;
                case ("LASERGAZE"):
                    new LaserGaze(player);
                    break;
                case ("REVERSAL"):
                    new Reversal(player);
                    break;
                case ("FINALEXECUTION"):
                    new FinalExecution(player);
                    break;
                case ("ELECTRONLANCE"):
                    new ElectronLance(player);
                    break;
                case ("DAZZLINGRAY"):
                    new DazzlingRay(player);
                    break;
                case ("RADIATIONBURST"):
                    new RadiationBurst(player);
                    break;
                case ("HOTSPOT"):
                    new HotSpot(player);
                    break;
                case ("DISINTEGRATION"):
                    new Disintegration(player);
                    break;
                case ("CONTAMINATE"):
                    new Contaminate(player);
                    break;
                case ("VIRTUSVEIL"):
                    new VirtusVeil(player);
                    break;
                case ("RADIAL"):
                    new Radial(player, false);
                    break;
                case ("BARRIER"):
                    Barrier.shatterBarrier(player);
                    break;
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
                case ("STARSURGE"):
                    new StarSurge(player);
                    break;
                case ("ASTRALFLIGHT"):
                    new AstralFlight(player);
                    break;
                case ("SOLARCYCLONE"):
                    new SolarCyclone(player);
                    break;
                case ("BLASTOFF"):
                    new BlastOff(player);
                    break;
                case ("MOONFALL"):
                    new MoonFall(player);
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
                case ("COSMOSCOPEVISION"):
                    CosmoscopeVision.removeVision(player);
                    break;
                case ("ANTIMATTER"):
                    new AntiMatter(player);
                    break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (event.isCancelled() || bPlayer == null)
            return;
        if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null))
            return;
        if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LaserJolts")) {
            if (CoreAbility.hasAbility(player, LaserJolts.class)) {
                ((LaserJolts) CoreAbility.getAbility(player, LaserJolts.class)).onClick();
            } else {
                new LaserJolts(player);
            }
        }
    }


    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause().equals(DamageCause.FALL) && event.getEntity() instanceof Player) {
            final Player player = ((Player) event.getEntity()).getPlayer();
            final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (bPlayer.hasElement(PCElement.LASER)) {
                final Block block = event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (block.getType().equals(Material.LIGHT_BLUE_STAINED_GLASS)) {
                    event.setDamage(0);
                    event.setCancelled(true);
                }
            } else if (bPlayer.hasElement(PCElement.COSMIC)) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    CoreAbility sol = CoreAbility.getAbility(PhotonBooster.class);

    @EventHandler(ignoreCancelled = true)
    public void onSprint(PlayerToggleSprintEvent event) {
        final Player player = event.getPlayer();
        final BendingPlayer bplayer = BendingPlayer.getBendingPlayer(player);

        if (bplayer.hasElement(PCElement.LASER) && bplayer.canBendPassive(sol) && (sol).isEnabled() && PassiveManager.hasPassive(player, sol)) {
            new PhotonBooster(player);

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
