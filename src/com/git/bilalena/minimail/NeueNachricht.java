package com.git.bilalena.minimail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;



// die Klasse ist für die Erstellung und Verschicken der Nachricht zuständig
public class NeueNachricht extends JDialog {
	private static final long serialVersionUID = 1L;
	// für die Eingabefelder
	private JTextField empfaenger, betreff;
	private JTextArea inhalt;	
	// für die Schaltflächen
	private JButton ok, abbrechen;
	
	
	// die innere Klasse für den ActionListener
	class NeuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// wurde auf OK geklickt?
			if (e.getActionCommand().equals("senden")) 
				// dann die Daten übernehmen
				senden();
			// wurde auf Abbrechen geklickt?
			if (e.getActionCommand().equals("abbrechen")) 
				// dann Dialog schließen
				dispose();		
			}			
		}
	
	
	// der Konstruktor
	public NeueNachricht(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Neue Nachricht");
		// die Oberfläche erstellen
		initGui();
		
		// Standardoperationen setzen
		// hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	

	// der zweite Konstruktor, der die Anfangswerte für die Antwort/Weiterleitung empfängt
	public NeueNachricht(JFrame parent, boolean modal, String empfaengerText, String betreffText, String inhaltText) {
		super(parent, modal);
		setTitle("Nachricht Beantworten oder Weiterleiten");
		initGui();
		empfaenger.setText(empfaengerText);
		betreff.setText(betreffText);
		inhalt.setText(inhaltText);
		
		// Standardoperationen setzen
		// hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	
	// die Oberfläche erzeugen
	private void initGui() {
		setLayout(new BorderLayout());
		JPanel oben = new JPanel();
		// wir nehmen ein GridLayout mit etwas Abstand zwischen Zeilen und Spalten
		oben.setLayout(new GridLayout(0, 2, 10, 5));
		// etwas Abstand zum Rand
		oben.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		oben.add(new JLabel("Empfänger: "));
		empfaenger = new JTextField();
		oben.add(empfaenger);
		oben.add(new JLabel("Betreff: "));
		betreff = new JTextField();
		oben.add(betreff);
		add(oben, BorderLayout.NORTH);
		
		inhalt = new JTextArea();
		// etwas Abstand zum Rand
		inhalt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// den Zeilumbruch aktivieren
		inhalt.setLineWrap(true);
		inhalt.setWrapStyleWord(true);
				
		// das Feld setzen wir in ein ScrollPane
		JScrollPane scroll = new JScrollPane(inhalt);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);
		
		JPanel unten = new JPanel();
		// die Schaltflächen
		ok = new JButton("Senden");
		ok.setActionCommand("senden");
		ok.setForeground(Color.decode("#14661e"));
		
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.setForeground(Color.decode("#d43f35"));	

		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);
		
		unten.add(ok);
		unten.add(abbrechen);
		add(unten, BorderLayout.SOUTH);
		
		// anzeigen
		setSize(600, 300);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);	
	}

	
	// Aufgabe 1
	// die Methode bekommt die E-mail und das Passwort aus der DatenBank
	// sie ist public für die Verwendung in der Klasse Empfangen deklariert
	public static String[] zugangsdatenErmitteln() {		
		// Variablen für die Zugangsdaten vereinbaren
		String  benutzername = "";
		String  kennwort = "";
		// sie werden in einem Array abgelegt
		String[] zugangsdaten = {};
		
		// die Zugangsdaten aus der DatenBank laden
		// die Verbindung zur DB herstellen
		Connection verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
		
		// die Email und Passwort ermitteln
		try {
			PreparedStatement prepState = verbindung.prepareStatement("SELECT email, passwort FROM benutzer");
			
			ResultSet ergebnis = prepState.executeQuery();
			
			if (ergebnis.next()) {
				 benutzername = ergebnis.getString("email");
				 kennwort = ergebnis.getString("passwort");
			}
			
			// die Verbindungen schließen
			ergebnis.close();
			prepState.close();
			verbindung.close();
			// die DB schließen
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
		
		// zugangsdaten in Array packen un zurückgeben
		zugangsdaten = new String[] {benutzername, kennwort};
		return zugangsdaten;
	}


	// die Methode legt einen neuen Datensatz an
	// hier rufen wir die Methode zugangsdatenErmitteln() auf,
	// um die Zugangsdaten nicht zweimal aus der DB ermitteln zu müssen
	private void senden() {
		// zugangsdatenErmitteln() aufrufen
		String[] zugangsdaten = zugangsdatenErmitteln();
		
		// für die Sitzung
		Session sitzung;
		
		// die Verbindung herstellen, hier übergeben werden zugangsdaten als Argument
		sitzung = verbindungHerstellen(zugangsdaten);
		
		// die Nachricht verschicken
		// nun mit der Email als zweiter Argument
		nachrichtVerschicken(sitzung, zugangsdaten[0]);
		nachrichtSpeichern();
	}
	
	

	// Methode bekommt zugangsdaten als Argument
	private Session verbindungHerstellen(String[] zugangsdaten) {
		// zugangsdaten aus dem Array ermitteln
		String benutzername = zugangsdaten[0];
		String kennwort = zugangsdaten[1];
		
		// der Server
		String server = "mail.gmx.net";
		
		// die Eigenschaften setzen
		Properties eigenschaften = new Properties();		
		// die Authentifizierung über TLS
		eigenschaften.put("mail.smtp.auth", "true");
		eigenschaften.put("mail.smtp.starttls.enable", "true");		
		// der Server
		eigenschaften.put("mail.smtp.host", server);		
		// der Port zum Senden
		eigenschaften.put("mail.smtp.port", "587");
		
		// das Session-Objekt erstellen
		Session sitzung = Session.getInstance(eigenschaften, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(benutzername, kennwort);
			}
		});
	
		return sitzung;
	}
	
	

	// die Methode bekommt als zweiter Argument die E-mail des Absenders
	// die E-mail muss nicht mehr hardcoded sein
	private void nachrichtVerschicken(Session sitzung, String absender) {

		try {
			// eine neue Nachricht vom Typ MimeMessage erzeugen
			MimeMessage nachricht = new MimeMessage(sitzung);
			
			// den Absender setzen
			nachricht.setFrom(new InternetAddress(absender));
			
			// den Empfänger
			nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger.getText()));
			
			// den Betreff
			nachricht.setSubject(betreff.getText());
			
			// und den Text
			nachricht.setText(inhalt.getText());
			
			// die Nachrict verschicken
			Transport.send(nachricht);
			
			JOptionPane.showMessageDialog(this, "die Nachricht wurde verschickt");
			
			// den Dialog schließen
			dispose();
		}
		catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
	
	
	private void nachrichtSpeichern() {
		// für die Verbindung
		Connection verbindung;
		
		// die Datenbank öffnen
		verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
		
		try {
			// einen Eintrag in der Tabelle "gesendet" anlegen
			// über ein vorbereitetes Statement
			PreparedStatement prepState;
			prepState = verbindung.prepareStatement("insert into gesendet (empfaenger, betreff, inhalt) values (?, ?, ?)");
			prepState.setString(1,  empfaenger.getText());
			prepState.setString(2, betreff.getText());
			prepState.setString(3,  inhalt.getText());
			
			// das Statement ausführen
			prepState.executeUpdate();
			verbindung.commit();
			
			// Verbindung schließen
			prepState.close();
			verbindung.close();
			// und die DB schließen
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}		
	}	
}
	


