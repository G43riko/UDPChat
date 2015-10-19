package org.chat.gui;

import java.awt.Panel;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.chat.Config;

public class LoginPanel extends Panel{
	private static final long serialVersionUID = 1L;

	private Gui parent;
	
	private JTextField login;
	private JTextField ip;
	private JTextField port;
	
	private JButton start;
	
	private JRadioButton guestOption;
	private JRadioButton hostOption;
	
	//CONSTRUCTORS
	
	public LoginPanel(Gui parent){
		this.parent = parent;
	}
	
	//CREATORS
	
	JPanel createLoginPanel(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Login"));
		
		panel.add(login = new JTextField("userName", 15));
	
		panel.add(start = new JButton("Start"));
		start.addActionListener(a -> parent.getChat().start(login.getText(), 
												  			  ip.getText(),
												  			  port.getText(), 
												  			  hostOption.isSelected()));
		
		return panel;
	}
	
	JPanel createCheckBoxesPanel(){
		JPanel panel = new JPanel();
		ButtonGroup bg = new ButtonGroup();
		
		guestOption = new JRadioButton("Guest", true);
		hostOption = new JRadioButton("Host", false);
		
		guestOption.addActionListener(a -> ip.setEnabled(guestOption.isSelected()));
		hostOption.addActionListener(a -> {
			ip.setEnabled(guestOption.isSelected()) ;
			ip.setText("localhost");
		});
		
		bg.add(guestOption);
		bg.add(hostOption);
		
		panel.add(guestOption);
		panel.add(hostOption);
		
		return panel;
	}

	JPanel createAdressPanel(){
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("IP Adress: "));
		panel.add(ip = new JTextField(Config.GUI_DEFAULT_ADRESS, 10));
		panel.add(new JLabel("PORT: "));
		panel.add(port = new JTextField(Config.GUI_DEFAULT_PORT, 3));
		
		return panel;
	}

}
