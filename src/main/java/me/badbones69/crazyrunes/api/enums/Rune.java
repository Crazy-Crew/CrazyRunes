package me.badbones69.crazyrunes.api.enums;

public enum Rune {
	
	MEDIC("Medic", 3, 1),
	TANK("Tank", 3, 3),
	PYRO("Pyro", 5, 10),
	SURVIVOR("Survivor", 1, 1),
	LEECH("Leech", 4, 15),
	REBORN("Reborn", 3, 10),
	ROCKET("Rocket", 4, 15),
	SPEED("Speed", 2, 50);
	
	private final String name;
	private final Integer maxLevel;
	private final Integer power;
	
	Rune(String name, Integer maxLevel, Integer power){
		this.name = name;
		this.maxLevel = maxLevel;
		this.power = power;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Integer getMaxLevel(){
		return this.maxLevel;
	}
	
	public Integer getPower(){
		return this.power;
	}
}