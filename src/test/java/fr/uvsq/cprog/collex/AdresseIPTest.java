package fr.uvsq.cprog.collex;

import org.junit.Test;

public class AdresseIPTest {
  @Test public void ok_min_max() {
    new AdresseIP("0.0.0.0");
    new AdresseIP("255.255.255.255");
  }
  @Test(expected = IllegalArgumentException.class) public void ko_256() {
    new AdresseIP("256.0.0.1");
  }
  @Test(expected = IllegalArgumentException.class) public void ko_forme() {
    new AdresseIP("1.2.3");
  }
}
