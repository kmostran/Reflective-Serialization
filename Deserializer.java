//Deserialization from JDOM document
//Reference: Java Reflection in action Forman and Forman

import org.jdom2.*;
import org.jdom2.Document;
import org.jdom2.Element;
import java.util.*;
import java.lang.reflect.*;

public class Deserializer
{	
	public Map table = new HashMap();

    public Deserializer() 
	{ 
	}
	
	public Object deserialize(Document doc) throws Exception
	{
		List objects = doc.getRootElement().getChildren();
		createInstances(objects);
		assignFields(objects);
		return table.get("0");
	}
	
	private void createInstances(List objects) throws Exception
	{
		for(int i=0; i<objects.size(); i++)
		{
			Element objElement = (Element)objects.get(i);
			Class objClass = Class.forName(objElement.getAttributeValue("class"));
			Object instance = null;
			if (objClass.isArray())
			{
				instance = Array.newInstance(objClass.getComponentType(), Integer.parseInt(objElement.getAttributeValue("length")));
			}
			else
			{
				Constructor objConstructor = objClass.getDeclaredConstructor(null);
				if (!Modifier.isPublic(objConstructor.getModifiers()))
				{
					objConstructor.setAccessible(true);
				}
				instance = objConstructor.newInstance(null);
			}
			table.put(objElement.getAttributeValue("id"), instance);
		}
	}
	
	private void assignFields(List objects) throws Exception 
	{
		for(int i=0; i<objects.size(); i++) 
		{
			Element objElement = (Element)objects.get(i);
			Object instance = table.get(objElement.getAttributeValue("id"));
			List fieldElements = objElement.getChildren();
			
			if(instance.getClass().isArray()) 
			{
				for(int j=0; j<fieldElements.size();j++)
				{					
					Array.set(instance, j, value((Element)fieldElements.get(j), instance.getClass().getComponentType()));
				}
			} 
			else 
			{
				for(int j=0; j<fieldElements.size();j++) 
				{
					Element fieldElement = (Element)fieldElements.get(j);
					Class declaringClass = Class.forName(fieldElement.getAttributeValue("declaringclass"));
					String fieldName = fieldElement.getAttributeValue("name");
					Field field = declaringClass.getDeclaredField(fieldName);
					
					if(Modifier.isFinal(field.getModifiers()))
					{
						continue;
					}
					if(!Modifier.isPublic(field.getModifiers()))
					{
						field.setAccessible(true);
					}
						
					Element valueElement = (Element)fieldElement.getChildren().get(0);
					field.set(instance, value(valueElement, field.getType()));
				}
			}	
		}
	}
	
	private Object value(Element element, Class type) throws ClassNotFoundException
	{
		String valType = element.getName();
		if (valType.equals("null"))
		{
			return null;
		}
		else if (valType.equals("reference"))
		{
			return table.get(element.getText());
		}
		else 
		{
			if (type.equals(boolean.class))
			{
				if (element.getText().equals("true"))
				{
					return Boolean.TRUE;
				}
				else
				{
					return Boolean.FALSE;
				}
			}
			else if (type.equals(byte.class))
			{
				return Byte.valueOf(element.getText());
			}				
			else if (type.equals(char.class))
			{
				return new Character(element.getText().charAt(0));
			}
			else if (type.equals(short.class))
			{
				return Short.valueOf(element.getText());
			}
			else if (type.equals(long.class))
			{
				return Long.valueOf(element.getText());
			}
			else if (type.equals(int.class))
			{
				return Integer.valueOf(element.getText());
			}
			else if (type.equals(double.class))
			{
				return Double.valueOf(element.getText());
			}
			else if (type.equals(float.class))
			{
				return Float.valueOf(element.getText());
			}
			else 
			{
				return element.getText();
			}
		}
	}
}