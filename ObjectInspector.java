//Object Visualization
//Info lists strictly for unit testing purposes

import java.util.*;
import java.lang.reflect.*;
import java.util.ArrayList;

public class ObjectInspector
{	
	public String format = "%-20s %-30s %n";
	public boolean recursive;
	public ArrayList<String> inspectedObjects = new ArrayList<String>();
	public ArrayList<String> comprehensiveInfo = new ArrayList<String>();

    public ObjectInspector() 
	{ 
	}

    /// <summary>
    /// Print initial message and call inspectItem to begin inspection
    /// </summary>
    public ArrayList<String> inspect(Object obj, boolean rec)
    {
		System.out.println("------------------------------------------------------");
		recursive = rec;
		inspectedObjects = new ArrayList<String>();
		System.out.println("INSIDE INSPECTOR, " + " RECURSIVE = " + recursive); 
		inspectItem(obj);
		System.out.println("------------------------------------------------------");
		return comprehensiveInfo;
	}
	
	/// <summary>
    /// Call the relevant inspection method depending on if the object is an array or not
    /// </summary>
	public void inspectItem(Object obj)
	{
		if(obj.getClass().isArray())
		{
			inspectArray(null, obj);
		}
		else
		{
			inspectObject(obj, obj.getClass());
		}
	}

    /// <summary>
    /// Inspect a standard non array object
    /// </summary>	
	public void inspectObject(Object obj, Class objClass)
	{
		inspectClasses(obj, objClass);
		inspectMethods(objClass);
		inspectConstructors(objClass);
		inspectFields(obj, objClass);
	}
	
	/// <summary>
    /// Inspect and print information about the class's name, superclasses and interfaces
    /// </summary>	
	public ArrayList<String> inspectClasses(Object obj, Class objClass)
	{
		ArrayList<String> info = new ArrayList<String>();
		
		System.out.printf(format, "CLASS", objClass.getName());
		info.add(objClass.getName());
		inspectedObjects.add(objClass.getName());
		
		String objectName;
		if(objClass.getSuperclass() != null)
		{
			objectName = objClass.getSuperclass().getName();
			System.out.printf(format, "SUPERCLASS", objectName);
			info.add(objectName);
			//if (!previouslyInspected(objectName))
			//{
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				//System.out.println("INSPECTING SUPERCLASS");
				//inspectedObjects.add(objectName);
				//inspectObject(obj, objClass.getSuperclass());
				//System.out.println("INSPECTING SUPERCLASS DONE");
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			//}
			
		}
		
		Class[] interfaces = objClass.getInterfaces();
		for(int i=0; i<interfaces.length; i++)
		{
			objectName = interfaces[i].getName();
			System.out.printf(format, "INTERFACE", objectName);
			info.add(objectName);
			//if (!previouslyInspected(objectName))
			//{
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				//System.out.println("INSPECTING INTERFACE");
				//inspectedObjects.add(objectName);
				//inspectObject(obj, interfaces[i]);
				//System.out.println("INSPECTING INTERFACE DONE");
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			//}
		}
		System.out.println();	
		
		comprehensiveInfo.addAll(info);
		return info;
	}

	/// <summary>
    ///Inspect and print information about the class's methods, and their names, exceptions, parameters, return types and modifiers
    /// </summary>
	public ArrayList<String> inspectMethods(Class objClass)
	{
		ArrayList<String> info = new ArrayList<String>();

		Method[] methods = objClass.getDeclaredMethods();
		for(int i=0; i<methods.length; i++)
		{
			Method currentMethod = methods[i];
			System.out.printf(format, "METHOD", currentMethod.getName());
			info.add(currentMethod.getName());
			
			Class[] exceptions = currentMethod.getExceptionTypes();
			for (int j=0; j<exceptions.length; j++) 
			{
				System.out.printf(format, "THROWS EXCEPTION", exceptions[j].getName());
				info.add(exceptions[j].getName());
			}
			
			Class[] parameters = currentMethod.getParameterTypes();
			for (int j=0; j<parameters.length; j++) 
			{
				System.out.printf(format, "PARAMETER TYPE", parameters[j].getName());
				info.add(parameters[j].getName());
			}

			System.out.printf(format, "RETURN TYPE", currentMethod.getReturnType().getName());
			info.add(currentMethod.getReturnType().getName());
			
			System.out.printf(format, "MODIFIERS", Modifier.toString(currentMethod.getModifiers()) + "\n");
			info.add(Modifier.toString(currentMethod.getModifiers()));
		}

		comprehensiveInfo.addAll(info);
		return info;
	}
	
	/// <summary>
    /// Inspect and print information about the class's constructors, and their names, parameters and modifiers
    /// </summary>
	public ArrayList<String> inspectConstructors(Class objClass)
	{
		ArrayList<String> info = new ArrayList<String>();

		Constructor constructors[] = objClass.getDeclaredConstructors();
        for(int i=0; i<constructors.length; i++) 
		{
			Constructor currentConstructor = constructors[i];
			System.out.printf(format, "CONSTRUCTOR", currentConstructor.getName());
			info.add(currentConstructor.getName());
			
			Class[] parameters = currentConstructor.getParameterTypes();
			for (int j=0; j<parameters.length; j++) 
			{
				System.out.printf(format, "PARAMETER TYPES", parameters[j].getName());
				info.add(parameters[j].getName());
			}

			System.out.printf(format, "MODIFIERS", Modifier.toString(currentConstructor.getModifiers()) + "\n");
			info.add(Modifier.toString(currentConstructor.getModifiers()));
		}	

        comprehensiveInfo.addAll(info);
		return info;
	}
	
