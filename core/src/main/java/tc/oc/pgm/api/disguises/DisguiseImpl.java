package tc.oc.pgm.api.disguises;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;

public class DisguiseImpl implements Disguised {

  private final UUID uuid;
  private final PlayerDisguise disguise;
  private final DisguiseAPI disguiseAPI;

  public DisguiseImpl(UUID uuid, PlayerDisguise disguise) {
    this.uuid = uuid;
    this.disguise = disguise;
    this.disguiseAPI = PGM.get().getDisguiseAPI();
  }

  @Override
  public boolean isDisguised() {
    return disguiseAPI != null && disguiseAPI.isDisguised(Bukkit.getPlayer(uuid));
  }

  @Override
  public boolean disguise(PlayerDisguise disguise) {
    return disguiseAPI != null && disguiseAPI.disguise(Bukkit.getPlayer(uuid), disguise);
  }

  @Override
  public boolean undisguise() {
    return false;
  }

  @Override
  public PlayerDisguise getDisguise() {
    return disguise;
  }

  @Override
  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }
}
