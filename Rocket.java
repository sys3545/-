package com.example.mysimulationapp;

public class Rocket extends GameObject {
    private float centerX; // 화면 중심 X
    private float centerY; // 화면 중심 Y

    private double range; // 지구 중심에서부터의 거리
    private float rangeX; // x 방향 range
    private float rangeY; // y 방향 range
    private double realRange; // range 에 해당하는 실제 거리
    private double height;

    private double gravity; // 현재 적용되는 있는 중력 크기
    private double gravityX;
    private double gravityY;
    private double angle; // 중심 행성과의 각도

    private double mSpeedX; // x방향 속도
    private double mSpeedY; // y방향 속도
    private double mAccelX; // x 가속도
    private double mAccelY; // y 가속도
    private double mDistanceX; // x 이동거리
    private double mDistanceY; // y 이동거리
    private double t = 0.08;

    private double mTrust = 0.0;
    private double trustX;
    private double trustY;

    private double flightAngle; // 비행 경로 각
    private int isFlight; // 비행 모드 ( 0 = shut down, 1 = ignition )
    private int isOnLand;

    public Rocket(float x, float y){
        super(x,y);
        centerX = 540;
        centerY = 942;
        flightAngle = ((-90)*Math.PI)/180;
        isOnLand = 1;
    }


    //// setter ////
    public void setRange(){
        if(mX>= centerX)
            rangeX = mX - centerX;
        else
            rangeX = centerX -mX;

        if(mY>= centerY)
            rangeY = mY - centerY;
        else
            rangeY = centerY -mY;

        range= Math.sqrt(Math.pow((double)rangeX, 2) + Math.pow((double)rangeY,2));
    }

    public void setRealRange(){
        realRange = range * 10 * 1000; // range * (실제와의 비율) * (km->m)
    }

    public void setHeight(){
       if(range <= 300)
           height = 0;
       else
           height = (range - 300)*10;
    }

    public void setGravity(){
        if(isOnLand == 0)
            gravity = G * (1.3 * Math.pow(10,24))/(getRealRange()*getRealRange()); // g= GN/r^2
        else
            gravity = 0;
    }

    public void setAngle() {
        angle = Math.acos((mX - centerX) / range);
    }

    public void setIsFlight(){
       if(isFlight == 0) {
           isFlight = 1;
           isOnLand = 0;
           mTrust = 100.0;
       }
       else if(isFlight == 1) {
           isFlight = 0;
           mTrust = 0.0;
       }
    }

    public void setSpeed(){
        gravityX = gravity*Math.cos(angle);
        gravityY = gravity*Math.sin(angle);

        double radFlightAngle = (flightAngle*180)/Math.PI;

        if(radFlightAngle >= -90 && radFlightAngle< 0 || radFlightAngle >= 270 && radFlightAngle < 360){ // 1사분면 (로켓기준)
            trustX = mTrust * Math.cos(-flightAngle);
            trustY = mTrust * Math.sin(-flightAngle);
        }
        else if(radFlightAngle >= -180 && radFlightAngle< -90 || radFlightAngle >= 180 && radFlightAngle < 270){ // 2사분면 (로켓기준)
            trustX = -Math.abs(mTrust*Math.cos(Math.PI+flightAngle));
            trustY = Math.abs(mTrust*Math.sin(Math.PI+flightAngle));
        }
        else if(radFlightAngle >= -270 && radFlightAngle< -180 || radFlightAngle >= 90 && radFlightAngle < 180){ // 3사분면 (로켓기준)
            trustX = -Math.abs(mTrust*Math.cos(Math.PI+flightAngle));
            trustY = -Math.abs(mTrust*Math.sin(Math.PI+flightAngle));
        }
        else if(radFlightAngle >= -360 && radFlightAngle< -270 || radFlightAngle >=0 && radFlightAngle <90) { // 4사분면 (로켓기준)
            trustX = Math.abs(mTrust*Math.cos(2*Math.PI+flightAngle));
            trustY = -Math.abs(mTrust*Math.sin(2*Math.PI+flightAngle));
        }

        setTrustDirection();

        if(mY >= centerY){
            gravityY = -gravityY;
            trustY = -trustY;
        }

        mAccelX = gravityX - trustX;
        mAccelY = gravityY - trustY;

        mSpeedX += mAccelX * t; // v = at
        mSpeedY += mAccelY * t; // v = at

        mDistanceX = (mSpeedX * t) + (0.5 * mAccelX * t * t);
        mDistanceY = (mSpeedY * t) + (0.5 * mAccelY * t * t);


        if (range < 300 ) { // 지구와 충돌
            mDistanceX = 0;
            mDistanceY = 0;
            if(isOnLand == 0){
                mSpeedX = 0;
                mSpeedY = 0;
            }
        }
    }

    public void setTrustDirection(){
        if(mX >= centerX && mY <= centerY){ // 1사분면
        }
        else if(mX <= centerX && mY <= centerY){ // 2사분면
        }
        else if(mX <= centerX && mY >= centerY){ // 3사분면
            trustY = -trustY;
        }
        else if(mX >= centerX && mY >= centerY){ // 4사분면
            trustY = -trustY;
        }
    }

    public void setFlightAngle(int dir){
        if(dir == 1) { //left
            flightAngle-=0.005;
        }
        else if(dir == 2) { //right
            flightAngle+=0.005;
        }

        if((flightAngle*180/Math.PI) >= 360 || (flightAngle*180/Math.PI) <= -360)
            flightAngle= 0;
    }

    public void move(){
        mX = mX - (float)(mDistanceX/10000);
        mY = mY + (float)(mDistanceY/10000);
    }

    //// getter ////
    public double getRange(){
       return range;
    }

    public double getRealRange(){
       return realRange;
    }

    public double getHeight(){
       return height;
    }

    public double getGravity(){
       return gravity;
    }

    public double getAngle(){
       return (angle*180)/Math.PI;
    }

    public double getRealSpeedX(){
        return mSpeedX;
    }

    public double getRealSpeedY(){
        return mSpeedY;
    }

    public double getSpeed(){
        double temp = Math.pow(mSpeedX, 2) + Math.pow(mSpeedY, 2);
        return Math.sqrt(temp);
    }

    public double getFlightAngle(){
       return (flightAngle*180)/Math.PI;
    }

    public double getFlightAngle_rad(){
       return flightAngle;
    }

    public double getTime(){
        return t;
    }

    public double getmAccelX(){
        return mAccelX;
    }

    public double getmAccelY(){
        return  mAccelY;
    }

    public double getGravityX(){
        return gravityX;
    }

    public double getGravityY(){
        return gravityY;
    }
}
