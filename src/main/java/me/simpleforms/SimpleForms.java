package me.simpleforms;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class SimpleForms extends JavaPlugin {

    private final HashMap<UUID, String> forms = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("SimpleForms enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setform")) {
            if (args.length != 1) {
                player.sendMessage("§cUsage: /setform <bat|frog|cat>");
                return true;
            }

            String form = args[0].toLowerCase();
            if (!form.equals("bat") && !form.equals("frog") && !form.equals("cat")) {
                player.sendMessage("§cInvalid form.");
                return true;
            }

            forms.put(player.getUniqueId(), form);
            player.sendMessage("§aForm set to §e" + form);
            return true;
        }

        if (command.getName().equalsIgnoreCase("transform")) {
            String form = forms.get(player.getUniqueId());

            if (form == null) {
                player.sendMessage("§cYou haven't selected a form.");
                return true;
            }

            // Remove old effects
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );

            switch (form) {
                case "bat" -> {
                    player.performCommand("disguise " + player.getName() + " bat");
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }

                case "frog" -> {
                    player.performCommand("disguise " + player.getName() + " frog");
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.JUMP_BOOST,
                            Integer.MAX_VALUE,
                            2
                    ));
                }

                case "cat" -> {
                    player.performCommand("disguise " + player.getName() + " cat");
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SPEED,
                            Integer.MAX_VALUE,
                            1
                    ));
                }
            }

            player.sendMessage("§aTransformed into §e" + form + "!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("untransform")) {
            player.performCommand("undisguise");
            player.setAllowFlight(false);
            player.setFlying(false);
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );
            player.sendMessage("§7You returned to human form.");
            return true;
        }

        return false;
    }
}
