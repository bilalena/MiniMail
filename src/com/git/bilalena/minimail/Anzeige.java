package com.git.bilalena.minimail;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Anzeige extends JDialog {
	private static final long serialVersionUID = 1L;
	
	// für die Eingabefelder
	private JTextField empfaengerField, betreffField;
	private JTextArea inhalTextArea;
	// für die Schaltflächen
	private JButton okButton;
	
	// dei innere Klasse für den Action Listener
	class NeuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// wurde auf OK gecklickt?
			if (e.getActionCommand().equals("ok"))
				// dann Dialog schließen
				dispose();			
		}		
	}
	
	
	// der Konstruktor
	public Anzeige(JFrame parent, boolean modal, String ID, String empfaenger, String betreff, String inhalt) {
		super(parent, modal);
		setTitle("Anzeige");
		// die oberfläche erstellen
		initGui(ID, empfaenger, betreff, inhalt);
		
		// Standaraktion setzen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	
	// Methode erstellt GUI
	private void initGui(String ID, String empfaenger, String betreff, String inhalt) {
		setLayout(new BorderLayout());
		
		JPanel oben = new JPanel();
		oben.add(new JLabel("Empfäenger: "));
		empfaengerField = new JTextField(empfaenger);
		oben.add(empfaengerField);
		oben.add(new JLabel("Betreff: "));
		betreffField = new JTextField(betreff);
		oben.add(betreffField);
		add(oben, BorderLayout.NORTH);
		
		inhalTextArea = new JTextArea(inhalt);
		// den Zeilenumbruch aktivieren
		inhalTextArea.setLineWrap(true);
		inhalTextArea.setWrapStyleWord(true);
		// das Feld setzen wir in ein ScrollPane
		JScrollPane scroll = new JScrollPane(inhalTextArea);
		
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);		
		
		// die Felder können nicht bearbeitet werden
		empfaengerField.setEditable(false);
		betreffField.setEditable(false);
		inhalTextArea.setEditable(false);
		
		
		JPanel unten = new JPanel();
		// die Schaltfläche
		okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		
		NeuListener listener = new NeuListener();
		okButton.addActionListener(listener);
		
		unten.add(okButton);
		add(unten, BorderLayout.SOUTH);
		
		// anzeigen
		setSize(600, 300);
		setVisible(true);		
	}
}
