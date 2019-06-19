//Object Creation
//Allows the user to create one or more objects from a selection of objects using some sort of text-based menu system or GUI.
//Can create:
//Simple object
//Object with references to other objects
//Object with array of primitives
//Object with array of references
//Object using collection instance

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class ObjectCreator
{	
	private ArrayList<Object> objects = new ArrayList<Object>();

    public ObjectCreator() 
	{
		initialize();
	}
	
	private void initialize()
	{
		printInitialMenu();
		Object obj = createObject(selectObject(), true);
		objects.add(obj);

		System.out.print("Enter 'c' to add another object, press enter to finish adding objects: ");
		Scanner scanner = new Scanner(System.in);
		String selection = scanner.nextLine();
		if (selection.equals("c"))
			initialize();
		
		//ObjectInspector objInspector = new ObjectInspector();//Testing
		//objInspector.inspect(obj, true);//Testing
	}

	private void printInitialMenu()
	{
		System.out.println("Which object would you like to create?");
		System.out.println("Marksman: simple object with primitives for instance variables");
		System.out.println("Squad: object that contains references to other objects");
		System.out.println("Skills: object that contains an array of primitives");
		System.out.println("Faction: object that contains an array of object references");
		System.out.println("Army: object that uses an instance of one of Java's collection classes (arraylist)");		
	}

	private String selectObject()
	{
		System.out.print("Specify object: ");
		Scanner scanner = new Scanner(System.in);
		String selection = scanner.nextLine();
		
		if (!(selection.equals("Marksman") || 
			  selection.equals("Squad") || 
			  selection.equals("Skills") || 
			  selection.equals("Faction") || 
			  selection.equals("Army")))
		{
			System.out.println("Input invalid");
			return selectObject();
		}
		
		return selection;
	}
	
	private Object createObject(String selection, boolean recursive)
	{
		Object obj = new Object();
		if(selection.equals("Marksman"))
		{
			System.out.println("Creating Marksman Object...");
			obj = new Marksman();
		}
		else if(selection.equals("Squad"))
		{
			System.out.println("Creating Squad Object...");
			obj = new Squad();
		}
		else if(selection.equals("Skills"))
		{
			System.out.println("Creating Skills Object...");
			obj = new Skills();
		}
		else if(selection.equals("Faction"))
		{
			System.out.println("Creating Faction Object...");
			obj = new Faction();
		}
		else if(selection.equals("Army"))
		{
			//Special case: collection
			System.out.println("Creating Army Object...");
			return newArmyObject();
		}
		Object[] fieldValues = getFieldInput(obj, recursive);
		createObjectWithFields(obj, fieldValues);
		return obj;
	}
	
	private Object[] getFieldInput(Object obj, boolean recursive)
	{
		Scanner scanner = new Scanner(System.in);
		Class objClass = obj.getClass();
		Field[] fields = objClass.getDeclaredFields();
		Object[] fieldValues = new Object[fields.length];
		String set;
		Field currentField;
        for(int i=0; i<fields.length; i++) 
		{
			currentField = fields[i];
			System.out.print("Set value for " + currentField.getType().getName() + " field " + "'" + currentField.getName() + "'?" + " (y/n): ");
			set = scanner.nextLine();

			if (set.equals("y"))
			{
				if (currentField.getType().isArray())
				{
					System.out.println("Field is array...");
					try
					{
						currentField.setAccessible(true);
						Object arrayObj = currentField.get(obj);
						int length = Array.getLength(arrayObj);
						Object[] fieldArray = new Object[length];
						if (arrayObj.getClass().getComponentType().isPrimitive())
						{
							for(int j=0; j<length; j++)
							{
								System.out.print("Value for element " + j + " in field array with element type " +  
												 arrayObj.getClass().getComponentType() + ": ");
								fieldArray[j] = scanner.nextLine();
							}
							fieldValues[i] = fieldArray;
						}
						else
						{
							if (recursive)
							{
								for(int j=0; j<length; j++)
								{
									System.out.println("Element " + j + " is object...");
									fieldArray[j] = createObject(arrayObj.getClass().getComponentType().getName(), false);
									System.out.println("Done setting element object.");
								}
								fieldValues[i] = fieldArray;
							}
							else
							{
								System.out.println("Can only set primitive instance variables of referenced objects.");
							}
						}
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}		
				}
				else if (currentField.getType().isPrimitive())
				{
					System.out.println("Field is primitive...");
					System.out.print("Value to set: ");
					fieldValues[i] = scanner.nextLine();					
				}
				else
				{
					if (recursive)
					{
						System.out.println("Field is object...");
						fieldValues[i] = createObject(currentField.getType().getName(), false);
						System.out.println("Done setting field object.");
					}
					else
					{
						System.out.println("Can only set primitive instance variables of referenced objects.");
					}
				}
			}
		}
		return fieldValues;
	}
	
	private void createObjectWithFields(Object obj, Object[] fieldValues)
	{
		Class objClass = obj.getClass();
		Field[] fields = objClass.getDeclaredFields();
		Object currentField;
		Type type;
		try
		{
			for(int i=0; i<fieldValues.length; i++)
			{
				if (fieldValues[i] == null)
					continue;
				currentField = fieldValues[i];
				fields[i].setAccessible(true);

				if (fields[i].getType().isPrimitive())
				{
					setPrimitiveField(obj, fieldValues, fields, fields[i].getType(), i);
				}
				else if(fields[i].getType().isArray())
				{
					Object arrayObj = (Object) fields[i].get(obj);
					int length = Array.getLength(arrayObj);
					if(arrayObj.getClass().getComponentType().isPrimitive())
					{
						for (int j=0; j<length; j++)
						{
							setPrimitiveArrayElement(arrayObj, currentField, arrayObj.getClass().getComponentType(), j);
						}
					}
					else
					{
						for (int j=0; j<length; j++)
						{
							Array.set(arrayObj, j, Array.get(currentField,j));
						}
					}
				}
				else
				{
					fields[i].set(obj, fieldValues[i]);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void setPrimitiveField(Object obj, Object[] fieldValues, Field[] fields, Type type, int index)
	{
		try
		{	
			if(type.equals(Boolean.class))
			{
				fields[index].setBoolean(obj, Boolean.parseBoolean(fieldValues[index].toString()));
			}
			else if(type.equals(char.class))
			{
				fields[index].set(obj, fieldValues[index].toString().charAt(0));
			}
			else if(type.equals(short.class))
			{
				fields[index].setShort(obj, Short.parseShort(fieldValues[index].toString()));
			}
			else if(type.equals(long.class))
			{
				fields[index].setLong(obj, Long.parseLong(fieldValues[index].toString()));
			}
			else if(type.equals(int.class))
			{
				fields[index].setInt(obj, Integer.parseInt(fieldValues[index].toString()));
			}
			else if(type.equals(double.class))
			{
				fields[index].setDouble(obj, Double.parseDouble(fieldValues[index].toString()));
			}
			else if(type.equals(float.class))
			{
				fields[index].setFloat(obj, Float.parseFloat(fieldValues[index].toString()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setPrimitiveArrayElement(Object arrayObj, Object currentField, Type type, int index)
	{
		try
		{	
			if(type.equals(Boolean.class))
			{
				Array.set(arrayObj, index, Boolean.parseBoolean(Array.get(currentField,index).toString()));
			}
			else if(type.equals(char.class))
			{
				Array.set(arrayObj, index, Array.get(currentField,index).toString().charAt(0));
			}
			else if(type.equals(short.class))
			{
				Array.set(arrayObj, index, Short.parseShort(Array.get(currentField,index).toString()));
			}
			else if(type.equals(long.class))
			{
				Array.set(arrayObj, index, Long.parseLong(Array.get(currentField,index).toString()));
			}
			else if(type.equals(int.class))
			{
				Array.set(arrayObj, index, Integer.parseInt(Array.get(currentField,index).toString()));
			}
			else if(type.equals(double.class))
			{
				Array.set(arrayObj, index, Double.parseDouble(Array.get(currentField,index).toString()));
			}
			else if(type.equals(float.class))
			{
				Array.set(arrayObj, index, Float.parseFloat(Array.get(currentField,index).toString()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Object newArmyObject()
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Creating Army object");
		System.out.print("Size of marksmen array: ");
		int size = scanner.nextInt();	
		ArrayList<Marksman> marksmen = new ArrayList<Marksman>();
		for(int i=0; i<size; i++)
		{
			System.out.println("Element " + i + " is object...");
			marksmen.add((Marksman) createObject("Marksman", false));
			System.out.println("Done setting element object.");								
		}
		return new Army(marksmen);
	}
	
	public ArrayList<Object> getObjects()
	{
		return objects;
	}
}