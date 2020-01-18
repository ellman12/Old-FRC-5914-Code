//This is the official and final competition code for Vulcan. This was made mainly to have a blank slate for the autonomous code

package org.usfirst.frc.team5914.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	private static final String Left = "Left";
	private static final String Middle = "Middle";
	private static final String Right = "Right";
	private String positionChoice;
	private SendableChooser<String> Position = new SendableChooser<>();

	private static final String Switch = "Switch";
	private static final String Scale = "Scale";
	private String targetChoice;
	private SendableChooser<String> Target = new SendableChooser<>();

	public static final String speedChoice = "speedChoice";
	public String speed;
	public SendableChooser<String> Speed = new SendableChooser<>();

	// Position = left, middle, or right
	// Target = Switch or Scale

	public static double speedCap = 1;

	// String for getting the locations of the colors for the Switch and the Scale.

	public static double speedFraction;

	static String gameData;

	static String InitGameData;

	PowerDistributionPanel pdp;

	// Declaring the Joystick

	static Joystick stick;

	// Incorporates the other classes into Robot.java.

	static VulcanDrive drive_system;

	static LiftMechanism lift_system;

	static IntakeMechanism intake_system;

	static WinchMechanism winch_system;

	static Autonomous auto_functions;

	// make sure autonomous only runs once
	static boolean autoComplete;

	boolean testComplete;

	double ampsAvg = 0;
	double wattsAvg = 0;
	double counterAmps = 0;
	double counterWatts = 0;
	double ampsHigh = -1;
	double wattsHigh = -1;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		Position.addObject("Left", Left);
		Position.addDefault("Middle", Middle);
		Position.addObject("Right", Right);
		SmartDashboard.putData("Position choice", Position);

		Target.addDefault("Switch", Switch);
		Target.addObject("Scale", Scale);
		SmartDashboard.putData("Target choice", Target);

		Speed.addObject("Speed Choice", speedChoice);
		SmartDashboard.putData("speedChoice", Speed);

		// Makes the String 'InitGameData' equal to the positions for the Switch and the
		// Scale.

		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();

		camera.setResolution(360, 240);
		camera.setFPS(15);

		// Creates the Joystick

		stick = new Joystick(0);

		// Makes the other classes usable in the Robot.java class.

		drive_system = new VulcanDrive();

		lift_system = new LiftMechanism();

		intake_system = new IntakeMechanism();

		winch_system = new WinchMechanism();

		auto_functions = new Autonomous();

		// Calibrates and resets the gyro's position.

		drive_system.gyro.calibrate();
		drive_system.gyro.reset();

		drive_system.frontLeft.setSafetyEnabled(false);
		drive_system.backLeft.setSafetyEnabled(false);
		drive_system.frontRight.setSafetyEnabled(false);
		drive_system.backRight.setSafetyEnabled(false);

		drive_system.drive.setSafetyEnabled(false);

		pdp = new PowerDistributionPanel(0);
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
		positionChoice = Position.getSelected();
		targetChoice = Target.getSelected();

		// Allows the Driver Station to get the positions for the Switch and the
		// Scale...

		autoComplete = false;

		drive_system.gyro.reset();

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		InitGameData = getGameData();

		drive_system.gyro.reset();

		drive_system.frontLeft.setSafetyEnabled(false);
		drive_system.backLeft.setSafetyEnabled(false);
		drive_system.frontRight.setSafetyEnabled(false);
		drive_system.backRight.setSafetyEnabled(false);

		// Decides which autonomous instance to run.
		// The user chooses the left (1), middle (2), or right (3) position through
		// SmartDashboard, then the program automatically chooses which switch statement
		// to run based on the positions of your team's color

		while (autoComplete == false) {

			switch (positionChoice) {

			case Left:

				switch (targetChoice) {

				case Switch:

					switch (InitGameData) {

					case "LL":

						auto_functions.LeftSwitchLL();

						break;

					case "RR":

						auto_functions.LeftSwitchRR();

						break;

					case "LR":

						auto_functions.LeftSwitchLR();

						break;

					case "RL":

						auto_functions.LeftSwitchRL();

						// auto_functions.Forward();

						break;
					}

					break;

				case Scale:

					switch (InitGameData) {

					case "LL":

						auto_functions.LeftScaleLL();

						// auto_functions.LeftSwitchLL();

						break;

					case "RR":

						auto_functions.LeftScaleRR();

						break;

					case "LR":

						auto_functions.LeftScaleLR();

						break;

					case "RL":

						auto_functions.LeftScaleRL();

						// auto_functions.Forward();

						break;
					}

					break;
				}
				break;

			case Middle:

				switch (targetChoice) {

				case Switch:

					switch (InitGameData) {

					case "LL":

						auto_functions.MiddleSwitchLL();

						break;

					case "RR":

						auto_functions.MiddleSwitchRR();

						break;

					case "LR":

						auto_functions.MiddleSwitchLR();

						break;

					case "RL":

						auto_functions.MiddleSwitchRL();

						break;
					}

					break;

				case Scale:

					switch (InitGameData) {

					case "LL":

						auto_functions.MiddleScaleLL();

						break;

					case "RR":

						auto_functions.MiddleScaleRR();

						break;

					case "LR":

						auto_functions.MiddleScaleLR();

						break;

					case "RL":

						auto_functions.MiddleScaleRL();

						break;
					}

					break;
				}

				break;
			case Right:

				switch (targetChoice) {

				case Switch:

					switch (InitGameData) {

					case "LL":

						auto_functions.RightSwitchLL();

						break;

					case "RR":

						auto_functions.RightSwitchRR();

						break;

					case "LR":

						auto_functions.RightSwitchLR();

						// auto_functions.Forward();

						break;

					case "RL":

						auto_functions.RightSwitchRL();

						break;
					}

					break;

				case Scale:

					switch (InitGameData) {

					case "LL":

						auto_functions.RightScaleLL();

						break;

					case "RR":

						auto_functions.RightScaleRR();

						// auto_functions.RightSwitchRR();

						break;

					case "LR":

						auto_functions.RightScaleLR();

						// auto_functions.Forward();
						break;

					case "RL":

						auto_functions.RightScaleRL();

						break;
					}
					break;
				}
				break;
			}
			autoComplete = true;
		}
		drive_system.allDrive.set(0);
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

		speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
		SmartDashboard.putNumber(speedChoice, speedCap * 100);

		double amps;
		double watts;
		double voltage;

		SmartDashboard.putNumber("Gyro", drive_system.gyro.getAngle());

		// Every button/joystick function in all classes

		drive_system.joystickArcadeDrive();

		lift_system.runLift();

		intake_system.Inhale();

		winch_system.WinchOperation();

		// // print out the battery stuff for testing
		// amps = pdp.getTotalCurrent();
		// watts = pdp.getTotalPower();
		// voltage = pdp.getVoltage();
		//
		// if (amps != 0) {
		// counterAmps += 1;
		// ampsAvg += amps;
		//
		// // System.out.println("Amps: " + amps);
		// // System.out.println("Watts: " + watts);
		// // System.out.println("Voltage: " + voltage);
		// // System.out.println("Winch Percentage: " + stick.getRawAxis(3) +
		// // stick.getRawAxis(2) * -1);
		// // System.out.println(" ");
		//
		// }
		//
		// if (watts != 0) {
		// counterWatts += 1;
		// wattsAvg += watts;
		// }
		//
		// if (amps > ampsHigh) {
		// ampsHigh = amps;
		// }
		// if (watts > wattsHigh) {
		// wattsHigh = watts;
		// }
		//
		// if (stick.getRawButton(8) == true) {
		// ampsAvg = ampsAvg - ampsHigh;
		// counterAmps = counterAmps - 1;
		// wattsAvg = wattsAvg - wattsHigh;
		// counterWatts = counterWatts - 1;
		// ampsAvg = ampsAvg / counterAmps;
		// wattsAvg = wattsAvg / counterWatts;
		// System.out.println("Amps Average: " + ampsAvg);
		// System.out.println("Watts Avg: " + wattsAvg);
		// System.out.println("Amps High: " + ampsHigh);
		// System.out.println("Watts High: " + wattsHigh);
		// counterAmps = 0;
		// counterWatts = 0;
		// wattsAvg = 0;
		// ampsAvg = 0;
		// wattsHigh = 0;
		// ampsHigh = 0;
		// }

	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	public String getGameData() {

		gameData = DriverStation.getInstance().getGameSpecificMessage();

		gameData = gameData.substring(0, 2);

		SmartDashboard.putString("Colors", gameData);

		return (gameData);
	}

}