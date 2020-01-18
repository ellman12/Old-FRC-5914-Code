package org.usfirst.frc.team5914.robot;

public class DistanceDetermination {

	int i;
	double distance[];
	int count[];

	DistanceDetermination(int n) {
		distance = new double[n];
		count = new int[n];

		for (i = 0; i < n; i++) {
			distance[i] = i * (1.0);

			count[i] = i * 50;
		}
	}

	int computeCount(double travel) {

		int index;
		double fraction;
		double dcount;

		index = (int) travel; // travel is measured in feet
								// table is spaced in increments of one foot
		fraction = (travel - distance[index]);

		dcount = (fraction * ((double) count[index + 1] - (double) count[index])) + count[index];

		return (int) dcount;
	}

}
