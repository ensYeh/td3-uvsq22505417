package fr.uvsq.cprog.collex;

public interface Commande {

  /**
   * Exécute la commande sur le système DNS.
   *
   * @param dns instance du système DNS sur laquelle la commande agit
   * @return le message à afficher après exécution (ou {@code ""} si aucun)
   */
  String execute(Dns dns);
}
