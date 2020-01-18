package org.usfirst.frc.team5914.robot;

import edu.wpi.first.wpilibj.Timer;

public class Autonomous {

	// target is the lazy way to type 90 degrees for turnLeft or turnRight.
	// Because 9 chances out of 10, target is going to be 90.
	// Work smarter, not harder.

	// private double target = 90.0;

	// Startup() is the process the robot does no matter where it is or what it is
	// trying to go for. Basically, it has it go forwards, then backwards to leave
	// the cube, then it drops the arms down, then it intakes the cube and is ready
	// to go.

	public double time[];
	public double distance[];

	public Autonomous() {
		int i;
		time = new double[32];
		distance = new double[32];

		for (i = 0; i < 32; i++) {
			distance[i] = 1.0 * (double) i;
		}
		time[0] = 0.0;
		time[1] = 0.17;
		time[2] = 0.25;
		time[3] = 0.35;
		time[4] = 0.3;
		time[5] = 0.4;
		time[6] = 0.625;
		time[7] = 0.8125;
		time[8] = 0.875;
		time[9] = 1.0;
		time[10] = 1.12;
		time[11] = 1.28;
		time[12] = 1.34;
		time[13] = 1.50;
		time[14] = 1.66;
		time[15] = 1.82;
		time[16] = 1.98;
		time[17] = 2.3;
		time[18] = 2.46;
		time[19] = 2.55;
		time[20] = 2.62;
		time[21] = 2.78;
		time[22] = 2.92;
		time[23] = 3.08;
		time[24] = 3.24;
		time[25] = 3.40;
		time[26] = 3.56;
		time[27] = 4.20;
		time[28] = 4.40;
		time[29] = 4.55;
		time[30] = 4.80;
		time[31] = 0; // impossible to use 31
	}

	public double determineTimeDelay(double travel) {
		int startIndex;
		double fraction;
		double delay;

		if (travel > 30.0) {
			return 0.0;
		}

		startIndex = (int) travel;
		fraction = travel - distance[startIndex];
		delay = fraction * (time[startIndex + 1] - time[startIndex]) + time[startIndex];

		return delay;
	}

	// Startup() works
	public void Startup() {

		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.2);

		Timer.delay(0.2);

		Robot.intake_system.Intake.set(.5);

		Timer.delay(.25);

		Robot.intake_system.Intake.set(0);

