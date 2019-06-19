//Serialization to JDOM document
//Reference: Java Reflection in action Forman and Forman

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.Map;
import org.jdom2.*;

public class Serializer 
{
	private Map table = new IdentityHashMap();
	private static Document doc = new Document(new Element("serialized"));
	
	public Serializer()
	{
	}
	
	public Document serialize(Object obj) throws Exception
	{
		String id = Integer.toString(table.size());
		table.put(obj, id);
		Class objClass = obj.getClass();
		Element objElement = new Element("object");
		objElement.setAttribute("class", objClass.getName());
		objElement.setAttribute("id", id);
		doc.getRootElement().addContent(objElement);
		
		if (objClass.isArray())
		{
			Class componentType = objClass.getComponentType();
			int length = Array.getLength(obj);
			objElement.setAttribute("length", Integer.toString(length));
			
			for (int i=0; i<length; i++)
			{
				objElement.addContent(variable(componentType, Array.get(obj, i)));
			}
		}
		else
		{
			Field[] fields = objClass.getDeclaredFields();
			Field currentField;
			for(int i=0; i<fields.length; i++)
			{
				currentField = fields[i];
				if(!Modifier.isPublic(currentField.getModifiers()))
				{
					currentField.setAccessible(true);
				}
				if(Modifier.isStatic(currentField.getModifiers()))
				{
					continue;
				}
				
				Element fieldElement = new Element("field");
				fieldElement.setAttribute("name", currentField.getName());
				fieldElement.setAttribute("declaringclass", currentField.getDeclaringClass().getName());
				
				Class fieldType = currentField.getType();
				Object fieldObject = currentField.get(obj);
				
				fieldElement.addContent(variable(fieldType, fieldObject));
				objElement.addContent(fieldElement);
			}
		}
	
		return doc;
	}
	
	private Element variable(Class type, Object child) throws Exception 
	{		
		if (child==null)
		{
			return new Element("null");
		}
		else if (type.isPrimitive()) 
		{
			Element value = new Element("value");
			value.setText(child.toString());
			return value;
		} 
		else 
		{
			Element ref = new Element("reference");
			if(table.containsKey(child))
			{
				ref.setText(table.get(child).toString());
			}
			else 
			{
				ref.setText(Integer.toString(table.size()));
				serialize(child);
			}
			return ref;
		}
	}
}
