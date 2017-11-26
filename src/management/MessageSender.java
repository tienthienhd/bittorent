package management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.swing.Timer;

import message.KeepAliveMessage;
import message.PeerMessage;

public class MessageSender implements HandleMessageSend {
	
	private OutputStream os;
	private LinkedList<PeerMessage> queueMessage;
	private Timer timer;
	private KeepAliveMessage keepAliveMessage;
	
	public MessageSender(OutputStream os) {
		this.os = os;
		this.queueMessage = new LinkedList<PeerMessage>();
		
		this.keepAliveMessage = KeepAliveMessage.craft();
		this.timer = new Timer(120*1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addMessage(keepAliveMessage);
				notifySend();
			}
		});
	}
	
	public void start() {
		this.timer.start();
	}
	
	public void stopSend() {
		this.timer.stop();
		try {
			this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void sendMessage(PeerMessage msg) {
//		System.out.println("Send Message From " + Thread.currentThread().getName() + ": " + msg.getType());
		try {
			os.write(msg.generateMessageToSend());
		} catch (IOException e) {
			this.timer.stop();
			e.printStackTrace();
		}
	}

	@Override
	public void notifySend() {
		while(!queueMessage.isEmpty()) {
			PeerMessage msg = queueMessage.remove();
			this.sendMessage(msg);
		}

	}
	
	

	@Override
	public void addMessage(PeerMessage msg) {
		this.queueMessage.add(msg);
	}

}