		Robot.drive_system.allDrive.set(0);
	}

	public void Forward() {

		Startup();

		Robot.drive_system.moveFwd(12);

		Robot.drive_system.allDrive.set(0);
	}

	public void LeftSwitch() {
		Startup();

		Robot.drive_system.turnLeft(6.5);

		Robot.drive_system.moveFwd(13);

		Timer.delay(1);

		Robot.drive_system.turnRightLoose(95);

		Robot.lift_system.Switch();

	}

	public void RightSwitch() {
		Startup();

		Robot.drive_system.turnRight(5);

		Robot.drive_system.moveFwd(13);

		Timer.delay(1);

		Robot.drive_system.turnLeftLoose(95);

		Robot.drive_system.moveFwd(.5);

		Robot.lift_system.Switch();
	}

	public void LeftScale() {
		Startup();

		Timer.delay(0.1);

		Robot.intake_system.Intake.set(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(.1);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.turnLeft(2.5);

		Robot.drive_system.moveFwd(28.2);

		Timer.delay(.3);

		if (Robot.drive_system.turnRightLoose(70) == -1) {
			return;
		}

		Timer.delay(.1);

		Robot.drive_system.moveBwd(1);

		Timer.delay(.1);

		Robot.drive_system.allDrive.set(0);

		Robot.lift_system.Scale();

		Robot.drive_system.allDrive.set(0);

		Timer.delay(.7);

		Robot.intake_system.Intake.set(-.6);

		Timer.delay(1);

		Robot.intake_system.Intake.set(0);

		Robot.lift_system.Lift.set(.25);

		Timer.delay(.8);

		Robot.lift_system.Lift.set(0);

		Timer.delay(.1);
	}

	public void RightScale() {
		Startup();

		Timer.delay(0.1);

		Robot.intake_system.Intake.set(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(.1);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.turnLeft(6.5);

		Robot.drive_system.moveFwd(29.2);

		Timer.delay(.3);

		if (Robot.drive_system.turnLeftLoose(60) == -1) {
			return;
		}

		Timer.delay(.1);

		Robot.drive_system.moveBwd(2.8);

		Timer.delay(.1);

		Robot.drive_system.allDrive.set(0);

		Robot.lift_system.Scale();

		Robot.drive_system.allDrive.set(0);

		Timer.delay(.7);

		Robot.intake_system.Intake.set(-.65);

		Robot.lift_system.Lift.set(.25);

		Timer.delay(.8);

		Robot.lift_system.Lift.set(0);

		Robot.intake_system.Intake.set(0);

		Timer.delay(.1);
	}

	public void RightMiddle() {

		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.3);

		Timer.delay(0.2);

		Robot.drive_system.turnRightLoose(35);

		Robot.drive_system.moveFwd(11.5);

		Robot.drive_system.turnLeftLoose(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(1.45);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.moveFwd(6);

	}

	public void RightMiddleCube() {

		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.3);

		Timer.delay(0.2);

		Robot.drive_system.turnRightLoose(35);

		Robot.drive_system.moveFwd(11.5);

		Robot.drive_system.turnLeftLoose(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(1.45);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.moveFwd(6);

		Robot.intake_system.Intake.set(-.5);

		Timer.delay(1);

		Robot.intake_system.Intake.set(0);

	}

	public void RightMiddleTwoCube() {

		// moving forward/stop to drop cube

		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.3);

		Timer.delay(0.2);

		// end of dropping cube

		// turn right 35 degrees

		Robot.drive_system.turnRightLoose(35);

		// raise the lift up

		Robot.lift_system.Lift.set(-.5);

		Timer.delay(.55);

		Robot.lift_system.Lift.set(0);

		// move forward 11.5 feet

		Robot.drive_system.moveFwd(7.7, true);

		// back to 0 degrees

		Robot.drive_system.turnLeft(0);

		// Move forward 6 feet

		Robot.drive_system.moveFwd(6);

		// spit out cube

		Robot.intake_system.Intake.set(-.5);

		Timer.delay(1);

		Robot.intake_system.Intake.set(0);

		// two cube start

		// move backwards 5 feet

		Robot.drive_system.moveBwd(3.5);

		// bring the lift down

		Robot.lift_system.Lift.set(0.35);

		Timer.delay(0.7);

		Robot.lift_system.Lift.set(0);

		Timer.delay(.1);

		// turn left 55 degrees to get the next cube

		Robot.drive_system.turnLeftLoose(55);

		// move forward 4 feet

		Robot.drive_system.moveFwd(3.5);

		// intake f r e s h cube

		Robot.intake_system.Intake.set(.6);

		Timer.delay(1.5);

		// back up 4 feet

		Robot.intake_system.Intake.set(0);

		Robot.drive_system.moveBwd(3.5);

		Timer.delay(.1);

		// turn back to 0

		Robot.drive_system.turnRightLoose(0);

		// Raise the lift up

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(1.45);

		Robot.lift_system.Lift.set(0);

		// move forward 6 feet

		Robot.drive_system.moveFwd(6);

		// splorf out the cube

		Robot.intake_system.Intake.set(-.5);

		Timer.delay(.1);

		Robot.lift_system.Lift.set(0);

		Robot.intake_system.Intake.set(0);

		Robot.drive_system.allDrive.set(0);

	}

	public void LeftMiddleTwoCube() {
		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.3);

		Timer.delay(0.2);

		Robot.drive_system.turnRightLoose(35);

		Robot.drive_system.moveFwd(11.5);

		Robot.drive_system.turnLeftLoose(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(1.45);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.moveFwd(6);

		Robot.intake_system.Intake.set(-.5);

		Timer.delay(1);

		Robot.intake_system.Intake.set(0);

		Robot.drive_system.moveBwd(2);

		Robot.lift_system.Lift.set(.3);

		Timer.delay(1.3);

		Robot.drive_system.turnLeft(90);

		Robot.drive_system.moveFwd(1);

		Robot.intake_system.Intake.set(.5);

		Robot.drive_system.moveBwd(1);

		Robot.drive_system.turnRight(0);

		Robot.lift_system.Switch();

		Robot.drive_system.moveFwd(2);

		Robot.intake_system.autoEject();

		Timer.delay(.1);

		Robot.drive_system.allDrive.set(0);

		Robot.intake_system.Intake.set(0);

		Robot.lift_system.Lift.set(0);
	}

	public void LeftMiddleCube() {
		Robot.drive_system.allDrive.set(.4);

		Timer.delay(.7);

		Robot.drive_system.allDrive.set(-0.3);

		Timer.delay(0.2);

		Robot.drive_system.turnLeftLoose(55);

		Robot.drive_system.moveFwd(15);

		Robot.drive_system.turnRightLoose(0);

		Robot.lift_system.Lift.set(-.3);

		Timer.delay(1.45);

		Robot.lift_system.Lift.set(0);

		Robot.drive_system.moveFwd(6);

		Robot.intake_system.Intake.set(-.5);

		Timer.delay(1);

		Robot.intake_system.Intake.set(0);
	}

	// LeftSwtichLL() works
	public void LeftSwitchLL() {

		LeftSwitch();

	}

	// LeftSwitchRR() has NOT been tested. Try this whenever possible.
	public void LeftSwitchRR() {

		Forward();

	}

	// LeftSwitchLR() has NOT been tested. Try this whenever possible.
	public void LeftSwitchLR() {
		LeftSwitch();
	}

	// LeftSwitchRL() has NOT been tested. Try this whenever possible.
	public void LeftSwitchRL() {
		LeftScale();
	}

	// LeftScaleLL() works
	public void LeftScaleLL() {
		LeftScale();
	}

	// LeftScaleRR() has NOT been tested. Try this whenever possible.
	public void LeftScaleRR() {
		Forward();
	}

	// LeftScaleLR() has NOT been tested. Try this whenever possible.
	public void LeftScaleLR() {
		LeftSwitch();
	}

	// LeftScaleRL() has NOT been tested. Try this whenever possible.
	public void LeftScaleRL() {
		LeftScale();
	}

	// MiddleSwitchLL() has NOT been tested. Try this whenever possible.
	public void MiddleSwitchLL() {

		LeftMiddleCube();

	}

	// MiddleSwitchRR() has NOT been tested. Try this whenever possible.
	public void MiddleSwitchRR() {

		RightMiddleCube();

	}

	// MiddleSwitchLR() has NOT been tested. Try this whenever possible.
	public void MiddleSwitchLR() {
		LeftMiddleCube();
	}

	// MiddleSwitchRL() has NOT been tested. Try this whenever possible.
	public void MiddleSwitchRL() {
		RightMiddleCube();
	}

	// MiddleScaleLL() has NOT been tested. Try this whenever possible.
	public void MiddleScaleLL() {
		LeftMiddleCube();
	}

	// MiddleScaleRR() has NOT been tested. Try this whenever possible.
	public void MiddleScaleRR() {
		RightMiddleCube();
	}

	// MiddleScaleLR() has NOT been tested. Try this whenever possible.
	public void MiddleScaleLR() {
		LeftMiddleCube();
	}

	// MiddleScaleRL() has NOT been tested. Try this whenever possible.
	public void MiddleScaleRL() {
		RightMiddleCube();
	}

	// RightSwitchLL() has NOT been tested. Try this whenever possible.
	public void RightSwitchLL() {
		Forward();
	}

	// RightSwitchRR() works.
	public void RightSwitchRR() {

		RightSwitch();

	}

	// RightSwitchLR() has NOT been tested. Try this whenever possible.
	public void RightSwitchLR() {
		RightScale();
	}

	public void RightSwitchRL() {
		RightSwitch();
	}

	// RightScaleLL() has NOT been tested. Try this whenever possible.
	public void RightScaleLL() {

		Forward();

	}

	// RightScaleRR() has NOT been tested. Try this whenever possible.
	public void RightScaleRR() {

		RightScale();

	}

	// RightScaleLR() has NOT been tested. Try this whenever possible.
	public void RightScaleLR() {
		RightScale();
	}

	// RightScaleRL() has NOT been tested. Try this whenever possible.
	public void RightScaleRL() {
		RightSwitch();
	}

}