package com.opsc7311.healthandfitness19013888;

public class User {

    public String fullName;
    public double height, weight, goalWeight, goalCalorie;
    public String setting;

    public User()
    {

    }

    public User(String fullName, double height, double weight, double goalWeight, double goalCalorie, String settings)
    {
        this.fullName = fullName;
        this.height = height;
        this.weight = weight;
        this.goalWeight = goalWeight;
        this.goalCalorie = goalCalorie;
        this.setting = settings;
    }

    public double getImperialHeight(){
        return this.height/30.48;

    }

    public double getImperialWeight(){
        return this.weight * 2.205;

    }

    public double getImperialGoalWeight(){
        return this.goalWeight * 2.205;

    }

}
