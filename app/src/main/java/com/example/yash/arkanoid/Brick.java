package com.example.yash.arkanoid;
/**
 * Created by yash on 25/9/16.
 */

import android.graphics.RectF;

//This is for each brick
public class Brick
{
    private RectF rect;
    private boolean isVisible;

    //Row and col for that brick and its width and height
    public Brick(int row, int column, int width, int height)
    {
        isVisible = true;
        //Padding of 1px
        int padding =  1;
        rect = new RectF(column*width + padding, row*height + padding, column*width + width -padding, row*height + height -padding);
    }

    public RectF getRect()
    {
        return this.rect;
    }

    public void setInvisible()
    {
        isVisible = false;
    }

    public boolean getVisibility()
    {
        return isVisible;
    }

}
