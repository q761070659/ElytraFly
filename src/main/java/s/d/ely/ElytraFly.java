package s.d.ely;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import static s.d.ely.ElytraUtils.removeElytraAppearance;
import static s.d.ely.ElytraUtils.sendElytraAppearance;

public class ElytraFly extends JavaPlugin implements Listener {

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        Bukkit.getPluginManager().registerEvents(new ElytraEventListener(), this);
        getLogger().info("ElytraFly 插件已启动！");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("ElytraFly 插件已关闭！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("elytrafly")) {
            Player targetPlayer;

            if (sender instanceof Player) {
                targetPlayer = (Player) sender;
            }
            else if (args.length > 0) {
                targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "玩家 " + args[0] + " 未找到或不在线！");
                    return true;
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "用法: /elytrafly [玩家]");
                return true;
            }

            if (targetPlayer.getGameMode() == GameMode.SURVIVAL || targetPlayer.getGameMode() == GameMode.ADVENTURE) {
                enableElytraFlight(targetPlayer);
                sender.sendMessage(ChatColor.GREEN + "已为玩家 " + targetPlayer.getName() + " 启用鞘翅飞行！");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "玩家 " + targetPlayer.getName() + " 必须在生存或冒险模式下才能使用此功能！");
                return true;
            }
        }
        return false;
    }

    private void enableElytraFlight(Player player) {
        if (player.isOnGround()) {

            player.setVelocity(player.getLocation().getDirection().multiply(0).setY(1.2));
            Bukkit.getScheduler().runTaskLater(this, () -> {

                player.setGliding(true);

                startElytraAppearanceTask(player);
            }, 20L);
        } else {
            player.setGliding(true);
            startElytraAppearanceTask(player);
        }
    }

    private void startElytraAppearanceTask(Player player) {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!player.isGliding() || player.isOnGround()) {
                Bukkit.getScheduler().cancelTask(player.getMetadata("elytraAppearanceTask").get(0).asInt());
                player.removeMetadata("elytraAppearanceTask", this);

                removeElytraAppearance(player);

                player.sendMessage(ChatColor.YELLOW + "鞘翅飞行已结束！");
                return;
            }

            sendElytraAppearance(player);
        }, 0L, 10L);

        player.setMetadata("elytraAppearanceTask", new FixedMetadataValue(this, taskId));
    }
}
