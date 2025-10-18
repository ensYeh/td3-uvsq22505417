package fr.uvsq.cprog.collex;
import org.junit.Test;
import static org.junit.Assert.*;

public class NomMachineTest {

  @Test
  public void testNomValide() {
    // Un FQDN valide doit contenir un point : machine.domaine
    NomMachine nom = new NomMachine("serveur01.exemple");
    assertEquals("serveur01", nom.getMachine());
    assertEquals("exemple", nom.getDomaine());
    assertEquals("serveur01.exemple", nom.getFqdn());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNomInvalideSansPoint() {
    new NomMachine("serveur01"); // doit lever IllegalArgumentException
  }

  @Test(expected = NullPointerException.class)
  public void testNomNull() {
    new NomMachine(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNomAvecPointEnFin() {
    new NomMachine("serveur01."); // invalide: rien après le point
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNomAvecPointEnDebut() {
    new NomMachine(".exemple"); // invalide: rien avant le point
  }
}
