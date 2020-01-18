//This is the code that I (Elliott) used to get Vulcan parade-ready. Should only be used on the practice robot (if it still exists when you are reading this).
/*
 *  LJB 11/24/2018:  Added a few functions to aid in using gyro and encoders to drive on a fixed heading and to drive a
 *  specific distance. These are to serve as experimental models for the use of these sensors.
 *  With respect to encoder exercises:
 *  1.  Added a more sophisticated algorithm that determines whether to increase or decrease one sides speed relative to the other
 *      to acheive equal encoder readings.
 *  2.  Set a maximum speed - when reached, this is when we decrease to acheive the desired correction.
 *  3.  
 *  Remaining Items to Think About:  
 *  1.  We need to use the PID functions for acceleration and deceleration in movements and turning.
 */

package org.usfirst.frc.team5914.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
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
	
	RobotDrive drive;

	int auto_init = 1;
	
	@Override
	public void robotInit() {

		drive = new RobotDrive();
		
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
	
//	 * 11/29/2018: In the following initialization we get an initial heading which
//	 * should be zero following call of the reset() function. We also get an initial
//	 * value for the encoder, which, at this point could be anything. We then
//	 * calculated the number of encoder counts to travel based on the wheel diameter
//	 * and the distance (in feet) we wish to travel.

	// 12/6/2018:
	// It appears that after a reset, the encoder counts INCREASE regardless of the
	// direction
	// The autonomous block below treats the case of moving in reverse
	// Suspected is that when we move to class-based structure, we may need to reset
	// the encoder if we're going to be going in reverse direction

	@Override
	public void autonomousInit() {

		// int counts;
		
		// Setup gyro
		drive.gyro.calibrate();
		drive.gyro.reset();

		// get an initial direction
		drive.initAngle = drive.gyro.getAngle();

		System.out.println("Initial Gyro Reading = " + drive.initAngle);

	}
	
	// * 11/29/2018: The goal
	// * here is to move forward to a target encoder count We start at a high speed
	// * and slow as we approach the target. At the end we output the overshoot.
	// * Question: Is braking the default mode for the Talon?

	@Override
	public void autonomousPeriodic() {
		
		if (auto_init == 1) {
			drive.driveFwd(5.0);
			auto_init = 0;
		}
		
/*
		// Read encoder #1
		// current_position = rMath.abs(backLeft.getSelectedSensorPosition(1));

		current_position = drive.backLeft.getSelectedSensorPosition(1);

		fraction = ((double) (drive.endPosition1 - current_position) / (double) (drive.endPosition1 - drive.initPosition1));

		System.out.println("current_position = " + current_position + " end_position = " + drive.endPosition1 + " fraction = "
				+ fraction);

		// We attempt a bit of proportional control as we approach the target. We want
		// to slow down
		// so that we don't overshoot.
		// Do we have braking enabled by default??
		if (current_position < drive.endPosition1) {
			if (fraction > 0.20) {
				moveBwd(speed, drive.initAngle);
			} else {
				moveBwd(0.4, drive.initAngle);
			}

			// moveFwd(speed, initAngle);

		} else {
			// stop movement
			drive.diff_drive.arcadeDrive(0, 0);
			System.out.println("overshoot = " + (current_position - drive.endPosition1));
		}
		*/
	}

	/*
	 * This function is called periodically during operator control. At this point
	 * we are merely trying to study the encoder function and determine if this is a
	 * viable method to drive the robot straight. 11/20/18: We appear to have a
	 * crude correction mechanism that does work toward equating the encoder values
	 * but it is not operating consistently. Problems that may occur with the
	 * attempts last night may be due to the speeds maxing out. The correction below
	 * decides (based on a maximum allowable speed) whether to reduce or increase
	 * the motor speed of a particular side to equate the left/right encoder
	 * readings. The code below is for the purpose of familiarizing the use of
	 * encoders. We may want to use the gyro instead for direction and one of the
	 * encoders for distance. Ultimately this would be in the autonomous part of the
	 * code. 11/29/2018: We noticed last session that not updating often produces a
	 * very choppy motor drive. Try it with a faster update rate and greater gain
	 * (speed correction).
	 */
	@Override
	public void teleopPeriodic() {

		
		double stick0;
		double stick1;

		stick0 = drive.stick.getRawAxis(0);
		stick1 = drive.stick.getRawAxis(1);

		drive.diff_drive.arcadeDrive(-stick1, stick0);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

}