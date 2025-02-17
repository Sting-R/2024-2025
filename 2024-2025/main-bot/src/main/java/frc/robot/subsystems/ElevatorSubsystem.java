package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;

import java.util.function.BooleanSupplier;

//import com.ctre.phoenix.Util;
import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.Utility;
import frc.robot.Constants.ElevatorConstants;

public class ElevatorSubsystem extends SubsystemBase {// makes elevator subsystem into an actual subsystem

    // Motors
    private final TalonFX m_elevatorLeftMotor;
    private final TalonFX m_elevatorRightMotor;
    // Limit Switches
    private DigitalInput m_elevatorTopLimitSwitch;
    private DigitalInput m_elevatorBottomLimitSwitch;
    // Encoder
    private final Encoder m_Encoder1;
    // private TitanQuadEncoder m_elevatorEncoder;
    private double m_elevatorEncoder; // Placeholder
    private ElevatorPresets currentElevatorState;
    // Constants
    private double motorElevatorSpeed;

    private ElevatorPresets currentElevatorState;
    

    public ElevatorSubsystem(int elevatorLeftMotorId, int elevatorRightMotorId) {// this is da place where stuff is
                                                                                 // actually initialized
        m_elevatorEncoder = 0; // Placeholder
        m_elevatorLeftMotor = new TalonFX(elevatorLeftMotorId);
        m_elevatorRightMotor = new TalonFX(elevatorRightMotorId);
        m_elevatorTopLimitSwitch = new DigitalInput(1);
        m_elevatorBottomLimitSwitch = new DigitalInput(2);
        m_Encoder1 = new Encoder(0, 1);
        currentElevatorState = ElevatorPresets.Lowest;
    }

    public enum ElevatorPresets {
        Lowest, // bottom
        Level1, // Levels of the reef
        Level2, Level3, Level4, Highest // top
    }

    public Command raiseElevator() {
        // Placeholder
        return runOnce(() -> {
            /* one-time action goes here */
            setMotorElevatorSpeed(0.1);
        });
    }

    public Command lowerElevator() {
        // Placeholder
        return runOnce(() -> {
            /* one-time action goes here */
            setMotorElevatorSpeed(-0.1);
        });
    }

    public void debuggingMethod() {
        SmartDashboard.putNumber("Elevator Encoder", m_Encoder1.get());
        SmartDashboard.putBoolean("Elevator Top Limit Switch", m_elevatorTopLimitSwitch.get());
        SmartDashboard.putBoolean("Elevator Bottom Limit Switch", m_elevatorBottomLimitSwitch.get());
    }

    public boolean isElevatorAtTop() {
        // Placeholder
        if (m_Encoder1.get() < ElevatorConstants.kElevatorEncoderTopValue && !m_elevatorTopLimitSwitch.get()) {
            return false;
        } else if (m_Encoder1.get() < ElevatorConstants.kElevatorEncoderTopValue && m_elevatorTopLimitSwitch.get()) {
            System.out.println("Error: Encoder And Top Limit Switch Mismatched");
            return true;
        } else {
            return true;
        }
    }

    public boolean isElevatorAtBottom() {
        // Placeholder
        if (m_Encoder1.get() > ElevatorConstants.kElevatorEncoderBottomValue && !m_elevatorBottomLimitSwitch.get()) {
            return false;
        } else if (m_Encoder1.get() > ElevatorConstants.kElevatorEncoderBottomValue
                && m_elevatorBottomLimitSwitch.get()) {
            System.out.println("Error: Encoder And Bottom Limit Switch Mismatched");
            return true;
        } else {
            return true;
        }
    }

