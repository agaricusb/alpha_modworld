package ee.lutsu.alpha.mc.modifyworld2;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.Configuration;

public abstract class BaseListener
{
	protected PlayerInformer informer;
	protected Configuration config;
	protected boolean informPlayers = false;
	protected boolean useMaterialNames = false;
	protected boolean checkMetadata = false;
	protected boolean checkItemUse = false;
	protected boolean enableWhitelist = false;
	protected Modifyworld2 parent;
	public boolean enabled = false;

	public BaseListener()
	{
		this.registerEvents(parent);
	}
	
	public void load()
	{
		this.informer = Modifyworld2.instance.informer;
		this.config = Modifyworld2.instance.config;
		this.parent = Modifyworld2.instance;
		
		this.informPlayers = config.get("General", "informPlayers", informPlayers).getBoolean(informPlayers);
		this.useMaterialNames = config.get("General", "use-material-names", useMaterialNames).getBoolean(useMaterialNames);
		this.checkMetadata = config.get("General", "check-metadata", checkMetadata).getBoolean(checkMetadata);
		this.checkItemUse = config.get("General", "item-use-check", checkItemUse).getBoolean(checkItemUse);
		this.enableWhitelist = config.get("General", "whitelist", enableWhitelist).getBoolean(enableWhitelist);
	}

	private String getEntityName(Entity entity)
	{
/*
		if (entity instanceof ComplexEntityPart) {
			return getEntityName(((ComplexEntityPart) entity).getParent());
		}*/

		String entityName = formatEnumString(entity.getEntityName());

		if (entity instanceof EntityItem) {
			entityName = getItemPermission(((EntityItem) entity).getEntityItem());
		}

		if (entity instanceof EntityPlayer) {
			return "player." + ((EntityPlayer) entity).username;
		} else if (entity instanceof EntityTameable) {
			EntityTameable animal = (EntityTameable) entity;

			return "animal." + entityName + (animal.isTamed() ? "." + animal.getOwner().getEntityName() : "");
		}


		EntityCategory category = EntityCategory.fromEntity(entity);

		if (category == null) {
			return entityName; // category unknown (ender crystal)
		}

		return category.getNameDot() + entityName;
	}
	
	private String getInventoryTypePermission(IInventory type) {
		return formatEnumString(type.getInvName());
	}

	// Functional programming fuck yeah
	private String getMaterialPermission(Block type) {
		return this.useMaterialNames ? formatEnumString(type.getUnlocalizedName()) : Integer.toString(type.blockID);
	}

	private String getMaterialPermission(int type) {
		return this.useMaterialNames ? formatEnumString(Block.blocksList[type].getUnlocalizedName()) : Integer.toString(type);
	}
	
	private String getMaterialPermission(int type, int metadata) {
		return getMaterialPermission(type) + (metadata > 0 ? ":" + metadata : "");
	}

	public String getItemPermission(ItemStack item) {
		return this.useMaterialNames ? item.getItemName() : String.valueOf(item.itemID);
	}

	/*
	protected boolean permissionDenied(Player player, String basePermission, Entity entity) {
		if (entity instanceof Player && PermissionsEx.isAvailable()) {
			PermissionUser entityUser = PermissionsEx.getUser((Player)entity);

			for (PermissionGroup group : entityUser.getGroups()) {
				if (permissionDenied(player, basePermission, "group", group.getName())) {
					return true;
				}
			}

			return permissionDenied(player, basePermission, "player", entityUser.getName());
		}

		return permissionDenied(player, basePermission, entity);
	}
	*/

	protected boolean permissionDenied(EntityPlayer player, String basePermission, Object... arguments) {
		String permission = assemblePermission(basePermission, arguments);
		boolean isDenied = !Permissions.canAccess(player, permission);

		if (isDenied) {
			this.informer.informPlayer(player, permission, arguments);
		}

		return isDenied;
	}

	protected boolean _permissionDenied(EntityPlayer player, String permission, Object... arguments) {
		return !Permissions.canAccess(player, assemblePermission(permission, arguments));
	}

