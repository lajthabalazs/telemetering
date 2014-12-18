package hu.droidium.remote_home_manager;

import hu.droidium.telemetering.interfaces.LanguageInterface;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUIClient extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5545975021432370757L;
	private JTextField inputField;
	private LanguageInterface languageInterface;
	private JTextArea responseArea;

	public GUIClient(LanguageInterface languageInterface) {
		this.languageInterface = languageInterface;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Telemetering demo client");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		inputField = new JTextField();
		inputField.setColumns(60);
		inputField.addActionListener(this);
		inputField.setText("Milyen meleg volt ma a nappaliban?");
		contentPane.add(inputField, BorderLayout.NORTH);  
		responseArea = new JTextArea();
		JScrollPane areaScrollPane = new JScrollPane(responseArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 400));
		contentPane.add(areaScrollPane, BorderLayout.CENTER);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text = inputField.getText();
		System.out.println("Question :" + text);
		String response = languageInterface.getResponse(text, System.currentTimeMillis());
		System.out.println(response);
		inputField.setText("");
		responseArea.setText("Q:\n" + text + "\nR:\n" + response);		
	}
}