package me.simpleforms;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
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

            // Remove any previous disguise
            DisguiseAPI.undisguiseToAll(player);

            // Clear potion effects
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );

            switch (form) {
                case "bat" -> {
                    DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.BAT));
                    player.setAllowFlight(true);
                    player.setFlying(true);

                    Bukkit.getScheduler().runTaskTimer(this, task -> {
                        if (!player.isOnline() || !player.isFlying()) {
                            task.cancel();
                            return;
                        }
                        if (player.getHealth() > 1.0) {
                            player.damage(1.0);
                        }
                    }, 40L, 40L);

                    player.sendMessage("§dYou transformed into a Bat!");
                }

                case "frog" -> {
                    DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.FROG));
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.JUMP_BOOST,
                            Integer.MAX_VALUE,
                            2
                    ));
                    player.sendMessage("§aYou transformed into a Frog!");
                }

                case "cat" -> {
                    DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CAT));
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SPEED,
                            Integer.MAX_VALUE,
                            1
                    ));
                    player.sendMessage("§6You transformed into a Cat!");
                }
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("untransform")) {
            DisguiseAPI.undisguiseToAll(player);
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