	protected String assemblePermission(String permission, Object... arguments) {
		StringBuilder builder = new StringBuilder(permission);

		if (arguments != null) {
			for (Object obj : arguments) {
				if (obj == null) {
					continue;
				}

				builder.append('.');
				builder.append(getObjectPermission(obj));
			}
		}

		return builder.toString();
	}

	protected String getObjectPermission(Object obj) {
		if (obj instanceof Entity) {
			return (getEntityName((Entity) obj));
		/*} else if (obj instanceof EntityType) {
			return formatEnumString(((EntityType)obj).name());*/
		/*} else if (obj instanceof BlockState) {
			return (getBlockPermission(((BlockState)obj).getBlock()));*/
		} else if (obj instanceof ItemStack) {
			return (getItemPermission((ItemStack) obj));
		/*} else if (obj instanceof Material) {
			return (getMaterialPermission((Material) obj));*/
		} else if (obj instanceof ee.lutsu.alpha.mc.modifyworld2.entities.Block) {
			return (getMaterialPermission(((ee.lutsu.alpha.mc.modifyworld2.entities.Block)obj).id, ((ee.lutsu.alpha.mc.modifyworld2.entities.Block)obj).type));
		} else if (obj instanceof Block) {
			return (getMaterialPermission((Block) obj));
		} else if (obj instanceof IInventory) {
			return getInventoryTypePermission((IInventory)obj);
		}

		return (obj.toString());
	}

	protected abstract void registerEvents(Modifyworld2 plugin);
	
	private String formatEnumString(String enumName) {
		return enumName.toLowerCase().replace("_", "");
	}
	
	public void sendToServerSpawn(EntityPlayerMP pl)
	{
		if (pl.dimension != 0)
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(pl, 0);

		WorldServer world = MinecraftServer.getServer().worldServerForDimension(pl.dimension);
		WorldInfo info = world.getWorldInfo();
		pl.setLocationAndAngles(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ(), 0, 0);
		
        world.theChunkProviderServer.loadChunk((int)pl.posX >> 4, (int)pl.posZ >> 4);

        while (!world.getCollidingBoundingBoxes(pl, pl.boundingBox).isEmpty())
            pl.setPosition(pl.posX, pl.posY + 1.0D, pl.posZ);
        
        pl.playerNetServerHandler.setPlayerLocation(pl.posX, pl.posY, pl.posZ, pl.rotationYaw, pl.rotationPitch);
	}
	
	public void respawnPlayer(EntityPlayerMP pl)
	{
		respawnPlayer(pl, pl.worldObj.provider.getRespawnDimension(pl));
	}
	
	public void respawnPlayer(EntityPlayerMP pl, int dim)
	{
		if (pl.dimension != dim)
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(pl, dim);
		
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(pl.dimension);
		ChunkCoordinates c = pl.getBedLocation();
		boolean forcedSpawn = pl.isSpawnForced();
		
		if (c != null)
			c = EntityPlayer.verifyRespawnCoordinates(world, c, forcedSpawn);

        if (c != null)
            pl.setLocationAndAngles((double)((float)c.posX + 0.5F), (double)((float)c.posY + 0.1F), (double)((float)c.posZ + 0.5F), 0.0F, 0.0F);
		else
		{
			pl.sendChatToPlayer("You don't have a bed to spawn in");
			WorldInfo info = world.getWorldInfo();
			pl.setLocationAndAngles(info.getSpawnX() + 0.5F, info.getSpawnY() + 0.1F, info.getSpawnZ() + 0.5F, 0, 0);
		}
        
        world.theChunkProviderServer.loadChunk((int)pl.posX >> 4, (int)pl.posZ >> 4);

        while (!world.getCollidingBoundingBoxes(pl, pl.boundingBox).isEmpty())
            pl.setPosition(pl.posX, pl.posY + 1.0D, pl.posZ);
		
		pl.playerNetServerHandler.setPlayerLocation(pl.posX, pl.posY, pl.posZ, pl.rotationYaw, pl.rotationPitch);
	}
}
