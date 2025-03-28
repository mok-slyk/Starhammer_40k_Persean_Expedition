package mok_slyk.shpe.scripts.world.systems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.locks.Condition;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import org.lazywizard.lazylib.MathUtils;
import org.magiclib.util.MagicCampaign;

public class Mercian {
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Mercian");
        system.getLocation().set(15000, 32000);

        system.setBackgroundTextureFilename("graphics/backgrounds/background6.jpg");

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI mercianStar = system.initStar("Mercian", // unique id for this star
                "star_orange", // id in planets.json
                900f, // radius (in pixels at default zoom)
                450); // corona radius, from star edge
        system.setLightColor(new Color(239, 195, 128));// light color in entire system, affects all entities

        //Delphis:
        PlanetAPI delphis = system.addPlanet("delphis", mercianStar, "Delphis", "rocky_metallic", 360 * (float) Math.random(), 150f, 3000f, 200f);
        MarketAPI delphisMarket = MagicCampaign.addSimpleMarket(delphis, "delphis_market", "Delphis", 6,  "shpe_mechanicus", false, false,
                Arrays.asList(Conditions.POPULATION_6, Conditions.POLLUTION, Conditions.ORE_ABUNDANT, Conditions.RARE_ORE_ULTRARICH),
                Arrays.asList(Industries.MEGAPORT,Industries.POPULATION, Industries.MINING, Industries.REFINING, Industries.MILITARYBASE),
                true, true, true, true, true, false
        );
        delphisMarket.addIndustry(Industries.ORBITALWORKS, Collections.singletonList(Items.PRISTINE_NANOFORGE));

        for (MarketConditionAPI mc : delphisMarket.getConditions()) {
            mc.setSurveyed(true);
        }
        sector.getEconomy().addMarket(delphisMarket, true);
        delphis.setFaction("shpe_mechanicus");
        delphis.setMarket(delphisMarket);
        delphis.setCustomDescriptionId("shpe_planet_delphis");

        // Delphis Ring:
        //system.addOrbitalJunk(delphis, "orbital_junk", 10, 12, 20, delphis.getRadius(), 110, delphis.getRadius()/20f, delphis.getRadius()/20f+10f, 60f, 360f);

        // Mercian Prime:
        PlanetAPI mercianPrime = system.addPlanet("mercian_prime", mercianStar, "Mercian Prime", "shpe_fledgling_hive", 360 * (float) Math.random(), 190f, 5000f, 300f);
        //PlanetConditionGenerator.generateConditionsForPlanet(mercianPrime, StarAge.AVERAGE);
        MarketAPI mercianMarket = MagicCampaign.addSimpleMarket(mercianPrime, "mercian_prime_market", "Mercian Prime", 7,  "shpe_imperium", false, false,
                Arrays.asList(Conditions.POPULATION_7, Conditions.REGIONAL_CAPITAL, Conditions.HABITABLE, Conditions.POLLUTION, Conditions.FARMLAND_ADEQUATE, Conditions.ORGANICS_ABUNDANT),
                Arrays.asList(Industries.MEGAPORT,Industries.POPULATION, Industries.HEAVYINDUSTRY, Industries.BATTLESTATION_MID, Industries.LIGHTINDUSTRY, Industries.MINING, Industries.GROUNDDEFENSES, Industries.HIGHCOMMAND),
                true, true, true, true, true, false
        );

        for (MarketConditionAPI mc : mercianMarket.getConditions()) {
            mc.setSurveyed(true);        }
        sector.getEconomy().addMarket(mercianMarket, true);
        mercianPrime.setFaction("shpe_imperium");
        mercianPrime.setMarket(mercianMarket);
        mercianPrime.setCustomDescriptionId("shpe_planet_mercian");

        // Halcyon
        PlanetAPI halcyon = system.addPlanet("halcyon", mercianStar, "Halcyon", "terran-eccentric", 360 * (float) Math.random(), 100f, 6500f, 500f);
        MarketAPI halcyonMarket = MagicCampaign.addSimpleMarket(halcyon, "halcyon_market", "Halcyon", 3,  "shpe_imperium", false, false,
                Arrays.asList(Conditions.POPULATION_3, Conditions.FARMLAND_ADEQUATE, Conditions.ORGANICS_COMMON),
                Arrays.asList(Industries.SPACEPORT,Industries.POPULATION, Industries.MINING),
                true, true, true, true, false, false
        );

        for (MarketConditionAPI mc : halcyonMarket.getConditions()) {
            mc.setSurveyed(true);
        }
        sector.getEconomy().addMarket(halcyonMarket, true);
        halcyon.setFaction("shpe_imperium");
        halcyon.setMarket(halcyonMarket);

        // The Halo
        system.addAsteroidBelt(mercianStar, 120, 7500f, 300f, 100, 360);
        system.addRingBand(mercianStar, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 7700f, 200);
        system.addRingBand(mercianStar, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 7300f, 200);

        // Mercian Extimus
        PlanetAPI extimus = system.addPlanet("extimus", mercianStar, "Mercian Extimus", "ice_giant", 360 * (float) Math.random(), 300f, 10100f, 800f);
        MarketAPI extimusMarket = MagicCampaign.addSimpleMarket(extimus, "extimus_market", "Mercian Extimus", 4,  "shpe_imperium", false, false,
                Arrays.asList(Conditions.POPULATION_4, Conditions.VOLATILES_ABUNDANT),
                Arrays.asList(Industries.SPACEPORT,Industries.POPULATION, Industries.MINING, Industries.FUELPROD),
                true, true, true, true, false, false
        );

        for (MarketConditionAPI mc : extimusMarket.getConditions()) {
            mc.setSurveyed(true);
        }
        sector.getEconomy().addMarket(extimusMarket, true);
        extimus.setFaction("shpe_imperium");
        extimus.setMarket(extimusMarket);

        // Thaum
        PlanetAPI thaum = system.addPlanet("thaum", extimus, "Thaum", "frozen", 360 * (float) Math.random(), 50f, 700f, 100f);

        //Finalize System:

        system.autogenerateHyperspaceJumpPoints(false, true);
        MagicCampaign.hyperspaceCleanup(system);
    }
}