package io.github.a5h73y.carz.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CarDataMap implements CarDataPersistence {

	private final Map<String, String> vehicleDetail = new HashMap<>();

	@Override
	public String getValue(VehicleDetailKey detailKey, ItemStack itemStack) {
		return vehicleDetail.get(generateKey(detailKey, itemStack));
	}

	@Override
	public String getValue(VehicleDetailKey detailKey, Entity vehicle) {
		return vehicleDetail.get(generateKey(detailKey, vehicle));
	}

	@Override
	public ItemStack setValue(VehicleDetailKey detailKey, ItemStack itemStack, String value) {
		vehicleDetail.put(generateKey(detailKey, itemStack), value);
		return itemStack;
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
		Map<String, String> vehicleDetailsCopy = vehicleDetail.keySet().stream()
				.filter(s -> s.startsWith(from.getEntityId() + "."))
				.collect(Collectors.toMap(s -> s, vehicleDetail::get));

		vehicleDetailsCopy.forEach((s, s2) -> {
			String nameSpace = s.split("\\.")[1];
			vehicleDetail.put(to.hashCode() + "." + nameSpace, s2);
			vehicleDetail.remove(s);
		});
	}

	@Override
	public void transferNamespaceKeyValues(ItemStack from, Entity to) {
		Map<String, String> vehicleDetailsCopy = vehicleDetail.keySet().stream()
				.filter(s -> s.startsWith(from.hashCode() + "."))
				.collect(Collectors.toMap(s -> s, vehicleDetail::get));

		vehicleDetailsCopy.forEach((s, s2) -> {
			String nameSpace = s.split("\\.")[1];
			vehicleDetail.put(to.getEntityId() + "." + nameSpace, s2);
			vehicleDetail.remove(s);
		});
	}

	@Override
	public void printDataDetails(Player player, Entity vehicle) {
		vehicleDetail.keySet().stream()
				.filter(s -> s.startsWith(vehicle.getEntityId() + "."))
				.forEach(s -> {
					String nameSpace = s.split("\\.")[1].replace("-key", "")
							.replace("-", " ");
					String value = vehicleDetail.get(s);
					player.sendMessage(nameSpace + " = " + value);
				});
	}

	private String generateKey(VehicleDetailKey detailKey, Entity vehicle) {
		return vehicle.getEntityId() + "." + detailKey.name();
	}
	
	private String generateKey(VehicleDetailKey detailKey, ItemStack itemStack) {
		return itemStack.hashCode() + "." + detailKey.name();
	}
}
