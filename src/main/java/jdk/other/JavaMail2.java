package main.java.jdk.other;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class JavaMail2 {
	/**
     * Message对象将存储我们实际发送的电子邮件信息，
     * Message对象被作为一个MimeMessage对象来创建并且需要知道应当选择哪一个JavaMail session。
     */
	private MimeMessage message;
	/**
     * Session类代表JavaMail中的一个邮件会话。
     * 每一个基于JavaMail的应用程序至少有一个Session（可以有任意多的Session）。
     * 
     * JavaMail需要Properties来创建一个session对象。
     * 寻找"mail.smtp.host"    属性值就是发送邮件的主机
     * 寻找"mail.smtp.auth"    身份验证，目前免费邮件服务器都需要这一项
     */
	private Session session;
	/***
     * 邮件是既可以被发送也可以被受到。JavaMail使用了两个不同的类来完成这两个功能：Transport 和 Store。 
     * Transport 是用来发送信息的，而Store用来收信。
     */
	private Transport transport;
	private Store store;
	/**
	 * 邮件配置文件
	 */
	private Properties properties = new Properties();
	private String hostName;
	private String mailUserName;
	private String mailPassword;
	
	public static void main(String []args) throws IOException{
		JavaMail2 jm = new JavaMail2(true);
		jm.senderMail("邮件测试", "Hello World!", "923847088@qq.com");
	}
	
	/**
	 * 初始化方法
	 * @param debug
	 */
	public JavaMail2(){
		
	}
	public JavaMail2(boolean debug) throws IOException{
		InputStream fis = new FileInputStream("mail.properties");
	    properties.load(fis);
		this.session = Session.getDefaultInstance(properties);
		session.setDebug(debug);
		this.message = new MimeMessage(session);
	}
	
	public void senderMail(String subject, String content, String recName){
		try {
			// 下面这个是设置发送人的Nick name
			InternetAddress from = new InternetAddress(MimeUtility.encodeWord("东方")+"<"+mailUserName+">");
			message.setFrom(from);
			
			// 收件人,收件类型
			InternetAddress to = new InternetAddress(recName);
			message.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件主题
			message.setSubject(subject);
			// 邮件内容
			message.setContent(content, "text/html;charset=UTF-8");
			// 保存邮件
			message.saveChanges();
			// 连接smtp邮件服务器
			transport = session.getTransport("smtp");
			transport.connect(hostName, mailUserName, mailPassword);
			// 发送邮件
			transport.sendMessage(message, message.getAllRecipients());
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(MessagingException e){
			e.printStackTrace();
		} finally{
			if(transport != null){
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
