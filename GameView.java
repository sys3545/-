package com.example.mysimulationapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    RocketGame mGame; // 게임의 스레드
    Paint rocketPaint; // 로켓의 페인트
    Paint earthPaint; // 지구의 페인트
    Paint trajectoryPaint; // 궤적의 페인트
    Paint textPaint; // 텍스트 페인트

    DecimalFormat formatter; // 소숫점 표시기
    int mode;
    int temp = 8;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.BLACK);
        formatter = new DecimalFormat("#,##0.0000"); // 포멧 지정
        mode = 0;

        // 텍스트 페인트 초기설정
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);

        // 로켓 페인트 초기설정
        rocketPaint = new Paint();
        rocketPaint.setColor(Color.RED);
        rocketPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 지구 페인트 초기설정
        earthPaint = new Paint();
        earthPaint.setColor(Color.rgb(90, 160, 220));
        earthPaint.setStyle(Paint.Style.FILL);

        // 궤적 페인트 초기설정
        trajectoryPaint = new Paint();
        trajectoryPaint.setColor(Color.WHITE);
        trajectoryPaint.setStrokeWidth(2);
        trajectoryPaint.setStyle(Paint.Style.STROKE);

        // 게임 생성
        mGame = new RocketGame(this);

        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mGame.start(); // 게임시작
        Toast.makeText(getContext(), "게임 시작", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mGame.interrupt();
    }

    @Override // 그리기
    protected void onDraw(Canvas canvas) {
        Earth earth = mGame.getEarth();
        Rocket rocket = mGame.getRocket();
        // drawTrajectory(canvas, trajectoryPaint, rocket); // 실시간 궤적 그리기 -> 주석 풀면 그려짐
        canvas.drawCircle(earth.getX(), earth.getY(), earth.getRadius(), earthPaint); // 지구그리기
        drawRocket(canvas, rocketPaint, rocket); // 로켓 그리기
        canvas.drawText("Gravity :  ("+formatter.format(rocket.getGravity())+" m/s^2)", 50, 50 , textPaint);
        canvas.drawText("Height :   ("+formatter.format(rocket.getHeight())+" km)", 50, 100 , textPaint);
        canvas.drawText("Velocity : ("+formatter.format(rocket.getSpeed())+" m/s)", 50, 150, textPaint);
    }

    // 여기에 비행 각도를 고려해서 그려야함
    public void drawRocket(Canvas canvas, Paint paint, Rocket rocket){
        Path path = new Path();

        path.moveTo(rocket.getX()+(float)(20*Math.cos(rocket.getFlightAngle_rad())), rocket.getY()+(float)(20*Math.sin(rocket.getFlightAngle_rad())));
        path.lineTo(rocket.getX()+(float)(8*Math.cos(rocket.getFlightAngle_rad()+(((90)*Math.PI)/180))),
                rocket.getY()+(float)(8*Math.sin(rocket.getFlightAngle_rad()+(((90)*Math.PI)/180))));
        path.lineTo(rocket.getX()+(float)(8*Math.cos(rocket.getFlightAngle_rad()+(((-90)*Math.PI)/180))),
                rocket.getY()+(float)(8*Math.sin(rocket.getFlightAngle_rad()+(((-90)*Math.PI)/180))));
        path.moveTo(rocket.getX()+(float)(20*Math.cos(rocket.getFlightAngle_rad())), rocket.getY()+(float)(20*Math.sin(rocket.getFlightAngle_rad())));
        path.close();
        canvas.drawPath(path, paint);
    }

    // 실시간 궤적 그리기
    public void drawTrajectory(Canvas canvas , Paint paint, Rocket rocket){
        Path path = new Path();
        int i;
        double speedX;
        double speedY;
        double distanceX = 0;
        double distanceY = 0;

        path.moveTo(rocket.getX(), rocket.getY());
        speedX = rocket.getRealSpeedX();
        speedY = rocket.getRealSpeedY();
        for(i=1; i<=1001; i+=10){
            speedX += rocket.getGravityX() * (rocket.getTime()*i);
            speedY += rocket.getGravityY() * (rocket.getTime()*i);

            distanceX = (speedX*(rocket.getTime()*i)) + (0.5 * rocket.getGravityX() * (rocket.getTime()*i)*(rocket.getTime()*i));
            distanceY = (speedY*(rocket.getTime()*i)) + (0.5 * rocket.getGravityY() * (rocket.getTime()*i)*(rocket.getTime()*i));
            path.lineTo(rocket.getX() - (float)(distanceX/10000), rocket.getY()+ (float)(distanceY/10000));
        }
        path.moveTo(rocket.getX() - (float)(distanceX/10000), rocket.getY()+ (float)(distanceY/10000));
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override // 터치입력 - 비행경로각
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: // 눌렀으면
                if(x > 540 && y > 1620) // right
                    mGame.dir = 2;
                else if(x < 540 && y > 1620) // left
                    mGame.dir = 1;
                break;
            case MotionEvent.ACTION_UP: // 손을 떼면
                mGame.dir = 0;
                break;
            default:
                return false;
        }

        this.invalidate();
        return true;
    }

    public void launch(){
        Toast.makeText(getContext(), "발사", Toast.LENGTH_SHORT).show();
        mGame.launch();
    }

    /// 배속 처리
    public void faster(){
        switch(mGame.gameSpeed){
            case 8:
                temp = 4;
                break;
            case 4:
                temp = 2;
                break;
            case 2:
                temp = 1;
                break;
            case 1:
                break;
        }
        mGame.setGameSpeed(temp);
    }

    public void slower(){
        switch(mGame.gameSpeed){
            case 1:
                temp = 2;
                break;
            case 2:
                temp = 4;
                break;
            case 4:
                temp = 8;
                break;
            case 8:
                break;
        }
        mGame.setGameSpeed(temp);
    }
}
