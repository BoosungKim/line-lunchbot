package com.example.bot.constant;

/**
 * Created by lineplus on 2017. 6. 7..
 */
public enum Birthday {
	준영("0607"),
	소정("1226"),
	재호("0703"),
	상욱("0516"),
	형준("0325"),
	종혁("0715"),
	주용(""),
	부성("0214"),
	지호(""),
	영준("");
	private String day;
	Birthday(String day){
		this.day = day;
	}
	public String getDay() {
		return day;
	}
}
