package tc.oc.pgm.command;

import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Default;
import javax.annotation.Nullable;
import net.kyori.text.TextComponent;
import net.kyori.text.TranslatableComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.Permissions;
import tc.oc.pgm.api.coins.Coins;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

public final class CoinCommand {
  @Command(
      aliases = {"coins"},
      desc = "Show how many coins you have",
      usage = "[player] - defaults to own")
  public void coins(MatchPlayer player, CommandSender sender, @Nullable Player affected) {
    if (affected != null) {
      MatchPlayer affect = player.getMatch().getPlayer(affected);
      if (affect != null)
        player.sendMessage(
            TranslatableComponent.of(
                "coins.get.other",
                TextColor.YELLOW,
                TextComponent.of(affect.getCoins().getCoins())));
    } else {
      player.sendMessage(
          TranslatableComponent.of(
              "coins.get",
              TextColor.YELLOW,
              TextComponent.of(player.getCoins().getCoins(), TextColor.GOLD)));
    }
  }

  @Command(
      aliases = {"setcoins"},
      desc = "Sets the amount of coins",
      usage = "[player] [amount]",
      perms = Permissions.STAFF)
  public void setcoins(
      MatchPlayer player, CommandSender sender, Player affected, @Default("1") int amount) {
    MatchPlayer a;
    if (affected == null) {
      a = player;
    } else {
      Match match = player.getMatch();
      a = match.getPlayer(affected);
      if (a == null) {
        player.sendMessage("Invalid player!");
        return;
      }
    }
    Coins coins = a.getCoins();
    coins.setCoins(amount);
    player.sendMessage(
        TranslatableComponent.of(
            "coins.set", TextColor.YELLOW, a.getName(), TextComponent.of(coins.getCoins())));
    a.sendMessage(
        TranslatableComponent.of(
            "death.getcoins",
            TextColor.YELLOW,
            TextComponent.of(coins.getCoins(), TextColor.GOLD)));
  }

  @Command(
      aliases = {"givecoins"},
      desc = "Gives coins to player",
      usage = "[player] [amount]",
      perms = Permissions.STAFF)
  public void givecoins(
      MatchPlayer player,
      CommandSender sender,
      @Nullable Player affected,
      @Default("1") int amount) {
    MatchPlayer a;
    if (affected == null) {
      a = player;
    } else {
      Match match = player.getMatch();
      a = match.getPlayer(affected);
    }
    if (a != null) {
      Coins coins = a.getCoins();
      coins.addCoins(amount);
      player.sendMessage(
          TranslatableComponent.of(
              "coins.set", TextColor.YELLOW, a.getName(), TextComponent.of(coins.getCoins())));
      a.sendMessage(
          TranslatableComponent.of(
              "death.getcoins", TextColor.YELLOW, TextComponent.of(amount, TextColor.GOLD)));
    }
  }
}
