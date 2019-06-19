//Sending of byte stream

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.Socket;

public class Sender 
{
	public static ArrayList<Object> objects = new ArrayList<Object>();

	public static void main(String[] args) throws NoSuchMethodException, SecurityException 
	{
		ObjectCreator objCreator = new ObjectCreator();
		objects = objCreator.getObjects();
		Serializer serializer = new Serializer();
		Document doc = null;

		String host = "localhost"; //***
		int port = 7777; //***

		try
		{
			InetAddress address = InetAddress.getByName(host);
			Socket connection = new Socket(address, port);
			OutputStream os = connection.getOutputStream();
			
			for(Object obj : objects)
			{
				doc = serializer.serialize(obj);	
			}

			System.out.println("SENDER: Objects created, sending data...");
			XMLOutputter out = new XMLOutputter();
			out.setFormat(Format.getPrettyFormat());
			out.output(doc, os);
			//out.output(doc, System.out);//Testing
			
			os.flush();
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}