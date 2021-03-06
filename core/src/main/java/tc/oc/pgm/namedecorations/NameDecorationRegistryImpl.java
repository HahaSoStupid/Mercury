package tc.oc.pgm.namedecorations;

import java.util.UUID;
import javax.annotation.Nullable;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.event.NameDecorationChangeEvent;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.PlayerJoinMatchEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.util.text.TextFormatter;

public class NameDecorationRegistryImpl implements NameDecorationRegistry, Listener {

  private NameDecorationProvider provider;

  public NameDecorationRegistryImpl(@Nullable NameDecorationProvider provider) {
    this.provider = provider;
  }

  @EventHandler
  public void onJoinMatch(PlayerJoinMatchEvent event) {
    Player player = event.getPlayer().getBukkit();
    player.setDisplayName(getDecoratedName(player, event.getNewParty()));
  }

  @EventHandler
  public void onPartyChange(PlayerPartyChangeEvent event) {
    Player player = event.getPlayer().getBukkit();
    player.setDisplayName(getDecoratedName(player, event.getNewParty()));
  }

  @EventHandler
  public void onNameDecorationChange(NameDecorationChangeEvent event) {
    if (event.getUUID() == null) return;

    final Player player = Bukkit.getPlayer(event.getUUID());
    final MatchPlayer matchPlayer = getPlayer(player);
    if (matchPlayer == null) return;
    matchPlayer.getBukkit().setDisplayName(getDecoratedName(player, matchPlayer.getParty()));
  }

  @Override
  public String getDecoratedName(Player player, Party party) {
    MatchPlayer matchPlayer = getPlayer(player);
    String name = matchPlayer == null ? player.getName() : matchPlayer.getNameLegacy();
    return getPrefix(player.getUniqueId())
        + (party == null ? ChatColor.RESET : party.getColor())
        + name
        + getSuffix(player.getUniqueId())
        + ChatColor.WHITE;
  }

  @Override
  public String getDecoratedNameWithoutFlair(Player player, Party party) {
    MatchPlayer matchPlayer = getPlayer(player);
    String name = matchPlayer == null ? player.getName() : matchPlayer.getNameLegacy();
    return (party == null ? ChatColor.RESET : party.getColor()) + name + ChatColor.WHITE;
  }

  @Override
  public String getSuffix(Player player) {
    return getSuffix(player.getUniqueId());
  }

  @Override
  public String getMessageColor(Player player) {
    return getMessageColor(player.getUniqueId());
  }

  @Override
  public Component getDecoratedNameComponent(Player player, Party party) {
    MatchPlayer matchPlayer = getPlayer(player);
    String name = matchPlayer == null ? player.getName() : matchPlayer.getNameLegacy();
    return TextComponent.builder()
        .append(getPrefixComponent(player.getUniqueId()))
        .append(name, party == null ? TextColor.WHITE : TextFormatter.convert(party.getColor()))
        .append(getSuffixComponent(player.getUniqueId()))
        .build();
  }

  public String getPrefix(UUID uuid) {
    return provider != null ? provider.getPrefix(uuid) : "";
  }

  public String getSuffix(UUID uuid) {
    return provider != null ? provider.getSuffix(uuid) : "";
  }

  public String getMessageColor(UUID uuid) {
    return provider != null ? provider.getMessageColor(uuid) : "&r";
  }

  public Component getPrefixComponent(UUID uuid) {
    return provider != null ? provider.getPrefixComponent(uuid) : TextComponent.empty();
  }

  public Component getSuffixComponent(UUID uuid) {
    return provider != null ? provider.getSuffixComponent(uuid) : TextComponent.empty();
  }

  private MatchPlayer getPlayer(Player player) {
    return PGM.get().getMatchManager().getPlayer(player);
  }

  @Override
  public void setProvider(@Nullable NameDecorationProvider provider) {
    this.provider = provider;
  }

  @Nullable
  @Override
  public NameDecorationProvider getProvider() {
    return provider;
  }
}
