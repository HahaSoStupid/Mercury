package tc.oc.pgm.db;

import java.util.UUID;
import org.bukkit.Bukkit;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.coins.Coins;

public class CoinsImpl implements Coins {

  private final UUID id;
  private long coins;

  CoinsImpl(UUID id, long amount) {
    this.id = id;
    this.coins = amount;
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public long getCoins() {
    return coins;
  }

  // @Override
  public void setCoins(long amount) {
    this.coins = amount;
    if (PGM.get().getEconomy().isEnabled()
        && !PGM.get().getEconomy().hasAccount(Bukkit.getOfflinePlayer(id))) {
      PGM.get().getEconomy().createPlayerAccount(Bukkit.getOfflinePlayer(id));
      PGM.get().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(id), coins);
    }
  }

  @Override
  public void addCoins(long amount) {
    setCoins(this.coins + amount);
    if (PGM.get().getEconomy().isEnabled())
      PGM.get().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(id), amount);
  }

  @Override
  public void removeCoins(long amount) {
    setCoins(this.coins - amount);
    if (PGM.get().getEconomy().isEnabled())
      PGM.get().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(id), amount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Coins)) return false;
    return getId().equals(((Coins) o).getId());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id != null ? ("{ id=" + id.toString() + ", coins=" + coins + " }") : "{}";
  }
}
