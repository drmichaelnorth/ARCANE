package gov.anl.cue.arcane.engine.matrix;

import static java.lang.Math.*;

public class MatrixFormulationConcrete extends MatrixFormulationAbstract {

	double a_Converter1 = 0.0;
	double a_Converter1_combined = 0.0;
	double a_Converter2 = 12.4;
	double a_Converter2_combined = 12.4;
	double a_Sink1 = 0.0;
	double a_Sink1_combined = 0.0;
	double a_Source1 = 0.0;
	double a_Source1_combined = 0.0;
	double a_Source2 = 0.0;
	double a_Source2_combined = 0.0;
	double b_Converter1 = 0.0;
	double b_Converter1_combined = 0.0;
	double b_Converter2 = 29.3;
	double b_Converter2_combined = 29.3;
	double b_Sink1 = 0.0;
	double b_Sink1_combined = 0.0;
	double b_Source1 = 0.0;
	double b_Source1_combined = 0.0;
	double b_Source2 = 0.0;
	double b_Source2_combined = 0.0;
	double c_Converter1 = 0.0;
	double c_Converter1_combined = 0.0;
	double c_Converter2 = 0.0;
	double c_Converter2_combined = 0.0;
	double c_Sink1 = 0.0;
	double c_Sink1_combined = 0.0;
	double c_Source1 = 39.8;
	double c_Source1_combined = 39.8;
	double c_Source2 = 0.0;
	double c_Source2_combined = 0.0;

	public MatrixFormulationConcrete(double newStepSize) {
		super(newStepSize);
	}

	@Override
	public void knit() {

		a_Converter1_combined = a_Converter1 + b_Converter1;
		a_Converter2_combined  += ((a_Converter2 + b_Converter2) * this.stepSize);
		a_Sink1_combined = a_Sink1 + b_Sink1 + c_Sink1;
		a_Source1_combined = 10.4;
		a_Source2_combined = 11.6;
		b_Converter1_combined = 9.3;
		b_Converter2_combined  += ((a_Converter2 + b_Converter2) * this.stepSize);
		b_Sink1_combined = a_Sink1 + b_Sink1 + c_Sink1;
		b_Source1_combined = c_Source1;
		b_Source2_combined = sin(b_Source2);
		c_Converter1_combined = a_Converter1 + b_Converter1;
		c_Converter2_combined = 4.5;
		c_Sink1_combined = a_Sink1 + c_Sink1;
		c_Source1_combined  += ((b_Source1 + c_Source1) * this.stepSize);
		c_Source2_combined = c_Source2;

	}

	@Override
	public void split() {

		a_Converter1 = 0.5 * a_Converter1_combined + 0.25 * a_Converter2_combined + 1.0 * a_Source1_combined + 1.0 * a_Source2_combined;
		a_Converter2 = 0.5 * a_Converter1_combined + 0.5 * a_Sink1_combined;
		a_Sink1 = 0.25 * a_Converter2_combined;
		a_Source1 = 0.25 * a_Converter2_combined + 0.35 * a_Sink1_combined;
		a_Source2 = 0.25 * a_Converter2_combined + 0.15 * a_Sink1_combined;
		b_Converter1 = 0.5 * b_Converter1_combined + 0.25 * b_Converter2_combined + 1.0 * b_Source1_combined + 0.5 * b_Sink1_combined;
		b_Converter2 = 0.5 * b_Converter1_combined + 0.5 * b_Source2_combined;
		b_Sink1 = 0.25 * b_Converter2_combined;
		b_Source1 = 0.25 * b_Converter2_combined + 0.5 * b_Sink1_combined;
		b_Source2 = 0.25 * b_Converter2_combined + 0.5 * b_Source2_combined;
		c_Converter2 = 0.5 * c_Source1_combined + 0.25 * c_Source2_combined + 0.5 * c_Sink1_combined;
		c_Sink1 = 0.5 * c_Source1_combined + 0.25 * c_Source2_combined + 0.5 * c_Sink1_combined;
		c_Source1 = 0.5 * c_Converter2_combined + 0.25 * c_Source2_combined;
		c_Source2 = 0.5 * c_Converter2_combined + 0.25 * c_Source2_combined;

	}

	public Double calculateFitnessValue() {

		return (a_Converter1 + b_Converter1) + (a_Converter2 + b_Converter2 + c_Converter2) + (sin(a_Source1)) + (b_Source2) + (c_Sink1);

	}

}
