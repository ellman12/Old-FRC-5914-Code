/////////////////////////////////////////////////////////////////////
//  File:  Pneumatics.java
/////////////////////////////////////////////////////////////////////
//
//Purpose:  Defines the class responsible for all of the pneumatics
// stuff on the robot for the 2019 FRC competition (Luna).
//
//Programmers:  Elliott DuCharme for Caledonia Robotic Warriors team 5914
//
//Revision Date: 2/5/2019
//
//Remarks: 
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class Pneumatics {

    // Linking classes together
    Robot robot;
    RobotDrive drive;

    // Creates the compressor
    Compressor compressor;

    // Creates the double solenoid
    DoubleSolenoid intakeSolenoid;

    // Creates both solenoids for the 3rd platform design
    Solenoid pedestal;

    // determines if the button for the solenoids is pressed, and activates them to
    // lift the robot
    boolean isUp;

    public Pneumatics() {

        drive = new RobotDrive();
        robot = new Robot();

        compressor = new Compressor(0);

        intakeSolenoid = new DoubleSolenoid(0, 1);

        compressor.start();
    }

    // This method controls the pneumatic arms
    public void IntakeArms() {
        if (drive.stick.getRawButton(robot.RIGHT_SHOULDER_BUTTON) == false) {
            intakeSolenoid.set(DoubleSolenoid.Value.kForward);
        } else if (drive.stick.getRawButton(robot.RIGHT_SHOULDER_BUTTON) == true) {
            intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
    }

}