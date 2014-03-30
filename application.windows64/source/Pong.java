import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Pong extends PApplet {

class Ball{
 //Global Variables
 int x_move = 1;
 int y_move = 1;
 int x = 0;
 int y = 0;
 int speed = 1;
 int x_radius = 10;
 int y_radius = 10;
 int _color = 0;
 //boundaries for bouncing
 int topBound, bottomBound, leftBound, rightBound;
 //constructor
 Ball(int _x, int _y){
   x= _x;
   y = _y;
 }
 Ball(int _speed){
    x = (int)random(10,width-10);
    y = (int)random(10, height - 10);
    speed = _speed;
    x_move = 1;
    y_move = 1; 
    _color = (int)random(0,255);
    setBounds(0, height, 0, width);
 }
 
 public void run(Paddle leftPaddle, Paddle rightPaddle){
   display();
   move();
   bounce(leftPaddle, rightPaddle);
 }
 public void move(){
    x += (x_move * speed);
    y += (y_move * speed);
    if(y > height - y_radius){
      y = height -y_radius; 
  }  
 }
 //functions
 public void display(){
   fill(255);
   stroke(255);
    ellipse(x, y, 2 * x_radius, 2 * y_radius); 
 }
 public void bounce(){
    //If X is out of bounds
    if(x >= width-x_radius | x <= 0 + x_radius)
        //reverse direction, reduce speed by moment cost
        x_move = -(x_move); 
    //If Y is out of bounds 
    if(y >= height-y_radius | y <= 0 + y_radius)
        //reverse direction, reduce speed by moment cost
        y_move = -(y_move);    
 }
 public void bounce(Paddle lPddl, Paddle rPddl){
   /*if ball has contacted left paddle*/
   if((x - x_radius) == lPddl.right())//if along the active zone
     if(y <=lPddl.top() && y >= lPddl.bottom()){
       println("Left Paddle Contacted");
       x_move = abs(x_move);
   } 
   /*If the ball contacts the right paddle*/
   if((x - x_radius) - lPddl.right() <= speed )//if along the active zone
     if(y >=lPddl.top() && y <= lPddl.bottom())
       deflect(RIGHT);
   if(rPddl.left() - (x + x_radius) <= speed)//if along the active zone
     if(y >=rPddl.top() && y <= rPddl.bottom())
       deflect(LEFT);
   /*Top and bottom bounds*/
  if((y + y_radius) >= height - 50)
    deflect(UP);
  if((y - y_radius) <= 50)
    deflect(DOWN);
    /*Goal bounds*/
   if((x - x_radius) > rPddl.right()){
       lPddl.scorePoint();
       respawn();
   }    
   if((x - x_radius) < lPddl.left()){
       rPddl.scorePoint();    
       respawn();
   }
 }
 public void respawn(){
   
       x = (int)random((width / 4), 3 * (width / 4));
       y = (int)random((height / 4), 3 * (height / 4));
       x_move = (int)random(-1,1) > 0 ? 1 : -1;
       x_move = (int)random(-1,1) > 0 ? 1 : -1;
 }
 public void deflect(int direction){
  switch(direction){
    case UP:
      y_move = abs(y_move) * -1;
      break;
    case DOWN:
       y_move = abs(y_move);
       break;
    case LEFT:
       x_move =  abs(x_move) * -1;
       break;  
    case RIGHT:
       x_move = abs(x_move);
       break;
  }
 }
 public void setBounds(int top,int bottom,int left,int right){
   topBound = top;
   bottomBound = bottom;
   leftBound = left;
   rightBound = right;
 } 
 //returns UP, DOWN, LEFT, RIGHT, or -1 depending on OoB condition
 public int bounds(){
   if(x >= rightBound-x_radius)
     return RIGHT;
   if(x <= leftBound)  
     return LEFT;
   if(y >= bottomBound-y_radius)
      return DOWN;
   if(y <= topBound + y_radius) 
     return UP;
   return -1;  
 }
}
/*********************PADDLE***************************************/
class Paddle{
   int score;
   int x, y;
   int dy;
   int _width, _height;
   int screen_bottom, screen_top;
   //Constructor
   Paddle(int in_x, int in_y, int in_height){
     x = in_x;
     y = in_y;
     _width = 8;
     _height = in_height;
     dy = 5;
     score = 0;
   }
   public void setBounds(int min, int max){
      screen_bottom = max;
      screen_top = min; 
   }
   //Set paddle speed
   public void setSpeed(int speed){
      dy = speed; 
   }
   //React paddle to movement (For key control). Respects bounds. 
   public void move(int direction){
      if(direction == UP && y > (screen_top + dy))
       y -= dy;
      else if(direction == DOWN && (y + _height) < screen_bottom)
       y  += dy;
   }
   //reacts to whether the ball is above or below paddle and calls movePaddle accordingly
   public void autoMove(int ball_y){
      if(ball_y < y)
         move(UP);
       else if(ball_y > (y +  _height))
         move(DOWN);
   }
   //get top of paddle in Y coordinates
   public int top(){
      return y; 
   }
   //get bottom of paddle in Y coordinates
   public int bottom(){
      return (y+ _height);
   }
   public int left(){
      return x; 
   }
   public int right(){
     return x + _width;
   }
   public void scorePoint(){
      score++; 
   }
   public int score(){
      return score; 
   }
   //Draw the paddle given a far-right X coordinate
   public void paint(){
      fill(245);
      stroke(245);
      rect(x,y,_width, _height);
   }
}
/*******************   PONG    ********************/
   /*  Paddles have bounds, a top and a height, as well as the score*/
   Paddle leftPaddle, rightPaddle;
   int mvLeft, mvRight;
   Ball ball;
   boolean playing;
   int winning_score = 10;
   /** Methods **/
   
   //Constuctor
   public void setup(){
     //init dimensions
     size(800,600);
     background(127);
     smooth();
     frameRate(120); 
     ball = new Ball(3);
     playing = true;
     /*TODO set bounds*******************************************************************/
     //init Paddles
     mvLeft = 0;
     leftPaddle = new Paddle(50,50,80);
     leftPaddle.setBounds(45, height - 50);
     
     mvRight = 0;
     rightPaddle = new Paddle(width - 50,50,80);
     rightPaddle.setBounds(45, height - 50);
   }
   
   //Run method to be called on each frame
   public void draw(){
     background(127);
     if(!gameOver()){
       drawFrame();
       movePaddles();
       ball.run(leftPaddle, rightPaddle);
     }else{
       drawWinner();
     }  
   }
   /*returns true if game is over*/
   public boolean gameOver(){
      return playing = (leftPaddle.score() >= winning_score
                      || rightPaddle.score >= winning_score); 
   }
   public void drawWinner(){
     String winner;
     
     if(leftPaddle.score() > rightPaddle.score())
       winner = "left";
     else winner = "right";
      
     text("Congratulations " + winner + " player!",80,height/2);
   }
   //Draw static field, paddles, and ball
   public void drawFrame(){
     //Draw score
     fill(200);
     stroke(200);
     rect(58,50,(width-100)-8, (height-100));
     textFont(createFont("Arial", 50, true));
     fill(245);
     text(leftPaddle.score(),width/2-100,50);
     text(rightPaddle.score(),width/2+50,50);
     //Draw paddles
     leftPaddle.paint();
     rightPaddle.paint();
   }
   
   public void movePaddles(){
      leftPaddle.move(mvLeft);
      rightPaddle.move(mvRight); 
   }
   public void keyPressed(){
    if(key == CODED){
        if(keyCode == UP)
          mvRight = UP;
        if(keyCode == DOWN){
          mvRight = DOWN;
        }  
    }else{
      if(key == 'w')
        mvLeft = UP;
      if(key == 's')
        mvLeft = DOWN;
    }
   }
  public void keyReleased(){
    if(key == CODED)
        if(keyCode == UP || keyCode == DOWN)
          mvRight = 0;
      if(key == 'w'|| key == 's')
        mvLeft = 0;
  }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Pong" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
