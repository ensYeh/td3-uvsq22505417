package fr.uvsq.cprog.collex;
import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DnsTest {
  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  private Path makeDb(String content) throws IOException {
    Path f = tmp.newFile("db.txt").toPath();
    Files.write(f, content.getBytes());
    return f;
  }

  @Test public void chargement_et_recherches() throws Exception {
    Path db = makeDb("www.uvsq.fr 193.51.31.90\n");
    Dns dns = new Dns(db);
    assertEquals("193.51.31.90",
        dns.getItem(new NomMachine("www.uvsq.fr")).getIp().value());
    assertEquals("www.uvsq.fr",
        dns.getItem(new AdresseIP("193.51.31.90")).getNom().getFqdn());
  }

  @Test public void ajout_et_persistance() throws Exception {
    Path db = makeDb("");
    Dns dns = new Dns(db);
    dns.addItem(new AdresseIP("1.2.3.4"), new NomMachine("a.b"));
    List<String> lines = Files.readAllLines(db);
    assertFalse(lines.isEmpty());
  }

  @Test(expected = IllegalStateException.class)
  public void doublon_nom() throws Exception {
    Path db = makeDb("x.d 1.1.1.1\n");
    Dns dns = new Dns(db);
    dns.addItem(new AdresseIP("2.2.2.2"), new NomMachine("x.d"));
  }

  @Test(expected = IllegalStateException.class)
  public void doublon_ip() throws Exception {
    Path db = makeDb("x.d 1.1.1.1\n");
    Dns dns = new Dns(db);
    dns.addItem(new AdresseIP("1.1.1.1"), new NomMachine("y.d"));
  }
}
