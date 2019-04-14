
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
*
* @author Shahar & Tomer
*/
public class Client implements Runnable
{
	String name;
	String socketAddr;
	String msgServer="";
	String namesFServer="";
	private final String TO="to: ";
	Socket socket = null;       //Socket object for communicating
	PrintWriter out = null;    //socket output to server - for sending data through the socket to the server
	BufferedReader br = null;
	InputStreamReader in=null;
	private javax.swing.JTextArea mainTXTA ; 
	private javax.swing.JTextArea connectedTXTA;
	private javax.swing.JComboBox<String> ConnectedCB;
	boolean success=false;

	/**@param name client name<br>
	 * @param socketAddr socket  address<br>
	 * @param mainTXTA pointer to main chat window<br>
	 * @param connectedTXTA pointer to connected users window  **/ 
	public Client(String name,String socketAddr,javax.swing.JTextArea mainTXTA,javax.swing.JTextArea connectedTXTA,javax.swing.JComboBox<String> ConnectedCB ) 
	{
		this.name = name;
		this.socketAddr = socketAddr;
		this.mainTXTA=mainTXTA;
		this.connectedTXTA=connectedTXTA;
		this.ConnectedCB= ConnectedCB;
	}
	/**Used for getting the name of the client**/
	public String getName() 
	{
		return name;
	}
	/**Used for getting the socket address of the client**/
	public String getSocketAddr() 
	{
		return socketAddr;
	}

	/**Void method that establish the very first connection to the server,
	 *<br>Throws:UnknownHostException,IOException,NullPointerException
	 *<br>Creates PrintWriter,InpputStreamReader and BufferedReader to the current socket **/
	public void StartClient ()
	{
		try 
		{
			socket = new Socket(socketAddr, 45000);   //establish the socket connection between the client and the server
			out = new PrintWriter(socket.getOutputStream(), true);  //open a PrintWriter on the socket
			in=new InputStreamReader(socket.getInputStream());
			br = new BufferedReader(in);  //open a BufferedReader on the socket
			success=true;

		} catch (UnknownHostException e) 
		{
			mainTXTA.append("could not find this host- try another \n".toUpperCase());
		} catch (IOException e) 
		{
			mainTXTA.append("could not connect to this host- try another \n".toUpperCase());
		}
		catch (NullPointerException e) 
		{
			mainTXTA.append("null \n".toUpperCase());
		}
	}

	/**Void method that stop the connection to the server,
	 * <br>Throws:IOException
	 * <br>Close PrintWriter,InpputStreamReader of current socket **/
	public void StopClient ()
	{
		out.println("Disconnect: "+name);
		try
		{
			out.close();
			in.close();
			socket.close();
		} catch (IOException ex)
		{
			System.exit(1);
		}
	}
	/**Void method that sends the client message to Everyone
	 * <br>gets 1 string (the message)**/
	public void sendMsg(String msg)
	{
		if(this!=null && msg!=null)
		{
			out.println(msg);  
		}
	}
	/**Void method that sends the client private message
	 * <br>gets 2 strings (the message,name of the receiver) **/
	public void sendPrivateMsg(String msg,String name)
	{
		if(this!=null && msg!=null && name!=null)
		{
			out.println(msg+TO+name);    
		}
	}
	/**Void method that sends the client name to the server
	 * <br>call immediately after establish connection with server!  **/
	public void sendName(String name)
	{
		if(out!=null && this!=null && name!=null)
		{
			out.println(name);
		}	
	}
	/**Runnable listener to message from the Server
	 * <br>Throws: IOException  **/
	@Override
	public void run() 
	{
		try 
		{
			while ((!socket.isClosed())&&(msgServer=br.readLine())!=null)
			{
				if(msgServer.startsWith("names:"))
				{
					connectedTXTA.setText("");
					msgServer= msgServer.subSequence(7, msgServer.length()-1).toString(); 
					msgServer=msgServer.replaceAll(", ", "\n");                                     
					connectedTXTA.append(msgServer);
					ConnectedCB.removeAllItems();
					ConnectedCB.addItem("Everyone");

					while(msgServer!=null && msgServer.length()>0)
					{
						if(msgServer.contains("\n"))
						{
							String nextName = msgServer.substring(0,msgServer.indexOf("\n"));
							ConnectedCB.addItem(nextName);        
							msgServer = msgServer.substring(msgServer.indexOf("\n")+1, msgServer.length());
						}
						else
						{
							ConnectedCB.addItem(msgServer);
							msgServer = "";
						}
					}
				}

				else
				{
					mainTXTA.append(msgServer+"\n");
				}
			}

			mainTXTA.append("You are offline \n".toUpperCase());
			success=false;
			StopClient();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			if (!socket.isClosed())
			{
				mainTXTA.append("SERVER IS DOWN \n");
			}
			else
			{
				mainTXTA.append("YOU ARE DISCONNECTED \n");
			}
			
			success=false;
			StopClient();
		}
	}
}
