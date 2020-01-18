package org.usfirst.frc.team5914.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

public class IntakeMechanism {

	Spark RightIntake, LeftIntake;
	SpeedControllerGroup Intake;

	public IntakeMechanism() {

		RightIntake = new Spark(8);
		LeftIntake = new Spark(9);
		Intake = new SpeedControllerGroup(RightIntake, LeftIntake);

		RightIntake.setInverted(true);

	}

	public void Inhale() {

		// If button 5 is pressed (left button) intake. If button 6 (right button) is
		// pressed, outtake. Else, don't run motors.

		if (Robot.stick.getRawButton(5) == true) {

			Intake.set(.6);

		} else if (Robot.stick.getRawButton(3) == true) {

			Intake.set(-.5);

		} else if (Robot.stick.getRawButton(6) == true) {

			Intake.set(-.3);

		} else {

			Intake.set(0);
		}
	}

	public void autoEject() {

		Intake.set(-.4);
		
		Timer.delay(.8);
		
		Intake.set(0);

	}

}
