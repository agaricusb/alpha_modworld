package ee.lutsu.alpha.mc.modifyworld2.handlers;

import ee.lutsu.alpha.mc.modifyworld2.Modifyworld2;
import ee.lutsu.alpha.mc.modifyworld2.BaseListener;
import ee.lutsu.alpha.mc.modifyworld2.PlayerInformer;
import ee.lutsu.alpha.mc.modifyworld2.entities.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class BlockListener extends BaseListener
{
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event)
	{
		if (!enabled)
			return;
		
		if (event.action == Action.LEFT_CLICK_BLOCK)
		{
			int id = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
			int type = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
			if (permissionDenied(event.entityPlayer, "modifyworld.blocks.destroy", new Block(id, type)))
				event.setCanceled(true);
		}
		else if (event.action == Action.RIGHT_CLICK_BLOCK)
		{
			int id = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
			int type = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
			if (permissionDenied(event.entityPlayer, "modifyworld.blocks.interact", new Block(id, type)))
				event.setCanceled(true);
				
			ItemStack inHand = event.entityPlayer.getHeldItem();
			if (inHand == null)
				return;
			
			id = inHand.itemID;
			type = inHand.getMaxDamage() < 1 ? inHand.getItemDamage() : 0;
			if (permissionDenied(event.entityPlayer, "modifyworld.blocks.place", new Block(id, type)))
				event.setCanceled(true);
		}
	}

	@Override
	protected void registerEvents(Modifyworld2 plugin)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
}
