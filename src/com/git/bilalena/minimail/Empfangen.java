package com.git.bilalena.minimail;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.sun.mail.imap.protocol.FLAGS;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Multipart;
import jakarta.mail.BodyPart;


// die Klasse zum:
// Fenster erstellen GUI;
// Tabelle zum Anzeigen im Fenster erstellen;
// nach dem Öffnen des Fensters Verbindung zum GMX POP3S-Server herstellen;
// Nachrichten abrufen;
// in Derby DB speichern;
// empfangene Nachrichten vom POP3-Server löschen;
// Nachrichten aus Derby DB lesen;
// in einer Tabelle anzeigen;
// Doppelklicken, um die ausgewählte Nachricht im Anzeigefenster anzuzeigen.
public class Empfangen extends JFrame {
	
	private static final long serialVersionUID = 1L;
	// für die Tabelle
	private JTable tabelle;
	private DefaultTableModel modell;
	
	// Aufgabe 2
	// für die Aktionen
	private BearbeitenAktionen beantwortenAct, weiterleitenAct;
	
	
	// eine innere Klasse für den WindowListener und den ActionListener
	// die Klasse ist vom WindowAdapter abgeleitet
	class MeinWindowAdapter extends WindowAdapter {
		@ Override
		public void windowOpened(WindowEvent e) {
			// die Methode nachrichtenEmpfangen() aufrufen
			nachrichtenEmpfangen();		
			}
	}
	

	// eine innere Klasse für die Aktionen
	class BearbeitenAktionen extends AbstractAction {

		// der Konstruktor
		public BearbeitenAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			// den Konstruktor übergeordneter Klasse aufrufen
			super(text, icon);
			
