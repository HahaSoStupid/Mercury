package tc.oc.pgm.api.disguises;

import app.ashcon.intake.Command;
import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.Permissions;
import tc.oc.pgm.api.event.NameDecorationChangeEvent;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;
import tc.oc.pgm.api.match.event.MatchUnloadEvent;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.namedecorations.NameDecorationRegistry;
import tc.oc.pgm.util.chat.Audience;

public class DisguisedManager implements Listener {

  private final List<UUID> disguisedPlayers;
  private final NameDecorationRegistry named;
  private final DisguiseAPI disguiseAPI;
  private final MatchManager matchManager;

  public DisguisedManager() {
    this.disguisedPlayers = new ArrayList<>();
    this.named = PGM.get().getNameDecorationRegistry();
    this.disguiseAPI = PGM.get().getDisguiseAPI();
    this.matchManager = PGM.get().getMatchManager();
  }

  public boolean addDisguisedPlayer(Player player, PlayerDisguise disguise) {
    if (player == null || disguiseAPI == null) return false;
    if (!disguisedPlayers.contains(player.getUniqueId())) {
      disguisedPlayers.add(player.getUniqueId());
      return disguiseAPI.disguise(player, disguise);
    }
    return false;
  }

  public boolean removeDisguisedPlayer(Player player) {
    if (player == null || disguiseAPI == null) return false;
    return (disguisedPlayers.remove(player.getUniqueId()) && disguiseAPI.undisguise(player));
  }

  public List<UUID> getDisguisedPlayers() {
    if (disguiseAPI == null) return null;
    return disguisedPlayers;
  }

  public boolean isDisguised(Player player) {
    if (player == null || disguiseAPI == null) return false;
    return disguisedPlayers.contains(player.getUniqueId()) || disguiseAPI.isDisguised(player);
  }

  private void setDisguise(MatchPlayer matchPlayer, PlayerDisguise disguise, String displayName) {
    matchPlayer.disguise(disguise);
    Player p = matchPlayer.getBukkit();
    matchPlayer
        .getBukkit()
        .setDisplayName(named.getDecoratedNameWithoutFlair(p, matchPlayer.getParty()));
    disguise.setDisplayName(named.getDecoratedNameWithoutFlair(p, matchPlayer.getParty()));
    matchPlayer.getMatch().callEvent(new NameDecorationChangeEvent(matchPlayer.getId()));
    matchPlayer.sendWarning(
            TextComponent.of(("You have been disguised as " + displayName), TextColor.GREEN));
  }

  private void setUndisguise(MatchPlayer matchPlayer) {
    matchPlayer.undisguise();
    matchPlayer.getMatch().callEvent(new NameDecorationChangeEvent(matchPlayer.getId()));
    matchPlayer.sendWarning(TextComponent.of(("You have been revealed")));
  }

  public boolean resolvePlayer(Player player) {
    return (player != null && matchManager.getPlayer(player) != null);
  }

  @Command(
      aliases = {"hideas"},
      desc = "Hides yourself as another player",
      usage = "[a player name]",
      perms = Permissions.STAFF)
  public void hide(Audience audience, CommandSender sender, String name) {
    if (sender instanceof Player && disguiseAPI != null) {
      String displayName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name));
      PlayerDisguise disguise = new PlayerDisguise(name, displayName);
      MatchPlayer matchPlayer = matchManager.getPlayer((Player) sender);
      if (matchPlayer == null) return;
      setDisguise(matchPlayer, disguise, displayName);
      matchPlayer.sendWarning(
          TextComponent.of(("You have been disguised as " + displayName), TextColor.GREEN));
      return;
    }
    audience.sendWarning(TextComponent.of("You are already disguised!", TextColor.RED));
  }

  @Command(
      aliases = {"unhide", "reveal"},
      desc = "Reveals yourself or a player",
      usage = "[player] - defaults to yourself",
      perms = Permissions.STAFF)
  public void unhide(Audience audience, CommandSender sender, @Nullable MatchPlayer target) {
    if (disguiseAPI == null) return;
    if (sender instanceof Player) {
      if (target == null) {
        if (isDisguised((Player) sender)) {
          MatchPlayer matchPlayer = matchManager.getPlayer((Player) sender);
          if (matchPlayer != null) setUndisguise(matchPlayer);
          return;
        }
        audience.sendWarning(TextComponent.of("You are not disguised!", TextColor.RED));
      }
      if (isDisguised(target.getBukkit())) {
        setUndisguise(target);
        return;
      }
      audience.sendWarning(TextComponent.of("Player is not disguised!", TextColor.RED));
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (disguiseAPI == null) return;
    UUID uuid = event.getPlayer().getUniqueId();
    if (isDisguised(event.getPlayer())) disguiseAPI.undisguise(event.getPlayer());
    disguisedPlayers.remove(uuid);
  }

  @EventHandler
  public void onMatchLoad(MatchLoadEvent event) {
    if (disguiseAPI == null) return;
    Collection<MatchPlayer> matchPlayers = event.getMatch().getPlayers();
    for (MatchPlayer matchPlayer : matchPlayers) {
      if (matchPlayer.isDisguised()) matchPlayer.disguise(matchPlayer.getDisguise());
    }
  }

  @EventHandler
  public void onMatchUnload(MatchUnloadEvent event) {
    if (disguiseAPI == null) return;
    Collection<MatchPlayer> matchPlayers = event.getMatch().getPlayers();
    for (MatchPlayer matchPlayer : matchPlayers) {
      if (matchPlayer.isDisguised()) matchPlayer.disguise(matchPlayer.getDisguise());
    }
  }

  @EventHandler
  public void onMatchPhase(MatchPhaseChangeEvent event) {
    if (event.getNewPhase() == MatchPhase.IDLE) {
      Collection<MatchPlayer> matchPlayers = event.getMatch().getPlayers();
      for (MatchPlayer matchPlayer : matchPlayers) {
        if (matchPlayer.isDisguised()) matchPlayer.disguise(matchPlayer.getDisguise());
      }
    }
  }
}
