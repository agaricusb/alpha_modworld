package ee.lutsu.alpha.mc.modifyworld2.handlers;

import ee.lutsu.alpha.mc.modifyworld2.Modifyworld2;
import ee.lutsu.alpha.mc.modifyworld2.BaseListener;
import ee.lutsu.alpha.mc.modifyworld2.PlayerInformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityListener extends BaseListener {

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onEntityAttack(AttackEntityEvent event) 
	{
		if (!enabled)
			return;
		
		if (permissionDenied(event.entityPlayer, "modifyworld.damage.deal", event.entityPlayer))
			event.setCanceled(true);

		if (event.target instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.target;
			if (_permissionDenied(player, "modifyworld.damage.take", event.entityPlayer))
				event.setCanceled(true);
		}

	}
	
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onEntityDamaged(LivingAttackEvent event) 
	{
		if (!enabled)
			return;
		
		if (event.entityLiving instanceof EntityPlayer)
		{ // player are been damaged by environment
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if (_permissionDenied(player, "modifyworld.damage.take",  event.source.damageType.toLowerCase().replace("_", ""))) {
				event.setCanceled(true);
				
				if (event.source == DamageSource.outOfWorld && player instanceof EntityPlayerMP)
					respawnPlayer((EntityPlayerMP)player);
			}
		}
	}
/*
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onEntityTame(EntityTameEvent event) {
		if (!(event.getOwner() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getOwner();

		if (permissionDenied(player, "modifyworld.tame", event.getEntity())) {
			event.setCancelled(true);
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player player = (Player) event.getTarget();
			if (_permissionDenied(player, "modifyworld.mobtarget",  event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}*/

	@Override
	protected void registerEvents(Modifyworld2 plugin) 
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
}