	/// <summary>
    /// Inspect and print information about the class's fields, and their names, types, modifiers and parameters
    /// </summary>
	public ArrayList<String> inspectFields(Object obj, Class objClass)
	{
		ArrayList<String> info = new ArrayList<String>();

		Field[] fields = objClass.getDeclaredFields();
        for(int i=0; i<fields.length; i++) 
		{
			try
			{
				Field currentField = fields[i];
				currentField.setAccessible(true);
				
				System.out.printf(format, "FIELD", currentField.getName());
				info.add(currentField.getName());
				
				if (currentField.getType().isArray())
				{
					inspectArray(currentField, currentField.get(obj));
					System.out.println();
					continue;
				}
				
				System.out.printf(format, "TYPE", currentField.getType().getName());
				info.add(currentField.getType().getName());

				System.out.printf(format, "MODIFIERS", Modifier.toString(currentField.getModifiers()));	
				info.add(Modifier.toString(currentField.getModifiers()));
				
				if(currentField.getType().isPrimitive())
				{
					System.out.printf(format, "VALUE", currentField.get(obj));	
					info.add(currentField.get(obj).toString());
				}
				else
				{
					Object currentObject =  currentField.get(obj);
					if (currentObject == null)
					{
						System.out.printf(format, "VALUE", "null");
						info.add("null");
					}
					else if (currentField.getType().getName().equals("java.util.ArrayList"))
					{
						System.out.println("######################################################");
						System.out.println("INSPECTING ARRAYLIST");
						inspectArrayList(currentObject);
						System.out.println("INSPECTING ARRAYLIST DONE");
						System.out.println("######################################################");
					}
					else
					{
						System.out.printf(format, "VALUE", currentField.getType().getName() + " " + System.identityHashCode(currentField));
						info.add(currentField.getType().getName() + " hash");
						if (recursive)
						{
							String objectName = currentField.getType().getName();
							//if (!previouslyInspected(objectName))
							//{
								System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
								System.out.println("INSPECTING FIELD");
								inspectedObjects.add(objectName);
								inspectItem(currentObject);
								System.out.println("INSPECTING FIELD DONE");
								System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							//}
						}
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			System.out.println();
		}		
        
        comprehensiveInfo.addAll(info);
		return info;
	}

    /// <summary>
    /// Inspect and print information about the an array object, including the array's name, type, length and contents
    /// </summary>	
	public ArrayList<String> inspectArray(Field field, Object obj)
	{
		ArrayList<String> info = new ArrayList<String>();

		System.out.println("######################################################");
		System.out.println("INSPECTING ARRAY");

		if (field == null)
		{
			System.out.printf(format, "NAME", "none");
			info.add("none");
		}
		else
		{
			System.out.printf(format, "NAME", field.getName());
		}	

		System.out.printf(format, "COMPONENT TYPE", obj.getClass().getComponentType().getName());
		info.add(obj.getClass().getComponentType().getName());
		
		int length = Array.getLength(obj);
		System.out.printf(format, "LENGTH", length);
		info.add(Integer.toString(length));
		
		if(length == 0)
		{
			System.out.printf(format, "CONTENTS", "Emtpy Array");
			info.add("Emtpy Array");
		}
		else
		{
			String contents = "";
			for(int i=0; i<length; i++)
			{
				Object element = Array.get(obj, i);
				if(element == null) 
				{
					contents += "[null] ";
				} 
				else 
				{
					contents += "[" + element + "] ";
				}
			}
			System.out.printf(format, "CONTENTS", contents);
		}
		
		for(int i=0; i<length; i++)
		{
			Object element = Array.get(obj, i);
			if(element == null) { }
			else if((!obj.getClass().getComponentType().isPrimitive()) && recursive)
			{
				String objectName = element.getClass().getName();

				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println("INSPECTING ARRAY ELEMENT");
				inspectedObjects.add(objectName);
				inspectItem(element);
				System.out.println("INSPECTING ARRAY ELEMENT DONE");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");	
			}	
		}

		System.out.println("INSPECTING ARRAY DONE");
		System.out.println("######################################################");
		
		comprehensiveInfo.addAll(info);
		return info;
	}

    /// <summary>
    /// Check to see if an object has already been inspected
	/// Method no longer used, but can be easily used if behaviour of inspector is to be changed
    /// </summary>	
	public boolean previouslyInspected(String object)
	{
		for(int i=0; i<inspectedObjects.size(); i++)
		{
			if (inspectedObjects.get(i).equals(object))
			{
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> inspectArrayList(Object obj)
	{
		ArrayList<String> info = new ArrayList<String>();

		String contents = "";
		int i = 0;
		String componentType = "Empty Array";
		for (Object currentObj : (List<?>)obj) 
		{
			contents += "[" + currentObj.toString() + "]";
			i++;	
			if (currentObj != null)
			{
				componentType = currentObj.getClass().getName();
			}
		}

		System.out.printf(format, "LENGTH", i);
		info.add(Integer.toString(i));
		System.out.printf(format, "COMPONENT TYPE", componentType);
		info.add(componentType);
		System.out.printf(format, "CONTENTS", contents);
		info.add(contents);
		
		for (Object currentObj : (List<?>)obj) 
		{
			if ((currentObj != null) && recursive)
			{
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println("INSPECTING ARRAYLIST ELEMENT");
				inspectedObjects.add(currentObj.getClass().getName());
				inspectItem(currentObj);
				System.out.println("INSPECTING ARRAYLIST ELEMENT DONE");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			}			
		}
		
		comprehensiveInfo.addAll(info);
		return info;
	}
}
