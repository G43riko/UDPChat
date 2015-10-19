package org.chat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.chat.Config;
import org.chat.core.Server;

public class ChatPanel extends Panel{
	private static final long serialVersionUID = 1L;

	private Gui parent;
	
	private JTextArea chatHistory = new JTextArea(10,25);
	private JTextArea message;
	
	private JButton sentMessage;
	private JButton logout;
	
	private JSpinner maxSize;
	
	//CONSTRUCTORS
	
	public ChatPanel(Gui parent){
		this.parent = parent;
	}
	
	//OTHERS
	
	public void appendText(String txt){
		chatHistory.append(txt);
	}
	
	//CREATORS
	

	JPanel createTextAreaPanel(){
		JPanel panel = new JPanel();
		
		chatHistory.setLineWrap(true);
		chatHistory.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(chatHistory);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scroll);

		return panel;
	}
	
	JPanel createMessagePanel(){
		JPanel panel = new JPanel();
		panel.add(message = new JTextArea(2, 19));
		panel.add(sentMessage = new JButton("Send"));
		
		sentMessage.addActionListener(a -> {
			parent.getChat().sendMessage(message.getText(), Server.CLIENT_SEND_MSG);
			message.setText("");
		});

		
		return panel;
	}
	
	JPanel createLogoutPanel(String name){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		//panel.add(new JLabel(name), BorderLayout.WEST);
		
		maxSize = new JSpinner(new SpinnerNumberModel(10,
													  1,
													  Config.CHAT_TOTAL_MAX_MSG_SIZE,
													  Config.CHAT_DEFAULT_MSG_SIZE));
		
		
		JPanel p = new JPanel();
		p.add(new JLabel("message size: "));
		maxSize.getEditor().setPreferredSize(new Dimension(20,20));
		p.add(maxSize);
		panel.add(p,BorderLayout.CENTER);
		panel.add(logout = new JButton("Logout"), BorderLayout.EAST);
		logout.addActionListener(a -> parent.getChat().stop(true));
		
		return panel;
	}
	
	//GETTERS
	
	int getMaxSize(){
		if(maxSize == null)
			return Config.CHAT_TOTAL_MAX_MSG_SIZE;
		return (int)maxSize.getValue();
	}
	
}
