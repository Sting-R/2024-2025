package frc.robot.archived_classes;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;
import org.ironmaple.simulation.seasonspecific.crescendo2024.CrescendoNoteOnField;

import com.ctre.phoenix6.swerve.SwerveDrivetrain;
import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.DriveTrainConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class MapleSimSubsystem extends SubsystemBase {

    private DriveTrainSimulationConfig driveTrainSimulationConfig;

    public MapleSimSubsystem(CommandSwerveDrivetrain drivetrain) {
        // Placeholder

        // Obtains the default instance of the simulation world, which is a Crescendo
        // Arena.
        // SimulatedArena.getInstance();
        // Overrides the default simulation (isn't supported right now)
        // SimulatedArena.overrideInstance(SimulatedArena newInstance);

        // Create and configure a drivetrain simulation configuration
        driveTrainSimulationConfig = DriveTrainSimulationConfig.Default()
                // Specify gyro type (for realistic gyro drifting and error simulation)
                .withGyro(COTS.ofPigeon2())
                // Specify swerve module (for realistic swerve dynamics)
                .withSwerveModule(COTS.ofSwerveXFlipped(DCMotor.getKrakenX60(1), // Drive motor is a Kraken X60
                        DCMotor.getKrakenX60(1), // Steer motor is a Kraken
                        COTS.WHEELS.DEFAULT_NEOPRENE_TREAD.cof, // Use the COF for Neoprene Wheels
                        9)) // L3 Gear ratio
                // Configures the track length and track width (spacing between swerve modules)
                .withTrackLengthTrackWidth(Inches.of(DriveTrainConstants.lengthBetweenSwerveModules),
                        Inches.of(DriveTrainConstants.lengthBetweenSwerveModules))
                // Configures the bumper size (dimensions of the robot bumper)
                .withBumperSize(Inches.of(DriveTrainConstants.lengthOfBumpers),
                        Inches.of(DriveTrainConstants.lengthOfBumpers));
    }

    public void addGamePiece() {
        SimulatedArena.getInstance().addGamePiece(new CrescendoNoteOnField(new Translation2d(3, 3)));
    }

    public void clearGamePieces() {
        SimulatedArena.getInstance().clearGamePieces();
    }

    // i guess ill add a debugging method here
    public void debugOrSomething() {
        SmartDashboard.putString("Yeah this is difficult. Have track length X: ",
                driveTrainSimulationConfig.trackLengthX().toString());
        SmartDashboard.putString("Here track width y i guess", driveTrainSimulationConfig.trackWidthY().toString());
        // To many things to put all things from this class.
    }
}