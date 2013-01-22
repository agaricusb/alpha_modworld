package ee.lutsu.alpha.mc.modifyworld2;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ru.tehkode.permissions.IPermissions;

public class Permissions
{
	private static int pexOn = 0;
	private static IPermissions pex = null;
	
	private static boolean pexAvailable()
	{
		if (pexOn == 0)
		{
			for (ModContainer cont : Loader.instance().getModList())
			{
				if (cont.getModId().equalsIgnoreCase("PermissionsEx"))
				{
					if (cont.getMod() instanceof IPermissions)
						pex = (IPermissions)cont.getMod();
					
					break;
				}
			}
			pexOn = pex == null ? 2 : 1;
		}
		
		return pexOn == 1;
	}
	
	public static boolean canAccess(EntityPlayer name, String node)
	{
		return canAccess(name.username, String.valueOf(name.dimension), node);
	}
	
	public static boolean canAccess(String name, String world, String node)
	{
		if (!pexAvailable())
			throw new RuntimeException("PEX not found");
		
		return pex.has(name, node, world);
	}
	
	public static String getPrefix(String player, String world)
	{
		if (!pexAvailable())
			return "";

		return pex.prefix(player, world);
	}
	
	public static String getPostfix(String player, String world)
	{
		if (!pexAvailable())
			return "";

		return pex.suffix(player, world);
	}
}
