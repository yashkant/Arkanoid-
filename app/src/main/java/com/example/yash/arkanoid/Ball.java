package com.example.yash.arkanoid;

/**
 * Created by yash on 25/9/16.
 */

import android.graphics.RectF;
import java.util.Random;

public class Ball
{
    RectF rect;
    float xVelocity;
    float yVelocity;
    float ballWidth = 10;
    float ballHeight = 10;

    public Ball(int screenX, int screenY)
    {
        xVelocity = 200;
        yVelocity = -400;
        rect = new RectF();
    }

    public RectF getRect()
    {
        return rect;
    }

    public void update(long fps)
    {
        //xvelocity can be negative
        rect.left = rect.left + (xVelocity/fps);
        rect.top = rect.top + (yVelocity/fps);
        rect.right = rect.left+ballWidth;
        rect.bottom = rect.top + ballHeight;//Doubtful
    }

    public void reverseXVelocity()
    {
        xVelocity = -xVelocity;
    }

    public void reverseYVelocity()
    {
        yVelocity = -yVelocity;
    }

    //This generates 2 nos if 0 reverse else do nothing
    public void setRandomXVelocity()
    {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0)
            reverseXVelocity();
    }

    public void clearObstacleX(float x)
    {
        rect.left = x;
        rect.right = x + ballWidth;

    }

    public void clearObstacleY(float y)
    {
        rect.bottom = y;
        rect.top = y - ballWidth;
    }

    public void reset(int x, int y)
    {
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

}
