package com.example.mysimulationapp;

public class Earth extends GameObject {

    public Earth(float x, float y){
        super(x,y);
        mRadius = 300; // 1 = 10km
        mRealRadius = 3000; // km
        mMass = 1.3 * Math.pow(10,24); // kg
    }

    public float getRadius() {
        return mRadius;
    }

}
