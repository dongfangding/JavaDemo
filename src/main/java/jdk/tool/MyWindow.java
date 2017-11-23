package main.java.jdk.tool;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
public class MyWindow extends JFrame{

	
	/**
	 * 
	 */
	MyWindow(){
//		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setSize(1100, 650);
		this.setVisible(true);
		Container con = this.getContentPane();
		con.setLayout(new BorderLayout());
		
		JTabbedPane jtp = new JTabbedPane();
		EntityPanel entityPanel = new EntityPanel();
		jtp.add("生成Entity",entityPanel);
		con.add(jtp,BorderLayout.CENTER);
		
		this.validate();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
