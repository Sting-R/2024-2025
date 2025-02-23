package frc.robot.subsystems;

//"A good programmers code needs not any comments, for it comments on itself" - IDK who I made it up
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CoralArmConstants;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Encoder;

public class CoralArmSubsystem extends SubsystemBase {
    // Motor(s)
    private final TalonSRX m_CoralArmMotor;
    // Encoder
    private final Encoder m_coralEncoder;
    // Current state
    private CoralArmLevels currentCoralArmState;

    public CoralArmSubsystem() {
        // Initialized Stuff
        m_coralEncoder = new Encoder(CoralArmConstants.kCoralEncoderID1, CoralArmConstants.kCoralEncoderID2);
        m_CoralArmMotor = new TalonSRX(CoralArmConstants.coralArmMotorID);
        currentCoralArmState = CoralArmLevels.Down;
    }

    public enum CoralArmLevels {
        Down, Up, Stop
    }

    public Command CommandsetCoralArmVoltage(double voltage, CoralArmLevels desiredState) {
        return runOnce(() -> {
            if (voltage > 0) {
                if (m_coralEncoder.get() < CoralArmConstants.kCoralEncoderTopValue) {
                    setCoralArmVoltage(voltage);
                } else if (m_coralEncoder.get() > CoralArmConstants.kCoralEncoderTopBuffer) {
                    System.out.println("How is the arm not broken yet? Amazingly, you didn't break it.");
                    setCoralArmVoltage(0);
                } else {
                    setCoralArmVoltage(0);
                    currentCoralArmState = desiredState;
                }
            } else if (voltage < 0) {
                if (m_coralEncoder.get() > CoralArmConstants.kCoralEncoderBottomValue) {
                    setCoralArmVoltage(voltage);
                } else if (m_coralEncoder.get() < CoralArmConstants.kCoralEncoderBottomBuffer) {
                    System.out.println("How is the arm not broken yet? Amazingly, you didn't break it.");
                    setCoralArmVoltage(0);
                } else {
                    setCoralArmVoltage(0);
                    currentCoralArmState = desiredState;
                }
            } else {
                setCoralArmVoltage(0);
                currentCoralArmState = desiredState;
            }
        });
    }

    public void setCoralArmVoltage(double voltage) {
        m_CoralArmMotor.set(ControlMode.PercentOutput, voltage);
        // Add more motors here if neccessary
    }

    public CoralArmLevels getCurrentCoralArmState() {
        return currentCoralArmState;
    }

    public BooleanSupplier isCoralArmAtDesiredState(CoralArmLevels desiredState) {
        return () -> currentCoralArmState == desiredState;
    }

    public Command emergencyStop() {// Just in case the driver wants to stop the arm without stopping the whole
                                    // program (and for some reason the Command Voltage function doesn't stop the
                                    // arm after it gets above a certain point).
        return runOnce(() -> {
            setCoralArmVoltage(0);
            System.out.println("Emergency Stop Pressed! Stay Still And Don't Move The Arm...");
            if (m_coralEncoder.get() > CoralArmConstants.kCoralEncoderTopValue) {
                do {// Hey look at that, a do while loop has a use!
                    setCoralArmVoltage(-0.25);
                } while (m_coralEncoder.get() > CoralArmConstants.kCoralEncoderTopValue);
                setCoralArmVoltage(0);
            } else if (m_coralEncoder.get() < CoralArmConstants.kCoralEncoderTopValue) {
                do {
                    setCoralArmVoltage(0.25);
                } while (m_coralEncoder.get() < CoralArmConstants.kCoralEncoderTopValue);
                setCoralArmVoltage(0);
            } else {
                System.out.println("Arm is already at top");
            }
            System.out.println("Arm Is Now Reset. Carry On.");
        });
    }
}
