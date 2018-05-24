package me.kevinchik.www.jumpguy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    public static final int WIDTH = 270;
    public static final int HEIGHT = 480;
    public static final int CEILING = 225;
    private int screenWidth;
    private int screenHeight;
    private final SensorManager sensorManager;
    private final Sensor sensor;
    protected GameThread thread;
    private Title title;
    private Background bg;
    private Player player;
    private ArrayList<Platform> platform;
//    private Score score;
    private int[] fade = {0, 0};
//    private boolean startScreen = true;
//    private int finalScore;

    protected Game(Context context, int screenWidth, int screenHeight) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void update() {
        if (player.isAlive()) {

            player.update();
            title.update();
            for(int i = 0; i < platform.size(); i++) {
                platform.get(i).update();
            }

            if (player.isPlaying()) {
                title.setAy(-4);
                if (title.getY() <= -264) {
                    title.setAy(0);
                    title.setVy(0);
//                    startScreen = false;
                    title.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.score));
                }
                if (player.getTmpVy() == 1) {
                    if (player.getY() < CEILING) {
                        player.setTmpY(player.getY());
                        player.setTmpVy(player.getVy());
                    }
                    for (Platform p : platform) {
                        p.setVy(0);
                    }
                } else {
                    if (player.getVy() <= 0) {
                        player.setY(CEILING);
                    } else {
                        player.setTmpVy(1);
                    }
                    for (Platform p : platform) {
                        p.setVy(-player.getVy());
                    }
                }

                int count = 0;
                int prvY = 0;
                for (int i = 0; i < platform.size(); i++) {
                    if (platform.get(i).getY() < 0) {
                        count++;
                    } else if (platform.get(i).getY() + 20 > HEIGHT) {
                        platform.remove(i);
                    } else {
                        prvY = platform.get(i).getY();
                    }
                }
                if (count == 0) {
                    platform.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), (int) (Math.random() * (WIDTH - 64)), prvY - 32 - (int) (Math.random() * 64)));
                }

                for (int i = 0; i < platform.size(); i++) {
                    if (player.getVy() > 2 && player.feet().intersect(platform.get(i).body())) {
                        player.setY(platform.get(i).getY() - player.getH());
                        player.setVy(player.getJump());
                    }
                }

                if (player.getY() > HEIGHT) {
                    player.setAlive(false);
                    fade[0] = 1;
                }
            }
        } else {
            start();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = screenWidth / (WIDTH * 1.f);
        final float scaleFactorY = screenHeight / (HEIGHT * 1.f);
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            for(int i = 0; i < platform.size(); i++) {
                platform.get(i).draw(canvas);
            }
            player.draw(canvas);
            title.draw(canvas);

            Paint paint = new Paint();

//            if (!startScreen && !player.isPlaying()) {
//                Rect bounds = new Rect();
//                paint.getTextBounds(String.valueOf(finalScore), 0, String.valueOf(finalScore).length(), bounds);
//                int scoreWidth = bounds.width();
//                score.setX(WIDTH / 2 - scoreWidth * 3);
//                score.setSize(40);
//                score.setY(218);
//                score.setARGB(255, 229, 147, 0);
//                score.draw(canvas, finalScore);
//                score.setY(215);
//                score.setARGB(255, 239, 186, 0);
//                score.draw(canvas, finalScore);
//            } else {
//                score.setX(10);
//                score.setY(50);
//                score.setSize(60);
//                score.setARGB(255, 255, 255, 255);
//                score.draw(canvas, player.getScore());
//            }

            paint.setARGB(fade[0], 0, 0, 0);
            canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
            canvas.restoreToCount(savedState);
        }
    }

    protected void start() {
        if (fade[0] != 0 && fade[1] == 0) {
//            finalScore = player.getScore();
            fade[0] += 15;
            if (fade[0] + 15 >= 255) {
                fade[1] = 1;
                bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.menu));
                platform = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    platform.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), 40 * i, 400));
                }
                for (int i = 7; i >= 0; i--) {
                    platform.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), (int) (Math.random() * (WIDTH - 64)), 50 * i));
                }
                player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player));
                title.setY(0);
            }
        } else if (fade[0] != 0 && fade[1] == 1) {
            fade[0] -= 15;
            if (fade[0] - 15 <= 0) {
                fade[0] = 0;
                fade[1] = 0;
                player.setAlive(true);
                player.setPlaying(false);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        start();
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.menu));
        title = new Title(BitmapFactory.decodeResource(getResources(), R.drawable.title));
        platform = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            platform.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), 30 * i - 10, 400));
        }
        for (int i = 7; i >= 0; i--) {
            platform.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), (int) (Math.random() * (WIDTH - 64)), 50 * i));
        }
//        score = new Score(getContext());
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player));
        player.setAlive(true);
        player.setPlaying(false);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        sensorManager.unregisterListener(this);
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            player.setPlaying(true);
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tilt = event.values[0];
            player.setVx((int)(Math.floor(tilt) * -2));
            if (player.getVx() < 0) {
                player.setFacingLeft(true);
            } else {
                player.setFacingLeft(false);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}