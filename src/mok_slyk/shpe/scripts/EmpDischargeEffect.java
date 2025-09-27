package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import mok_slyk.shpe.scripts.utils.SHPEUtils;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class EmpDischargeEffect implements ProximityExplosionEffect {
    private static Logger log = Global.getLogger(ProximityExplosionEffect.class);
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","emp_discharge"),
                explosion.getLocation(),
                new Vector2f(),
                new Vector2f(40,40),
                new Vector2f(90,90),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(200,200,255,165),
                true,
                0,
                0.2f,
                0.4f
        );
        log.info(explosion.getEmpAmount());
        CombatEngineAPI engine = Global.getCombatEngine();
        for (ShipAPI ship: CombatUtils.getShipsWithinRange(explosion.getLocation(), explosion.getCollisionRadius()*0.5f)) {
            if (ship.getOwner() == originalProjectile.getOwner()) continue;
            Vector2f hit = null;
            if (ship.getCollisionRadius() < 60) {
                hit = SHPEUtils.getRandomPointInBounds(ship);
            } else {
                hit = SHPEUtils.getRandomPointInBoundsAndCircle(ship, explosion.getLocation(), explosion.getCollisionRadius()*0.5f, 32);
            }
            if (hit == null) return;
            engine.spawnEmpArcVisual(explosion.getLocation(), explosion, hit, ship, 1.5f, new Color(68, 106, 255), new Color(221, 255, 255));
            engine.applyDamage(ship, hit, originalProjectile.getDamageAmount(), originalProjectile.getDamageType(), originalProjectile.getEmpAmount(), false, false, originalProjectile.getSource());
        }
        //engine.spawnEmpArc(originalProjectile.getSource(), explosion.getLocation(), null, )
    }
}
