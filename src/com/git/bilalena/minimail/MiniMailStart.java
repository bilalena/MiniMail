package com.git.bilalena.minimail;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MiniMailStart extends JFrame {
	private static final long serialVersionUID = 1L;


	// die innere Klasse für den ActionListener
	class MeinListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// wurde auf Senden gecklickt?
			if (e.getActionCommand().equals("senden")) {
				// dann das Senden starten
				senden();
			}
			// wurde auf Empfangen gecklickt?
			if (e.getActionCommand().equals("empfangen"))
				empfangen();
			// wurde auf Beenden gecklickt?
			if (e.getActionCommand().equals("ende"))
				beenden();			
		}		
	}
	
	
	// der Konstruktor
	public MiniMailStart(String titel) {
		super(titel);
		// ein FlowLayout
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		// die Schaltflächen
		JButton liste = new JButton("Senden");
		liste.setActionCommand("senden");
		
		JButton einzel = new JButton("Empfangen");
		einzel.setActionCommand("empfangen");
		
		JButton beenden = new JButton("Beenden");
		beenden.setActionCommand("ende");
		
		
		// mit dem Listener verbinden
		MeinListener listener = new MeinListener();
		liste.addActionListener(listener);
		einzel.addActionListener(listener);
		beenden.addActionListener(listener);
		
		// zum FlowLayout hinzufügen
		add(liste);
		add(einzel);
		add(beenden);
		
		// Größe setzen, Standardverhalten festlegen, anzeigen
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);		
	}
	
	private void senden() {
		new Senden();
	}
	
	
	private void empfangen() {
		new Empfangen();
	}
	
	
	private void beenden() {
		System.exit(0);
	}
}
