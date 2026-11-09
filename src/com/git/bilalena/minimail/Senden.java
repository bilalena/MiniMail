package com.git.bilalena.minimail;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;

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


// die Klasse ist für das Hauptfenster, Tabelle 'gesendet' und die Aktualisierung dieser Tabelle zuständig
public class Senden extends JFrame {
	private static final long serialVersionUID = 1L;

	// für die Aktion
	private MeineAktionen sendenAct;
	
	// für die Tabelle
	private JTable tabelle;
	// für das Model
	private DefaultTableModel modell;
	
	// innere Klasse für die Aktionen
	class MeineAktionen extends AbstractAction {
		
		private static final long serialVersionUID = 1L;


		// der Konstruktor
		public MeineAktionen (String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			// den Konstruktor der übergeordneter Klasse aufrufen
			super(text, icon);
			// die Beschreibung für den Bildschirmtipp setzen
			putValue(SHORT_DESCRIPTION, beschreibung);
			// den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			// das Action Kommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("senden"))
				senden();			
		}		
	}
	
	
	// der Konstruktor der Klasse Senden
	Senden() {
		super();
		
		setTitle("E-Mail senden");
		// wir nehmen ein Border-Layout
		setLayout(new BorderLayout());
		
		// die Aktionen erstellen
		sendenAct = new MeineAktionen("Neue E-Mail", new ImageIcon("icons/mail-generic.gif"), "Erstellt eine neue E-Mail", null, "senden");
		// die Symbolleiste oben einfügen
		add(symbolleiste(), BorderLayout.NORTH);
		
		setVisible(true);
		setSize(700, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();
	}
	

	// die Symbolleiste erzeugen und zurückgeben
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();
		// die Symbolleiste über die Aktionen einbauen
		leiste.add(sendenAct);
		// die Leiste zurückgeben
		return leiste;		
	}
	
    
	private void tabelleErstellen() {
		// Als Bezeichner fpe die Nummer dr Nachricht wird # benutzt
	    String[] spaltennamen = {"#", "Empfänger", "Betreff", "Text"};

	    // das Modell erstellen
	    modell = new TabelleModel(spaltennamen, 0);

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
				// war es ein Doppelklick?
				if (e.getClickCount() == 2) {
					// die Zeile beschaffen
					int zeile = tabelle.getSelectedRow();
					
					// die Daten beschaffen
					if (zeile >= 0) {
						String empfaenger, betreff, inhalt, ID;
						ID = tabelle.getModel().getValueAt(zeile, 0).toString();
						empfaenger = tabelle.getModel().getValueAt(zeile, 1).toString();
						betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
						inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
						// und anzeigen
						// übergeben wird der Frame der äuseren Klasse
						new Anzeige(Senden.this, true, ID, empfaenger, betreff, inhalt);
					}
				}
			}
		});		
	}
	
	
	// die Tabelle  aktualisieren
	private void tabelleAktualisieren() {
		// für die Anzeige in der Tabelle, anstatt ID aus Derby
		int nummer = 1;
		
		// für den Datenbankzugriff
		Connection verbindung;
		ResultSet ergebnisMenge;
		
		// für die Spalten
		String empfaenger, betreff, inhalt;
		// die Inhalte löschen
		modell.setRowCount(0);
		
		try {
			// Verbindung herstellen und Ergebnismenge beschaffen
			verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");			
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM gesendet");
			
			// die Einträge in die Tabelle schreiben
			while (ergebnisMenge.next()) {

				empfaenger = ergebnisMenge.getString("empfaenger");
				betreff = ergebnisMenge.getString("betreff");
				
				// den Inhalt vom CLOB beschaffen und in einen String umwandeln
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int)clob.length());
				
				// die Zeile zum Modell hinzufügen
				// dazu benutzen wir ein Array vom Typ Object
				modell.addRow(new Object[] {nummer, empfaenger, betreff, inhalt});
				// die Nummer erhöhen
				nummer++;
			}
			// die Verbindungen wieder schließen und trennen
			ergebnisMenge.close();
			verbindung.close();
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,  "Problem: \n" + e.toString());
		}
	}
	
	
	// die Methode sendet
	private void senden() {
		// den Dialog für eine neue Nachricht modal anzeigen
		new NeueNachricht(this, true);
		// nach dem Versenden lassen wir die Anzeige aktualisisren
		tabelleAktualisieren();
	}
}
