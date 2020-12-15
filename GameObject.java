package com.example.mysimulationapp;

public class GameObject {
    float mX;
    float mY;

    float mRadius; // 게임상의 반지름
    double mRealRadius; // 실제 반지름

    double mMass; // 질량
    double G; // 6.673 * 10^-11 (중력상수)

    public GameObject(float x, float y) {
        mX = x;
        mY = y;
        G = 6.673 * Math.pow(10,-11);
    }

    public float getX() { return mX; }
    public float getY() { return mY; }
    public double getMass() { return mMass; }
    public double getG() { return G; }
}
