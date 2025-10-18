package fr.uvsq.cprog.collex;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Test;

public class DnsTUITest {

  /** Utilitaire pour construire une TUI en mémoire. */
  private static class Harness implements AutoCloseable {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final PrintStream out = new PrintStream(baos);
    final DnsTUI tui;

    Harness(String input) {
      this.tui = new DnsTUI(new Scanner(input), out);
    }
    String getOut() { return new String(baos.toByteArray()); }

    @Override public void close() {
      out.flush();
      out.close();
    }
  }

  // -------------------- Constructeur --------------------

  @Test(expected = NullPointerException.class)
  public void constructor_nullScanner_throws() {
    new DnsTUI(null, System.out);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_nullOut_throws() {
    new DnsTUI(new Scanner(""), null);
  }

  // -------------------- parse(...) : lignes invalides --------------------

  @Test
  public void parse_null_returnsErreur() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c = tui.parse(null);
    String res = c.execute(null); // lambda renvoie un message d'erreur
    assertTrue(res.startsWith("ERREUR"));
  }

  @Test
  public void parse_empty_returnsNoOp() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c = tui.parse("   ");
    String res = c.execute(null); // lambda retourne ""
    assertEquals("", res);
  }

  // -------------------- parse(...) : quit/exit --------------------

  @Test
  public void parse_quit_returnsCommandeQuit() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    assertTrue(tui.parse("quit") instanceof CommandeQuit);
    assertTrue(tui.parse("QUIT") instanceof CommandeQuit);
    assertTrue(tui.parse("exit") instanceof CommandeQuit);
  }

  // -------------------- parse(...) : ls --------------------

  @Test
  public void parse_ls_missingArg_isError() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c1 = tui.parse("ls");
    Commande c2 = tui.parse("ls   ");
    String r1 = c1.execute(null);
    String r2 = c2.execute(null);
    assertTrue(r1.startsWith("ERREUR"));
    assertTrue(r2.startsWith("ERREUR"));
  }

  @Test
  public void parse_ls_withDomain_returnsCommandeLs() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c = tui.parse("ls uvq.fr");
    assertTrue(c instanceof CommandeLs);
  }

  @Test
  public void parse_ls_withOptionA_returnsCommandeLs() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c = tui.parse("ls -a example.com");
    assertTrue(c instanceof CommandeLs);
  }

  // -------------------- parse(...) : add --------------------

  @Test
  public void parse_add_ok_returnsCommandeAdd() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    Commande c = tui.parse("add 192.168.1.10 host.example.com");
    assertTrue(c instanceof CommandeAdd);
  }

  @Test
  public void parse_add_badArity_isError() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    String r1 = tui.parse("add 192.168.1.10").execute(null);
    String r2 = tui.parse("add 192.168.1.10 host extra").execute(null);
    assertTrue(r1.startsWith("ERREUR"));
    assertTrue(r2.startsWith("ERREUR"));
  }

  @Test
  public void parse_add_invalidIp_orInvalidName_isErrorMessage() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    String r1 = tui.parse("add 999.999.999.999 host.example.com").execute(null);
    String r2 = tui.parse("add 1.2.3.4 not_a_valid_fqdn").execute(null);
    assertTrue(r1.startsWith("ERREUR"));
    assertTrue(r2.startsWith("ERREUR"));
  }

  // -------------------- parse(...) : recherche IP/Nom --------------------

  @Test
  public void parse_ip_returnsCommandeIp() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    assertTrue(tui.parse("8.8.8.8") instanceof CommandeIp);
    assertTrue(tui.parse("1.2.3.4") instanceof CommandeIp);
  }

  @Test
  public void parse_fqdn_returnsCommandeNom() {
    DnsTUI tui = new DnsTUI(new Scanner(""), System.out);
    assertTrue(tui.parse("www.example.com") instanceof CommandeNom);
    assertTrue(tui.parse("sub.domain.co.uk") instanceof CommandeNom);
  }

  // -------------------- nextCommande() : lecture + parse --------------------

  @Test
  public void nextCommande_readsFromScanner_andParses() {
    try (Harness h = new Harness("quit\n")) {
      Commande c = h.tui.nextCommande();
      assertTrue(c instanceof CommandeQuit);
      // Vérifie le prompt
      assertTrue(h.getOut().startsWith("> "));
    }
  }

  // -------------------- affiche(...) --------------------

  @Test
  public void affiche_nullOrEmpty_printsNothing() {
    try (Harness h = new Harness("")) {
      h.tui.affiche(null);
      h.tui.affiche("");
      assertEquals("", h.getOut());
    }
  }

@Test
public void affiche_text_printsLine() {
  try (Harness h = new Harness("")) {
    h.tui.affiche("Hello");
    String out = h.getOut();
    String[] lines = out.split("\\R"); // split sur \n,\r\n, etc.
    assertTrue("Aucune sortie", lines.length >= 1);
    assertEquals("Hello", lines[0]);
  }
}

}
