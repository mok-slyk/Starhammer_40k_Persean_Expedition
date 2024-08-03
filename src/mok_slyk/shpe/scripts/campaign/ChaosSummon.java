package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;
@Deprecated
public class ChaosSummon extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        if (command.equals("canSummonShips")) {
            return canSummonShips();
        } else if (command.equals("canSummonWeapons")) {
            return canSummonWeapons();
        } else if (command.equals("genWeapons")) {
            genWeapons();
        }
        return false;
    }

    protected boolean canSummonShips() {
        return false;
    }

    protected boolean canSummonWeapons() {
        return false;
    }

    protected boolean genWeapons() {
        return false;
    }
}
