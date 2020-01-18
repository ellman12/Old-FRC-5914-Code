package org.usfirst.frc.team5914.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  WPI_TalonSRX frontRight, frontLeft, backRight, backLeft, candyShooter, spinnyThing;

  DifferentialDrive drive;

  SpeedControllerGroup leftDrive, rightDrive;

  Joystick stick; 
  
  public static double speedCap = 1; 
  
  public static final String speedChoice = "speedChoice";
  String speed;
  public SendableChooser<String> Speed = new SendableChooser<>();

  @Override
  public void robotInit() {

	Speed.addDefault("Speed Choice", speedChoice);
	SmartDashboard.putData("speedChoice", Speed);
	  
	// These will need to obviously be adjusted	
    frontRight = new WPI_TalonSRX(999);
    frontLeft = new WPI_TalonSRX(998);
    backRight = new WPI_TalonSRX(997);
    backLeft = new WPI_TalonSRX(996);
    candyShooter = new WPI_TalonSRX(995);
    spinnyThing = new WPI_TalonSRX(900);

    leftDrive = new SpeedControllerGroup(frontLeft, frontRight);
    rightDrive = new SpeedControllerGroup(frontRight, frontLeft);

    drive = new DifferentialDrive(leftDrive, rightDrive);
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

	speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
	SmartDashboard.putNumber(speedChoice, speedCap * 100);
	
    //Joystick inputs are squared
    drive.arcadeDrive(stick.getRawAxis(1), stick.getRawAxis(0), true);

    // Right analog trigger
    candyShooter.set(stick.getRawAxis(3));
  }

  @Override
  public void testPeriodic() {
  }
}