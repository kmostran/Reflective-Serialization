//Receiving of byte stream

import org.jdom2.*;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.util.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;

public class Receiver
{
	public static ArrayList<Object> objects = new ArrayList<Object>();

	public static void main(String[] args) throws Exception
	{
		ObjectInspector objInspector = new ObjectInspector();
		Deserializer deserializer = new Deserializer();
		
		int port = 7777; //***
		ServerSocket socket = new ServerSocket(port);
		System.out.println("RECIEVER: Waiting for connection from client...");

		while(true) 
		{
			Socket connection = socket.accept();
			System.out.println("RECEIVER: received connection from client.");
			
			InputStream is = connection.getInputStream();
			SAXBuilder parser = new SAXBuilder();
			Document doc = null;

			try 
			{
				System.out.println("RECEIVER: Deserializing objects...");
				doc = parser.build(is);
				Object obj = deserializer.deserialize(doc);

				System.out.println("RECEIVER: Inspecting objects...");
				for(int i=0; i<deserializer.table.size(); i++)
				{
					System.out.println("RECEIVER: Inspecting object " + i);
					objInspector.inspect(deserializer.table.get(Integer.toString(i)), false);
				}				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			is.close();
			connection.close();
			System.exit(0);
		}
	}
}