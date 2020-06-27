package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Permissions;
import org.bukkit.entity.Player;

/**
 * Player Permission related utility methods.
 */
public class PermissionUtils {

	/**
	 * Check if the player has the specified permission.
	 * This will return true if permissions are disabled (Not Strict).
	 *
	 * @param player target player
	 * @param permission required {@link Permissions}
	 * @return player has permission
	 */
	public static boolean hasPermission(Player player, Permissions permission) {
		if (!Carz.getDefaultConfig().getBoolean("Other.UsePermissions")) {
			return true;
		}

		return hasStrictPermission(player, permission);
	}

	/**
	 * Strict check if the player has the specified permission.
	 * It is strict as the config to use permissions is ignored.
	 * The player will be sent a message if they don't have the permission.
	 *
	 * @param player target player
	 * @param permission required {@link Permissions}
	 * @return player has permission
	 */
	public static boolean hasStrictPermission(Player player, Permissions permission) {
		return hasStrictPermission(player, permission, true);
	}

	/**
	 * Strict check if the player has the specified permission.
	 * It is strict as the config to use permissions is ignored.
	 *
	 * @param player target player
	 * @param permission the required {@link Permissions}
	 * @param displayMessage display failure message
	 * @return player has permission
	 */
	public static boolean hasStrictPermission(Player player, Permissions permission, boolean displayMessage) {
		if (player.hasPermission(permission.getPermission())
				|| player.hasPermission(Permissions.ALL.getPermission())
				|| player.isOp()) {
			return true;
		}

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.NoPermission",
					permission.getPermission(), player);
		}
		return false;
	}
}
