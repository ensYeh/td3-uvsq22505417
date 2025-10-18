package fr.uvsq.cprog.collex;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Représente une adresse IPv4 immuable, validée à la construction.
 */
public final class AdresseIP implements Comparable<AdresseIP> {

  /** Expression régulière pour une IPv4 (0.0.0.0 à 255.255.255.255). */
  private static final Pattern IPV4 = Pattern.compile(
      "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\."
          + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");

  /** Chaîne IPv4 originale (non normalisée). */
  private final String value;

  /**
   * Crée une adresse IPv4 validée.
   *
   * @param value chaîne au format IPv4 (ex. {@code "192.168.0.1"})
   * @throws NullPointerException si {@code value} est {@code null}
   * @throws IllegalArgumentException si {@code value} n'est pas une IPv4 valide
   */
  public AdresseIP(String value) {
    this.value = Objects.requireNonNull(value, "value").trim();
    if (!IPV4.matcher(this.value).matches()) {
      throw new IllegalArgumentException("Adresse IP invalide: " + value);
    }
  }

  /** Retourne la chaîne IPv4 telle que fournie. */
  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AdresseIP)) {
      return false;
    }
    AdresseIP that = (AdresseIP) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Compare lexicalement par octets (numériquement, de gauche à droite).
   */
  @Override
  public int compareTo(AdresseIP other) {
    String[] a = this.value.split("\\.");
    String[] b = other.value.split("\\.");
    for (int i = 0; i < 4; i++) {
      int ai = Integer.parseInt(a[i]);
      int bi = Integer.parseInt(b[i]);
      if (ai != bi) {
        return Integer.compare(ai, bi);
      }
    }
    return 0;
  }
}