			// die Beschreibung setzen
			putValue(SHORT_DESCRIPTION, beschreibung);
			// den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			// das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		
		@Override
		public void actionPerformed(ActionEvent e) {
			// wurde auf "Beantworten" geklickt?
			if (e.getActionCommand().equals("antworten"))
				// die Methode beantworten() aufrufen
				beantworten();
			// wurde auf "Weiterleiten" geklickt?
			if (e.getActionCommand().equals("weiterleiten"))
				// die Methode weiterleiten() aufrufen
				weiterleiten();
		}
	}
	
	
	// der Konstruktor
	public Empfangen() {
		super();

		
		setTitle("E-Mail Empfangen");
		setLayout(new BorderLayout());
		

		// die Aktionen erstellen
		beantwortenAct = new BearbeitenAktionen("Beantworten E-Mail", 
				new ImageIcon("icons/mail-reply.gif"), 
				"Eine E-Mail beantworten", 
				null, 
				"antworten");
		weiterleitenAct = new BearbeitenAktionen("Weiterleiten E-Mail", 
				new ImageIcon("icons/mail-forward.gif"), 
				"Eine E-Mail weiterleiten", 
				null, 
				"weiterleiten");
				
		// die Symbolleiste oben hinzufügen
		add(bearbSymbolleiste(), BorderLayout.NORTH);		
		setVisible(true);
		setSize(700, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						
		// den Listener verbinden
		addWindowListener(new MeinWindowAdapter());
				
		// die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();		
	}
	
	

	// Methode erstellt und gibt bearbSymbolleiste() zurück
	private JToolBar bearbSymbolleiste() {
		JToolBar bearbeitenBar = new JToolBar();
		// die Symbole für die Aktionen einbauen
		bearbeitenBar.add(beantwortenAct);
		bearbeitenBar.add(weiterleitenAct);
		
		// die Leiste zurückgeben
		return bearbeitenBar;
	}
	
	

	// Methode zum 	Nachricht Beantworten
	private void beantworten() {
		// für die Prüfung, ob eine Nachricht ausgewählt wurde
		// bekommen die Nummer der ausgewälter Zeile
		int zeile = tabelle.getSelectedRow();
		
		// wenn keine Nachricht zur Beantwortung ausgewählt wurde, eine Meldung ausgeben
		if (zeile == -1) {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Nachricht aus");
			return;
		}
		
		// Daten für die neue Nachricht aus der 'alten' Nachricht beschaffen
		String sender = tabelle.getModel().getValueAt(zeile, 1).toString();
		String betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
		String inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
		
		// der neue Inhalt soll den alten und eine Trennlinie enthalten
		String neuerInhalt = 
				"\n\n\n\n------------Text der ursprünglichen Nachricht------------\n\n"
				+ inhalt;
		
		// den Konstruktor des Fensters NeueNachricht für die Beantwortung und Weiterleitung aufrufen
		new NeueNachricht(Empfangen.this, true, sender, "AW: " + betreff, neuerInhalt);
	}
	
	

	// Methode für das Weiterleiten
	private void weiterleiten() {
		
		// erstmal prüfen, ob eine Nachricht ausgewählt wurde
		int zeile = tabelle.getSelectedRow();
		// wenn keine Nachricht ausgewählt wurde, eine Meldung ausgeben
		if (zeile == -1) {
			JOptionPane.showMessageDialog(this,  "Bitte wählen Sie eine Nachricht aus");		
			return;
		}
		
		// Daten für die neue Nachricht aus der alten Nachricht beschaffen
		String betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
		String inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
		
		String neuerInhalt = 
				"\n\n\n\n------------Text der ursprünglichen Nachricht------------\n\n"
				+ inhalt;
		
		// den Konstruktor des Fensters NeuNachricht für die Weiterleiten/Beantworten aufrufen
		// beim Weiterleiten bleibt die Empfängeradresse leer
		new NeueNachricht(Empfangen.this, true, "", "WG: " + betreff, neuerInhalt);
	}
	
	
	// Methode erstellt eine Tabelle für GUI
	private void tabelleErstellen() {
		// für die Spaltenbezeichner
		// wir verwenden keine IDs mehr aus der Datenbank, sondern eine Nummer #
		String[] spaltenNamen = {"#", "Sender", "Betreff", "Text"};
		
	    // das Modell aus eigener Klasse erstellen
	    modell = new TabelleModel(spaltenNamen, 0);

	    // die Tabelle erzeugen
	    tabelle = new JTable(modell);
	    
	    // ein wenig Schönheit
	    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	    renderer.setHorizontalAlignment(SwingConstants.LEFT);
	    renderer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

	    // alle Spalten anpassen
	    // den Renderer für alle Spalten setzen
		for (int i = 0; i < tabelle.getColumnCount(); i++) {
		    tabelle.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}

	    // die Tabelle konfigurieren
	    tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

	    // die Breite der ersten Spalte Nummer anpassen
	    tabelle.getColumnModel().getColumn(0).setMaxWidth(50);
	    tabelle.getColumnModel().getColumn(0).setPreferredWidth(45);

	    // volle Höhe nutzen
	    tabelle.setFillsViewportHeight(true);

	    // die Tabelle in ein ScrollPane setzen
	    JScrollPane scroll = new JScrollPane(tabelle);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

	    add(scroll);
				
		
		// einen Maus-Listener ergänzen
		tabelle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// war es ein DoppelKlick?
				if (e.getClickCount() == 2) {
					int zeile = tabelle.getSelectedRow();
					// die Daten beschaffen
					String sender, betreff, inhalt, nummer;
					nummer = tabelle.getModel().getValueAt(zeile, 0).toString();
					sender = tabelle.getModel().getValueAt(zeile, 1).toString();
					betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
					inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
					
					// und anzeigen
					// übergeben wird der Frame der äuseren Klasse
					new Anzeige(Empfangen.this, true, nummer, sender, betreff, inhalt);
				}				
			}
		});
	}
	

	// Methode aktualisiert die Daten in der Tabelle
	// sie nimmt Daten aus der DB und schreibt sie in die Tabelle fürs GUI
	private void tabelleAktualisieren() {
		// für die Anzeige der Nummer in der Tabelle, anstatt ID aus Derby
		int nummer = 1;
		
		// für den DatenBankzugriff
		Connection verbindung;
		ResultSet ergebnisMenge;
		
		// für die Spalten
		String sender, betreff, inhalt;
		// die Inhalte löschen
		modell.setRowCount(0);
		
		try {
			// Verbindung herstellen und Ergebnismenge beschaffen
			verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");

			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM empfangen");

			// die Einträge in die Tabelle schreiben
			while (ergebnisMenge.next()) {
				// ID = ergebnisMenge.getString("iNummer");
				sender = ergebnisMenge.getString("sender");
				betreff = ergebnisMenge.getString("betreff");				
				// den Inhalt vom CLOB beschaffen und in einen String umwandeln
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int) clob.length());
				
				// die Zeile zum Modell hinzufügen
				// dazu benutzen wir ein Array vom Typ Object
				modell.addRow(new Object[] {nummer, sender, betreff, inhalt});
				// die Nummer erhöhen
				nummer++;
			}
			
			// die Verbindungen schließen und trennen
			ergebnisMenge.close();
			verbindung.close();
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n " + e.toString());
		}
	}
	
	
	// Methode empfängt Nachrichten
	private void nachrichtenEmpfangen() {
		nachrichtenAbholen();
		// nach dem Empfangen lassen wir die Anzeige aktualisieren
		tabelleAktualisieren();
	}


	// Methode zum Abholen Nachrichten vom POP3S Server
	private void nachrichtenAbholen() {
		// die Zugangsdaten aus der DatenBank ermitteln
		String[] zugangsdaten = NeueNachricht.zugangsdatenErmitteln();

		String benutzername = zugangsdaten[0];
		String kennwort = zugangsdaten[1];
		
		// der Server
		String server = "pop.gmx.net";
		
		// die Eigenschaften setzen
		Properties eigenschaften = new Properties();
		// das Protokoll
		eigenschaften.put("mail.store.protocol", "pop3");
		// den Host
		eigenschaften.put("mail.pop3.host", server);
		// den Port zum Empfangen
		eigenschaften.put("mail.pop3.port", "995");
		// die Authentifizierung über TLS
		eigenschaften.put("mail.pop3starttls.enable", "true");
		// das Session-Objekt erstellen
		Session sitzung = Session.getDefaultInstance(eigenschaften);
		
		// das Store-Objekt über die Sitzung erzeugen
		try (Store store = sitzung.getStore("pop3s")) {
			 // und verbinden
			store.connect(server, benutzername, kennwort);
			// ein OrdnerObjekt füe den Posteingang erzeugen
			Folder posteingang = store.getFolder("INBOX");
			// und öffnen
			// dabei sind die Änderungen zugelassen
			posteingang.open(Folder.READ_WRITE);
			
			// die Nachrichten beschaffen
			Message nachrichten[] = posteingang.getMessages();
			
			// gibt es neue Nachrichten?
			if (nachrichten.length != 0) {
				// dann die Anzahl zeigen
				JOptionPane.showMessageDialog(this, "Es gibt " + posteingang.getUnreadMessageCount() + " neue Nachrichten");
				
				// jede Nachricht verarbeiten
				for (Message nachricht : nachrichten)
					nachrichtVerarbeiten(nachricht);
			}
			else 
				JOptionPane.showMessageDialog(this, "Es gibt keine neue Nachrichten.");
			
			// den Ordner schließen
			// durch das Argument true werden die Nachrichten gelöscht
			posteingang.close(true);		
	  }	
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
	
	
	// Methode verarbeitet Nachrichten
	private void nachrichtVerarbeiten(Message nachricht) {
		try {
			String sender = nachricht.getFrom()[0].toString();
			String betreff = nachricht.getSubject();
			String inhalt = "";

			// ist es einfacher Text?
			if (nachricht.isMimeType("text/plain")) {
				inhalt = nachricht.getContent().toString();
			}

			// enthält die Nachricht mehrere Teile?
			else if (nachricht.isMimeType("multipart/alternative")) {
				Multipart multipart = (Multipart) nachricht.getContent();

				// den ersten Teil (text/plain) beschaffen
				BodyPart teil = multipart.getBodyPart(0);
				inhalt = teil.getContent().toString();
			}

			// sonst geben wir eine Meldung aus
			else {
				JOptionPane.showMessageDialog(this, "Der Typ der Nachricht " + nachricht.getContentType() + " kann nicht verarbeitet werden");
			}

			// die Nachricht speichern
			nachrichtSpeichern(sender, betreff, inhalt);

			// und zum Löschen markieren
			nachricht.setFlag(FLAGS.Flag.DELETED, true);

		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Problem: \n" + e.toString());
		}
	}


	// Methode speichert eine Nachricht in der DatenBank
	private void nachrichtSpeichern(String sender, String betreff, String inhalt) {
		// für die Verbindung
		Connection verbindung;
		
		// die DatenBank öffnen
		verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");

		try {
			// einen Eintrag in der Tabelle empfangen anlegen
			// über ein vorbereitetes Statement
			PreparedStatement prepState;
			
			prepState = verbindung.prepareStatement("insert into empfangen (sender, betreff, inhalt) values (?, ?, ?)");
			prepState.setString(1, sender);
			prepState.setString(2, betreff);
			prepState.setString(3, inhalt);
			
			// das Statement ausführen
			prepState.executeUpdate();
			verbindung.commit();
			
			// Verbindung schließen
			prepState.close();
			verbindung.close();
			// und ide DatenBank schließen
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");			
		}
		catch (Exception e ) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}	
	}	
}
