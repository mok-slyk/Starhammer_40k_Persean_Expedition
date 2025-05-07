package mok_slyk.shpe.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.alliances.Alliance;
import mok_slyk.shpe.scripts.world.systems.Mercian;

public class SHPERelations {
    public void generate(SectorAPI sector) {
        FactionAPI imperium = sector.getFaction("shpe_imperium");
        FactionAPI mechanicus = sector.getFaction("shpe_mechanicus");
        FactionAPI chaos = sector.getFaction("shpe_chaos");
        FactionAPI khorne = sector.getFaction("shpe_khorne");
        FactionAPI nurgle = sector.getFaction("shpe_nurgle");
        FactionAPI tzeentch = sector.getFaction("shpe_tzeentch");
        FactionAPI slaanesh = sector.getFaction("shpe_slaanesh");
        FactionAPI player = sector.getFaction(Factions.PLAYER);

        for (FactionAPI other : Global.getSector().getAllFactions()) {
            String id = other.getId();
            if (!(id.contains("shpe_chaos") || id.contains("shpe_khorne") || id.contains("shpe_nurgle") || id.contains("shpe_slaanesh") || id.contains("shpe_tzeentch"))) {
                chaos.setRelationship(id, RepLevel.INHOSPITABLE);
                khorne.setRelationship(id, RepLevel.INHOSPITABLE);
                nurgle.setRelationship(id, RepLevel.INHOSPITABLE);
                tzeentch.setRelationship(id, RepLevel.INHOSPITABLE);
                slaanesh.setRelationship(id, RepLevel.INHOSPITABLE);
            }
        }

        player.setRelationship("shpe_chaos", -0.65f);
        player.setRelationship("shpe_khorne", -0.65f);
        player.setRelationship("shpe_nurgle", -0.65f);
        player.setRelationship("shpe_tzeentch", -0.65f);
        player.setRelationship("shpe_slaanesh", -0.65f);

        imperium.setRelationship("shpe_mechanicus", 1);
        imperium.setRelationship("shpe_chaos", -1);
        imperium.setRelationship("shpe_khorne", -1);
        imperium.setRelationship("shpe_nurgle", -1);
        imperium.setRelationship("shpe_tzeentch", -1);
        imperium.setRelationship("shpe_slaanesh", -1);
        imperium.setRelationship(Factions.PIRATES, -0.75f);
        imperium.setRelationship(Factions.LUDDIC_PATH, -0.5f);

        mechanicus.setRelationship("shpe_chaos", -1);
        mechanicus.setRelationship("shpe_khorne", -1);
        mechanicus.setRelationship("shpe_nurgle", -1);
        mechanicus.setRelationship("shpe_tzeentch", -1);
        mechanicus.setRelationship("shpe_slaanesh", -1);
        mechanicus.setRelationship(Factions.PIRATES, -0.75f);
        mechanicus.setRelationship(Factions.LUDDIC_PATH, -0.5f);

        chaos.setRelationship("shpe_khorne", 0.75f);
        chaos.setRelationship("shpe_nurgle", 0.75f);
        chaos.setRelationship("shpe_tzeentch", 0.75f);
        chaos.setRelationship("shpe_slaanesh", 0.75f);

        khorne.setRelationship("shpe_nurgle", -0.5f);
        khorne.setRelationship("shpe_tzeentch", -0.5f);
        khorne.setRelationship("shpe_slaanesh", -1);

        nurgle.setRelationship("shpe_slaanesh", -0.5f);
        nurgle.setRelationship("shpe_tzeentch", -1);

        tzeentch.setRelationship("shpe_slaanesh", -0.5f);

        boolean isNexerelinEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (isNexerelinEnabled) {
            Alliance greaterImperium = AllianceManager.createAlliance("shpe_imperium", "shpe_mechanicus", Alliance.Alignment.IDEOLOGICAL);
            greaterImperium.setName("Greater Imperium");
            greaterImperium.addPermaMember("shpe_imperium");
            greaterImperium.addPermaMember("shpe_mechanicus");
        }
    }
}
