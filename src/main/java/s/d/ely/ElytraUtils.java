package s.d.ely;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ElytraUtils {

    public static void sendElytraAppearance(Player player) {
        ItemStack elytraItem = ItemStack.builder()
                .type(ItemTypes.ELYTRA)
                .amount(1)
                .build();


        Equipment chestEquipment = new Equipment(EquipmentSlot.CHEST_PLATE, elytraItem);

        WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment(
                player.getEntityId(),
                Collections.singletonList(chestEquipment)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, equipmentPacket);

    }

    public static void removeElytraAppearance(Player player) {
        WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment(
                player.getEntityId(),
                Collections.singletonList(new Equipment(EquipmentSlot.CHEST_PLATE, com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY))
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, equipmentPacket);
    }
}
