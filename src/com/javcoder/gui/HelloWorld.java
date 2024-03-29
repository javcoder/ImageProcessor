package com.javcoder.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class HelloWorld extends JFrame {
	private static final long serialVersionUID = 9072175355128953521L;
	
	private JLabel jLabel;
	private JTextField jTextField;
	private JButton jButton;

	public HelloWorld() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);       
		this.setSize(300, 200);
		this.getContentPane().setLayout(null);
		this.add(getJLabel(), null);
		this.add(getJTextField(), null);
		this.add(getJButton(), null);
		this.setTitle("HelloWorld");
	}

	private javax.swing.JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(34, 49, 53, 18);
			jLabel.setText("Name:");
		}
		return jLabel;
	}

	private javax.swing.JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(96, 49, 160, 20);
		}
		return jTextField;
	}

	private javax.swing.JButton getJButton() {
		if (jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(103, 110, 71, 27);
			jButton.setText("OK");
		}
		return jButton;
	}

	public static void main(String[] args) {
		HelloWorld w = new HelloWorld();
		w.setVisible(true);
	}

}