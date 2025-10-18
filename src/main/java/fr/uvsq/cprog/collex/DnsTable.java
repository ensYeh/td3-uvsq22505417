package fr.uvsq.cprog.collex;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Table DNS : gère les associations entre noms de machines (FQDN) et adresses IP.
 * <p>
 * Les données sont stockées dans un fichier texte où chaque ligne suit le format :
 * <pre>
 *   fqdn adresse_ip
 * </pre>
 * Exemple :
 * <pre>
 *   serveur1.example.com 192.168.0.10
 *   serveur2.example.com 192.168.0.11
 * </pre>
 */
public final class DnsTable {

  /** Fichier contenant la base DNS. */
  private final Path dbFile;

  /** Indexation par nom de machine (pour recherche par FQDN). */
  private final Map<NomMachine, AdresseIP> byName = new HashMap<>();

  /** Indexation par adresse IP (pour recherche inverse). */
  private final Map<AdresseIP, NomMachine> byIp = new HashMap<>();

  /**
   * Construit une table DNS à partir d’un fichier de base.
   * Si le fichier n’existe pas, il est créé vide.
   *
   * @param dbFile chemin du fichier de base (ex. {@code Path.of("dns.txt")})
   * @throws IOException si une erreur de lecture survient
   */
  public DnsTable(Path dbFile) throws IOException {
    this.dbFile = Objects.requireNonNull(dbFile, "dbFile");
    if (!Files.exists(dbFile)) {
      Files.createFile(dbFile);
    }
    load();
  }

  /**
   * Charge les entrées DNS depuis le fichier.
   */
  private void load() throws IOException {
    List<String> lines = Files.readAllLines(dbFile, StandardCharsets.UTF_8);
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.isEmpty() || trimmed.startsWith("#")) {
        continue; // commentaire ou ligne vide
      }
      String[] parts = trimmed.split("\\s+");
      if (parts.length != 2) {
        throw new IllegalArgumentException("Ligne invalide : " + line);
      }
      NomMachine nom = new NomMachine(parts[0]);
      AdresseIP ip = new AdresseIP(parts[1]);
      insert(nom, ip);
    }
  }

  /**
   * Enregistre les entrées actuelles dans le fichier.
   * Les lignes sont triées par nom FQDN pour garantir la stabilité.
   */
  private void persist() {
    try {
      List<DnsItem> items = new ArrayList<>();
      for (Map.Entry<NomMachine, AdresseIP> e : byName.entrySet()) {
        items.add(new DnsItem(e.getKey(), e.getValue()));
      }

      // Tri par nom de machine
      items.sort(Comparator.comparing(i -> i.getNom().getFqdn()));

      List<String> out = new ArrayList<>();
      for (DnsItem it : items) {
        out.add(it.getNom().getFqdn() + " " + it.getIp().value());
      }

      Files.write(dbFile, out, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Erreur d'écriture de la base : " + e.getMessage(), e);
    }
  }

  /**
   * Insère silencieusement une paire nom/IP dans les deux index.
   */
  private void insert(NomMachine nom, AdresseIP ip) {
    byName.put(nom, ip);
    byIp.put(ip, nom);
  }

  /** Recherche une entrée par adresse IP. */
  public DnsItem getItem(AdresseIP ip) {
    NomMachine nom = byIp.get(ip);
    return (nom == null) ? null : new DnsItem(nom, ip);
  }

  /** Recherche une entrée par nom de machine (FQDN). */
  public DnsItem getItem(NomMachine nom) {
    AdresseIP ip = byName.get(nom);
    return (ip == null) ? null : new DnsItem(nom, ip);
  }

  /**
   * Retourne la liste des entrées appartenant à un domaine donné.
   *
   * @param domaine nom du domaine (ex. {@code "example.com"})
   * @return liste immuable triée par nom de machine
   */
  public List<DnsItem> getItems(String domaine) {
    List<DnsItem> res = new ArrayList<>();
    for (Map.Entry<NomMachine, AdresseIP> e : byName.entrySet()) {
      if (e.getKey().getDomaine().equals(domaine)) {
        res.add(new DnsItem(e.getKey(), e.getValue()));
      }
    }
    res.sort(Comparator.comparing(i -> i.getNom().getFqdn()));
    return Collections.unmodifiableList(res);
  }

  /**
   * Ajoute une nouvelle entrée (nom + IP) si elles n’existent pas déjà.
   * Persiste immédiatement dans le fichier.
   *
   * @param ip adresse IP à associer
   * @param nom nom de machine à associer
   * @throws IllegalStateException si le nom ou l’adresse IP existent déjà
   */
  public void addItem(AdresseIP ip, NomMachine nom) {
    if (byName.containsKey(nom)) {
      throw new IllegalStateException("Le nom de machine existe déjà !");
    }
    if (byIp.containsKey(ip)) {
      throw new IllegalStateException("L'adresse IP existe déjà !");
    }
    insert(nom, ip);
    persist();
  }
}
