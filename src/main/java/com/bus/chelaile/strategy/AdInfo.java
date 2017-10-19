package com.bus.chelaile.strategy;

public class AdInfo {
	private int adid;
	private int priority;

	public String toString()
	{
		return "AdInfo(adid=" + getAdid() + ", priority=" + getPriority() + ")";
	}

	public int hashCode()
	{
		int prime = 59;
		int result = 1;
		result = result * prime + getAdid();
		result = result * prime + getPriority();
		return result;
	}

	protected boolean canEqual(Object other)
	{
		return other instanceof AdInfo;
	}

	public boolean equals(Object o)
	{
		if (o == this) {
			return true;
		}
		if (!(o instanceof AdInfo)) {
			return false;
		}
		AdInfo other = (AdInfo)o;
		if (!other.canEqual(this)) {
			return false;
		}
		if (getAdid() != other.getAdid()) {
			return false;
		}
		return getPriority() == other.getPriority();
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public void setAdid(int adid)
	{
		this.adid = adid;
	}

	public int getPriority()
	{
		return this.priority;
	}

	public int getAdid()
	{
		return this.adid;
	}

	public AdInfo(int adid, int priority)
	{
		this.adid = adid;
		this.priority = priority;
	}
}
