package fr.uvsq.cprog.collex;


/** Quitter l'application. */
public final class CommandeQuit implements Commande {
@Override public String execute(Dns dns) { throw new QuitException(); }
}