package com.sinosoft.utility;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.sinosoft.lis.encrypt.LisIDEA;
public class EncodePD extends JFrame implements ActionListener,FocusListener{

	private JButton read,change,save;
	private JLabel lDBType,lIP,lPort,lDBName,lUserName,lPassWord;
	private JTextField tDBType,tIP,tPort,tDBName,tUserName;
	private JPasswordField tPassWord;
	private Font font;
	private File file;
	private String[] info;
	private boolean[] changeInfo;
	private JRadioButton radio1,radio2;
	private ButtonGroup group;
	private Document doc;
	private SAXReader reader;

	public EncodePD(){
		super("生成或修改配置文件");
		this.font = new Font("宋体",Font.PLAIN,12);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.init();
		
		Container content = this.getContentPane();
		content.setLayout(new BorderLayout());
		//content.add(BorderLayout.NORTH,this.read);
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(7,2));
		center.add(this.lDBType);
		center.add(this.tDBType);
		center.add(this.lIP);
		center.add(this.tIP);
		center.add(this.lPort);
		center.add(this.tPort);
		center.add(this.lDBName);
		center.add(this.tDBName);
		center.add(this.lUserName);
		center.add(this.tUserName);
		center.add(this.lPassWord);
		center.add(this.tPassWord);
		center.add(this.radio1);
		center.add(this.radio2);
		content.add(BorderLayout.CENTER,center);
		JPanel bottom = new JPanel();
		bottom.add(this.read);
		bottom.add(this.change);
		bottom.add(this.save);
		content.add(BorderLayout.SOUTH,bottom);
		int width = 350,height=300;
		int ScreenWidth=this.getToolkit().getScreenSize().width;
		int ScreenHeight = this.getToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setLocation((ScreenWidth-this.getWidth())/2,(ScreenHeight-this.getHeight())/2);
		this.show();
		info = new String[6];
		this.setInfo();
		this.setChangeInfo();
	}
	
	private void init(){
		this.read = new JButton("读取已有配置文件");
		this.change = new JButton("修改配置文件");
		this.save = new JButton("保存修改");
		
		this.lDBType = new JLabel("    数据库类型：");
		this.lIP = new JLabel("    服务器IP：");
		this.lPort = new JLabel("    端口号：");
		this.lDBName = new JLabel("    数据库服务名：");
		this.lUserName = new JLabel("    用户名：");
		this.lPassWord = new JLabel("    密码：");
		
		this.tDBType = new JTextField("ORACLE");
		this.tIP = new JTextField("");
		this.tPort = new JTextField("1521");
		this.tDBName = new JTextField("");
		this.tUserName = new JTextField("");
		this.tPassWord = new JPasswordField("123456");
		
		this.radio1 = new JRadioButton("修改已有配置文件");
		this.radio2 = new JRadioButton("新建配置文件");
		
		this.group = new ButtonGroup();
		this.group.add(this.radio1);
		this.group.add(this.radio2);
		
		this.read.setFont(this.font);
		this.change.setFont(this.font);
		this.save.setFont(this.font);

		this.lDBType.setFont(this.font);
		this.lIP.setFont(this.font);
		this.lPort.setFont(this.font);
		this.lDBName.setFont(this.font);
		this.lUserName.setFont(this.font);
		this.lPassWord.setFont(this.font);

		this.tDBType.setFont(this.font);
		this.tIP.setFont(this.font);
		this.tPort.setFont(this.font);
		this.tDBName.setFont(this.font);
		this.tUserName.setFont(this.font);
		this.tPassWord.setFont(this.font);
		
		this.radio1.setFont(this.font);
		this.radio2.setFont(this.font);
		this.radio1.setSelected(true);
		
		this.setEditable(false);
		
		this.read.addActionListener(this);
		this.change.addActionListener(this);
		this.save.addActionListener(this);
		
		this.tDBType.addFocusListener(this);
		this.tIP.addFocusListener(this);
		this.tPort.addFocusListener(this);
		this.tDBName.addFocusListener(this);
		this.tUserName.addFocusListener(this);
		this.tPassWord.addFocusListener(this);

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EncodePD mypd = new EncodePD();

	}

	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成方法存根
		if(e.getSource()==this.read){
			this.readConfigFile();
			
		}else if(e.getSource()==this.change){
			this.setEditable(true);
		}else if(e.getSource()==this.save){
			boolean hasChange = false;
			for(int i=0;i<6;i++){
				if(this.changeInfo[i]){
					hasChange = true;
				}
			}
			if(this.radio1.isSelected() && !hasChange){
				JOptionPane.showMessageDialog(this.getContentPane(),"您没有作出任何修改，不需要保存！","Infomation",JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if(this.hasEmpty()){
				JOptionPane.showMessageDialog(this.getContentPane(),"所有配置项均不能为空，请输入相关信息！","Infomation",JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if(this.radio1.isSelected()){
				if(this.file==null){
					int ans = JOptionPane.showConfirmDialog(this.getContentPane(),"您没有读取任何已有的配置文件，是否新建一个配置文件？","确认对话框",JOptionPane.YES_NO_CANCEL_OPTION);
					if(ans==JOptionPane.YES_OPTION){
						this.saveConfigFile(true);
					}else{
						return;
					}
				}else{
					this.saveConfigFile(false);
				}
			}else{
				this.saveConfigFile(true);
			}
			this.setEditable(false);
		}
	}
	
	private void readConfigFile(){
		JFileChooser jfc = new JFileChooser();
		this.file = new File(System.getProperty("user.home")+"\\桌面");
		jfc.setCurrentDirectory(this.file);
		jfc.showOpenDialog(this.getContentPane());
		this.file = jfc.getSelectedFile();
		if(this.file==null){
			JOptionPane.showMessageDialog(this.getContentPane(),"您没有选择任何配置文件！","Infomation",JOptionPane.INFORMATION_MESSAGE);
			return;
		}else if(!this.file.getName().endsWith(".xml")){
			JOptionPane.showMessageDialog(this.getContentPane(),"配置文件必须为xml文件！","Infomation",JOptionPane.WARNING_MESSAGE);
			return;
		}else if(!this.file.canRead()){
			JOptionPane.showMessageDialog(this.getContentPane(),"配置文件不可读！\n可能原因:\n\t1、配置文件只读；\n\t2、配置文件正在被别的程序修改。","Infomation",JOptionPane.WARNING_MESSAGE);
			return;
		}else if(!this.file.canWrite()){
			JOptionPane.showMessageDialog(this.getContentPane(),"配置文件不可写！\n可能原因:\n\t1、配置文件只读；\n\t2、配置文件正在被别的程序修改。","Infomation",JOptionPane.WARNING_MESSAGE);
			return;
		}
		reader = new SAXReader();
		reader.setEncoding("GB2312");
		try {
			doc = reader.read(this.file);
			Element root = doc.getRootElement();
			Element foo;
			Iterator i;
			String temp1="",temp2="";
			for (i = root.elementIterator();i.hasNext();){
				foo = (Element)i.next();
				temp1 = foo.getName().toLowerCase();
				temp2 = foo.getTextTrim();
				if(temp2.length()%16==0){
					temp2 = this.decryptString(temp2);
				}
				if(temp1.equals("dbtype")){
					this.tDBType.setText(temp2);
				}else if(temp1.equals("ip")){
					this.tIP.setText(temp2);
				}else if(temp1.equals("port")){
					this.tPort.setText(temp2);
				}else if(temp1.equals("dbname")){
					this.tDBName.setText(temp2);
				}else if(temp1.equals("username")){
					this.tUserName.setText(temp2);
				}else if(temp1.equals("password")){
					this.tPassWord.setText(temp2);
				}
			}
			i = null;
			foo = null;
			root = null;
			reader = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		this.setInfo();
		this.setChangeInfo();
		this.radio1.setSelected(true);
		this.radio2.setSelected(false);
	}
	private boolean saveConfigFile(boolean createflag){
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GB2312");
		String[] newInfo = this.getInfo();
		if(createflag){
			String[] items = {"DBType","IP","Port","DBName","UserName","PassWord"};
			this.doc = DocumentHelper.createDocument();
			Element root = this.doc.addElement( "config" );
			Element foo;
			for(int i=0;i<6;i++){
				foo = root.addElement(items[i]);
				foo.setText(this.encryptString(newInfo[i]));
			}
			JFileChooser jfc = new JFileChooser();
			this.file = null;
			boolean continueflag = true;
			while(continueflag){
				jfc.showSaveDialog(this.getContentPane());
				this.file = jfc.getSelectedFile();
				if( this.file==null || (this.file!=null && this.file.getName().endsWith(".xml"))){
					continueflag = false;
				}else{
					if(this.file!=null && !this.file.getName().endsWith(".xml") && this.file.getName().lastIndexOf(".")!=-1){
						JOptionPane.showMessageDialog(this.getContentPane(),"输入的文件名字必须以“.xml”为扩展名字！","Infomation",JOptionPane.WARNING_MESSAGE);
					}else if(this.file!=null && this.file.getName().lastIndexOf(".")==-1){
						this.file = new File(this.file.getAbsolutePath()+".xml");
						continueflag = false;
					}
					
				}
			}
			if(this.file!=null){
				FileWriter fileWriter;
				try {
					fileWriter = new FileWriter(this.file);
					XMLWriter xmlWriter = new XMLWriter(fileWriter,format);
					xmlWriter.write(this.doc);
					xmlWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
					JOptionPane.showMessageDialog(this.getContentPane(),"创建配置文件时出错！","Infomation",JOptionPane.ERROR_MESSAGE);
					return false;
				}
				JOptionPane.showMessageDialog(this.getContentPane(),"成功创建配置文件！","Infomation",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(this.getContentPane(),"您没有选择保存操作！","Infomation",JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}else{
			Element root = doc.getRootElement();
			Element foo;
			Iterator i;
			int j=0;
			for (i = root.elementIterator(),j=0;i.hasNext();j++){
				foo = (Element)i.next();
				foo.setText(this.encryptString(newInfo[j]));
			}
			i = null;
			foo = null;
			root = null;
			reader = null;
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(this.file);
				XMLWriter xmlWriter = new XMLWriter(fileWriter,format);
				xmlWriter.write(this.doc);
				xmlWriter.close();
				fileWriter.close();
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
				JOptionPane.showMessageDialog(this.getContentPane(),"修改配置文件时出错！","Infomation",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			JOptionPane.showMessageDialog(this.getContentPane(),"成功修改配置文件！","Infomation",JOptionPane.INFORMATION_MESSAGE);
		}
		this.setChangeInfo();
		this.setInfo();
		return true;
	}
	private String encryptString(String planText){
		
		LisIDEA lisieda = new LisIDEA();
    	String encrypt="";
    	int strlen=planText.length(),len=0;
    	if(strlen%8==0){
    		len = strlen / 8;
    	}else if(strlen%8>0){
    		len = strlen / 8 + 1;
    	}
    	for(int i=0;i<len;i++){
    		if(i==len-1){
    			encrypt += lisieda.encryptString(planText.substring(i*8, strlen));
    		}else{
    			encrypt += lisieda.encryptString(planText.substring(i*8, (i+1)*8));
    		}
    	}
    	return encrypt;
	}
	private String decryptString(String encryptText){
		
		LisIDEA lisieda = new LisIDEA();
    	String decrypt="";
    	int strlen=encryptText.length(),len=0;
    	strlen=encryptText.length();
    	if(strlen%16==0){
    		len = strlen / 16;
    	}else if(strlen%16>0){
    		len = strlen / 16 + 1;
    	}
    	for(int i=0;i<len;i++){
    		decrypt += lisieda.decryptString(encryptText.substring(i*16, (i+1)*16));
    	}
    	return decrypt;
	}
	
	private void setEditable(boolean bool){
		this.tDBType.setEditable(bool);
		this.tIP.setEditable(bool);
		this.tPort.setEditable(bool);
		this.tDBName.setEditable(bool);
		this.tUserName.setEditable(bool);
		this.tPassWord.setEditable(bool);
	}

	public void focusGained(FocusEvent e) {
		// TODO 自动生成方法存根
		//JOptionPane.showMessageDialog(this.getContentPane(),"Get","Infomation",JOptionPane.INFORMATION_MESSAGE);
	}

	public void focusLost(FocusEvent e) {
		// TODO 自动生成方法存根
		//JOptionPane.showMessageDialog(this.getContentPane(),"Lost","Infomation",JOptionPane.INFORMATION_MESSAGE);
		if(e.getSource()==this.tDBType && this.tDBType.isEditable()){
			
		}else if(e.getSource()==this.tDBType && this.tDBType.isEditable()){
			if(!this.tDBType.getText().trim().equals(this.info[0])){
				this.changeInfo[0] = true;
			}else{
				this.changeInfo[0] = false;
			}
		}else if(e.getSource()==this.tIP && this.tIP.isEditable()){
			if(!this.tIP.getText().trim().equals(this.info[1])){
				this.changeInfo[1] = true;
			}else{
				this.changeInfo[1] = false;
			}
		}else if(e.getSource()==this.tPort && this.tPort.isEditable()){
			if(!this.tDBType.getText().trim().equals(this.info[2])){
				this.changeInfo[2] = true;
			}else{
				this.changeInfo[2] = false;
			}
		}else if(e.getSource()==this.tDBName && this.tDBName.isEditable()){
			if(!this.tDBType.getText().trim().equals(this.info[3])){
				this.changeInfo[3] = true;
			}else{
				this.changeInfo[3] = false;
			}
		}else if(e.getSource()==this.tUserName && this.tUserName.isEditable()){
			if(!this.tDBType.getText().trim().equals(this.info[4])){
				this.changeInfo[4] = true;
			}else{
				this.changeInfo[4] = false;
			}
		}else if(e.getSource()==this.tPassWord && this.tPassWord.isEditable()){
			if(!String.valueOf(this.tPassWord.getPassword()).trim().equals(this.info[5])){
				this.changeInfo[5] = true;
			}else{
				this.changeInfo[5] = false;
			}
		}
	}
	private void setInfo(){
		this.info[0] = this.tDBType.getText().trim();
		this.info[1] = this.tIP.getText().trim();
		this.info[2] = this.tPort.getText().trim();
		this.info[3] = this.tDBName.getText().trim();
		this.info[4] = this.tUserName.getText().trim();
		this.info[5] = String.valueOf(this.tPassWord.getPassword()).trim();
	}
	private String[] getInfo(){
		String[] newInfo = new String[6];
		newInfo[0] = this.tDBType.getText().trim();
		newInfo[1] = this.tIP.getText().trim();
		newInfo[2] = this.tPort.getText().trim();
		newInfo[3] = this.tDBName.getText().trim();
		newInfo[4] = this.tUserName.getText().trim();
		newInfo[5] = String.valueOf(this.tPassWord.getPassword()).trim();
		return newInfo;
	}
	private void setChangeInfo(){
		this.changeInfo = new boolean[6];
		for(int i=0;i<6;i++){
			this.changeInfo[i] = false;
		}
	}
	private boolean hasEmpty(){
		if(this.tDBType.getText().trim().equals("")){
			return true;
		}else if(this.tDBType.getText().trim().equals("")){
			return true;
		}else if(this.tIP.getText().trim().equals("")){
			return true;
		}else if(this.tPort.getText().trim().equals("")){
			return true;
		}else if(this.tDBName.getText().trim().equals("")){
			return true;
		}else if(this.tUserName.getText().trim().equals("")){
			return true;
		}else if(String.valueOf(this.tPassWord.getPassword()).trim().equals("")){
			return true;
		}
		return false;
	}
}

