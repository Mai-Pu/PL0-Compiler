package com.maip.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.maip.Dao.Panalyzer;
import com.maip.table.Pcode;
import com.maip.table.Pcode.code;
import com.maip.table.symbolTable.symbolItem;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;

/**
 * 程序运行界面
 */

public class Mainfram extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private Panalyzer panalyzer;
	private boolean sourceempty = false;
	private boolean otherempty = false;
	private JTable table_1;
	private JTable table_2;
	private JTable table_3;
	private JTable table_4;
	private JScrollPane scrollPane_4;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mainfram frame = new Mainfram();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Mainfram() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 989, 623);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setBorder (BorderFactory.createTitledBorder ("源代码"));
		scrollPane.setToolTipText("\u6E90\u4EE3\u7801");
		scrollPane.setBounds(10, 10, 329, 454);
		contentPane.add(scrollPane);
		
		table = new JTable(){
			 public boolean isCellEditable(int row, int column) {
				 return false;
			 }
		};
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"\u6E90\u4EE3\u7801"
			}
		));
		table.setShowGrid(false);
		scrollPane.setViewportView(table);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder (BorderFactory.createTitledBorder ("字母表"));
		scrollPane_1.setToolTipText("\u5B57\u6BCD\u8868");
		scrollPane_1.setBounds(349, 10, 420, 262);
		contentPane.add(scrollPane_1);
		
		table_1 = new JTable(){
			 public boolean isCellEditable(int row, int column) {
				 return false;
			 }
		};
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"name", "level", "address", "type", "value"
			}
		));
		scrollPane_1.setViewportView(table_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setToolTipText("\u76EE\u6807\u4EE3\u7801");
		scrollPane_2.setBounds(349, 281, 420, 293);
		contentPane.add(scrollPane_2);
		
		table_2 = new JTable(){
			 public boolean isCellEditable(int row, int column) {
				 return false;
			 }
		};
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"\u76EE\u6807\u4EE3\u7801"
			}
		));
		table_2.setShowGrid(false);
		scrollPane_2.setViewportView(table_2);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setToolTipText("\u8F93\u51FA\u7ED3\u679C");
		scrollPane_3.setBounds(779, 10, 184, 91);
		contentPane.add(scrollPane_3);
		
		table_3 = new JTable(){
			 public boolean isCellEditable(int row, int column) {
				 return false;
			 }
		};
		table_3.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"\u8F93\u51FA\u7ED3\u679C"
			}
		));
		scrollPane_3.setViewportView(table_3);
		
		JButton btnNewButton = new JButton("\u6253\u5F00\u6587\u4EF6");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(0);
				int temp = chooser.showOpenDialog(null);
				if(temp == 1){
					return;
				}else{
					File f= chooser.getSelectedFile(); 
					String filePath = f.getAbsolutePath();
					System.out.println(filePath);
					Process proc;
					try {
		                String encoding="GBK";
		                File file=new File(filePath);
		                if(file.isFile() && file.exists()){ 
		                	panalyzer = new Panalyzer();
		                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
		                    BufferedReader bufferedReader = new BufferedReader(read);
		                    String lineTxt = null;
		                    ((DefaultTableModel)table.getModel()).setRowCount(0);
		                    ((DefaultTableModel)table_1.getModel()).setRowCount(0);
		                    ((DefaultTableModel)table_2.getModel()).setRowCount(0);
		                    ((DefaultTableModel)table_3.getModel()).setRowCount(0);
		                    ((DefaultTableModel)table_4.getModel()).setRowCount(0);
		                    while((lineTxt = bufferedReader.readLine()) != null){
		                    	Vector vi=new Vector();
		    					vi.add(lineTxt);
		    					panalyzer.getLex().getReadtxt().add(lineTxt.trim()+' ');
		    					((DefaultTableModel)table.getModel()).addRow(vi);
		                    }
		                    sourceempty = true;
		                    read.close();
		                }else{
		                	System.out.println("找不到指定的文件");
		                }
		        	} catch (Exception e1) {
		        		System.out.println("读取文件内容出错");
		        		e1.printStackTrace();
		        	}
				}
			}
		});
		btnNewButton.setFocusable(false);
		btnNewButton.setBounds(779, 348, 184, 59);
		contentPane.add(btnNewButton);
		
		JButton button = new JButton("\u7F16\u8BD1");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(sourceempty)
				{
					sourceempty = false;
					panalyzer.start(0,(DefaultTableModel)table_4.getModel());
					for(symbolItem pp :panalyzer.table.table)
					{
						Vector vi=new Vector();
    					vi.add(pp.name);
    					vi.add(pp.level);
    					vi.add(pp.address);
    					vi.add(pp.type);
    					vi.add(pp.value);
    					((DefaultTableModel)table_1.getModel()).addRow(vi);
					}
					int i=0;
					for(code c: panalyzer.pcode.P)
					{
						Vector vi=new Vector();
    					vi.add(i+" "+c.F+" "+c.l+" "+c.a);
    					i++;
    					((DefaultTableModel)table_2.getModel()).addRow(vi);
					}
					otherempty = true;
				}
				else {
					JOptionPane.showMessageDialog(null, "请先打开文件", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		button.setFocusable(false);
		button.setBounds(779, 417, 184, 59);
		contentPane.add(button);
		
		JButton button_1 = new JButton("\u8FD0\u884C");
		button_1.setFocusable(false);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(otherempty)
				{
					panalyzer.pcode.runP((DefaultTableModel)table_3.getModel());
				}
				else {
					JOptionPane.showMessageDialog(null, "请先进行编译", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		button_1.setBounds(779, 486, 184, 59);
		contentPane.add(button_1);
		
		scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(779, 111, 184, 222);
		scrollPane_4.setBorder (BorderFactory.createTitledBorder ("编译错误"));
		contentPane.add(scrollPane_4);
		
		table_4 = new JTable(){
			 public boolean isCellEditable(int row, int column) {
				 return false;
			 }
		};
		table_4.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"\u884C\u53F7", "\u9519\u8BEF\u539F\u56E0"
			}
		));
		scrollPane_4.setViewportView(table_4);
		
		JTextArea txtrmp = new JTextArea();
		txtrmp.setFont(new Font("Monospaced", Font.PLAIN, 15));
		txtrmp.setDisabledTextColor(Color.LIGHT_GRAY);
		txtrmp.setBackground(Color.ORANGE);
		txtrmp.setEditable(false);
		txtrmp.setText("               \r\n       \u672C\u8F6F\u4EF6\u4F5C\u8005\uFF1AMP\r\n       \u5982\u6709\u540C\u540D\uFF0C\u7EAF\u5C5E\u865A\u6784");
		txtrmp.setBounds(10, 474, 329, 100);
		contentPane.add(txtrmp);
		setLocationRelativeTo(null); 
	}
}
