package fr.uvsq.cprog.collex;

/**
 * Commande permettant d’ajouter une nouvelle entrée DNS (association IP ↔ FQDN).
 * <p>
 * Exemple d’utilisation :
 * <pre>
 *   Commande cmd = new CommandeAdd(
 *       new AdresseIP("192.168.0.10"),
 *       new NomMachine("serveur1.example.com"));
 *   cmd.execute(dns);
 * </pre>
 */
public final class CommandeAdd implements Commande {

  /** Adresse IP à ajouter. */
  private final AdresseIP ip;

  /** Nom de machine associé à l’adresse IP. */
  private final NomMachine nom;

  /**
   * Crée une commande d’ajout d’entrée DNS.
   *
   * @param ip adresse IP à associer
   * @param nom nom de machine à associer
   */
  public CommandeAdd(AdresseIP ip, NomMachine nom) {
    this.ip = ip;
    this.nom = nom;
  }

  /**
   * Exécute l’ajout de l’entrée dans la base DNS.
   *
   * @param dns instance du système DNS
   * @return une chaîne vide si l’opération s’est bien déroulée
   * @throws IllegalStateException si le nom ou l’adresse IP existent déjà
   */
  @Override
  public String execute(Dns dns) {
    dns.addItem(ip, nom);
    return ""; // pas de sortie particulière si OK
  }
}
