package me.kevinchik.www.jumpguy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Player extends Entity {

    private boolean playing;
    private boolean alive;
    private boolean facingLeft = true;
    private int tmpVy = 1;
    private int tmpY;
    private int score = 0;
    Bitmap[] image = new Bitmap[2];

    Player(Bitmap res) {
        super(Game.WIDTH / 2 - 9, 376, 1, 18, 24, 3);
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(res, i * getScaledW(), 0, getScaledW(), getScaledH());
            image[i] = Bitmap.createScaledBitmap(image[i], getW(), getH(), false);
        }
    }

    void update() {
        //wrap
        if (getX() < -getW())
            setX(Game.WIDTH);
        else if (getX() > Game.WIDTH)
            setX(-getW());

        setX(getX() + getVx());
//        setVx(getVx() + getAx());


        if (isPlaying()) {
            setY(getY() + getVy());
            setVy(getVy() + getAy());
        }
    }

    void draw(Canvas canvas) {
        if (facingLeft)
            canvas.drawBitmap(image[0], getX(), getY(), null);
        else
            canvas.drawBitmap(image[1], getX(), getY(), null);
    }

    Rect feet(){
        return new Rect(getX() + 3, getY() + getH() - 1, getX() + 12, getY() + getH());
    }

    boolean isAlive() {
        return alive;
    }

    void setAlive(boolean alive) {
        this.alive = alive;
    }

    boolean isPlaying() {
        return playing;
    }

    void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    void setTmpVy(int tmpVy) {
        this.tmpVy = tmpVy;
    }

    int getTmpVy() {
        return tmpVy;
    }

    public int getTmpY() {
        return tmpY;
    }

    public void setTmpY(int tmpY) {
        this.tmpY = tmpY;
    }

    int getJump() {
        return -16;
    }

    int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }
}
