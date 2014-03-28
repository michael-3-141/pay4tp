package me.michael.e.pay4tp.command;

import java.util.HashMap;
import java.util.Map;

public class TpPoints{

	private int publicPoints;
	public Map<String, Integer> points; 
	
	public TpPoints(int publicPoints) {
		this.publicPoints = publicPoints;
		points = new HashMap<String, Integer>();
	}
	
	public TpPoints()
	{
		this.publicPoints = 0;
		this.points = new HashMap<String, Integer>();
	}

	public int getPublicPoints() {
		return publicPoints;
	}
	
	public int getPoints(String player, boolean usePublic)
	{
		if(usePublic)
		{
			return publicPoints;
		}
		else
		{
			return this.points.get(player);
		}
	}

	public void setPoints(int points) {
		this.publicPoints = points;
	}
	
	public boolean chargePoints(int points)
	{
		if(this.publicPoints < points)
		{
			return false;
		}
		else
		{
			this.publicPoints -= points;
			return true;
		}
	}
	
	public boolean chargePlayer(int points, String player)
	{
		if(this.points.containsKey(player))
		{
			if(this.points.get(player) < points)
			{
				return false;
			}
			else
			{
				this.points.put(player, this.points.get(player) - points);
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean charge(int points, String player, boolean usePublic)
	{
		if(usePublic)
		{
			return chargePoints(points);
		}
		else
		{
			return chargePlayer(points, player);
		}
	}
	
	public void addPublicPoints(int points)
	{
		this.publicPoints += points;
	}
	
	public void addPrivatePoints(int points, String player)
	{
		if(this.points.containsKey(player))
		{
			this.points.put(player, this.points.get(player) + points);
		}
		else
		{
			this.points.put(player, points);
		}
	}
	
	public void addPoints(int points, String player, boolean usePublic)
	{
		if(usePublic)
		{
			addPublicPoints(points);
		}
		else
		{
			addPrivatePoints(points, player);
		}
	}
	

}
