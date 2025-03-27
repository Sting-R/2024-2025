package frc.robot.subsystems;

import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimulationSubsystem extends SubsystemBase {

    private final Field2d m_field;

    Trajectory m_trajectory;
    SwerveModuleState[] states = new SwerveModuleState[] { new SwerveModuleState(), new SwerveModuleState(),
            new SwerveModuleState(), new SwerveModuleState() };

    StructArrayPublisher<SwerveModuleState> publisher = NetworkTableInstance.getDefault()
            .getStructArrayTopic("MyStates", SwerveModuleState.struct).publish();

    public SimulationSubsystem() {

        // Create the trajectory to follow in autonomous. It is best to initialize
        // trajectories here to avoid wasting time in autonomous.
        m_trajectory = TrajectoryGenerator.generateTrajectory(new Pose2d(0, 0, Rotation2d.fromDegrees(0)),
                List.of(new Translation2d(1, 1), new Translation2d(2, -1)), new Pose2d(3, 0, Rotation2d.fromDegrees(0)),
                new TrajectoryConfig(Units.feetToMeters(3.0), Units.feetToMeters(3.0)));
        // Create and push Field2d to SmartDashboard.
        m_field = new Field2d();
        SmartDashboard.putData(m_field);
        // Push the trajectory to Field2d.
        m_field.getObject("traj").setTrajectory(m_trajectory);

        // Do this in either robot or subsystem init
        SmartDashboard.putData("Field", m_field);

    }

    public void SimulationOutput(CommandSwerveDrivetrain drivetrainSubsystem) {

        // Do this in either robot periodic or subsystem periodic
        m_field.setRobotPose(drivetrainSubsystem.getEstimatedPose());
        // states[0].speedMetersPerSecond =
        // publisher.set(drivetrainSubsystem.getModules());
    }

}
