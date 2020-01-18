package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

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

  // This variable determines whether or not the joystick inputs will be squared
  // or not
  // The only time this is false is when the controller is in CubeInputsMode
  public boolean SquareInputs;

  public static double speedCap = 1;

  public static final String speedChoice = "speedChoice";
  String speed;
  public SendableChooser<String> Speed = new SendableChooser<>();

  // Linking other classes together
  RobotDrive drive;
  Intake intake;
  Lift lift;
  Pneumatics pneumatics;
  // Endgame endgame;

  // These variables are used for the multiple-button-press thing
  boolean pressedYonce;
  boolean pressedYtwice;
  boolean pressedYthrice;

  boolean pressedXonce;
  boolean pressedXtwice;
  boolean pressedXthrice;
  boolean pressedXquad;

  // Gets the current match time, for using timers without delaying the whole
  // system or for setting certain powers for endgame
  // double gameTime;

  // Gets the current game time and creates a delay without delaying the whole
  // system. CANNOT BE USED IN AUTO
  // double gameDelay;

  // Allows the function to continue after using gameDelay
  // boolean gameStart;

  // Used at the beginning of the delay to grab the gametime and allow the
  // equation to work properly
  // boolean delayStart;

  // Used to get the starting delay time and allow the equation to work
  // double delayStartingGameTime;

  @Override
  public void robotInit() {
    SmartDashboard.putData("speedChoice", Speed);

    drive = new RobotDrive();
    intake = new Intake();
    lift = new Lift();
    pneumatics = new Pneumatics();
    // endgame = new Endgame();
    // gameStart = true;
    // delayStart = true;
    // delayStartingGameTime = 0.0;
    SquareInputs = true;

    // pressedYonce = false;
    // pressedYtwice = false;
    // pressedYthrice = false;

    // pressedXonce = false;
    // pressedXtwice = false;
    // pressedXthrice = false;
    // pressedXquad = false;
  }

  @Override
  public void robotPeriodic() {
    // gameTime = Timer.getMatchTime();
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {

    teleopPeriodic();

  }

  @Override
  public void teleopPeriodic() {

    speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
    SmartDashboard.putNumber(speedChoice, speedCap * 100);

    // if (drive.stick.getRawButton(3) == true) {
    // intake.leftIntake.set(-1);
    // intake.rightIntake.set(-1);
    // } else {
    // intake.leftIntake.stopMotor();
    // intake.rightIntake.stopMotor();
    // }

    // Bringing methods from the other classes into this class
    intake.Inhale();
    intake.Exhale();
    intake.Tilt();
    // lift.OuterLift();
    lift.Carriage();
    pneumatics.IntakeArms();
    // endgame.TouchpadPressed();
    // endgame.Hab2();
    // drive.CubeJoystickInputsMode();

    // This is a part of the endgame mode
    // if (gameTime < 30.0) {
    // pneumatics.ThirdPlatform();
    // }

    // Used for eliminating "magic" numbers
    // LEFT_ANALOG_STICK_X_AXIS = drive.stick.getRawAxis(0);
    // LEFT_ANALOG_STICK_Y_AXIS = -drive.stick.getRawAxis(1);

    // Basic ArcadeDrive function used for driving
    // drive.diff_drive.arcadeDrive(-drive.stick.getRawAxis(1),
    // drive.stick.getRawAxis(0) * .76, true);

    // Used for parades and stuff
    drive.diff_drive.arcadeDrive(-drive.stick.getRawAxis(1) / 2, drive.stick.getRawAxis(0) / 2);
  }

  @Override
  public void testPeriodic() {
  }

  // This method is a much better way to add delays to robot programs
  // It has not been tested, and most likely does not work
  // public void delaySeconds(double delayInSeconds) {
  // if (delayStart = true) {
  // delayStartingGameTime = gameTime;
  // delayStart = false;
  // }
  // if (delayStartingGameTime - delayInSeconds < gameTime) {
  // gameStart = false;
  // }
  // if (delayStartingGameTime - delayInSeconds > gameTime) {
  // gameStart = true;
  // delayStart = true;
  // }
  // }

  // This method is for getting button presses for BUTTON_Y
  // public void gamePositionsY() {
  // if (drive.stick.getRawButton(BUTTON_Y) == true && pressedYonce == false &&
  // pressedYtwice == false
  // && pressedYthrice == false) {
  // pressedYonce = true;
  // } else if (drive.stick.getRawButton(BUTTON_Y) == true && pressedYonce == true
  // && pressedYtwice == false
  // && pressedYthrice == false) {
  // pressedYonce = false;
  // pressedYtwice = true;
  // } else if (drive.stick.getRawButton(BUTTON_Y) == true && pressedYonce ==
  // false && pressedYtwice == true
  // && pressedYthrice == false) {
  // pressedYtwice = false;
  // pressedYthrice = true;
  // } else if (drive.stick.getRawButton(BUTTON_Y) == true && pressedYonce ==
  // false && pressedYtwice == false
  // && pressedYthrice == true) {
  // pressedYthrice = false;
  // pressedYonce = true;
  // }
  // }

  // // This method is for getting button presses for BUTTON_X
  // public void gamePositionsX() {
  // if (drive.stick.getRawButton(BUTTON_X) == true && pressedXonce == false &&
  // pressedXtwice == false
  // && pressedXthrice == false && pressedXquad == false) {
  // pressedXonce = true;
  // } else if (drive.stick.getRawButton(BUTTON_X) == true && pressedXonce == true
  // && pressedXtwice == false
  // && pressedXthrice == false && pressedXquad == false) {
  // pressedXonce = false;
  // pressedXtwice = true;
  // } else if (drive.stick.getRawButton(BUTTON_X) == true && pressedXonce ==
  // false && pressedXtwice == true
  // && pressedXthrice == false && pressedXquad == false) {
  // pressedXtwice = false;
  // pressedXthrice = true;
  // } else if (drive.stick.getRawButton(BUTTON_X) == true && pressedXonce ==
  // false && pressedXtwice == false
  // && pressedXthrice == true && pressedXquad == false) {
  // pressedXthrice = false;
  // pressedXquad = true;
  // } else if (drive.stick.getRawButton(BUTTON_X) == true && pressedXonce ==
  // false && pressedXtwice == false
  // && pressedXthrice == false && pressedXquad == true) {
  // pressedXquad = false;
  // pressedXonce = true;
  // }
  // }

  //
  // public void motorGamePositionsHatch() {
  // if (pressedYonce == true) {
  // intake.tiltPredefinedPositions("Plate Level 1");
  // } else if (pressedYtwice == true) {
  // intake.tiltPredefinedPositions("Plate Level 2");
  // } else if (pressedYthrice == true) {
  // intake.tiltPredefinedPositions("Plate Level 3");
  // } else {
  // intake.intakeTilt.stopMotor();
  // }
  // }

  // public void motorGamePositionsBall() {
  // if (pressedXonce == true) {
  // intake.tiltPredefinedPositions("Ball Pickup");
  // } else if (pressedXtwice == true) {
  // intake.tiltPredefinedPositions("Ball Rocket 1");
  // } else if (pressedXthrice == true) {
  // intake.tiltPredefinedPositions("Ball Cargo Ship");
  // } else if (pressedXquad == true) {
  // intake.tiltPredefinedPositions("Ball Rocket 2");
  // } else {
  // intake.intakeTilt.stopMotor();
  // }
  // }
}