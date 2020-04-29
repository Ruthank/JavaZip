import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.*;
import java.awt.Font;


public class decompress extends JFrame {

	private JPanel contentPane;
	private JTextField tfFrom;
	private JLabel label2;
	private JTextField tfTo;
	private JButton btnSelectDic;
	private JButton btnNewButton;
	private File zipFile;
	private File dicFile;
	private final int SUCCESS = 1;
	private final int FAILED = 0;
	private final int ERROR = -1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					decompress frame = new decompress();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public int doDecompress() {
		try {
			
			//ZipInputStream默认使用UTF-8编码，遇到中文文件名会出错。
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile), Charset.forName("GBK"));
		    ZipEntry entry;
		    while((entry = zis.getNextEntry()) != null) {

		    	setTitle("正在解压缩：" + entry.getName()); //用窗口title显示正在解压的文件名
		    	File newFile = new File(dicFile.getAbsoluteFile() + "/" + entry.getName());
				
				//如果遇到目录，则说明是空文件夹。
				//isDirectory()方法是看目录的最后一位是否为'/'，如果用了'\\'可能判不出来。
	    		if (entry.isDirectory()) {
	    			newFile.mkdir();
	    			continue;
	    		}
	    		else {
					//递归创建父目录，再创建文件。
	    			createDir(newFile.getParentFile());
	    			newFile.createNewFile();
	    		}
		    	//写入文件
		    	FileOutputStream fos = new FileOutputStream(newFile);
		    	int c;
		    	while((c = zis.read()) != -1)
		    		fos.write(c);
		    	fos.close();
		    }  
		    zis.close();
		    setTitle("JavaZip-解压文件"); //title恢复
		    
		} catch (IOException ioe) {
			System.out.println(ioe);
			setTitle("JavaZip-解压文件"); //title恢复
			return FAILED;
		}
		return SUCCESS;
	}
	
	/*
	递归创建目录。
	若该目录已存在，说明其子目录也可以被创建，return
	若该目录不存在，递归判父目录。
	*/
	void createDir(File parent) {
		while(!parent.exists()) {
			createDir(parent.getParentFile());
			parent.mkdir();
		}
	}

	/**
	 * Create the frame.
	 */
	public decompress() {
		setTitle("JavaZip-\u89E3\u538B\u6587\u4EF6");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 457, 327);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tfFrom = new JTextField();
		tfFrom.setEditable(false);
		tfFrom.setBounds(15, 59, 245, 29);
		contentPane.add(tfFrom);
		tfFrom.setColumns(10);
		
		JButton btnSelectZip = new JButton("\u6D4F\u89C8");
		btnSelectZip.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		btnSelectZip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//用FileChooser类选择要解压的Zip文件。
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY); //模式设置为只能选文件
				fc.showOpenDialog(null); //设置起始目录，null为默认目录。
				zipFile = fc.getSelectedFile(); // getSelectedFile方法返回FileChooser类选定的文件。
				if (zipFile == null) return;
				String text = zipFile.getAbsolutePath();
				if (!".zip".equals(
						text.substring(text.lastIndexOf("."), text.length()).toLowerCase()
						)) {
					JOptionPane.showMessageDialog(null, "选择的文件不是.zip文件！");
					return; //选定的不是Zip文件，返回。
				}
				tfFrom.setText(text); //选定文件后，写到tfFrom里
			}
		});
		btnSelectZip.setBounds(290, 59, 123, 29);
		contentPane.add(btnSelectZip);
		
		JLabel label1 = new JLabel("\u9009\u62E9\u6587\u4EF6\uFF1A");
		label1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		label1.setBounds(15, 15, 108, 29);
		contentPane.add(label1);
		
		label2 = new JLabel("\u89E3\u538B\u5230\uFF1A");
		label2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		label2.setBounds(15, 114, 108, 29);
		contentPane.add(label2);
		
		tfTo = new JTextField();
		tfTo.setEditable(false);
		tfTo.setColumns(10);
		tfTo.setBounds(15, 158, 245, 29);
		contentPane.add(tfTo);
		
		btnSelectDic = new JButton("\u6D4F\u89C8");
		btnSelectDic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //模式设置为只能选目录
				fc.showOpenDialog(null);
				dicFile = fc.getSelectedFile();
				if (dicFile == null) return;
				tfTo.setText(dicFile.getAbsolutePath());
			}
		});
		btnSelectDic.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		btnSelectDic.setBounds(290, 158, 123, 29);
		contentPane.add(btnSelectDic);
		
		btnNewButton = new JButton("\u5F00\u59CB\u89E3\u538B");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (zipFile == null)
					JOptionPane.showMessageDialog(null, "请选择要解压的文件！");
				else if (dicFile == null) 
					JOptionPane.showMessageDialog(null, "清选择解压到的目录！");
				else {
					int status = doDecompress();
					if (status == SUCCESS) 
						JOptionPane.showMessageDialog(null, "解压成功！");
					else
						JOptionPane.showMessageDialog(null, "解压失败！");
				}
			}
		});
		btnNewButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		btnNewButton.setBounds(162, 213, 123, 29);
		contentPane.add(btnNewButton);
		
	}
}
