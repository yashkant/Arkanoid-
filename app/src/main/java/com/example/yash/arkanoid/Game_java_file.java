package com.example.yash.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class Game_java_file extends Activity


{

    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        //This block is executed only once, what it does is just to set and
        //initialize breakout view as default view :)
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    class BreakoutView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing; // game control variable
        boolean paused = true;
        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;
        int screenX;
        int screenY;
        Paddle paddle;
        Ball ball;
        //Array of bricks
        Brick[] bricks = new Brick[200];
        int numBricks = 0;//Keep track of no of bricks that are drawn

        //Adding the sound using FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        //Score and other stats
        int score = 0;
        //Lives
        int lives = 3;


        public BreakoutView(Context context) {
            super(context); //This is used to create objects of parent class prior to this
            ourHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            paddle = new Paddle(screenX,screenY);
            ball = new Ball(screenX,screenY);

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();
        }

        public void createBricksAndRestart()
        {
            //Restart the game
            ball.reset(screenX,screenY);
            int brickWidth = screenX/8;
            int brickHeight = screenY/10;

            //Building a wall of bricks
            numBricks = 0;

            for(int column = 0; column<8 ;column++)
                for(int row = 0;row<3;row++)
                {
                    bricks[numBricks] = new Brick(row,column,brickWidth,brickHeight);
                    numBricks++;
                }

            if(lives == 0)
            {
                score = 0;
                lives = 3;
            }

        }

        //Used from Runnable
        @Override
        public void run() {
            //Used to calculate the fps and screen_resolution only
            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused) {
                    //Updates the breakout view
                    update();
                }

                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                //Storing the resolution of screen in two variables done in const.

                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        //Things that are to be updated collisions, movements and etc.

        public void update()
        {
            paddle.update(fps);
            ball.update(fps);
            //Ball to bricks
            for(int i = 0; i<numBricks;i++)
            {
                if(bricks[i].getVisibility())
                {
                    if(RectF.intersects(bricks[i].getRect(),ball.getRect()))
                    {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        //soundID, leftvol, rightvol, priority
                        //loop this means the no of times loop is required
                        //playback speed is last argument
                        soundPool.play(explodeID,1,1,0,0,1);
                    }
                }
            }
            //Ball to paddle
            if(RectF.intersects(paddle.getRect(),ball.getRect()))
            {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep1ID,1,1,0,0,1);
            }

            //Ball with bottom of the screen
            if(ball.getRect().bottom > screenY)
            {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY-2);
                //lose a life
                lives--;
                soundPool.play(loseLifeID,1,1,0,0,1);
                if(lives == 0)
                {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            //Ball with top of the screen
            if(ball.getRect().top < 0)
            {
                ball.reverseYVelocity();
                //As ball has a height of 10px
                ball.clearObstacleY(12);
                soundPool.play(beep2ID,1,1,0,0,1);
            }

            //Left and right wall collision

            if(ball.getRect().left < 0)
            {
                ball.reverseXVelocity();
                //ClearObstacle works on left portion
                ball.clearObstacleX(2);
                soundPool.play(beep3ID,1,1,0,0,1);
            }

            if(ball.getRect().right > screenX-10)
            {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX-22);
                soundPool.play(beep3ID,1,1,0,0,1);
            }

            //Pause if cleared screen occurs

            if(score == numBricks*10)
            {
                paused = true;
                createBricksAndRestart();
            }

        }

        public void draw()
        {
            if (ourHolder.getSurface().isValid())
            {
                //Locking the canvas so that it doesn't change abruptly during the method
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182)); // Paint the canvas
                paint.setColor(Color.argb(255, 255, 255, 255));

                //Drawing the paddle
                canvas.drawRect(paddle.getRect(),paint);

                //Drawing the ball
                canvas.drawRect(ball.getRect(),paint);

                //Drawing the bricks
                paint.setColor(Color.argb(255,249,129,0));
                for(int i =0 ;i<numBricks;i++)
                {
                    if(bricks[i].getVisibility())
                    {
                        canvas.drawRect(bricks[i].getRect(),paint);
                    }
                }

                //Drawing the HUD
                paint.setColor(Color.argb(255,255,255,255));
                paint.setTextSize(40);
                //Draw Score
                canvas.drawText("Score: " + score + "Lives: " + lives,10,50,paint);
                //All the bricks are broken ?
                if(score == numBricks*10)
                {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!",10,screenY/2,paint);
                }
                //Are any lives left
                if(lives<=0)
                {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!",10,screenY/2,paint);
                }
                ourHolder.unlockCanvasAndPost(canvas);

            }
        }

        //this method will finally be used in overriding the actual onPause method
        public void pause() {
            playing = false;
            try {
                //This block joins the current thread with the main thread and executes it sequentially
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error: ", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            //gameThread is declared on the beginning
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                //Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if (motionEvent.getX() > screenX / 2)
                        paddle.setMovementState(paddle.RIGHT);
                    else
                        paddle.setMovementState(paddle.LEFT);
                    break;
                //Player has removed his finger from the screen
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

    }

    //Overriding default activity methods with ours
    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }

    //Made 2 changes in android's activity in manifest file ie landscape and theme
    //Finished the paddle class


}




















