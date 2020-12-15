package com.example.mysimulationapp;

import android.widget.Toast;

public class RocketGame extends Thread {
    GameView mGameView; // 스레드에서 뷰를 엑세스 하기 위해
    Earth earth;
    Rocket rocket;
    int tick;
    int dir; // 로켓 기울임 방향
    int gameSpeed; // 게임 배속 (8이 1배속, 4가 2배속, 2가 4배속, 1은 8배속)

    public RocketGame(GameView gameView){
        this.mGameView = gameView;
        earth = new Earth(540, 942);
        rocket = new Rocket(540 , 942 - earth.getRadius());
        tick = 0;
        dir = 0;
        gameSpeed = 8;
    }

    @Override
    public void run() {
        try {
            while (true) { // 여기는 자동으로 해야하는 작업을 처리한다.
                long startTime = System.currentTimeMillis(); // while 문 진입 시간을 기록
                rocket.setFlightAngle(dir);
                rocket.setGravity(); // gravity 설정
                rocket.setRange(); // range 설정
                rocket.setRealRange(); // realRange 설정
                rocket.setHeight(); // height 설정
                rocket.setAngle(); // angle 설정
                tick++;

                if(tick >= gameSpeed) {
                    rocket.setSpeed(); // speed 설정
                    rocket.move();
                    tick = 0;
                }

                mGameView.invalidate(); // 뷰를 다시 그리는 것도 자동 (mGameView 의 onDraw 호출)

                long sleepTime = 10 - (System.currentTimeMillis() - startTime);
                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }
            }
        } catch (InterruptedException ie) {
            mGameView.invalidate(); //interrupt 가 실행되면 여기로 온다
            Toast.makeText(mGameView.getContext() , "게임 종료", Toast.LENGTH_SHORT).show();
        }
    }

    public void launch(){
        rocket.setIsFlight();
        //rocket.setIsLand();
    }

    public void setGameSpeed(int speed){
        gameSpeed = speed;
    }


    /// getter ///
    Earth getEarth() {
        return earth;
    }

    Rocket getRocket(){
        return rocket;
    }
}
