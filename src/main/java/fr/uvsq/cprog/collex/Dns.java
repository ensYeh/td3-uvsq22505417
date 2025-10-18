package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Dns {
  private final Map<NomMachine, AdresseIP> byName = new LinkedHashMap<>();
  private final Map<AdresseIP, NomMachine> byIp = new LinkedHashMap<>();
  private final Path dbFile;

  public Dns() {
    try {
      Properties p = new Properties();
      try (InputStream in =
               getClass().getClassLoader().getResourceAsStream("dns.properties")) {
        if (in == null) {
          throw new IllegalStateException("dns.properties introuvable");
        }
        p.load(in);
      }
      String path = p.getProperty("db.file");
      if (path == null) {
        throw new IllegalStateException("Propriété db.file absente");
      }
      this.dbFile = Paths.get(path);
      load();
    } catch (IOException e) {
      throw new IllegalStateException("Erreur de chargement: " + e.getMessage(), e);
    }
  }

  public Dns(Path dbFile) {
    this.dbFile = Objects.requireNonNull(dbFile);
    try {
      if (Files.notExists(dbFile)) {
        Path parent = dbFile.getParent();
        if (parent != null && Files.notExists(parent)) {
          Files.createDirectories(parent);
        }
        Files.createFile(dbFile);
      }
      load();
    } catch (IOException e) {
      throw new IllegalStateException("Erreur init base test: " + e.getMessage(), e);
    }
  }

  private void load() throws IOException {
    if (Files.notExists(dbFile)) {
      return;
    }
    List<String> lines = Files.readAllLines(dbFile, StandardCharsets.UTF_8);
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.isEmpty() || trimmed.startsWith("#")) {
        continue;
      }
      String[] parts = trimmed.split("\\s+");
      if (parts.length != 2) {
        continue;
      }
      NomMachine nom = new NomMachine(parts[0]);
      AdresseIP ip = new AdresseIP(parts[1]);
      insert(nom, ip);
    }
  }

  private void persist() {
    try {
      List<DnsItem> items = new ArrayList<>();
      for (Map.Entry<NomMachine, AdresseIP> e : byName.entrySet()) {
        items.add(new DnsItem(e.getKey(), e.getValue()));
      }
      items.sort(Comparator.comparing(i -> i.getNom().getFqdn()));
      List<String> out = new ArrayList<>();
      for (DnsItem it : items) {
        out.add(it.getNom().getFqdn() + " " + it.getIp().value());
      }
      Files.write(dbFile, out, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Erreur écriture base: " + e.getMessage(), e);
    }
  }

  private void insert(NomMachine nom, AdresseIP ip) {
    byName.put(nom, ip);
    byIp.put(ip, nom);
  }

  public DnsItem getItem(AdresseIP ip) {
    NomMachine nom = byIp.get(ip);
    return (nom == null) ? null : new DnsItem(nom, ip);
  }

  public DnsItem getItem(NomMachine nom) {
    AdresseIP ip = byName.get(nom);
    return (ip == null) ? null : new DnsItem(nom, ip);
  }

  public List<DnsItem> getItems(String domaine) {
    List<DnsItem> res = new ArrayList<>();
    for (Map.Entry<NomMachine, AdresseIP> e : byName.entrySet()) {
      if (e.getKey().getDomaine().equals(domaine)) {
        res.add(new DnsItem(e.getKey(), e.getValue()));
      }
    }
    res.sort(Comparator.comparing(DnsItem::getNom));
    return Collections.unmodifiableList(res);
  }

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