    public void setMotorElevatorSpeed(double voltage) {
        // Placeholder

        // BEN: If this method truly will be run once (as above), then the stop
        // conditions will only be evaluated once.
        // We need to check the conditions continually somehow.

        debuggingMethod();
        if (voltage > 0 && !isElevatorAtTop()) {// if motor is moving and elevators at the top, make both move up
            m_elevatorLeftMotor.set(voltage);
            m_elevatorRightMotor.set(-voltage);
            Utility.printLn("Elevator is moving up");
        } else if (voltage < 0 && !isElevatorAtBottom()) {// or if its at bottom make move down
            m_elevatorLeftMotor.set(voltage);
            m_elevatorRightMotor.set(-voltage);
            Utility.printLn("Elevator is moving down");
        } else {// otherwise just no speed
            m_elevatorLeftMotor.set(0);
            m_elevatorRightMotor.set(0);
            Utility.printLn("Elevator is at the top or bottom and therefore the motors have been stopped");
        }

    }

    public void moveElevatorToPreset(ElevatorPresets desiredPreset) {// tells elevator where to go based on certain
                                                                     // presets using distgusting switch case statements
        switch (desiredPreset) {
        case Level1:
            moveElevatorToPosition(ElevatorConstants.ElevatorPreset.level1EncoderValue, desiredPreset);
            break;
        case Level2:
            moveElevatorToPosition(ElevatorConstants.ElevatorPreset.level2EncoderValue, desiredPreset);
            break;
        case Level3:
            moveElevatorToPosition(ElevatorConstants.ElevatorPreset.level3EncoderValue, desiredPreset);
            break;
        case Level4:
            moveElevatorToPosition(ElevatorConstants.ElevatorPreset.level4EncoderValue, desiredPreset);
            break;
        }
    }

    /**
     * The moveElevatorToPosition method takes a double as a parameter. This value
     * represents the number of rotations necessary to reach the target position.
     * fun fact: 6!! is 6 * 4 * 2 while 7!! is 7 * 5 * 3 * 1. Kind of like a
     * selective factorial. Yeah idk why I said that. Moving on
     * 
     * @param position
     */
    public void moveElevatorToPosition(double position, ElevatorPresets desiredPreset) {// moves elevator to a certain
                                                                                        // position based on the encoder
                                                                                        // value
        // WILL HAVE TO REVERSE ONE MOTOR DEPENDING ON ORIENTATION!!!!
        debuggingMethod();
        if (m_Encoder1.get() <= (position + 5) && !isElevatorAtBottom()) { // checking to see if the motor wants to move
                                                                           // down and makes sure the elevator isn't at
                                                                           // the bottom
            m_elevatorLeftMotor.set(-motorElevatorSpeed);
            m_elevatorRightMotor.set(motorElevatorSpeed);
            Utility.printLn("Elevator is moving down");
        } else if (m_Encoder1.get() >= (position - 5) && !isElevatorAtTop()) { // checking to see if the motor wants to
                                                                               // move up and makes sure the elevator
                                                                               // isn't at the top
            m_elevatorLeftMotor.set(motorElevatorSpeed);
            m_elevatorRightMotor.set(-motorElevatorSpeed);
            Utility.printLn("Elevator is moving up");1
        } else {
            m_elevatorLeftMotor.set(0);
            m_elevatorRightMotor.set(0);
            Utility.printLn(
                    "Elevator is at the top, bottom, desired position, or error occurred and therefore the motors have been stopped");
            currentElevatorState = desiredPreset;
        }
    }

    public ElevatorPresets getElevatorState() {
        return currentElevatorState;
    }

    public BooleanSupplier isElevatorAtDesiredState(ElevatorPresets desiredState) {
        return () -> currentElevatorState == desiredState;
    }

    public Command commandVoltage(double voltage) {// just exists because just felt like it
        return runOnce(() -> {
            setMotorElevatorSpeed(voltage);
        });
    }

    public Command commandMoveToPreset(ElevatorPresets desiredPreset) {// The first function in a very tall dependancy
                                                                       // tree
        return runOnce(() -> {
            moveElevatorToPreset(desiredPreset);
        });
    }

}
// a comment because why not :)
// This is a very cool comment *wink* *wink* *nudge* *nudge*
