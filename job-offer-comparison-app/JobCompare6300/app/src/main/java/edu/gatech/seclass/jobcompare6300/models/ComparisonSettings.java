package edu.gatech.seclass.jobcompare6300.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;


@Entity(tableName = "comparison_settings")
public class ComparisonSettings implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int salaryWeight = 1;
    private int bonusWeight = 1;
    private int tuitionWeight = 1;
    private int insuranceWeight = 1;
    private int discountWeight = 1;
    private int adoptionWeight = 1;


    private int validateWeight(int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Weight must be an integer between 0 and 9.");
        }
        return value;
    }


    public ComparisonSettings(int salaryWeight, int bonusWeight, int tuitionWeight,
                              int insuranceWeight, int discountWeight, int adoptionWeight) {
        this.salaryWeight = validateWeight(salaryWeight);
        this.bonusWeight = validateWeight(bonusWeight);
        this.tuitionWeight = validateWeight(tuitionWeight);
        this.insuranceWeight = validateWeight(insuranceWeight);
        this.discountWeight = validateWeight(discountWeight);
        this.adoptionWeight = validateWeight(adoptionWeight);
    }
    public ComparisonSettings(){
        this.salaryWeight = 1;
        this.bonusWeight = 1;
        this.tuitionWeight = 1;
        this.insuranceWeight = 1;
        this.discountWeight = 1;
        this.adoptionWeight = 1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public float getWeightsSum() {
        return salaryWeight + bonusWeight + tuitionWeight + insuranceWeight + discountWeight + adoptionWeight;
    }

    // Getters and Setters

    public int getSalaryWeight() {
        return salaryWeight;
    }

    public void setSalaryWeight(int salaryWeight) {
        this.salaryWeight = validateWeight(salaryWeight);
    }

    public int getBonusWeight() {
        return bonusWeight;
    }

    public void setBonusWeight(int bonusWeight) {
        this.bonusWeight = validateWeight(bonusWeight);
    }

    public int getTuitionWeight() {
        return tuitionWeight;
    }

    public void setTuitionWeight(int tuitionWeight) {
        this.tuitionWeight = validateWeight(tuitionWeight);
    }

    public int getInsuranceWeight() {
        return insuranceWeight;
    }

    public void setInsuranceWeight(int insuranceWeight) {
        this.insuranceWeight = validateWeight(insuranceWeight);
    }

    public int getDiscountWeight() {
        return discountWeight;
    }

    public void setDiscountWeight(int discountWeight) {
        this.discountWeight = validateWeight(discountWeight);
    }

    public int getAdoptionWeight() {
        return adoptionWeight;
    }

    public void setAdoptionWeight(int adoptionWeight) {
        this.adoptionWeight = validateWeight(adoptionWeight);
    }

//    public int getWeights() {
//        return salaryWeight, bonusWeight, tuitionWeight, insuranceWeight, discountWeight, adoptionWeight;
//    }

    public void setWeights(int salaryWeight, int bonusWeight, int tuitionWeight,
                           int insuranceWeight, int discountWeight, int adoptionWeight) {
        this.salaryWeight = validateWeight(salaryWeight);
        this.bonusWeight = validateWeight(bonusWeight);
        this.tuitionWeight = validateWeight(tuitionWeight);
        this.insuranceWeight = validateWeight(insuranceWeight);
        this.discountWeight = validateWeight(discountWeight);
        this.adoptionWeight = validateWeight(adoptionWeight);
    }
}