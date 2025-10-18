package fr.uvsq.cprog.collex;

import java.util.Objects;

/** Représente un nom qualifié de machine (FQDN). */
public final class NomMachine implements Comparable<NomMachine> {
  private final String fqdn;
  private final String machine;
  private final String domaine;

  public NomMachine(String fqdn) {
    this.fqdn = Objects.requireNonNull(fqdn, "fqdn").trim();
    int idx = this.fqdn.indexOf('.');
    if (idx <= 0 || idx == this.fqdn.length() - 1) {
      throw new IllegalArgumentException("Nom de machine invalide: " + fqdn);
    }
    this.machine = this.fqdn.substring(0, idx);
    this.domaine = this.fqdn.substring(idx + 1);
  }

  public String getMachine() { return machine; }
  public String getDomaine() { return domaine; }
  public String getFqdn() { return fqdn; }

  @Override public String toString() { return fqdn; }

  @Override public boolean equals(Object o) {
    return (o instanceof NomMachine) && fqdn.equals(((NomMachine) o).fqdn);
  }

  @Override public int hashCode() { return fqdn.hashCode(); }

  @Override public int compareTo(NomMachine other) { return fqdn.compareTo(other.fqdn); }
}
