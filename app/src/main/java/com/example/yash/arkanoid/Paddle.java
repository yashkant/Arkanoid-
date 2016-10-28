package com.example.yash.arkanoid; /**
 * Created by yash on 19/9/16.
 */

import android.graphics.RectF;

//Lib to create rectangles

public class Paddle
{
    //Initializing object coordinates and its dimensions all private to class
    private RectF rect;
    private float length;
    private float height;
    private float x;
    private float y;
    private float paddleSpeed;

    //Paddle movement
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int paddleMoving = STOPPED;

    //Constructor

    public Paddle(int screenX, int screenY)
    {
        length = 130;
        height = 20;

        // Start paddle in roughly the screen centre
        x = screenX / 2;
        y = screenY - 20;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the paddle in pixels per second
        paddleSpeed = 350;
    }

    //Defining the class methods
    //This make the rectangle available in Breakout class
    public RectF getRect()
    {
        return rect;
    }

    public void setMovementState(int state)
    {
        paddleMoving = state;
    }

    public void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        //these both are parameters of paddle class
        rect.left = x;
        rect.right = x + length;
    }

//Finished here :)
}
