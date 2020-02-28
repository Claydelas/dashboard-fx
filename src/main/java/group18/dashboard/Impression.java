package group18.dashboard;

import java.util.Date;


// Skeleton code, replace with Jeremy's stuff
public class Impression {
    private Date date;
    private String id;
    private Gender gender;
    private int age;
    private Income income;
    private double cost;

    public Impression(Date date, String id, Gender gender, int age, Income income, double cost) {
        this.date = date;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.income = income;
        this.cost = cost;
    }

    public Date getDate() {
        return date;
    }

    public String getID() {
        return id;
    }

    public Gender getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public Income getIncome() {
        return income;
    }

    public double getCost() {
        return cost;
    }
}

