package ee.lutsu.alpha.mc.modifyworld2.handlers;

import ee.lutsu.alpha.mc.modifyworld2.Modifyworld2;
import ee.lutsu.alpha.mc.modifyworld2.BaseListener;
import ee.lutsu.alpha.mc.modifyworld2.PlayerInformer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

public class VehicleListener extends BaseListener {

/*
	@EventHandler(priority = EventPriority.LOW)
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (!(event.getAttacker() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getAttacker();
		if (permissionDenied(player, "modifyworld.vehicle.destroy", event.getVehicle())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntered();
		if (permissionDenied(player, "modifyworld.vehicle.enter", event.getVehicle())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		if (_permissionDenied(player, "modifyworld.vehicle.collide", event.getVehicle())) {
			event.setCancelled(true);
			event.setCollisionCancelled(true);
			event.setPickupCancelled(true);
		}
	}*/

	@Override
	protected void registerEvents(Modifyworld2 plugin)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	
}