//This is the code that I (Elliott) used to get Vulcan parade-ready. Should only be used on the practic robot (if it still exists when you are reading this).

package org.usfirst.frc.team5914.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	double error = 0;
	double left_speed = 0.5;
	double right_speed = 0.5;

	public int enc_rel_pos_1;
	public int enc_rel_pos_2;

	// enc_rel_pos_1 and enc_rel_pos_2 are relative to some starting point

	public int initPosition1;
	public int initPosition2;

	// initPosition1 and initPosition2 are starting values

	public static double speedCap = 1;

	static Joystick stick;

	WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;

	SpeedControllerGroup leftDrive;
	SpeedControllerGroup rightDrive;
	SpeedControllerGroup allDrive;

	DifferentialDrive drive;

	public static final String speedChoice = "speedChoice";
	String speed;
	public SendableChooser<String> Speed = new SendableChooser<>();

	public static final String encoderPOS = "encoderPOS";
	String ENC_POS;
	public SendableChooser<String> ENC = new SendableChooser<>();

	@Override
	public void robotInit() {

		Speed.addDefault("Speed Choice", speedChoice);
		SmartDashboard.putData("speedChoice", Speed);

		ENC.addDefault("Encoder Position", encoderPOS);
		SmartDashboard.putNumber("ENC_POS", enc_rel_pos_1);

		stick = new Joystick(0);

		frontLeft = new WPI_TalonSRX(3);
		backLeft = new WPI_TalonSRX(1);
		frontRight = new WPI_TalonSRX(2);
		backRight = new WPI_TalonSRX(4);

		leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
		rightDrive = new SpeedControllerGroup(frontRight, backRight);
		allDrive = new SpeedControllerGroup(frontLeft, frontRight, backLeft, backRight);

		// drive = new DifferentialDrive(leftDrive, rightDrive);

		// Making the motors all turn the right way
		// frontLeft.setInverted(true);
		frontRight.setInverted(true);
		// backLeft.setInverted(true);
		backRight.setInverted(true);

		backLeft.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		backRight.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		initPosition1 = backLeft.getSelectedSensorPosition(1);

		initPosition1 = Math.abs(initPosition1);

		initPosition2 = frontRight.getSelectedSensorPosition(2);

		initPosition2 = Math.abs(initPosition2);

		System.out.println("initPosition = " + initPosition1 + " initPosition2 = " + initPosition2);

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {

		frontLeft.set(.5);
		frontRight.set(.5);

		backRight.set(.5);
		backLeft.set(.5);

	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

		int raw_enc_1;
		int raw_enc_2;
		// Direct reading of the encoder

		speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
		SmartDashboard.putNumber(speedChoice, speedCap * 100);

		raw_enc_1 = backLeft.getSelectedSensorPosition(1);

		enc_rel_pos_1 = Math.abs(raw_enc_1) - initPosition1;

		// Calculate the relative position using the raw reading and the initial reading

		raw_enc_2 = frontRight.getSelectedSensorPosition(2);

		enc_rel_pos_2 = Math.abs(raw_enc_2) - initPosition2;

		System.out.println("raw_enc_1 = " + enc_rel_pos_1 + " raw_enc_2 = " + enc_rel_pos_2);

		error = enc_rel_pos_1 - enc_rel_pos_2;

		if (error > 50) {
			leftDrive.set(left_speed);
			right_speed += 0.002;
			rightDrive.set(right_speed);
		} else if (error < -50) {
			rightDrive.set(right_speed);
			left_speed += 0.002;
			leftDrive.set(left_speed);
		} else {
			rightDrive.set(right_speed);
			leftDrive.set(left_speed);
		}

		double stick1;
		double stick0;

		stick0 = stick.getRawAxis(0);
		stick1 = stick.getRawAxis(1);

		// old stuff that I (Elliott) just commented out

		// stick0 = Robot.stick.getRawAxis(0);
		// stick1 = Robot.stick.getRawAxis(1);

		// stick0 = Robot.stick.getRawAxis(0) * Robot.speedFraction;
		// stick1 = Robot.stick.getRawAxis(1) * Robot.speedFraction;

		// THE NON NEGATED VERSION OF THESE 2 IS PROBABLY THE ONE THAT IS GOING TO WORK
		// (CORRECTLY)!

		// drive.arcadeDrive(-stick0 * -speedCap, stick1 * -speedCap);

		// THIS WORKS!
		// |
		// \/
		// drive.arcadeDrive(stick0 * speedCap, -stick1 * speedCap);

		// a = frontLeft
		// b = frontRight
		// x = backLeft
		// y = backRight
		/*
		 * if (stick.getRawButton(1) == true) { frontLeft.set(0.4); } else {
		 * frontLeft.set(0); }
		 * 
		 * if (stick.getRawButton(2) == true) { frontRight.set(0.4); } else {
		 * frontRight.set(0); }
		 * 
		 * if (stick.getRawButton(3) == true) { backLeft.set(0.4); } else {
		 * backLeft.set(0); }
		 * 
		 * if (stick.getRawButton(4) == true) { backRight.set(0.4); } else {
		 * backRight.set(0); }
		 * 
		 * if (stick.getRawButton(6) == true) { frontRight.set(0.4); backRight.set(0.4);
		 * } else { frontRight.set(0); backRight.set(0); }
		 * 
		 * if (stick.getRawButton(5) == true) { frontLeft.set(0.4); backLeft.set(0.4); }
		 * else { frontLeft.set(0); backLeft.set(0); }
		 */
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}