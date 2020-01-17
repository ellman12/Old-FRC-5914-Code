/////////////////////////////////////////////////////////////////////
//  File:
/////////////////////////////////////////////////////////////////////
//
//  Purpose:
//
//  Programmer:
//
//  Environment:
//
//  Inception Date:
//
//  Revisions:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

//  This program dealt specifically with the "go kart" practice robot.  The
//  source file associated with the robot drive "RobotDrive.java" used Sim
//  motors vs. the Neo motors in the actual robot.
package frc.robot;

//import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // final String defaultAuto = "Default";
  // final String customAuto = "My Auto";
  // String autoSelected;
  // SendableChooser<String> chooser = new SendableChooser<>();

  double speedCap = 1;

  // Used for autonomous
  int autoInit = 1;

  // These should be in the file/class where the constructor for the
  // threads are called. It is anticipated that other threads involving
  // the motion of the inner and outer lift mechanisms would also
  // play off of these definitions. Note the use of "static"; this allows
  // these variables to be visible in other classes. LJB 3/4/19.
  final static int START_POS = 0;
  final static int LOAD_PLATE = 1;
  final static int LOAD_BALL = 2;
  final static int PLACE_PLATE_L1 = 3;
  final static int PLACE_PLATE_L2 = 4;
  final static int PLACE_PLATE_L3 = 5;
  final static int SHOOT_BALL_CARGO = 6;
  final static int SHOOT_BALL_RL1 = 7;
  final static int SHOOT_BALL_RL2 = 8;
  // final static int SHOOT_BALL_RL3 = 9;

  // These variables are specific to the
  // tilt thread. The nature of the run()
  // function within the thread prohibits
  // passing of variables as function arguments.
  public static boolean tilt_thread_active = false;
  public static double tilt_position = 0;
  public static int tilt_op = START_POS;

  public static CANSparkMax tilt_motor;
  public static CANEncoder tilt_enc;

  public static boolean inner_lift_thread_active = false;
  public static int inner_lift_position = 0;
  public static int inner_lift_op = START_POS;

  public static WPI_TalonSRX inner_lift;
  public static Encoder MagEncoder; // Encoder for the Redline motor

  // Creating drive motors for outer lift
  public static WPI_TalonSRX left_motor;
  public static WPI_TalonSRX right_motor;

  public static SpeedControllerGroup outer_lift;
  public static boolean outer_lift_thread_active = false;
  public static int outer_lift_position = 0;
  public static int outer_lift_op = START_POS;

  public static boolean sensor_thread_active = false;

  final int BUTTON_A = 1;
  final int BUTTON_B = 2;
  final int BUTTON_X = 3;
  final int BUTTON_Y = 4;
  final int LEFT_SHOULDER_BUTTON = 5;
  final int RIGHT_SHOULDER_BUTTON = 6;
  final int BACK_BUTTON = 7;
  final int START_BUTTON = 8;
  final int LEFT_ANALOG_STICK_BUTTON = 9;
  final int RIGHT_ANALOG_STICK_BUTTON = 10;
  public double LEFT_ANALOG_STICK_X_AXIS; // = drive.stick.getRawAxis(0);
  public double LEFT_ANALOG_STICK_Y_AXIS; // = drive.stick.getRawAxis(1);
  double LEFT_ANALOG_TRIGGER; // = drive.stick.getRawAxis(2);
  double RIGHT_ANALOG_TRIGGER; // = drive.stick.getRawAxis(3);
  // double RIGHT_ANALOG_STICK_X_AXIS; // = drive.stick.getRawAxis(4);
  // double RIGHT_ANALOG_STICK_Y_AXIS; // = drive.stick.getRawAXIS(5);

  public static final String speedChoice = "speedChoice";
  String speed;
  public SendableChooser<String> Speed = new SendableChooser<>();

  Joystick stick;

  RobotDrive drive;
  Intake intake;
  Pneumatics pneumatics;

  @Override
  public void robotInit() {
    SmartDashboard.putData("speedChoice", Speed);

    drive = new RobotDrive();
    intake = new Intake();
    pneumatics = new Pneumatics();

    // Create the motor drive and encoder for the intake tilt
    tilt_motor = new CANSparkMax(5, MotorType.kBrushless);
    tilt_enc = new CANEncoder(tilt_motor);
    tilt_motor.setIdleMode(IdleMode.kBrake);
    tilt_motor.setMotorType(MotorType.kBrushless);

    tilt_enc.setPosition(0.0);
    tilt_position = tilt_enc.getPosition();
    System.out.println("RobotInit Tilt Encoder Value = " + tilt_position);

    // Redline encoder, 1024 counts per rev.
    // Define an encoder on Channels 0 and 1 without reversing direction and 4x
    // encoding type. // I believe that "false" means we don't reverse direction.
    MagEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
    MagEncoder.reset(); // Interesting does this set the count to zero?
    // Output count to console
    System.out.println("Initial RedLine Encoder count = " + MagEncoder.get());

    // Motors and encoders for the outer lift. Encoder is on output shaft,
    // encoder has 4095 counts per rev.
    left_motor = new WPI_TalonSRX(7);
    right_motor = new WPI_TalonSRX(8);

    // This is the inner lift parameter
    inner_lift = new WPI_TalonSRX(9);
    inner_lift.setInverted(true);

    outer_lift = new SpeedControllerGroup(left_motor, right_motor);
    left_motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
    left_motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);
    outer_lift_position = left_motor.getSelectedSensorPosition();
    System.out.println("Outer lift position = " + outer_lift_position);

    // This is for the manual drive - no encoders required
    stick = new Joystick(0);

    drive.diff_drive.setSafetyEnabled(false);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
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

    // autoSelected = chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto selector", defaultAuto);
    // System.out.println("Auto selected: " + autoSelected);

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // if (autoInit == 1) {
    // drive.driveFwd(5.0);
    // drive.turnRight_Arcade(90);
    // if (tilt_thread_active == false) {
    // tilt_thread_active = true;
    // tilt_op = SHOOT_BALL_CARGO;
    // new IntakeTilt("SHOOT_BALL_CARGO");
    // Timer.delay(0.2);
    // } else {
    // System.out.println("Tilt Thread Active");
    // }
    // autoInit = 0;
    // }
  }

  /**
   * This function is called periodically during operator control.
   */
  // It is within this function that one will decide how to position
  // the tilt and inner and outer carriages for the various tasks.
  // Depending on button pushes or another scheme, threads will be
  // created and executed. You should also be able to drive the
  // robot using the joystick. The key thing that will need to
  // be determined empirically is the necessary delays within the
  // thread run() functions to allow each thread (including the main
  // thread - this one) to access the CPU.
  @Override
  public void teleopPeriodic() {

    speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
    SmartDashboard.putNumber(speedChoice, speedCap * 100);

    if (drive.stick.getRawButton(BUTTON_A) == true) {
      intake.leftIntake.set(-.4);
      intake.rightIntake.set(-.4);
    } else if (drive.stick.getRawButton(BUTTON_A) == false && drive.stick.getRawButton(LEFT_SHOULDER_BUTTON) == false) {
      intake.leftIntake.stopMotor();
      intake.rightIntake.stopMotor();
    }

    // This method is used for intaking balls
    if (drive.stick.getRawButton(LEFT_SHOULDER_BUTTON) == true) {
      intake.leftIntake.set(.4);
      intake.rightIntake.set(.4);
    } else if (drive.stick.getRawButton(LEFT_SHOULDER_BUTTON) == false && drive.stick.getRawButton(BUTTON_A) == false) {
      intake.leftIntake.stopMotor();
      intake.rightIntake.stopMotor();
    }

    // Thread creation and execution. Note that this call would be
    // preceeded with some sort of button push. You would not want
    // to create this over and over - there has to be some operator
    // input that decides this is to be done. Brady's button scheme
    // is required. But this is how you start the thread.

    // your robot arcade drive would be here with a possible short delay
    // to allow timesharing between the joystick operations and the
    // thread execution.

    // Manually operate the tilt motor - this seems to work
    double stick5;
    stick5 = stick.getRawAxis(5);

    // Prevent use of the joystick when the tilt thread is active
    if (tilt_thread_active == false) {
      if (stick5 > -0.1 && stick5 < 0.1) {
        tilt_motor.stopMotor();
      } else {
        tilt_motor.set(stick5);
      }
    }

    // Need to provide manual capability for inner and outer lifts.

    // Arcade drive setup and execution.
    double stick0;
    double stick1;

    stick0 = stick.getRawAxis(0);
    stick1 = stick.getRawAxis(1);

    drive.diff_drive.arcadeDrive(-stick1 / 2, stick0 / 3);

    // This is ready to be tested
    // drive.diff_drive.arcadeDrive(-stick.getRawAxis(1) * speedCap,
    // stick.getRawAxis(0) * speedCap);

    // Here we attempt to use button presses to operate
    // the tilt. 3/14/2019: The flag "tilt_thread_active"
    // will ignore acting on the button press once the thread
    // is started. Flag is reset on completion of the thread
    // in the thread run() function.
    // if (stick.getRawButton(4) == true) {
    // System.out.println("Y button pressed");
    // if (tilt_thread_active == false) {
    // tilt_thread_active = true;
    // tilt_op = LOAD_PLATE;
    // new IntakeTilt("Load Plate");
    // Timer.delay(0.2);
    // } else {
    // System.out.println("Tilt Thread Active");
    // }
    // }

    // if (stick.getRawButton(3) == true) {
    // System.out.println("X button pressed");
    // if (tilt_thread_active == false) {
    // tilt_thread_active = true;
    // tilt_op = LOAD_BALL;
    // new IntakeTilt("Load Ball");
    // Timer.delay(0.2);
    // } else {
    // System.out.println("Tilt Thread Active");
    // }
    // }

    // if (stick.getRawButton(1) == true) {
    // System.out.println("A button pressed");
    // if (tilt_thread_active == false) {
    // tilt_thread_active = true;
    // tilt_op = SHOOT_BALL_CARGO;
    // new IntakeTilt("Shoot Ball Cargo");
    // Timer.delay(0.2);
    // } else {
    // System.out.println("Tilt Thread Active");
    // }
    // }

    if (sensor_thread_active = false) {

      new SensorThread("ReadSensors");
      sensor_thread_active = true;
    }

    // This variable is used for making the inner carriage go up/down
    double triggerAxis;

    // Axes 2 and 3 are the left and right analog triggers, respectively
    triggerAxis = drive.stick.getRawAxis(2) - drive.stick.getRawAxis(3);
    inner_lift.set(triggerAxis * 0.50);

    pneumatics.IntakeArms();
  }

  @Override
  public void testPeriodic() {
  }

}