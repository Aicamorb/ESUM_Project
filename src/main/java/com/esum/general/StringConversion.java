package com.esum.general;

public class StringConversion {
	private static String e = "&";
	public static String strCombination(String[] s)
	{
		String ret = s[0];
		for(int i=1;i<s.length;i++)
		{
			ret = ret+StringConversion.e + s[i];
		}
		return ret;
		

	}
	public static String[] strDismantle(String s)
	{
        String[] ret = s.split(StringConversion.e);
        return ret;
	}
	public static String idTransfer(String[] s)
	{
		String ret = "\"'"+s[0]+"'";
		for(int i =1;i<s.length;i++)
		{
			ret=ret+",'"+s[i]+"'";
		}
		return ret+"\"";
		
	}
}
