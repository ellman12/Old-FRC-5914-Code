package org.usfirst.frc.team5914.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	// AUTO
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	// Compressor
	final String defaultCompressor = "Default";
	final String compressorOff = "Compressor Off";
	String compressorSelected;
	SendableChooser<String> compressorChooser = new SendableChooser<>();

	CANTalon liftM1, liftM2, leftF, leftR, rightF, rightR;
	// channels for motors
	final int leftMotorChannel = 1;
	final int rightMotorChannel = 2;// 3
	final int leftRearMotorChannel = 4;
	final int rightRearMotorChannel = 3;// 2

	final int gyroChannel = 0; // analog input

	NetworkTable table;
	Relay light;
	RobotDrive myRobot;
	Joystick xBoxController;
	ADXRS450_Gyro gyro;
	BuiltInAccelerometer acclmtr;
	CameraServer server;
	Compressor comp;
	DoubleSolenoid dubSol;
	Solenoid sol1, sol2;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public Robot() {
		table = NetworkTable.getTable("GRIP/myBlobsReport");
	}

	@Override
	public void robotInit() {
		// CameraServer.getInstance().startAutomaticCapture();

		// auto
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);

		// Compressor
		compressorChooser.addDefault("Compressor Auto Cycle", defaultCompressor);
		compressorChooser.addObject("Compressor Off", compressorOff);
		SmartDashboard.putData("Compressor Settings", compressorChooser);

		////////////////////////////////////
		leftF = new CANTalon(leftMotorChannel);
		leftR = new CANTalon(leftRearMotorChannel);
		rightF = new CANTalon(rightMotorChannel);
		rightR = new CANTalon(rightRearMotorChannel);

		// leftR.configMaxOutputVoltage(3);
		// rightR.configNominalOutputVoltage(5, -5);
		light = new Relay(0);
		// make objects for drive train, xBoxController, and gyro
		myRobot = new RobotDrive(leftF, leftR, rightF, rightR);
		myRobot.setInvertedMotor(MotorType.kFrontLeft, true);
		myRobot.setInvertedMotor(MotorType.kRearLeft, true);

		liftM1 = new CANTalon(5);
		liftM2 = new CANTalon(6);

		xBoxController = new Joystick(0);

		gyro = new ADXRS450_Gyro();
		acclmtr = new BuiltInAccelerometer(Accelerometer.Range.k8G);
		comp = new Compressor(0);
		sol1 = new Solenoid(2);
		sol2 = new Solenoid(0);
		dubSol = new DoubleSolenoid(3, 4);

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */

	double tapeX;
	double tapeZ;
	String tape = SmartDashboard.getString("COG_X", " ");

	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			light.set(Relay.Value.kOff);
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			light.set(Relay.Value.kForward);
			break;
		}

		// = (SmartDashboard.getNumber("COG_X", 0.0) - 119.5) / 239;
		// tapeZ = SmartDashboard.getNumber("COG_BOX_SIZE", 0.0);
		// comp.setClosedLoopControl(true);

		// System.out.println(tapeX);
		// gyro.reset();
		// myRobot.mecanumDrive_Cartesian(0, 0, -tapeX,
		// 0/*gyro.getAngle()*/);

	}

	public void teleopInit() {
		// gyro.calibrate();
		startedClimb = false;
		gyro.reset();

		compressorSelected = compressorChooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Compressor selected: " + compressorSelected);
	}

	/**
	 * This function is called periodically during operator control
	 */
	double throttle;
	double newX, newY;
	boolean yes = true;

	@Override
	public void teleopPeriodic() {

		while (isOperatorControl() && isEnabled()) {
			GRIPMath();

			// tableSort( x, xBlobs, xBlobs1, blobCenter);
			// SmartDashboard.putString("TAPEX", table.getString("COG_X", ""));
			// System.out.println(tapeX);
			// System.out.println(SmartDashboard.getNumber("COG_X", 0));
			// SmartDashboard.putNumber("ACCL",acclmtr.getY());
			table.putNumber("ACCL", -1 / (acclmtr.getY() * 10 + 1));
			lift();
			driveStraight();

			sensitivexBoxController();

			// xyz mapped to zyx on controller

			pneu();

			// SmartDashboard.getNumber("COG_X");

			if (xBoxController.getRawButton(1) == true && light.get() == Relay.Value.kOff && yes == true) {
				yes = false;
				light.set(Relay.Value.kForward);
			} else if (xBoxController.getRawButton(1) == true && light.get() == Relay.Value.kForward && yes == true) {
				yes = false;
				light.set(Relay.Value.kOff);
			} else if (xBoxController.getRawButton(1) == false) {
				yes = true;
			}
			table.putNumber("GRYO", gyro.getAngle());
		}
	}// end
		// telop

	// double blobCenter;
	double blobx1, bloby1, blobSize1, YBLOB;

	void GRIPMath() {
		double[] defaultValue = new double[0];
		double[] xBlobs = table.getNumberArray("x", defaultValue);
		if (xBlobs.length == 1) {
			SmartDashboard.putNumber("xBlob", (int) xBlobs[0]);
			blobx1 = (int) xBlobs[0];

		} else if (xBlobs.length > 1) {
			double max = 0;
			int indexMax = -1;
			for (int i = 0; i < xBlobs.length; i++) {
				if (xBlobs[i] > max) {
					indexMax = i;
					max = xBlobs[i];
				}
			}
			SmartDashboard.putNumber("xBlob2", (int) xBlobs[indexMax]);
			blobCenter = ((int) xBlobs[indexMax] + blobx1) / 2;
			SmartDashboard.putNumber("Center of Blob", blobCenter);
			if (YBLOB < 160) {
				sol2.set(true);
				myRobot.mecanumDrive_Cartesian((blobCenter - 114) / 228 - .1,
						Math.abs(.7 * Math.sin(2 * Math.PI * .009 * YBLOB + .5)), 0, 0);
			} else {

				myRobot.mecanumDrive_Cartesian(0, Math.abs(.7 * Math.sin(2 * Math.PI * .009 * YBLOB + .5)), 0, 0);
			}
			System.out.println((blobCenter - 114) / 228 - .1);
		}
		////////////////////////////
		double[] yBlobs = table.getNumberArray("y", defaultValue);
		if (yBlobs.length == 1) {
			SmartDashboard.putNumber("yBlob", (int) yBlobs[0]);
			bloby1 = (int) yBlobs[0];

		} else if (yBlobs.length > 1) {

			double max = 0;
			int indexMax = -1;
			for (int i = 0; i < yBlobs.length; i++) {
				if (yBlobs[i] > max) {
					indexMax = i;
					max = yBlobs[i];
				}
			}
			SmartDashboard.putNumber("yBlob2", (int) yBlobs[indexMax]);
			YBLOB = ((int) yBlobs[indexMax]);// *(bloby1)/bloby1;
			System.out.println(YBLOB);
		}
	}

	String x = "x";
	double[] xBlobs;
	double xBlobs1;
	double blobCenter;

	// tableSort( x, xBlobs, xBlobs1, blobCenter);

	void tableSort(String name, double[] arrayName, double numberTransfere, double blobMath) {
		double[] defaultValue = new double[0];
		arrayName = table.getNumberArray(name, defaultValue);
		if (arrayName.length == 1) {
			SmartDashboard.putNumber(arrayName + "1", (int) arrayName[0]);
			numberTransfere = (int) arrayName[0];

		} else if (arrayName.length > 1) {
			double max = 0;
			int indexMax = -1;
			for (int i = 0; i < arrayName.length; i++) {
				if (arrayName[i] > max) {
					indexMax = i;
					max = arrayName[i];
				}
			}
			SmartDashboard.putNumber(arrayName + "2", (int) arrayName[indexMax]);
			blobMath = ((int) arrayName[indexMax] + numberTransfere) / 2;
			SmartDashboard.putNumber("Math: " + arrayName, (blobMath - 119.5) / 239);
		}
	}

	int num;

	void driveStraight() {
		gyro.getAngle();
		if (xBoxController.getRawButton(5) == true) {
			num = 1;
		} else if (xBoxController.getRawButton(6) == true) {
			num = 2;
		} else {
			num = 0;
		}
		// System.out.println(xBoxController.getRawAxis(5));
		switch (num) {
		case 0:
			// normal drive
			myRobot.setInvertedMotor(MotorType.kFrontLeft, true);
			myRobot.setInvertedMotor(MotorType.kRearLeft, true);

			myRobot.mecanumDrive_Cartesian((xBoxController.getRawAxis(3) - xBoxController.getRawAxis(2)) * throttle,
					-newY, -newX, -gyro.getAngle());
			Timer.delay(0.005); // wait 5ms to avoid hogging CPU cycles
			break;

		case 1:
			myRobot.mecanumDrive_Cartesian((xBoxController.getRawAxis(3) - xBoxController.getRawAxis(2)) * throttle,
					-newY, -newX, 0);
			break;

		case 2:
			myRobot.mecanumDrive_Cartesian((xBoxController.getRawAxis(3) - xBoxController.getRawAxis(2)) * throttle,
					newY, -newX, 0);
			break;
		}
		if (xBoxController.getRawButton(10)) {
			myRobot.setInvertedMotor(MotorType.kFrontLeft, false);
			myRobot.setInvertedMotor(MotorType.kRearLeft, false);

			myRobot.drive(-xBoxController.getY(), gyro.getAngle());
			Timer.delay(0.004);
		} else {

		}
	}

	void sensitivexBoxController() {
		// middle =.5, up = slower, down = faster
		throttle = 1;//xBoxController.getRawAxis(5) / 2 + .6;

		if (Math.abs(xBoxController.getY()) > .3) {
			newY = xBoxController.getY() * throttle;
		} else {
			newY = 0;
		}
		if (Math.abs(xBoxController.getX()) > .3) {
			newX = xBoxController.getX() * throttle;
		} else {
			newX = 0;
		}

		if (xBoxController.getRawButton(2)) {
			gyro.reset();
		}
	}

	boolean compBool = true;

	void pneu() {
		compressorSelected = compressorChooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);

		switch (compressorSelected) {
		case compressorOff:
			compBool = false;
			break;
		case defaultCompressor:
		default:
			compBool = true;
			break;
		}

		if ((xBoxController.getRawButton(8) == true && comp.getClosedLoopControl() == false) || compBool == true) {
			comp.setClosedLoopControl(true);
			// xBoxController.setRumble(Joystick.RumbleType.kLeftRumble, 1);
		} else {
			if (xBoxController.getRawButton(7) == true && comp.getClosedLoopControl() == true || compBool == false) {
				comp.setClosedLoopControl(false);
				// xBoxController.setRumble(Joystick.RumbleType.kRightRumble,
				// 1);
			} else {
				// xBoxController.setRumble(Joystick.RumbleType.kLeftRumble, 0);
				// xBoxController.setRumble(Joystick.RumbleType.kRightRumble,
				// 0);
			}
		}
		/*
		 * if (xBoxController.getRawButton(4) == true) {
		 * 
		 * sol1.set(true); CameraServer.getInstance().startAutomaticCapture();
		 * 
		 * } else { sol1.set(false); }
		 */
		if (xBoxController.getRawButton(3) == true) {

			sol2.set(true);

		} else {
			sol2.set(false);
		}

	}

	boolean startedClimb;
boolean once = false;
	void lift() {

		// System.out.println(acclmtr.getY());

		if (xBoxController.getRawButton(4) == true) {
			sol1.set(true);
		} else if (startedClimb != true) {
			sol1.set(false);
		}

		if (xBoxController.getRawButton(4) == true) {
			if(once==false){
			Timer.delay(.3);
			}
			liftM1.set(-.3);
			liftM2.set(-.3);
			once=true;

		} else {
			once=false;
			liftM1.set(0);
			liftM2.set(0);
		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
