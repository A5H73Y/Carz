package io.github.a5h73y.carz.persistence;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CarDataMap implements CarDataPersistence {

	private final Map<String, String> vehicleDetail = new HashMap<>();

	private static final String VEHICLE_PREFIX = "V";
	private static final String ITEM_STACK_PREFIX = "I";

	@Override
	public String getValue(VehicleDetailKey detailKey, ItemStack itemStack) {
		return vehicleDetail.get(generateKey(detailKey, itemStack));
	}

	@Override
	public String getValue(VehicleDetailKey detailKey, Entity vehicle) {
		return vehicleDetail.get(generateKey(detailKey, vehicle));
	}

	@Override
	public void setValue(VehicleDetailKey detailKey, ItemStack itemStack, String value) {
		vehicleDetail.put(generateKey(detailKey, itemStack), value);
	}

	@Override
	public void setValue(VehicleDetailKey detailKey, Entity vehicle, String value) {
		vehicleDetail.put(generateKey(detailKey, vehicle), value);
	}

	@Override
	public boolean has(VehicleDetailKey detailKey, ItemStack itemStack) {
		return vehicleDetail.containsKey(generateKey(detailKey, itemStack));
	}

	@Override
	public boolean has(VehicleDetailKey detailKey, Entity vehicle) {
		return vehicleDetail.containsKey(generateKey(detailKey, vehicle));
	}

	@Override
	public void remove(VehicleDetailKey detailKey, Entity vehicle) {
		vehicleDetail.remove(generateKey(detailKey, vehicle));
	}

	@Override
	public void transferNamespaceKeyValues(Entity from, ItemStack to) {
		transferNamespaceKeyValues(VEHICLE_PREFIX + from.getEntityId() + ".",
				to.hashCode());
	}

	@Override
	public void transferNamespaceKeyValues(ItemStack from, Entity to) {
		transferNamespaceKeyValues(ITEM_STACK_PREFIX + from.hashCode() + ".",
				to.getEntityId());
	}

	private void transferNamespaceKeyValues(String keyPrefix, int to) {
		Map<String, String> vehicleDetailsCopy = vehicleDetail.keySet().stream()
				.filter(s -> s.startsWith(keyPrefix))
				.collect(Collectors.toMap(s -> s, vehicleDetail::get));

		vehicleDetailsCopy.forEach((s, s2) -> {
			String nameSpace = s.split("\\.")[1];
			vehicleDetail.put(to + "." + nameSpace, s2);
			vehicleDetail.remove(s);
		});
	}

	@Override
	public void printDataDetails(Player player, Entity vehicle) {
		printDataDetails(player, VEHICLE_PREFIX + vehicle.getEntityId() + ".");
	}

	@Override
	public void printDataDetails(Player player, ItemStack itemStack) {
		printDataDetails(player, ITEM_STACK_PREFIX + itemStack.hashCode() + ".");
	}

	private void printDataDetails(Player player, String keyPrefix) {
		vehicleDetail.keySet().stream()
				.filter(s -> s.startsWith(keyPrefix))
				.forEach(s -> {
					String nameSpace = s.split("\\.")[1].replace("-key", "")
							.replace("-", " ");
					String value = vehicleDetail.get(s);
					player.sendMessage(nameSpace + " = " + value);
				});
	}

	private String generateKey(VehicleDetailKey detailKey, Entity vehicle) {
		return VEHICLE_PREFIX + vehicle.getEntityId() + "." + detailKey.name();
	}

	private String generateKey(VehicleDetailKey detailKey, ItemStack itemStack) {
		return ITEM_STACK_PREFIX + itemStack.hashCode() + "." + detailKey.name();
	}
}
