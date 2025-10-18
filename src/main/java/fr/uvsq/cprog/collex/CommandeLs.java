package fr.uvsq.cprog.collex;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Commande permettant de lister les machines d’un domaine.
 * <p>
 * Exemple d’utilisation :
 * <pre>
 *   Commande cmd = new CommandeLs("example.com", true);
 *   System.out.println(cmd.execute(dns));
 * </pre>
 */
public final class CommandeLs implements Commande {

  /** Domaine à lister. */
  private final String domaine;

  /** Indique si le tri doit se faire par adresse IP (sinon par nom). */
  private final boolean sortByAddress;

  /**
   * Crée une commande de listage pour un domaine.
   *
   * @param domaine domaine à lister (ex. {@code "example.com"})
   * @param sortByAddress {@code true} pour trier par adresse IP, {@code false} pour trier par nom
   */
  public CommandeLs(String domaine, boolean sortByAddress) {
    this.domaine = domaine;
    this.sortByAddress = sortByAddress;
  }

  /**
   * Exécute la commande de listage sur le système DNS.
   *
   * @param dns instance du système DNS
   * @return une chaîne contenant toutes les entrées du domaine, une par ligne
   */
  @Override
  public String execute(Dns dns) {
    List<DnsItem> items = dns.getItems(domaine);

    // Tri éventuel par adresse IP
    if (sortByAddress) {
      items = items.stream()
          .sorted(Comparator.comparing(i -> i.getIp()))
          .collect(Collectors.toList());
    }

    // Formatage de la sortie
    return items.stream()
        .map(i -> i.getIp().value() + " " + i.getNom().getFqdn())
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
