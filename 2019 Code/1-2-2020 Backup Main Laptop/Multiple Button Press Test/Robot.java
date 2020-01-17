package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {

  Joystick stick;

  boolean A_once, A_twice, A_thrice;

  @Override
  public void robotInit() {

    stick = new Joystick(0);
    A_once = false;
    A_twice = false;
    A_thrice = false;

  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {

    if (stick.getRawButton(1) == true && A_once == false && A_twice == false && A_thrice == false) {
      A_once = true;
      
      // if (A_once == true) {

        // some method or something
        // set A_once to false
      // } 
      // else if {...?}

      System.out.println("          A Pressed Once");
      Timer.delay(0.3);

    }
    if (stick.getRawButton(1) == true && A_once == true && A_twice == false && A_thrice == false) {
      A_twice = true;
      A_once = false;
      System.out.println("          A Pressed Twice");
      Timer.delay(0.3);

    }
    if (stick.getRawButton(1) == true && A_once == false && A_twice == true && A_thrice == false) {
      A_thrice = true;
      A_twice = false;
      System.out.println("          A Pressed Thrice");
      Timer.delay(0.3);

    }
    if (stick.getRawButton(1) == true && A_once == false && A_twice == false && A_thrice == true) {
      A_thrice = false;
      System.out.println("          A Pressed 4 Times and Counter Reset");
      Timer.delay(0.3);
    }

  }

  @Override
  public void testPeriodic() {
  }
}
