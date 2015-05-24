package org.fruct.kareliafishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doomgiver on 24.03.15.
 */
class ObjectData {
	public static final int FISH = 1;
	public static final int LAKE = 2;
	public static final int HOSTEL = 3;
	public static final int SHOP = 4;
	public static final int BEHAVIOR_RULE = 5;
	public static final int FISHING_RULE = 6;
	public static final int RECIPE = 7;

	private Integer type;
	private String name;
	private String description;
	private String id;
	private Map<String, String> info = null;

	public ObjectData(Integer _type) throws NullPointerException, UnknownTypeObjectException
	{
		if (_type == null)
			throw new NullPointerException("_type is null");
		if (!(_type >= 1 && _type <= 4))
			throw new UnknownTypeObjectException("Unknown object's type:" + _type.toString());

		info = new HashMap<String, String>();
		type = _type;
		name = "no name";
		description = "no description";
		id = "no id";
	}

	public ObjectData(String _type) throws NullPointerException, UnknownTypeObjectException
	{
		_type = _type.toLowerCase();

		switch (_type)
		{
			case "fish":
				type = FISH;
				break;
			case "lake":
				type = LAKE;
				break;
			case "hostel":
				type = HOSTEL;
				break;
			case "shop":
				type = SHOP;
				break;
			default:
				throw new UnknownTypeObjectException("Unknown object's type: " + _type);
		}

		info = new HashMap<String, String>();
		name = "no name";
		description = "no description";
		id = "no id";
	}

	public Integer getType()
	{
		return type;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String _name) throws NullPointerException
	{
		if (_name == null || _name.equals(""))
			throw new NullPointerException("Object name cannot be empty");

		name = _name;
	}

	public String getDescription()
	{
		return description;
	}
	public void setDescription(String _description) throws NullPointerException
	{
		if (_description == null)
			throw new NullPointerException("Object's description cannot be null");

		description = _description;
	}

	public String getId()
	{
		return id;
	}
	public void setId(String _id)
	{
		id = _id;
	}

	public String getInfo(String _key)
	{
		return info.get(_key);
	}
	public void setInfo(String _key, String _value)
	{
		info.put(_key, _value);
	}
}

class UnknownTypeObjectException extends Exception
{
	public UnknownTypeObjectException()
	{
	}
	public UnknownTypeObjectException(String _dsc)
	{
		super(_dsc);
	}
}