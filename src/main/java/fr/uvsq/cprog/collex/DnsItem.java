package fr.uvsq.cprog.collex;

import java.util.Objects;

/**
 * Représente une entrée DNS, c’est-à-dire une association entre
 * un nom de machine ({@link NomMachine}) et une adresse IP ({@link AdresseIP}).
 *
 * <p>Exemple :
 * <pre>{@code
 *   DnsItem item = new DnsItem(
 *       new NomMachine("serveur1.example.com"),
 *       new AdresseIP("192.168.0.10"));
 * }</pre>
 */
public final class DnsItem {

  /** Nom de machine associé. */
  private final NomMachine nom;

  /** Adresse IP correspondante. */
  private final AdresseIP ip;

  /**
   * Crée une nouvelle entrée DNS (nom + IP).
   *
   * @param nom le nom de machine (non {@code null})
   * @param ip l’adresse IP correspondante (non {@code null})
   * @throws NullPointerException si {@code nom} ou {@code ip} est {@code null}
   */
  public DnsItem(NomMachine nom, AdresseIP ip) {
    this.nom = Objects.requireNonNull(nom, "nom");
    this.ip = Objects.requireNonNull(ip, "ip");
  }

  /** Retourne le nom de machine. */
  public NomMachine getNom() {
    return nom;
  }

  /** Retourne l’adresse IP. */
  public AdresseIP getIp() {
    return ip;
  }

  @Override
  public String toString() {
    return ip + " " + nom;
  }
}
