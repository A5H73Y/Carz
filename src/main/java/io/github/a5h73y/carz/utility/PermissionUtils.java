package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Permissions;
import org.bukkit.entity.Player;

public class PermissionUtils {

	/**
	 * Check if the player has the specified permission.
	 * This will return true if permissions are disabled
	 * @param player
	 * @param permission
	 * @return boolean
	 */
	public static boolean hasPermission(Player player, Permissions permission) {
		if (!Carz.getInstance().getConfig().getBoolean("Other.UsePermissions"))
			return true;

		return hasStrictPermission(player, permission);
	}

	/**
	 * Check if the player has the specified permission.
	 * The player will be sent a message if they don't have the permission.
	 * @param player
	 * @param permission
	 * @return hasPermission
	 */
	public static boolean hasStrictPermission(Player player, Permissions permission) {
		return hasStrictPermission(player, permission, true);
	}

	/**
	 * Check if the player has the specified permission.
	 * This will strictly check if the player has permission / op.
	 * @param player
	 * @param permission
	 * @param displayMessage
	 * @return hasPermission
	 */
	public static boolean hasStrictPermission(Player player, Permissions permission, boolean displayMessage) {
		if (player.hasPermission(permission.getPermission())
				|| player.hasPermission(Permissions.ALL.getPermission())
				|| player.isOp())
			return true;

		if (displayMessage) {
			player.sendMessage(TranslationUtils.getTranslation("Error.NoPermission")
					.replace("%PERMISSION%", permission.getPermission()));
		}
		return false;
	}

}
