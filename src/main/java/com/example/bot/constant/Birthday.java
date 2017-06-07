package com.example.bot.constant;

/**
 * Created by lineplus on 2017. 6. 7..
 */
public enum Birthday {
	Junyeong("0607"),
	Sojeong(""),
	Jaeho(""),
	Sangwook(""),
	Hyeongjun(""),
	Jonghewk(""),
	Juyong(""),
	Busung(""),
	Jiho(""),
	Youngjun("");
	private String day;
	Birthday(String day){
		this.day = day;
	}
	public String getDay() {
		return day;
	}
}
