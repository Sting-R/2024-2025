
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.drive_commands;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.LimeLightSubsystem;

public class TurnToAngle extends Command {

	public double getRotation(int tagID) {
		SmartDashboard.putNumber("Tagid", tagID);
		System.out.println("TAG ID: " + tagID);
		switch (tagID) {
		// BLUE ALLIANCE ONES - GYRO MUST BE ZEROD CORRECTLY
		case 18:
			return 0.0 + 120;
		case 19:
			return 300 + 120;
		case 20:
			return 240 + 120;
		case 21:
			return 180 + 120;
		case 22:
			return 120 + 120;
		case 17:
			return 60 + 120;
		// RED ALLIANCE ONES
		case 7:
			return 0.0;
		case 8:
			return 300;
		case 9:
			return 240;
		case 10:
			return 180;
		case 11:
			return 120;
		case 6:
			return 60;
		default:
			return 0.0;
		}
	}

	/** Creates a new TurnToAngle. */
	private CommandSwerveDrivetrain m_drivetrain;
	private PIDController thetaController = new PIDController(1, 0, 0.1);
	private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
	private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second
																						// max angular velocity
	private LimeLightSubsystem limelightSubsystem;
	private double m_angle;

	private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric().withDeadband(MaxSpeed * .05)
			.withRotationalDeadband(MaxAngularRate * .05) // Add a 5% deadband
			.withRotationalDeadband(0); // I want field-centric
										// driving in open loop
										// voltage mode
	private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
	// private final SwerveRequest.PointWheelsAt point = new
	// SwerveRequest.PointWheelsAt();

	/**
	 * Constructor method for the GamepadDrive class - Creates a new GamepadDrive
	 * object.
	 */
	public TurnToAngle(CommandSwerveDrivetrain drivetrain, LimeLightSubsystem limelightSubsystem) {
		super();
		addRequirements(drivetrain);
		thetaController.enableContinuousInput(-Math.PI, Math.PI);
		m_drivetrain = drivetrain;
		m_angle = getRotation(limelightSubsystem.getTargetAprilTagID());
		this.limelightSubsystem = limelightSubsystem;
	}

	@Override
	public void execute() {

		m_angle = getRotation(limelightSubsystem.getTargetAprilTagID());
		double thetaOutput = 0;
		double horizontal_angle = m_angle;
		double setpoint = Math.toRadians(horizontal_angle);

		thetaController.setSetpoint(setpoint);
		// if (!thetaController.atSetpoint() ){
		thetaOutput = thetaController.calculate(m_drivetrain.getEstimatedPose().getRotation().getRadians(), setpoint);
		// }

		m_drivetrain.setControl(drive.withVelocityX(0).withVelocityY(0).withRotationalRate(-thetaOutput));

	}

	@Override
	public void end(boolean interrupted) {
		m_drivetrain.applyRequest(() -> brake);
	}

}
