package tc.oc.pgm.api.disguises;

import de.robingrether.idisguise.disguise.PlayerDisguise;
import org.bukkit.entity.Player;

public interface Disguised {
  boolean isDisguised();

  boolean disguise(PlayerDisguise disguise);

  boolean undisguise();

  PlayerDisguise getDisguise();

  Player getPlayer();
}
