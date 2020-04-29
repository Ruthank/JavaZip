import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class zip extends JFrame {

	private JPanel contentPane;
	private JLabel label2;
	private JButton btnSelectFile;
	private JButton btnNewButton;
	private JScrollPane Jsp;
	
	private File[] chosenFiles;
	private JTextArea tfFrom;
	private JTextArea ta;
	private String newZipName;
	private String newZipPath;
	private FileOutputStream fos;
	
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
					zip frame = new zip();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private int doZip() {
		if (chosenFiles.length == 0) return FAILED; //啥都没选
		
		//获取当前目录作为newZipPath
		newZipPath = chosenFiles[0].getAbsolutePath();
		int endIndex = newZipPath.length()-1;
		while(newZipPath.charAt(endIndex) != '\\' 
				&& newZipPath.charAt(endIndex) != '/') 
			endIndex--; //正反斜杠都判，保险
		newZipPath = newZipPath.substring(0, endIndex);
		
		try {
			//创建zip文件
			//zip规范 目录使用正斜杠，Entry.isDictionary()里判的就是正斜杠
			File newZip = new File(newZipPath + "/" + newZipName + ".zip"); 
			if (newZip.exists()) {
				//当前目录存在同名文件
				JOptionPane.showMessageDialog(null, "存在同名zip文件，请重新输入文件名！");
				return FAILED;
			} else
				newZip.createNewFile();
			
			//创建输出流
			fos = new FileOutputStream(newZip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			//调用compressDic压缩整个chosenFiles数组
			int status = compressDic(chosenFiles, zos, "");
			zos.close();
			return status;
		
		} catch (IOException ioe) {
			System.out.println(ioe);
			return ERROR;
		}
	}
	
	
	/**
	压缩同一目录下的多个文件/文件夹
	遇到文件夹递归调用
	**/
	//files 同一文件夹下的多个文件; zos ; pre 前缀目录
	private int compressDic(File[] files, ZipOutputStream zos, String pre){
		try {
			for (File src : files) {
				setTitle(src.getAbsolutePath());
				//如果是文件，直接putEntry压缩
				if (src.isFile()) {
					zos.putNextEntry(new ZipEntry(pre + src.getName()));
					FileInputStream fin = new FileInputStream(src);
					int c;
					while((c = fin.read()) != -1)
						zos.write(c);
					zos.closeEntry();
					fin.close();
				} else {
				//如果是文件夹
					File[] listFiles = src.listFiles();
					
					if (listFiles.length == 0) { 
						zos.putNextEntry(new ZipEntry(pre + src.getName() + "/")); //空文件夹，后面加个'/'和文件作为区分
						System.out.println(pre+src.getName()+"/");
						zos.closeEntry();
					} else { //非空，递归
						int status = compressDic(listFiles, zos, pre + src.getName() + "/");
						if (status != SUCCESS) return status;
					}
				}
			}
			setTitle("JavaZip-压缩文件");
		}
		catch (IOException ioe) {
			System.out.println(ioe);
			setTitle("JavaZip-压缩文件");
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Create the frame.
	 */
	public zip() {
		setTitle("JavaZip-\u538B\u7F29\u6587\u4EF6");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 459, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnSelectFile = new JButton("\u6D4F\u89C8");
		btnSelectFile.setBounds(290, 77, 123, 40);
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //可选文件和文件夹
				fc.setMultiSelectionEnabled(true); //可选多个
				fc.showOpenDialog(null);
				chosenFiles = fc.getSelectedFiles();
				if (chosenFiles.length == 0) return;
				String s = "";
				for (int i = 0; i < chosenFiles.length; i++)
					s = s + chosenFiles[i].getAbsolutePath() + "\n";
				tfFrom.setText(s);
			}
		});
		contentPane.setLayout(null);
		btnSelectFile.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		contentPane.add(btnSelectFile);
		
		JLabel label1 = new JLabel("\u9009\u62E9\u6587\u4EF6\u6216\u6587\u4EF6\u5939\uFF08\u6309\u4F4Fctrl\u53EF\u9009\u591A\u4E2A\uFF09\uFF1A");
		label1.setBounds(15, 15, 369, 29);
		label1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		contentPane.add(label1);
		
		label2 = new JLabel("\u65B0\u6587\u4EF6\u540D\uFF08\u4E0D\u9700\u8981\u52A0.zip\u540E\u7F00\uFF09\uFF1A");
		label2.setBounds(15, 149, 302, 29);
		label2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		contentPane.add(label2);
		
		btnNewButton = new JButton("\u5F00\u59CB\u538B\u7F29");
		btnNewButton.setBounds(154, 237, 123, 29);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chosenFiles == null)
					JOptionPane.showMessageDialog(null, "请选择要压缩的文件！");
				else if ((newZipName = ta.getText()).equals(""))
					JOptionPane.showMessageDialog(null, "请输入新文件名！");
				else {
					int status = doZip();
					if (status == SUCCESS) 
						JOptionPane.showMessageDialog(null, "压缩成功！");
					else
						JOptionPane.showMessageDialog(null, "压缩失败！");
				}
			}
		});
		btnNewButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		contentPane.add(btnNewButton);
		
		//JScroollPane类实现滚动条效果
		Jsp = new JScrollPane();
		Jsp.setBounds(15, 44, 245, 100);
		contentPane.add(Jsp);
		
		tfFrom = new JTextArea();
		tfFrom.setEditable(false);
		Jsp.setViewportView(tfFrom); //带滚动的TextArea
		
		ta = new JTextArea();
		ta.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
		ta.setBounds(15, 183, 407, 34);
		contentPane.add(ta);
	}
}
