package mypkg;

public class Util {

	public static String[] splitByComma(String input) {
		return input.split(", *");		
	}
	
	public static String[] splitByEquals(String input) {
		return input.split("=");		
	}
}
