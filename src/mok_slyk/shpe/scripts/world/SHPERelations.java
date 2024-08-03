package mok_slyk.shpe.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import mok_slyk.shpe.scripts.world.systems.Mercian;

public class SHPERelations {
    public void generate(SectorAPI sector) {
        FactionAPI imperium = sector.getFaction("shpe_imperium");
        FactionAPI mechanicus = sector.getFaction("shpe_mechanicus");
        FactionAPI player = sector.getFaction(Factions.PLAYER);

        imperium.setRelationship("shpe_mechanicus", 100);
    }
}
