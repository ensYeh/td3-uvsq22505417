package fr.uvsq.cprog.collex;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Tests unitaires pour la classe {@link CommandeLs}.
 */
public class CommandeLsTest {

  /** Crée rapidement un DnsItem. */
  private static DnsItem item(String ip, String fqdn) {
    return new DnsItem(new NomMachine(fqdn), new AdresseIP(ip));
  }

  /**
   * Double de test : simule un objet Dns renvoyant une liste d'items connue.
   */
  private static Dns dnsReturning(String expectedDomain, List<DnsItem> items) {
    return new Dns() {
      @Override
      public List<DnsItem> getItems(String domaine) {
        if (!expectedDomain.equals(domaine)) {
          throw new AssertionError("Domaine inattendu : " + domaine);
        }
        return items;
      }
    };
  }

  /**
   * Vérifie que la commande trie bien par adresse IP quand sortByAddress = true.
   */
  @Test
  public void execute_sortByAddress_true_trieParAdresseIP() {
    List<DnsItem> data = Arrays.asList(
        item("193.51.31.154", "poste.uvsq.fr"),
        item("193.51.25.12", "ecampus.uvsq.fr"),
        item("193.51.31.90", "www.uvsq.fr"),
        item("193.51.25.24", "pikachu.uvsq.fr")
    );
    Dns dns = dnsReturning("uvsq.fr", data);

    Commande cmd = new CommandeLs("uvsq.fr", true);
    String out = cmd.execute(dns);

    String nl = System.lineSeparator();
    String expected =
        "193.51.25.12 ecampus.uvsq.fr" + nl +
        "193.51.25.24 pikachu.uvsq.fr" + nl +
        "193.51.31.90 www.uvsq.fr" + nl +
        "193.51.31.154 poste.uvsq.fr";

    assertThat(out, is(expected));
  }

  /**
   * Vérifie que la commande conserve l'ordre fourni par Dns quand sortByAddress = false.
   */
  @Test
  public void execute_sortByAddress_false_conserveOrdreDns() {
    List<DnsItem> nameSorted = Arrays.asList(
        item("193.51.25.12", "ecampus.uvsq.fr"),
        item("193.51.31.154", "poste.uvsq.fr"),
        item("193.51.31.90", "www.uvsq.fr")
    );
    Dns dns = dnsReturning("uvsq.fr", nameSorted);

    Commande cmd = new CommandeLs("uvsq.fr", false);
    String out = cmd.execute(dns);

    String nl = System.lineSeparator();
    String expected =
        "193.51.25.12 ecampus.uvsq.fr" + nl +
        "193.51.31.154 poste.uvsq.fr" + nl +
        "193.51.31.90 www.uvsq.fr";

    assertThat(out, is(expected));
  }

  /**
   * Vérifie que la commande retourne une chaîne vide pour un domaine sans machine.
   */
  @Test
  public void execute_domaineVide_retourneChaineVide() {
    Dns dns = dnsReturning("vide.fr", Collections.emptyList());

    Commande cmd = new CommandeLs("vide.fr", false);
    String out = cmd.execute(dns);

    assertThat(out, is(""));
  }

  /**
   * Vérifie le format de sortie : "ip fqdn" sur chaque ligne.
   */
  @Test
  public void execute_formatSortie_ipEspaceFqdn_uneLigneParItem() {
    List<DnsItem> data = Arrays.asList(
        item("10.0.0.1", "a.ex"),
        item("10.0.0.2", "b.ex")
    );
    Dns dns = dnsReturning("ex", data);

    Commande cmd = new CommandeLs("ex", false);
    String out = cmd.execute(dns);

    String nl = System.lineSeparator();
    String expected =
        "10.0.0.1 a.ex" + nl +
        "10.0.0.2 b.ex";

    assertThat(out, is(expected));
  }

  /**
   * Vérifie que le tri est numérique (pas lexicographique) sur les adresses IP.
   * Exemple : 10.0.0.9 < 10.0.0.10
   */
  @Test
  public void execute_triNumerique_etPasLexico_exemple_9_vs_10() {
    List<DnsItem> data = Arrays.asList(
        item("10.0.0.10", "ten.ex"),
        item("10.0.0.9", "nine.ex")
    );
    Dns dns = dnsReturning("ex", data);

    Commande cmd = new CommandeLs("ex", true);
    String out = cmd.execute(dns);

    String nl = System.lineSeparator();
    String expected =
        "10.0.0.9 nine.ex" + nl +
        "10.0.0.10 ten.ex";

    assertThat(out, is(expected));
  }
}
