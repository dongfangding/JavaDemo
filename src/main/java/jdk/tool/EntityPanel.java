package main.java.jdk.tool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class EntityPanel extends JPanel{
	private JTextField txtTableName;
	private JComboBox cobEntityType;
	private JTextArea area;
	private JTextArea area2;
	private JTextArea area3;
	private JTextArea area4;
	private JTextArea area5;
	private String entityType="baseDomain";
	public EntityPanel(){
		this.setLayout(new BorderLayout());
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout());
		
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new GridLayout(3,2,8,8));
		basePanel.add(new JLabel("表名：",JLabel.RIGHT));
		txtTableName = new JTextField(15);
		basePanel.add(txtTableName);
		
		basePanel.add(new JLabel("继承类型：",JLabel.RIGHT));
		cobEntityType = new JComboBox();
		cobEntityType.addItem("baseDomain");
		cobEntityType.addItem("idDomain");
		cobEntityType.addItem("原始");
		cobEntityType.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				String s = (String)e.getItem();
				entityType=s;
			}
			
		});
		
		basePanel.add(cobEntityType);
		
		JButton btnReset=new JButton("重置");
		btnReset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				txtTableName.setText("");
			}
		});
		basePanel.add(btnReset);
		JButton btnOk=new JButton("开始");
		btnOk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				area.setText("数据处理中...");
				
				if(StrUtil.isNotEmpty(txtTableName.getText())){
					String tableName = txtTableName.getText().toUpperCase();
					JDBCUtil util = new JDBCUtil();
					String entityStr=util.printEntity(tableName, entityType);
					String daoStr = util.printDao();
					String daoImplStr =util.printDaoImpl();
					String serviceStr =util.printService();
					String webRecordStr =util.printRecord();
					area.setText(entityStr);
					area2.setText(daoStr);
					area3.setText(daoImplStr);
					area4.setText(serviceStr);
					area5.setText(webRecordStr);
					
				}else{
					area.setText("请输入表名！");
				}
				
			}
		});
		basePanel.add(btnOk);
		
		leftPanel.add(basePanel);
		this.add(leftPanel,BorderLayout.WEST);
		
		JPanel borderPanel = new JPanel();
		borderPanel.setLayout(new GridLayout(5,1,8,8));
		
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		
		area = new JTextArea(30,50);
		JScrollPane sp1 = new JScrollPane(area, v, h);
		borderPanel.add(sp1);
		
		area2 = new JTextArea(30,50);
		JScrollPane sp2 = new JScrollPane(area2, v, h);
		borderPanel.add(sp2);
		
		area3 = new JTextArea(30,50);
		JScrollPane sp3 = new JScrollPane(area3, v, h);
		borderPanel.add(sp3);
		
		area4 = new JTextArea(30,50);
		JScrollPane sp4 = new JScrollPane(area4, v, h);
		borderPanel.add(sp4);
		
		area5 = new JTextArea(30,50);
		JScrollPane sp5 = new JScrollPane(area5, v, h);
		borderPanel.add(sp5);
		
		
		this.add(borderPanel,BorderLayout.CENTER);
	}
}
