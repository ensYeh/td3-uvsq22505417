package fr.uvsq.cprog.collex;

/**
 * Commande permettant de rechercher un nom de machine (FQDN)
 * à partir d'une adresse IP.
 */
public final class CommandeIp implements Commande {

  /** Adresse IP à rechercher. */
  private final AdresseIP ip;

  /**
   * Crée une commande de recherche par adresse IP.
   *
   * @param ip adresse IP cible
   */
  public CommandeIp(AdresseIP ip) {
    this.ip = ip;
  }

  /**
   * Exécute la recherche sur le système DNS.
   *
   * @param dns instance du système DNS
   * @return le FQDN associé à l’adresse IP, ou un message d’erreur si introuvable
   */
  @Override
  public String execute(Dns dns) {
    DnsItem item = dns.getItem(ip);
    if (item == null) {
      return "ERREUR : Élément introuvable";
    }
    return item.getNom().getFqdn();
  }
}
