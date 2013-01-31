package ee.lutsu.alpha.mc.modifyworld2;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerInformer {

	public final static String PERMISSION_DENIED = "Sorry, you don't have enough permissions";
	public final static String WHITELIST_MESSAGE = "You are not allowed to join this server. Goodbye!";
	public final static String PROHIBITED_ITEM = "Prohibited item \"%s\" has been removed from your inventory.";
	public final static String DEFAULT_MESSAGE_FORMAT = "&f[&2Modifyworld&f]&4 %s";
	// Default message format
	protected String messageFormat = DEFAULT_MESSAGE_FORMAT;
	protected Map<String, String> messages = new HashMap<String, String>();
	// Flags
	protected boolean enabled = false;
	protected boolean individualMessages = false;
	protected String defaultMessage = PERMISSION_DENIED;

	public PlayerInformer(Configuration config) {
		this.enabled = config.get("General", "inform-players", enabled).getBoolean(enabled);

		this.loadConfig(config, "Messages");
	}

	private void loadConfig(Configuration config, String section) {

		this.defaultMessage = config.get(section, "default-message", this.defaultMessage).value;
		this.messageFormat = config.get(section, "message-format", this.messageFormat).value;
		this.individualMessages = config.get(section, "individual-messages", this.individualMessages).getBoolean(this.individualMessages);

		if (config.categories.get(section) != null)
		{
			for (Entry<String, Property> permission : config.categories.get(section).entrySet()) {
				setMessage(permission.getKey(), permission.getValue().value.replace("/", "."));
			}
		}
	}

	public void setMessage(String permission, String message) {
		messages.put(permission, message);
	}

	public String getMessage(String permission) {
		if (messages.containsKey(permission)) {
			return messages.get(permission);
		}

		String perm = permission;
		int index;

		while ((index = perm.lastIndexOf(".")) != -1) {
			perm = perm.substring(0, index);

			if (messages.containsKey(perm)) {
				String message = messages.get(perm);
				messages.put(permission, message);
				return message;
			}
		}

		return this.defaultMessage;
	}

	public String getMessage(EntityPlayer player, String permission)
	{
		String message;
		String perm = permission;
		int index;

		while ((index = perm.lastIndexOf(".")) != -1) {
			perm = perm.substring(0, index);

			message = Permissions.getOption(player.username, "permission-denied-" + perm, String.valueOf(player.dimension), null);
			if (message == null) {
				continue;
			}

			return message;
		}

		message = Permissions.getOption(player.username, "permission-denied", String.valueOf(player.dimension), null);

		if (message != null) {
			return message;
		}

		return getMessage(permission);
	}

	public void informPlayer(EntityPlayer player, String permission, Object... args) {
		if (!enabled) {
			return;
		}

		String message = getMessage(player, permission).replace("$permission", permission);

		for (int i = 0; i < args.length; i++) {
			message = message.replace("$" + (i + 1), describeObject(args[i]));
		}

		if (message != null && !message.isEmpty()) {
			player.sendChatToPlayer(String.format(messageFormat, message).replaceAll("&([a-z0-9])", "\u00A7$1"));
		}
	}

	protected String describeObject(Object obj) {
		/*if (obj instanceof ComplexEntityPart) { // Complex entities
			return describeObject(((ComplexEntityPart) obj).getParent());
		} else */ if (obj instanceof Item) { // Dropped items
			return ((Item) obj).getItemName();
		} else if (obj instanceof ItemStack) { // Items
			return ((ItemStack) obj).getItemName();
		} else if (obj instanceof Entity) { // Entities
			return ((Entity) obj).getEntityName().toString().toLowerCase().replace("_", " ");
		} else if (obj instanceof Block) { // Blocks
			return ((Block) obj).getBlockName();
		/*} else if (obj instanceof Material) { // Just material
			return describeMaterial((Material) obj);*/
		}

		return obj.toString();
	}
}
