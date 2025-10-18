package fr.uvsq.cprog.collex;

public final class CommandeNom implements Commande {
  private final NomMachine nom;

  public CommandeNom(NomMachine nom) {
    this.nom = nom;
  }

  @Override
  public String execute(Dns dns) {
    DnsItem item = dns.getItem(nom);
    return (item == null) ? "ERREUR : Élément introuvable" : item.getIp().value();
  }
}
