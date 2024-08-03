package mok_slyk.shpe.scripts.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import mok_slyk.shpe.scripts.world.systems.Mercian;

public class SHPESectorGen {
    public void generate(SectorAPI sector) {
        new Mercian().generate(sector);
    }
}
