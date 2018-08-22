package com.jo.enums;

public enum VideoStatusEnum {
	
	SUCCESS(1),   //可以播放
	FORBID(2);   //禁止播放
	
	public final int value;

	VideoStatusEnum(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
