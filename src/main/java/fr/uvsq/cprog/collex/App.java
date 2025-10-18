package fr.uvsq.cprog.collex;
import java.util.Scanner;

/**
 * Application console du mini-système DNS.
 */
public final class App {

  public static void main(String[] args) {
    new App().run();
  }

  /** Boucle interactive. */
  public void run() {
    Dns dns = new Dns();
    try (Scanner sc = new Scanner(System.in)) {
      DnsTUI tui = new DnsTUI(sc, System.out);
      while (true) {
        try {
          Commande c = tui.nextCommande();
          String out = c.execute(dns);
          tui.affiche(out);
        } catch (QuitException q) {
          break;
        } catch (Exception e) {
          tui.affiche("ERREUR : " + e.getMessage());
        }
      }
    }
  }
}
