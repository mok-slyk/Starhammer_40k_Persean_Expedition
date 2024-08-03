package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual.NEParams;
import com.fs.starfarer.api.impl.combat.RiftCascadeEffect;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import com.fs.starfarer.api.impl.combat.RiftTrailEffect;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;


public class VortexTorpedoOnHitEffect implements OnHitEffectPlugin {

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		Color color = RiftCascadeEffect.STANDARD_RIFT_COLOR;
		Object o = projectile.getWeapon().getSpec().getProjectileSpec();
		if (o instanceof MissileSpecAPI) {
			MissileSpecAPI spec = (MissileSpecAPI) o;
			color = spec.getExplosionColor();
		}
		
		NEParams p = createVortexRiftParams(color, 40f);
		p.fadeOut = 2f;
		p.hitGlowSizeMult = 1f;
		//p.invertForDarkening = NSProjEffect.STANDARD_RIFT_COLOR;
		RiftCascadeMineExplosion.spawnStandardRift(projectile, p);
		
		Vector2f vel = new Vector2f();
		if (target != null) vel.set(target.getVelocity());
		Global.getSoundPlayer().playSound("rifttorpedo_explosion", 1f, 1f, point, vel);
	}

	public static NEParams createVortexRiftParams(Color borderColor, float radius) {
		NEParams p = new NEParams();
		//p.radius = 50f;
		p.hitGlowSizeMult = .75f;
		//p.hitGlowSizeMult = .67f;
		p.spawnHitGlowAt = 0f;
		p.noiseMag = 1f;
		//p.fadeIn = 0f;
		//p.fadeOut = 0.25f;

		//p.color = new Color(175,100,255,255);

		//p.hitGlowSizeMult = .75f;
		p.fadeIn = 0.1f;
		//p.noisePeriod = 0.05f;
		p.underglow = new Color(150, 30, 255);
		//p.withHitGlow = i == 0;
		p.withHitGlow = true;

		//p.radius = 20f;
		p.radius = radius;
		//p.radius *= 0.75f + 0.5f * (float) Math.random();

		p.color = borderColor;
		return p;
	}
}




