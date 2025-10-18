package fr.uvsq.cprog.collex;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * TUI (Text User Interface) pour le mini-système DNS.
 * <p>
 * Parse les lignes de commande utilisateur et retourne une {@link Commande}
 * prête à être exécutée, puis affiche le résultat si nécessaire.
 */
public class DnsTUI {

  /** Motif de validation d'une IPv4. */
  private static final Pattern IPV4 = Pattern.compile(
      "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");

  private final Scanner in;
  private final PrintStream out;

  /**
   * Construit une TUI avec les flux d'entrée/sortie fournis.
   *
   * @param in  scanner utilisé pour lire les lignes de commande
   * @param out flux de sortie pour afficher les messages
   * @throws NullPointerException si {@code in} ou {@code out} est {@code null}
   */
  public DnsTUI(Scanner in, PrintStream out) {
    this.in = Objects.requireNonNull(in, "in");
    this.out = Objects.requireNonNull(out, "out");
  }

  /**
   * Lit une ligne de commande sur {@code in} et retourne la {@link Commande} correspondante.
   *
   * @return la commande interprétée
   */
  public Commande nextCommande() {
    out.print("> ");
    String line = in.nextLine().trim();
    return parse(line);
  }

  /**
   * Parse une ligne (sans lire depuis stdin) pour produire une {@link Commande}.
   * Utile pour les tests unitaires.
   *
   * @param line ligne de commande à parser
   * @return la commande correspondante ; une commande no-op pour ligne vide
   */
  public Commande parse(String line) {
    if (line == null) {
      return l -> "ERREUR : Commande invalide";
    }

    line = line.trim();
    if (line.isEmpty()) {
      return l -> "";
    }

    if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
      return new CommandeQuit();
    }

    if (line.startsWith("ls ")) {
      String rest = line.substring(3).trim();
      boolean sortByAddress = false;
      if (rest.startsWith("-a")) {
        sortByAddress = true;
        rest = rest.substring(2).trim();
      }
      if (rest.isEmpty()) {
        return l -> "ERREUR : Commande invalide";
      }
      return new CommandeLs(rest, sortByAddress);
    }

    if (line.startsWith("add ")) {
      String[] parts = line.split("\\s+");
      if (parts.length != 3) {
        return l -> "ERREUR : Commande invalide";
      }
      try {
        return new CommandeAdd(new AdresseIP(parts[1]), new NomMachine(parts[2]));
      } catch (IllegalArgumentException e) {
        return l -> "ERREUR : " + e.getMessage();
      }
    }

    if (IPV4.matcher(line).matches()) {
      // Ligne = IP → commande de recherche FQDN par IP
      return new CommandeIp(new AdresseIP(line));
    }

    if (line.contains(".")) {
      // Ligne = FQDN → commande de recherche IP par nom
      try {
        return new CommandeNom(new NomMachine(line));
      } catch (IllegalArgumentException e) {
        return l -> "ERREUR : " + e.getMessage();
      }
    }

    return l -> "ERREUR : Commande invalide";
  }

  /**
   * Affiche un texte sur {@code out} s'il est non vide.
   *
   * @param texte message à afficher
   */
  public void affiche(String texte) {
    if (texte != null && !texte.isEmpty()) {
      out.println(texte);
    }
  }
}
