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

        List<String> fireBlocks = createFireBlocksList();
        List<String> slowBlocks = createSlowBlocksList();
        List<String> damageBlocks = createDamageBlocksList();

        pkConfig.addDefault("Commands.Help.Elements.Cosmic", "Cosmic is the element of creation. " +
                "Cosmicbenders wield the true power of space time and the void. The element was first used by celestial spirits to create the galaxies and solar systems, lighting up the pure void that existed before. Shortly after most of the Lion Turtles got hunted down, " +
                "the celestial spirits disposed of cosmicbending, and decided to choose one person to wield the power before letting it die out. " +
                "The person was later known as Vantu, who was the only Cosmicbender capable of using the power of the cosmos. " +
                "After they passed on, the celestial spirits were sure the element was extinct." +
                " After the Harmonic Convergence in 171, just like Airbenders, mortal Cosmic benders also started appearing on Earth, but no one knew about" +
                " them as there were so few. " +
                "\\nCosmicbending is a powerful element, heavy with crowdcontrol and mobility." +
                " They focus on attacking multiple targets at once, pushing them away and pulling them in. " +
                "Cosmicbenders are peaceful yet dangerous They wield the ability to destroy and create." +
                "\nEnter /b display Cosmic for a list of the available cosmic abilities.");

        // -- Cosmic --

        //TheSkiesDescend
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Enabled", true);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Cooldown", 7000);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Damage", 4);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Speed", 50);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.CollisionRadius", 2);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.ExplosionRadius", 4);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Delay", 750);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.KnockUp", 2);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.Range", 40);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.LevitationDuration", 35);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.LevitationPower", 1);
        config.addDefault("Abilities.Cosmic.TheSkiesDescend.BlockRevertTime", 7500);
        pkConfig.addDefault("Abilities.Cosmic.TheSkiesDescend.DeathMessage", "{victim} was crushed after finding themselves under {attacker}'s {ability}");

        //MeteorShower
        config.addDefault("Abilities.Cosmic.MeteorShower.Enabled", true);
        config.addDefault("Abilities.Cosmic.MeteorShower.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.MeteorShower.Damage", 3);
        config.addDefault("Abilities.Cosmic.MeteorShower.SelectRange", 40);
        config.addDefault("Abilities.Cosmic.MeteorShower.ExplosionRadius", 4);
        config.addDefault("Abilities.Cosmic.MeteorShower.Speed", 3);
        config.addDefault("Abilities.Cosmic.MeteorShower.Meteorites", 5);
        config.addDefault("Abilities.Cosmic.MeteorShower.Duration", 15000);
        config.addDefault("Abilities.Cosmic.MeteorShower.RevertTime", 10000);

        //SpaceWarp
        config.addDefault("Abilities.Cosmic.SpaceWarp.Enabled", true);
        config.addDefault("Abilities.Cosmic.SpaceWarp.Cooldown", 10000);

        //CosmicBlast
        config.addDefault("Abilities.Cosmic.CosmicBlast.Enabled", true);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Cooldown", 1500);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Range", 25);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Damage", 2);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Speed", 20);
        config.addDefault("Abilities.Cosmic.CosmicBlast.CollisionRadius", 2);
        config.addDefault("Abilities.Cosmic.CosmicBlast.Knockback", 1);
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
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Cooldown", 3000);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Range", 75);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.Damage", 2);
        config.addDefault("Abilities.Cosmic.CosmicSeeker.GlowDuration", 100);
        pkConfig.addDefault("Abilities.Cosmic.CosmicSeeker.DeathMessage", "{attacker} found and killed {victim} using {ability}");

        //StellarBreath
        config.addDefault("Abilities.Cosmic.StellarBreath.Enabled", true);
        config.addDefault("Abilities.Cosmic.StellarBreath.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.StellarBreath.Duration", 3000);
        config.addDefault("Abilities.Cosmic.StellarBreath.Damage", 1.0);
        config.addDefault("Abilities.Cosmic.StellarBreath.Knockback", 0.8);
        config.addDefault("Abilities.Cosmic.StellarBreath.Range", 10);

        //StarSurge
        config.addDefault("Abilities.Cosmic.StarSurge.Enabled", true);
        config.addDefault("Abilities.Cosmic.StarSurge.Cooldown", 7000);
        config.addDefault("Abilities.Cosmic.StarSurge.Range", 15);
        config.addDefault("Abilities.Cosmic.StarSurge.Radius", 0.8);
        config.addDefault("Abilities.Cosmic.StarSurge.ExplosionRadius", 4);
        config.addDefault("Abilities.Cosmic.StarSurge.RevertTime", 30000);
        config.addDefault("Abilities.Cosmic.StarSurge.Damage", 2);
        config.addDefault("Abilities.Cosmic.StarSurge.Knockback", 0.5);
        config.addDefault("Abilities.Cosmic.StarSurge.Speed", 1);
        config.addDefault("Abilities.Cosmic.StarSurge.Particles.Distance", 1);
        config.addDefault("Abilities.Cosmic.StarSurge.Slow.Duration", 20);
        config.addDefault("Abilities.Cosmic.StarSurge.Slow.Amplifier", 1);

        //EventHorizon
        config.addDefault("Abilities.Cosmic.EventHorizon.Enabled", true);
        config.addDefault("Abilities.Cosmic.EventHorizon.Knockback", 1);
        config.addDefault("Abilities.Cosmic.EventHorizon.Cooldown", 5000);
        config.addDefault("Abilities.Cosmic.EventHorizon.Range", 22);
        config.addDefault("Abilities.Cosmic.EventHorizon.ChargeTime", 1500);
        config.addDefault("Abilities.Cosmic.EventHorizon.Damage", 4);
        config.addDefault("Abilities.Cosmic.EventHorizon.Growth", 0.450);
        pkConfig.addDefault("Abilities.Cosmic.EventHorizon.DeathMessage", "{victim} was caught and struck down in {attacker}'s {ability}");

        //AstralFlight
        config.addDefault("Abilities.Cosmic.AstralFlight.Enabled", true);
        config.addDefault("Abilities.Cosmic.AstralFlight.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.AstralFlight.MaxDuration", 5000);
        config.addDefault("Abilities.Cosmic.AstralFlight.FlightSpeed", 0.75);

        //ConstellationCutter
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Enabled", true);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Cooldown", 8000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Duration", 10000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.Damage", 2);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.SelectRange", 8);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.MaxStars", 10);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.DamageInterval", 1000);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastInterval", 50);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastRadius", 2);
        config.addDefault("Abilities.Cosmic.ConstellationCutter.BlastDamage", 1);
        pkConfig.addDefault("Abilities.Cosmic.ConstellationCutter.DeathMessage", "{victim} was burnt up while staring at {attacker}'s {ability}");

        //SuperNova
        config.addDefault("Abilities.Cosmic.SuperNova.Enabled", true);
        config.addDefault("Abilities.Cosmic.SuperNova.Cooldown", 25000);
        config.addDefault("Abilities.Cosmic.SuperNova.ChargeTime", 2500);
        config.addDefault("Abilities.Cosmic.SuperNova.Range", 30);
        config.addDefault("Abilities.Cosmic.SuperNova.Damage", 4);
        config.addDefault("Abilities.Cosmic.SuperNova.Speed", 20);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.Enabled", true);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.Radius", 3);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.RevertBlocks", true);
        config.addDefault("Abilities.Cosmic.SuperNova.DamageBlocks.RevertTime", 12000);
        pkConfig.addDefault("Abilities.Cosmic.SuperNova.DeathMessage", "{victim} couldn't handle the raw power of {attacker}'s {ability}");

        //WormHole
        config.addDefault("Abilities.Cosmic.WormHole.Enabled", true);
        config.addDefault("Abilities.Cosmic.WormHole.Cooldown", 30000);
        config.addDefault("Abilities.Cosmic.WormHole.Duration", 10000);
        config.addDefault("Abilities.Cosmic.WormHole.PortalRadius", 1);
        config.addDefault("Abilities.Cosmic.WormHole.Range", 50);
        config.addDefault("Abilities.Cosmic.WormHole.Speed", 30);
        config.addDefault("Abilities.Cosmic.WormHole.TeleportCooldown", 2000);

        // - Lunar -

        //MoonFall
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Cooldown", 10000);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.ChargeTime", 2000);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Damage", 3);
        config.addDefault("Abilities.Cosmic.Lunar.MoonFall.Range", 30);

        //LunarClap
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Enabled", true);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Cooldown", 6500);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Range", 15);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Damage", 2);
        config.addDefault("Abilities.Cosmic.Lunar.LunarClap.Speed", 40);

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
        config.addDefault("Abilities.Cosmic.Lunar.LunarGlint.Range", 30);
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
        config.addDefault("Abilities.Cosmic.MassManipulation.Cooldown", 10000);
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
        config.addDefault("Abilities.Cosmic.GravityManipulation.Cooldown", 500);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Damage", 2);
        config.addDefault("Abilities.Cosmic.GravityManipulation.Range", 20);
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
        config.addDefault("Abilities.Cosmic.GravityBinding.Cooldown", 15000);
        config.addDefault("Abilities.Cosmic.GravityBinding.Range", 20);
        config.addDefault("Abilities.Cosmic.GravityBinding.Damage", 3);
        config.addDefault("Abilities.Cosmic.GravityBinding.LeviDuration", 3000);
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
        config.addDefault("Abilities.Cosmic.Combos.AccretionDisk.MaxRadius", 5);
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
        bushMats.add(Material.SHORT_GRASS.toString());
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
