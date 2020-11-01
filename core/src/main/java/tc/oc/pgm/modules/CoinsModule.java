package tc.oc.pgm.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.kyori.text.TextComponent;
import net.kyori.text.TranslatableComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchModule;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.controlpoint.events.ControllerChangeEvent;
import tc.oc.pgm.core.Core;
import tc.oc.pgm.core.CoreLeakEvent;
import tc.oc.pgm.destroyable.Destroyable;
import tc.oc.pgm.destroyable.DestroyableDestroyedEvent;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.flag.event.FlagCaptureEvent;
import tc.oc.pgm.goals.Contribution;
import tc.oc.pgm.wool.PlayerWoolPlaceEvent;

@ListenerScope(MatchScope.RUNNING)
public class CoinsModule implements MatchModule, Listener {

  private final Match match;

  public CoinsModule(Match match) {
    this.match = match;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDeath(MatchPlayerDeathEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    if (event.getKiller() == event.getVictim().getParticipantState() || event.getKiller() == null) {
      return;
    }
    Optional<MatchPlayer> player = event.getKiller().getPlayer();
    if (!player.isPresent()) {
      return;
    }
    MatchPlayer killer = player.get();
    int random = (int) Math.floor(Math.random() * 10);
    addCoins(killer, random);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDestroy(DestroyableDestroyedEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    Destroyable destroyable = event.getDestroyable();
    List<? extends Contribution> sort = new ArrayList<>(destroyable.getContributions());
    for (Contribution entry : sort) {
      Optional<MatchPlayer> player = entry.getPlayerState().getPlayer();
      if (!player.isPresent()) {
        continue;
      }
      MatchPlayer destroyer = player.get();
      int random = (int) Math.floor(Math.random() * 10) + 20;
      addCoins(destroyer, random);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlace(PlayerWoolPlaceEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    Optional<MatchPlayer> player = event.getPlayer().getPlayer();
    if (!player.isPresent()) {
      return;
    }
    MatchPlayer placer = player.get();
    int random = (int) Math.floor(Math.random() * 10) + 20;
    addCoins(placer, random);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onLeak(CoreLeakEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    Core core = event.getCore();
    List<? extends Contribution> sort = new ArrayList<>(core.getContributions());
    for (Contribution entry : sort) {
      Optional<MatchPlayer> player = entry.getPlayerState().getPlayer();
      if (!player.isPresent()) {
        return;
      }
      MatchPlayer leaker = player.get();
      int random = (int) Math.floor(Math.random() * 10) + 20;
      addCoins(leaker, random);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onCapturePoint(ControllerChangeEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    Competitor competitor = event.getNewController();
    if (competitor == null) {
      return;
    }
    Collection<MatchPlayer> players = competitor.getPlayers();
    for (MatchPlayer player : players) {
      if (player == null) {
        continue;
      }
      int random = (int) Math.floor(Math.random() * 10) + 20;
      addCoins(player, random);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onFlagCapture(FlagCaptureEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    MatchPlayer capturer = event.getCarrier();
    if (capturer == null) {
      return;
    }
    int random = (int) Math.floor(Math.random() * 10) + 20;
    addCoins(capturer, random);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onWin(MatchFinishEvent event) {
    if (event.getMatch() != match) {
      return;
    }
    Collection<Competitor> winners = event.getWinners();
    Collection<Competitor> competitors = event.getMatch().getCompetitors();
    for (Competitor winner : winners) {
      for (Competitor all : competitors) {
        Collection<MatchPlayer> winnerPlayers = winner.getPlayers();
        Collection<MatchPlayer> allPlayers = all.getPlayers();
        for (MatchPlayer winnerPlayer : winnerPlayers) {
          for (MatchPlayer player : allPlayers) {
            if (winnerPlayer == null) {
              continue;
            }
            if (winnerPlayer == player) {
              int random = (int) Math.floor(Math.random() * 10) + 30;
              addCoins(winnerPlayer, random);
              continue;
            }
            int random = (int) Math.floor(Math.random() * 10);
            addCoins(player, random);
          }
        }
      }
    }
  }

  public void addCoins(MatchPlayer player, int amount) {
    player.getCoins().addCoins(amount);
    TextComponent message =
        TextComponent.builder()
            .append("    ")
            .append(
                TranslatableComponent.of(
                    "death.getcoins", TextColor.YELLOW, TextComponent.of(amount, TextColor.GOLD)))
            .build();
    player.sendMessage(message);
  }
}
