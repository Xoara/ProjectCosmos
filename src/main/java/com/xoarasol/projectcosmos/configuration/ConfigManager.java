package com.xoarasol.projectcosmos.configuration;

import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public ConfigManager() {
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = ProjectCosmos.getPlugin().getConfig();
        FileConfiguration pkConfig = com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.get();

        List<String> toBush = createBushList();
        List<String> toBricks = createBricksList();
        List<String> fireBlocks = createFireBlocksList();
        List<String> slowBlocks = createSlowBlocksList();
        List<String> damageBlocks = createDamageBlocksList();

        pkConfig.addDefault("Commands.Help.Elements.Laser", "Laser is the element of mystery. " +
                "Laserbenders may share their power source with firebenders, but their bending style ist vastly different. They specialize in exactitude and brutality, their targets may have an instant demise. Interestingly, laserbenders are also quite decent at defending themselves with barriers made out of force fields. They also tend to be psychic, with most abilities not requiring any martial arts. It is not yet known how or why these mysterious benders came to be, but many suspect, that there might've been more than just 5 Elemental Lion Turtles before Avatar Wan's time. \nEnter /b display Laser for a list of the available laser abilities.");
        pkConfig.addDefault("Commands.Help.Elements.Cosmic", "Cosmic is the element of creation. " +
                "Cosmicbenders wield the true power of space time and the void. The element was first used by celestial spirits to create the galaxies and solar systems, lighting up the pure void that existed before. Shortly after most of the Lion Turtles got hunted down, the celestial spirits disposed of cosmicbending, and decided to choose one person to wield the power before letting it die out. The person was later known as Vantu, who was the only Cosmicbender capable of using the power of the cosmos. After they passed on, the celestial spirits were sure the element was extinct. After the Harmonic Convergence in 171, just like Airbenders, mortal Cosmic benders also started appearing on Earth, but no one knew about them as there were so few. \\nCosmicbending is a powerful element, heavy with crowdcontrol and mobility. They focus on attacking multiple targets at once, pushing them away and pulling them in. Cosmicbenders are peaceful yet dangerous They wield the ability to destroy and create.\nEnter /b display Cosmic for a list of the available cosmic abilities.");

        // -- Air --

        // -- Water --

        // -- Earth --

        // -- Fire --

        // - Lightning -

        //ThunderSplice


        // - Combustion -

        //CombustingBeam
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Enabled", true);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Cooldown", 7800);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Damage", 4);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Speed", 35);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.CollisionRadius", 4);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.ExplosionRadius", 3);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Delay", 250);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.Range", 50);
        config.addDefault("Abilities.Fire.Combustion.CombustingBeam.BlockRevertTime", 7500);
        pkConfig.addDefault("Abilities.Fire.CombustingBeam.DeathMessage", "{victim} was destroyed {attacker}'s SUPER ULTIMATE MEGA DEATH ROCKET!");


        // -- Cosmic --

        //CosmoscopeVision
        config.addDefault("Abilities.Cosmic.CosmoscopeVision.Enabled", true);
        config.addDefault("Abilities.Cosmic.CosmoscopeVision.Cooldown", 720000);
        config.addDefault("Abilities.Cosmic.CosmoscopeVision.Duration", 0);
        config.addDefault("Abilities.Cosmic.CosmoscopeVision.NightVisionPower", 3);

        //TheSkiesDescend
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Enabled", true);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Cooldown", 7800);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Damage", 4);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Speed", 50);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.CollisionRadius", 2);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.ExplosionRadius", 4);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Delay", 750);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Range", 36);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.SlowDuration", 35);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.SlowPower", 1);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.BlockRevertTime", 7500);
        pkConfig.addDefault("Abilities.Cosmic.TheSkiesDescend.DeathMessage", "{victim} was crushed after finding themselves under {attacker}'s {ability}");

        //SpaceWarp
        config.addDefault("Abilities.Cosmic.SpaceWarp.Enabled", true);
        config.addDefault("Abilities.Cosmic.SpaceWarp.Cooldown", 12000);

        //GravityFlux
        config.addDefault("Abilities.Cosmic.GravityFlux.Enabled", true);
        config.addDefault("Abilities.Cosmic.GravityFlux.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.GravityFlux.Damage", 4);
        config.addDefault("Abilities.Cosmic.GravityFlux.Speed", 80);
        config.addDefault("Abilities.Cosmic.GravityFlux.Delay", 1200);
        config.addDefault("Abilities.Cosmic.GravityFlux.KnockUp", 2);
        config.addDefault("Abilities.Cosmic.GravityFlux.Range", 25);
        config.addDefault("Abilities.Cosmic.GravityFlux.LevitationDuration", 35);
        config.addDefault("Abilities.Cosmic.GravityFlux.LevitationPower", 1);
        config.addDefault("Abilities.Cosmic.GravityFlux.CollisionRadius", 2);
        config.addDefault("Abilities.Cosmic.GravityFlux.ExplosionRadius", 6);
        config.addDefault("Abilities.Cosmic.GravityFlux.BlockRevertTime", 5000);

        //CosmicBlast
        config.addDefault("Abilities.Cosmic.CosmicBlast.Enabled", true);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Cooldown", 5600);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Range", 15);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Damage", 2);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Speed", 20);
        config.addDefault("Abilities.Cosmic.CosmicBlast.CollisionKnockback", 1);
        config.addDefault("Abilities.Cosmic.CosmicBlast.ExplosionRadius", 2);
        pkConfig.addDefault("Abilities.Cosmic.CosmicBlast.DeathMessage", "{victim} was blasted out of this dimension by {attacker}'s {ability}");

        //GalacticRipple
        config.addDefault("Abilities.Cosmic.GalacticRipple.Enabled", true);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Cooldown", 8000);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Radius.MaxRadius", 5);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Radius.Increment", 0.2);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Radius.Interval", 200);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Damage", 1);
        config.addDefault("Abilities.Cosmic.GalacticRipple.Knockback", 2);
        pkConfig.addDefault("Abilities.Cosmic.GalacticRipple.DeathMessage", "{victim} couldn't handle the rapid expansion of the space around {attacker}'s {ability}");

        //CosmicSeeker
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Enabled", true);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Cooldown", 7000);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Range", 50);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Damage", 2);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.GlowDuration", 80);
        pkConfig.addDefault("Abilities.Cosmic.CosmicSeeker.DeathMessage", "{attacker} found and killed {victim} using {ability}");

        //StellarBreath
        config.addDefault("Abilities.Cosmic.StellarBreath.Enabled", true);
        config.addDefault("Abilities.Cosmic.StellarBreath.Cooldown", 6000);
        config.addDefault("Abilities.Cosmic.StellarBreath.Duration", 3500);
        config.addDefault("Abilities.Cosmic.StellarBreath.Damage", 1.0);
        config.addDefault("Abilities.Cosmic.StellarBreath.Knockback", 0.8);
        config.addDefault("Abilities.Cosmic.StellarBreath.Range", 10);

        //StarSurge
        config.addDefault("Abilities.Cosmic.StarSurge.Enabled", true);
        config.addDefault("Abilities.Cosmic.StarSurge.Cooldown", 7000);
        config.addDefault("Abilities.Cosmic.StarSurge.Range", 12);
        config.addDefault("Abilities.Cosmic.StarSurge.Radius", 0.8);
        config.addDefault("Abilities.Cosmic.StarSurge.ExplosionRadius", 4);
        config.addDefault("Abilities.Cosmic.StarSurge.RevertTime", 5000);
        config.addDefault("Abilities.Cosmic.StarSurge.Damage", 2);
        config.addDefault("Abilities.Cosmic.StarSurge.Knockback", 0.5);
        config.addDefault("Abilities.Cosmic.StarSurge.Speed", 1);
        config.addDefault("Abilities.Cosmic.StarSurge.Particles.Distance", 1);
        config.addDefault("Abilities.Cosmic.StarSurge.Slow.Duration", 20);
        config.addDefault("Abilities.Cosmic.StarSurge.Slow.Amplifier", 1);

        //EventHorizon
        config.addDefault("Abilities.Cosmic.EventHorizon.Enabled", true);
        config.addDefault("Abilities.Cosmic.EventHorizon.Knockback", 1);
        config.addDefault("Abilities.Cosmic.EventHorizon.Cooldown", 7800);
        config.addDefault("Abilities.Cosmic.EventHorizon.Range", 22);
        config.addDefault("Abilities.Cosmic.EventHorizon.ChargeTime", 800);
        config.addDefault("Abilities.Cosmic.EventHorizon.Damage", 2);
        config.addDefault("Abilities.Cosmic.EventHorizon.Growth", 0.450);
        pkConfig.addDefault("Abilities.Cosmic.EventHorizon.DeathMessage", "{victim} was caught and struck down in {attacker}'s {ability}");

        //AstralFlight
        config.addDefault("Abilities.Cosmic.AstralFlight.Enabled", true);
        config.addDefault("Abilities.Cosmic.AstralFlight.Cooldown", 8000);
        config.addDefault("Abilities.Cosmic.AstralFlight.MaxDuration", 3500);
        config.addDefault("Abilities.Cosmic.AstralFlight.FlightSpeed", 0.75);

        //ConstellationCutter
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Enabled", true);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Cooldown", 8000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Duration", 7000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Damage", 1);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.SelectRange", 8);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.MaxStars", 10);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.DamageInterval", 7000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastInterval", 50);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastRadius", 2);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastDamage", 1);
        pkConfig.addDefault("Abilities.Cosmic.ConstellationCutter.DeathMessage", "{victim} was burnt up while staring at {attacker}'s {ability}");

        //SuperNova
        config.addDefault("Abilities.Cosmic.SuperNova.Enabled", true);
        config.addDefault("Abilities.Cosmic.SuperNova.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.SuperNova.ChargeTime", 1350);
        config.addDefault("Abilities.Cosmic.SuperNova.Range", 26);
        config.addDefault("Abilities.Cosmic.SuperNova.Damage", 3);
        config.addDefault("Abilities.Cosmic.SuperNova.Speed", 20);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.Enabled", true);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.Radius", 3);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.RevertBlocks", true);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.RevertTime", 12000);
        pkConfig.addDefault("Abilities.Cosmic.SuperNova.DeathMessage", "{victim} couldn't handle the raw power of {attacker}'s {ability}");

        //WormHole
        config.addDefault("Abilities.Cosmic.WormHole.Enabled", true);
        config.addDefault("Abilities.Cosmic.WormHole.Cooldown", 9000);
        config.addDefault("Abilities.Cosmic.WormHole.Duration", 6000);
        config.addDefault("Abilities.Cosmic.WormHole.PortalRadius", 2);
        config.addDefault("Abilities.Cosmic.WormHole.Range", 20);
        config.addDefault("Abilities.Cosmic.WormHole.Speed", 30);
        config.addDefault("Abilities.Cosmic.WormHole.TeleportCooldown", 1000);

        // - Lunar -

        //MoonFall
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.ChargeTime", 2000);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Damage", 4);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Range", 30);

        //LunarClap
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Cooldown", 6500);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Range", 20);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Damage", 2);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Speed", 25);

        //Orbital
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Cooldown", 6000);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Radius", 5);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Duration", 4000);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Speed", 20);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.Knockback", 1.5);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.ExplosionRadius", 3);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.BlockRevertTime", 7500);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.LunarDamage", 2);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.LunarRadius", 5);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.LunarPullRange", 10);
        config.addDefault("Abilities.Cosmic.Lunar.Orbital.LunarPullFactor", 0.001);
        pkConfig.addDefault("Abilities.Cosmic.Lunar.Orbital.DeathMessage", "{victim} didn't survive the impact of {attacker}'s {ability}");

        //LunarGlint
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Cooldown", 6700);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Range", 14);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.ProjectileSpeed", 30);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Duration", 2000);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.CollisionRadius", 1);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Pull", 2);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.ImpactRadius", 4);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Knockback", 1);
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Damage", 3);
        pkConfig.addDefault("Abilities.Cosmic.Lunar.LunarGlint.DeathMessage", "{attacker} killed {victim} on impact using {ability}");

        //Rover
        config.addDefault("Abilities.Cosmic.Lunar.Rover.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.Cooldown", 9000);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.Duration", 60);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.Speed", 1.5);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.Damage", 2);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.ExplosionTicks", 30);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.ExplosionRadius", 3);
        config.addDefault("Abilities.Cosmic.Lunar.Rover.HopPower", 1);
        pkConfig.addDefault("Abilities.Cosmic.Rover.DeathMessage", "{victim} was ran over by Millie Bobby Brown");

        // - Solar -

        //SolarCyclone
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.Enabled", true);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.Cooldown", 6000);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.ChargeAnimation.RespectParticles", true);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.ChargeAnimation.Height", 2);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.ChargeAnimation.Radius", 1);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.ChargeTime", 400);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.Radius", 6);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.Damage", 2);
        config.addDefault("Abilities.Cosmic.Solar.SolarCyclone.Knockback", 2);
        pkConfig.addDefault("Abilities.Cosmic.Solar.SolarCyclone.DeathMessage", "{victim} was caught in {attacker}'s {ability}");

        //CrackOfDawn
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.Enabled", true);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.Cooldown", 8000);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.Damage", 1);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.RiseInterval", 400);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.MaxShots", 5);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.LevitationSpeed", 0.5);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.ShotRange", 30);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.ShotSpeed", 33);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.DisableThreshold", 7500);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.CollisionRadius", 0.8);
        config.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.DescendSpeed", 0.08);
        pkConfig.addDefault("Abilities.Cosmic.Solar.CrackOfDawn.DeathMessage", "{victim} died trying to tank {attacker}'s {ability}");

        //Pulsar
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Enabled", true);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Cooldown", 9000);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Range", 28);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Damage", 1);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.DamageInterval", 700);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.StartingRadius", 0.5);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.FinalRadius", 1.5);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Duration", 4000);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.ChargeTime", 1000);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.Recoil", 0.3);
        config.addDefault("Abilities.Cosmic.Solar.Pulsar.FormDistance", 5);
        pkConfig.addDefault("Abilities.Cosmic.Solar.Pulsar.DeathMessage", "{victim} was annihilated by {attacker}'s {ability}");

        //MassManipulation
        config.addDefault("Abilities.Cosmic.MassManipulation.Enabled", true);
        config.addDefault("Abilities.Cosmic.MassManipulation.Cooldown", 7000);
        config.addDefault("Abilities.Cosmic.MassManipulation.Duration", 8000);
        config.addDefault("Abilities.Cosmic.MassManipulation.Damage", 2);
        config.addDefault("Abilities.Cosmic.MassManipulation.PullRange", 15);
        config.addDefault("Abilities.Cosmic.MassManipulation.SpeedFactor", 3);
        config.addDefault("Abilities.Cosmic.MassManipulation.JumpFactor", 2);
        config.addDefault("Abilities.Cosmic.MassManipulation.FallingFactor", 2);
        config.addDefault("Abilities.Cosmic.MassManipulation.KnockReduction", 100);
        config.addDefault("Abilities.Cosmic.MassManipulation.Knockback", 1.3);
        config.addDefault("Abilities.Cosmic.MassManipulation.PullPower", 1.5);
        pkConfig.addDefault("Abilities.Cosmic.MassManipulation.DeathMessage", "{victim} got pulled in too close by {attacker}'s {ability}");

        // - Gravity -

        //GravityManipulation
        config.addDefault("Abilities.Cosmic.GravityManipulation.Enabled", true);
        config.addDefault("Abilities.Cosmic.GravityManipulation.SourceRange", 10);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Cooldown", 1700);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Damage", 2);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Range", 17);
        config.addDefault("Abilities.Cosmic.GravityManipulation.LaunchPower", 2.3);
        config.addDefault("Abilities.Cosmic.GravityManipulation.BlockRevertTime", 3000);
        config.addDefault("Abilities.Cosmic.GravityManipulation.CollisionRadius", 1.2);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Knockback", 0.3);
        config.addDefault("Abilities.Cosmic.GravityManipulation.AnimationSpeed", 0.8);
        config.addDefault("Abilities.Cosmic.GravityManipulation.SlownessDuration", 30);
        config.addDefault("Abilities.Cosmic.GravityManipulation.FireTicks", 30);
        config.addDefault("Abilities.Cosmic.GravityManipulation.ExtraDamage", 3);
        config.addDefault("Abilities.Cosmic.GravityManipulation.AffectingBlocks.DamageBlocks", damageBlocks);
        config.addDefault("Abilities.Cosmic.GravityManipulation.AffectingBlocks.FireBlocks", fireBlocks);
        config.addDefault("Abilities.Cosmic.GravityManipulation.AffectingBlocks.SlowBlocks", slowBlocks);
        pkConfig.addDefault("Abilities.Cosmic.GravityManipulation.DeathMessage", "{attacker} struck down {victim} with their {ability}");

        //GravityBinding
        config.addDefault("Abilities.Cosmic.GravityBinding.Enabled", true);
        config.addDefault("Abilities.Cosmic.GravityBinding.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.GravityBinding.Range", 20);
        config.addDefault("Abilities.Cosmic.GravityBinding.Damage", 2);
        config.addDefault("Abilities.Cosmic.GravityBinding.StunDuration", 20);
        pkConfig.addDefault("Abilities.Cosmic.GravityBinding.DeathMessage", "{victim} was fatally struck by {attacker}'s {ability}");

        //BlastOff
        config.addDefault("Abilities.Cosmic.BlastOff.Enabled", true);
        config.addDefault("Abilities.Cosmic.BlastOff.Cooldown", 2500);
        config.addDefault("Abilities.Cosmic.BlastOff.Knockback", 2);
        config.addDefault("Abilities.Cosmic.BlastOff.AffectRadius", 5);
        config.addDefault("Abilities.Cosmic.BlastOff.Duration", 2350);

        // - Dark Cosmic -

        //AntiMatter
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.Enabled", true);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.Cooldown", 15000);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.Range", 20);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.Damage", 8);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.Duration", 4000);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Enabled", true);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Regen", 15000);
        config.addDefault("Abilities.Cosmic.Dark.AntiMatter.BlockDamage.Radius", 3);
        pkConfig.addDefault("Abilities.Cosmic.AntiMatter.DeathMessage", "{victim}'s was wiped from existence by {attacker}'s {ability}");

        //NullSphere
        config.addDefault("Abilities.Cosmic.Dark.NullSphere.Enabled", true);
        config.addDefault("Abilities.Cosmic.Dark.NullSphere.Cooldown", 4000);
        config.addDefault("Abilities.Cosmic.Dark.NullSphere.Range", 50);
        config.addDefault("Abilities.Cosmic.Dark.NullSphere.Damage", 4);
        config.addDefault("Abilities.Cosmic.Dark.NullSphere.Speed", 20);
        pkConfig.addDefault("Abilities.Cosmic.NullSphere.DeathMessage", "{attacker} caught {victim} and stretched them apart using {ability}");

        // - Combos -

        //StellarCrash
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.Enabled", true);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.Cooldown", 11000);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.DownForce", 3);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.Duration", 2000);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.MaxDamage", 6);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.BaseDamage", 1);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.HeightScale", 7);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.DamageRange", 10);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.PullRange", 12);
        config.addDefault("Abilities.Cosmic.Combos.StellarCrash.PullForce", 3);
        pkConfig.addDefault("Abilities.Cosmic.StellarCrash.DeathMessage", "{victim} slammed down by {attacker}'s {ability}");

        //BlackHole
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.Enabled", true);
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.Cooldown", 15000);
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.PullRange", 12);
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.PullForce", 0.05);
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.Duration", 3000);
        config.addDefault("Abilities.Cosmic.Combos.BlackHole.SelectRange", 30);

        //AccretionDisk
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Enabled", true);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Cooldown", 13000);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.DamageInterval", 1000);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.MaxRadius", 4);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.MaxSpreads", 3);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Damage", 1);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Speed", 0.3);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Knockup", 1);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Levitation.Duration", 5);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.Levitation.Amplifier", 740);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.SlowFalling.Duration", 7000);
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.SlowFalling.Amplifier", 1);
        pkConfig.addDefault("Abilities.Cosmic.AccretionDisk.DeathMessage", "{victim} couldn't adapt to {attacker}'s {ability}");

        //TectonicDisruption
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.Enabled", true);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.Cooldown", 7900);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.SelectRange", 10);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.Duration", 740);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.Damage", 1);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.Knockup", 1);
        config.addDefault("Abilities.Cosmic.Combos.TectonicDisruption.BlockCollisionRadius", 1);
        pkConfig.addDefault("Abilities.Cosmic.TectonicDisruption.DeathMessage", "{victim} was rocked by the force of {attacker}'s {ability}");

        // - Passives -

        //Defiance
        config.addDefault("Passives.Cosmic.Defiance.Enabled", true);

        // -- Laser --

        //Boost
        config.addDefault("Abilities.Laser.Boost.Enabled", true);
        config.addDefault("Abilities.Laser.Boost.Cooldown", 6000);
        config.addDefault("Abilities.Laser.Boost.Duration", 200);
        config.addDefault("Abilities.Laser.Boost.Power", 1);

        //FinalExecution
        config.addDefault("Abilities.Laser.FinalExecution.Enabled", Boolean.valueOf(true));
        config.addDefault("Abilities.Laser.FinalExecution.Cooldown", Integer.valueOf(11000));
        config.addDefault("Abilities.Laser.FinalExecution.Speed", Double.valueOf(3.0D));
        config.addDefault("Abilities.Laser.FinalExecution.Range", 40);
        config.addDefault("Abilities.Laser.FinalExecution.Damage", Double.valueOf(3.0D));
        config.addDefault("Abilities.Laser.FinalExecution.StartingRadius", 0.5);
        config.addDefault("Abilities.Laser.FinalExecution.FinalRadius", 1.5);
        config.addDefault("Abilities.Laser.FinalExecution.ChargeTime", 1000);
        config.addDefault("Abilities.Laser.FinalExecution.Recoil", 0.3);
        config.addDefault("Abilities.Laser.FinalExecution.FormDistance", 5);
        config.addDefault("Abilities.Laser.FinalExecution.RadianceDuration", 80);
        pkConfig.addDefault("Abilities.Laser.FinalExecution.DeathMessage", "{victim} was brutally executed by {attacker}'s {ability}");

        //LaserFission
        config.addDefault("Abilities.Laser.LaserFission.Enabled", true);
        config.addDefault("Abilities.Laser.LaserFission.Cooldown", 2000);
        config.addDefault("Abilities.Laser.LaserFission.Range", 35);
        config.addDefault("Abilities.Laser.LaserFission.Damage", 2);
        config.addDefault("Abilities.Laser.LaserFission.CollisionRadius", 0.8);
        config.addDefault("Abilities.Laser.LaserFission.Speed", 22);
        config.addDefault("Abilities.Laser.LaserFission.MaxBounces", 12);
        config.addDefault("Abilities.Laser.LaserFission.SpeedModifier", 1.2);
        pkConfig.addDefault("Abilities.Laser.LaserFission.DeathMessage", "{victim} was split in two {attacker}'s {ability}");

        //LaserGaze
        config.addDefault("Abilities.Laser.LaserGaze.Enabled", true);
        config.addDefault("Abilities.Laser.LaserGaze.Cooldown", 10500);
        config.addDefault("Abilities.Laser.LaserGaze.Duration", 2500);
        config.addDefault("Abilities.Laser.LaserGaze.Range", 40);
        config.addDefault("Abilities.Laser.LaserGaze.Damage", 1);
        config.addDefault("Abilities.Laser.LaserGaze.DamageInterval", 700);
        config.addDefault("Abilities.Laser.LaserGaze.FireTicks", 1);
        config.addDefault("Abilities.Laser.LaserGaze.BlockRevertTime", 7500);

        //LaserJolts
        config.addDefault("Abilities.Laser.LaserJolts.Enabled", true);
        config.addDefault("Abilities.Laser.LaserJolts.Cooldown", 7000);
        config.addDefault("Abilities.Laser.LaserJolts.Damage", 2);
        config.addDefault("Abilities.Laser.LaserJolts.Range", 25);
        config.addDefault("Abilities.Laser.LaserJolts.Radius", 1.0);

        //ElectronLance
        config.addDefault("Abilities.Laser.ElectronLance.Enabled", true);
        config.addDefault("Abilities.Laser.ElectronLance.Cooldown", 7000);
        config.addDefault("Abilities.Laser.ElectronLance.Damage", 3);
        config.addDefault("Abilities.Laser.ElectronLance.Speed", 30);
        config.addDefault("Abilities.Laser.ElectronLance.Range", 25);
        config.addDefault("Abilities.Laser.ElectronLance.StartingRadius", 3.5);
        config.addDefault("Abilities.Laser.ElectronLance.ChargeTime", 750);
        config.addDefault("Abilities.Laser.ElectronLance.SecondHalfDamage", 3);

        //PhotonPunch
        config.addDefault("Abilities.Laser.PhotonPunch.Enabled", true);
        config.addDefault("Abilities.Laser.PhotonPunch.Cooldown", 6000);
        config.addDefault("Abilities.Laser.PhotonPunch.Damage", 1);
        config.addDefault("Abilities.Laser.PhotonPunch.Range", 15);
        config.addDefault("Abilities.Laser.PhotonPunch.Speed", 50);
        config.addDefault("Abilities.Laser.PhotonPunch.EntityKnockback", 1.5);
        config.addDefault("Abilities.Laser.PhotonPunch.SelfKnockback", 1.5);
        config.addDefault("Abilities.Laser.PhotonPunch.RadianceDuration", 80);

        //Reversal
        config.addDefault("Abilities.Laser.Reversal.Enabled", true);
        config.addDefault("Abilities.Laser.Reversal.Cooldown", 11000);
        config.addDefault("Abilities.Laser.Reversal.Damage", 3);
        config.addDefault("Abilities.Laser.Reversal.Range", 20);
        config.addDefault("Abilities.Laser.Reversal.Speed", 32);
        config.addDefault("Abilities.Laser.Reversal.Knockback", 1.2);
        config.addDefault("Abilities.Laser.Reversal.BlockRegenDelay", 7500);
        config.addDefault("Abilities.Laser.Reversal.CollisionRadius", 0.9);
        config.addDefault("Abilities.Laser.Reversal.ExplosionRadius", 3);
        pkConfig.addDefault("Abilities.Laser.Reversal.DeathMessage", "{victim} did not see {attacker}'s {ability} coming from behind them");

        //DazzlingRay
        config.addDefault("Abilities.Laser.DazzlingRay.Enabled", true);
        config.addDefault("Abilities.Laser.DazzlingRay.Cooldown", 8000);
        config.addDefault("Abilities.Laser.DazzlingRay.Damage", 2);
        config.addDefault("Abilities.Laser.DazzlingRay.Speed", 50);
        config.addDefault("Abilities.Laser.DazzlingRay.Knockback", 1.5);
        config.addDefault("Abilities.Laser.DazzlingRay.Range", 15);
        config.addDefault("Abilities.Laser.DazzlingRay.ChargeTime", 850);
        pkConfig.addDefault("Abilities.Laser.DazzlingRay.DeathMessage", "{victim} was bedazzled by {attacker}'s {ability}");

        //Barrier
        config.addDefault("Abilities.Laser.ForceField.Barrier.Enabled", true);
        config.addDefault("Abilities.Laser.ForceField.Barrier.Cooldown", 7500);
        config.addDefault("Abilities.Laser.ForceField.Barrier.Duration", 4500);
        config.addDefault("Abilities.Laser.ForceField.Barrier.Damage", 3);
        config.addDefault("Abilities.Laser.ForceField.Barrier.SelectRange", 14);
        config.addDefault("Abilities.Laser.ForceField.Barrier.PlaceRange", 3);
        config.addDefault("Abilities.Laser.ForceField.Barrier.Radius", 4);
        pkConfig.addDefault("Abilities.Laser.ForceField.Barrier.DeathMessage", "{victim} was standing too close to {attacker}'s {ability}");

        //VirtusVeil
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.Enabled", true);
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.Knockback", 1);
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.Cooldown", 7800);
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.Range", 25);
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.ChargeTime", 1500);
        config.addDefault("Abilities.Laser.ForceField.VirtusVeil.Damage", 2);

        //EnergyBolt
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Enabled", true);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Cooldown", 9500);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Delay", 300);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Damage", 3);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Speed", 45);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.Range", 22);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.CollisionRadius", 0.8);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.EffectRadius", 4);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.BlockRegenDelay", 7500);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.PlasmaDuration", 30);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.FrostDuration", 80);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.FireTicks", 85);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.NecrosisDuration", 35);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.ToDeadBushes", toBush);
        config.addDefault("Abilities.Laser.ForceField.EnergyBolt.ToCrackedBricks", toBricks);
        pkConfig.addDefault("Abilities.Laser.ForceField.EnergyBolt.DeathMessage", "{attacker} shot straight through {victim} using {ability}");


        //Radial
        config.addDefault("Abilities.Laser.ForceField.Radial.Enabled", true);
        config.addDefault("Abilities.Laser.ForceField.Radial.Cooldown", 6000);
        config.addDefault("Abilities.Laser.ForceField.Radial.Damage", 1.25);
        config.addDefault("Abilities.Laser.ForceField.Radial.Speed", 70);
        config.addDefault("Abilities.Laser.ForceField.Radial.Duration", 10000);
        config.addDefault("Abilities.Laser.ForceField.Radial.ClickRadius", 2);
        config.addDefault("Abilities.Laser.ForceField.Radial.SneakRadius", 10);

        //HotSpot
        config.addDefault("Abilities.Laser.Radiation.HotSpot.Enabled", true);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.Cooldown", 7000);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.Damage", 2);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.Radius", 3);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.ExplosionRadius", 3.5);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.Duration", 5000);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.BlockRevertTime", 6000);
        config.addDefault("Abilities.Laser.Radiation.HotSpot.RadianceDuration", 100);
        pkConfig.addDefault("Abilities.Laser.Radiation.HotSpot.DeathMessage", "{victim} was exploded by {attacker}'s {ability}");

        //RadiationBurst
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.Enabled", true);
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.Cooldown", 7500);
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.ChargeTime", 900);
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.Damage", 2);
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.Steps", 25);
        config.addDefault("Abilities.Laser.Radiation.RadiationBurst.RadianceDuration", 100);

        //Contaminate
        config.addDefault("Abilities.Laser.Radiation.Contaminate.Enabled", true);
        config.addDefault("Abilities.Laser.Radiation.Contaminate.Cooldown", 11500);
        config.addDefault("Abilities.Laser.Radiation.Contaminate.Range", 10);
        config.addDefault("Abilities.Laser.Radiation.Contaminate.ChargeTime", 850);
        config.addDefault("Abilities.Laser.Radiation.Contaminate.NauseaDuration", 150);
        config.addDefault("Abilities.Laser.Radiation.Contaminate.WeaknessDuration", 50);

        //Disintegration
        config.addDefault("Abilities.Laser.Radiation.Disintegration.Enabled", true);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.Damage", 1);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.Cooldown", 10000);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.Range", 7.0);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.ChargeTime", 2000);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.Push", 1.5);
        config.addDefault("Abilities.Laser.Radiation.Disintegration.RadianceDuration", 80);
        pkConfig.addDefault("Abilities.Radiation.Disintegration.DeathMessage", "{victim} was disintegrated {attacker}'s {ability}");

        //Dispersion
        config.addDefault("Abilities.Laser.Combos.Dispersion.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.Dispersion.Cooldown", 9000);
        config.addDefault("Abilities.Laser.Combos.Dispersion.Damage", 2);
        config.addDefault("Abilities.Laser.Combos.Dispersion.Speed", 25);
        config.addDefault("Abilities.Laser.Combos.Dispersion.CollisionRadius", 0.8);
        config.addDefault("Abilities.Laser.Combos.Dispersion.Range", 40);
        config.addDefault("Abilities.Laser.Combos.Dispersion.RadianceDuration", 50);
        config.addDefault("Abilities.Laser.Combos.Dispersion.Angle", 90);
        config.addDefault("Abilities.Laser.Combos.Dispersion.FanRange", 25);
        pkConfig.addDefault("Abilities.Laser.Dispersion.DeathMessage", "{victim} got yassified by {attacker}'s {ability}");

        //ForceOfWill
        config.addDefault("Abilities.Laser.Combos.ForceOfWill.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.ForceOfWill.Cooldown", 20000);
        config.addDefault("Abilities.Laser.Combos.ForceOfWill.Damage", 1);
        config.addDefault("Abilities.Laser.Combos.ForceOfWill.Range", 10);
        config.addDefault("Abilities.Laser.Combos.ForceOfWill.Duration", 3000);

        //Updraft
        config.addDefault("Abilities.Laser.Combos.Updraft.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.Updraft.Cooldown", 15000);
        config.addDefault("Abilities.Laser.Combos.Updraft.Damage", 3);
        config.addDefault("Abilities.Laser.Combos.Updraft.FireTicks", 0);
        config.addDefault("Abilities.Laser.Combos.Updraft.KnockUp", 2);
        pkConfig.addDefault("Abilities.Laser.Combos.Updraft.DeathMessage", "{victim} was launched into the air by {attacker}'s {ability}");

        //Decontaminate
        config.addDefault("Abilities.Laser.Combos.Decontaminate.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.Decontaminate.Cooldown", 11000);
        config.addDefault("Abilities.Laser.Combos.Decontaminate.Duration", 6000);

        //Fallout
        config.addDefault("Abilities.Laser.Combos.Fallout.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.Fallout.Cooldown", 15000);
        config.addDefault("Abilities.Laser.Combos.Fallout.Duration", 500);
        config.addDefault("Abilities.Laser.Combos.Fallout.Damage", 2);
        config.addDefault("Abilities.Laser.Combos.Fallout.Radius", 2.0);
        config.addDefault("Abilities.Laser.Combos.Fallout.Speed", 2.2);
        config.addDefault("Abilities.Laser.Combos.Fallout.RadianceDuration", 80);
        config.addDefault("Abilities.Laser.Combos.Fallout.RadianceDamage", 3);
        config.addDefault("Abilities.Laser.Combos.Fallout.RadiantSpeed", 2.5);
        pkConfig.addDefault("Abilities.Laser.Combos.Fallout.DeathMessage", "{victim} died of radiation posioning caused by {attacker}'s {ability} Contamination");

        //Ionize
        config.addDefault("Abilities.Laser.Combos.Ionize.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.Ionize.Cooldown", 18500);
        config.addDefault("Abilities.Laser.Combos.Ionize.Range", 30);
        config.addDefault("Abilities.Laser.Combos.Ionize.Speed", 20);
        config.addDefault("Abilities.Laser.Combos.Ionize.RadianceDuration", 165);
        config.addDefault("Abilities.Laser.Combos.Ionize.SelfRadianceDuration", 80);

        //PhotonRing
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Cooldown", 12500);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Radius", 8);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Duration", 8000);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.RadianceDuration", 30);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Speed", 25);
        config.addDefault("Abilities.Laser.Combos.PhotonRing.Range", 15);

        //SystemicShock
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Enabled", true);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Cooldown", 13000);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Range", 18);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Damage", 2);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Speed", 37);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.Radius", 1);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.AbilityCooldown", 6500);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.EffectDuration", 40);
        config.addDefault("Abilities.Laser.Combos.SystemicShock.HoldTime", 5000);
        pkConfig.addDefault("Abilities.Laser.SystemicShock.DeathMessage", "{victim}'s organs failed after being subject to {attacker}'s {ability}");

        //- Passives -

        //PhotonBooster
        config.addDefault("Passives.Laser.PhotonBooster.Enabled", true);
        config.addDefault("Passives.Laser.PhotonBooster.SpeedFactor", 3);

        //Irradiation
        config.addDefault("Passives.Laser.Irradiation.Enabled", true);


        config.options().copyDefaults(true);
        pkConfig.options().copyDefaults(true);
        ProjectCosmos.getPlugin().saveConfig();
        com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.save();
    }

    private List<String> createBushList() {
        List<String> bushMats = new ArrayList<>();
        bushMats.add(Material.DANDELION.toString());
        bushMats.add(Material.POPPY.toString());
        bushMats.add(Material.BLUE_ORCHID.toString());
        bushMats.add(Material.ALLIUM.toString());
        bushMats.add(Material.AZURE_BLUET.toString());
        bushMats.add(Material.CORNFLOWER.toString());
        bushMats.add(Material.LILAC.toString());
        bushMats.add(Material.LILY_OF_THE_VALLEY.toString());
        bushMats.add(Material.PEONY.toString());
        bushMats.add(Material.ROSE_BUSH.toString());
        bushMats.add(Material.SUNFLOWER.toString());
        bushMats.add(Material.ORANGE_TULIP.toString());
        bushMats.add(Material.PINK_TULIP.toString());
        bushMats.add(Material.RED_TULIP.toString());
        bushMats.add(Material.WHITE_TULIP.toString());
        bushMats.add(Material.FERN.toString());
        bushMats.add(Material.LARGE_FERN.toString());
        bushMats.add(Material.GRASS.toString());
        bushMats.add(Material.TALL_GRASS.toString());
        bushMats.add(Material.SPRUCE_SAPLING.toString());
        bushMats.add(Material.ACACIA_SAPLING.toString());
        bushMats.add(Material.BIRCH_SAPLING.toString());
        bushMats.add(Material.DARK_OAK_SAPLING.toString());
        bushMats.add(Material.OAK_SAPLING.toString());
        bushMats.add(Material.JUNGLE_SAPLING.toString());
        return bushMats;
    }

    private List<String> createBricksList() {
        List<String> brickMats = new ArrayList<>();
        brickMats.add(Material.STONE_BRICKS.toString());
        brickMats.add(Material.MOSSY_STONE_BRICKS.toString());
        return brickMats;
    }

    private List<String>  createFireBlocksList() {
        List<String> fireBlocks = new ArrayList<>();
        fireBlocks.add(Material.MAGMA_BLOCK.toString());
        return fireBlocks;
    }

    private List<String> createSlowBlocksList() {
        List<String> slowBlocks = new ArrayList<>();
        slowBlocks.add(Material.ICE.toString());
        slowBlocks.add(Material.PACKED_ICE.toString());
        slowBlocks.add(Material.BLUE_ICE.toString());
        slowBlocks.add(Material.SNOW_BLOCK.toString());
        return slowBlocks;
    }

    private List<String> createDamageBlocksList() {
        List<String> damageBlocks = new ArrayList<>();
        damageBlocks.add(Material.OBSIDIAN.toString());
        damageBlocks.add(Material.CRYING_OBSIDIAN.toString());
        damageBlocks.add(Material.CACTUS.toString());
        damageBlocks.add(Material.COAL_BLOCK.toString());
        damageBlocks.add(Material.IRON_BLOCK.toString());
        damageBlocks.add(Material.GOLD_BLOCK.toString());
        damageBlocks.add(Material.DIAMOND_BLOCK.toString());
        damageBlocks.add(Material.ANCIENT_DEBRIS.toString());
        damageBlocks.add(Material.NETHERITE_BLOCK.toString());
        return damageBlocks;
    }
}
