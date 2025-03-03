package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Logging;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.Constants.ElevatorConstants;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VoltageOut;
//import com.ctre.phoenix.Util;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.robot.Utility;

public class ElevatorSubsystem extends SubsystemBase {// makes elevator subsystem into an actual subsystem
    private final Logging elevatorLogger;
    // Motors
    private final TalonFX m_elevatorLeftMotor;
    private final TalonFX m_elevatorRightMotor;
    // Limit Switches
    private DigitalInput m_elevatorTopLimitSwitch;
    private DigitalInput m_elevatorBottomLimitSwitch;
    // Encoder
    private final Encoder m_leftElevatorEncoder;
    // private final Encoder m_rightElevatorEncoder;
    private ElevatorPresets currentElevatorState;
    // Constants
    private double motorMaxElevatorSpeed; // Might take out in favor of motion magic
    private final double leftElevatorEncoderTopValue;
    private final double leftElevatorEncoderBottomValue;
    private final double rightElevatorEncoderTopValue;
    private final double rightElevatorEncoderBottomValue;
    private final double preset1EncoderValue;
    private final double preset2EncoderValue;
    private final double preset3EncoderValue;
    private final double preset4EncoderValue;

    // Test values for motion magic
    private MotionMagicVoltage setVoltage;
    // private StateManager stateManager;
    public static final double MOTION_MAGIC_ACCELERATION = 80;// 120
    public static final double MOTION_MAGIC_VELOCITY = 100;// 170
    private VoltageOut voltageOut = new VoltageOut(0.0);

    // private final SysIdRoutine m_sysIdRoutine = new SysIdRoutine(new
    // SysIdRoutine.Config(null, // Use default ramp rate
    // // (1 V/s)
    // Volts.of(1), // Reduce dynamic step voltage to 4 to prevent brownout
    // null, // Use default timeout (10 s)
    // // Log state with Phoenix SignalLogger class
    // (state) -> SignalLogger.writeString("state", state.toString())),
    // new SysIdRoutine.Mechanism(
    // (volts) ->
    // m_elevatorLeftMotor.setControl(voltageOut.withOutput(volts.in(Volts))), null,
    // this));

    public ElevatorSubsystem() {// constructor for the elevator subsystem
        // Motor Initialization
        m_elevatorLeftMotor = new TalonFX(ElevatorConstants.kElevatorLeftMotorID);
        m_elevatorRightMotor = new TalonFX(ElevatorConstants.kElevatorRightMotorID);
        // Limit Switch Initialization
        m_elevatorTopLimitSwitch = new DigitalInput(1);
        m_elevatorBottomLimitSwitch = new DigitalInput(0);
        // Encoder Initialization
        m_leftElevatorEncoder = new Encoder(ElevatorConstants.kLeftElevatorEncoderID1,
                ElevatorConstants.kLeftElevatorEncoderID2);
        // m_rightElevatorEncoder = new
        // Encoder(ElevatorConstants.kRightElevatorEncoderID1,
        // ElevatorConstants.kRightElevatorEncoderID2);
        // Current State Initialization
        currentElevatorState = ElevatorPresets.Level2;
        elevatorLogger = new Logging("ElevatorSubsystem");
        // Constants Initialization
        motorMaxElevatorSpeed = ElevatorConstants.kMaxMotorElevatorSpeed;
        leftElevatorEncoderTopValue = ElevatorConstants.kLeftElevatorEncoderTopValue;
        leftElevatorEncoderBottomValue = ElevatorConstants.kLeftElevatorEncoderBottomValue;
        rightElevatorEncoderTopValue = ElevatorConstants.kRightElevatorEncoderTopValue;
        rightElevatorEncoderBottomValue = ElevatorConstants.kRightElevatorEncoderBottomValue;
        preset1EncoderValue = ElevatorConstants.ElevatorPreset.level1EncoderValue;
        preset2EncoderValue = ElevatorConstants.ElevatorPreset.level2EncoderValue;
        preset3EncoderValue = ElevatorConstants.ElevatorPreset.level3EncoderValue;
        preset4EncoderValue = ElevatorConstants.ElevatorPreset.level4EncoderValue;

        SetUpElevatorMotors();

        setVoltage = new MotionMagicVoltage(0).withSlot(0);
        setVoltage.UpdateFreqHz = 1000;
    }

    public enum ElevatorPresets { // different levels for the coral reef
        Level1, Level2, Level3, Level4
    }

    private void SetUpElevatorMotors() {
        // This function is courtesy of the Team 1764 codebase
        TalonFXConfiguration leftConfig = new TalonFXConfiguration();

        TalonFXConfiguration rightConfig = new TalonFXConfiguration();

        leftConfig.Slot0.kP = 10; // p pid //4.1
        leftConfig.Slot0.kD = 0;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
        leftConfig.Slot0.kV = 0;
        leftConfig.Slot0.kA = 0;
        leftConfig.Slot0.kG = 0.65;

        leftConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        leftConfig.MotorOutput.PeakForwardDutyCycle = motorMaxElevatorSpeed;
        leftConfig.MotorOutput.PeakReverseDutyCycle = -0.05; // can bump up to 12 or something
        leftConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        leftConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        leftConfig.CurrentLimits.StatorCurrentLimit = 60;

        rightConfig.Slot0.kP = 10; // p pid //4.1
        rightConfig.Slot0.kD = 0;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
        rightConfig.Slot0.kV = 0;
        rightConfig.Slot0.kA = 0;
        rightConfig.Slot0.kG = 0.65;

        rightConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        rightConfig.MotorOutput.PeakForwardDutyCycle = motorMaxElevatorSpeed;
        rightConfig.MotorOutput.PeakReverseDutyCycle = -0.05; // can bump up to 12 or something
        rightConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive; // TODO: FIND IF TRUE OR NOT BEFORE
                                                                             // U
        // FRY ROBOT
        rightConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        rightConfig.CurrentLimits.StatorCurrentLimit = 60;

        MotionMagicConfigs motionMagicConfigs = new MotionMagicConfigs();
        motionMagicConfigs.withMotionMagicAcceleration(MOTION_MAGIC_ACCELERATION)
                .withMotionMagicCruiseVelocity(MOTION_MAGIC_VELOCITY);

        m_elevatorLeftMotor.getConfigurator().apply(leftConfig);
        m_elevatorRightMotor.getConfigurator().apply(rightConfig);
        m_elevatorLeftMotor.getConfigurator().apply(motionMagicConfigs);
        m_elevatorRightMotor.getConfigurator().apply(motionMagicConfigs);

        m_elevatorRightMotor.setControl(new StrictFollower(m_elevatorLeftMotor.getDeviceID()));

    }

    public void elevatorVoltageMM(double desiredEncoderValue) {
        // TOOD: NEED FEEDFORWARD

        @SuppressWarnings("unused")

        StatusCode val = m_elevatorLeftMotor.setControl(setVoltage.withPosition(desiredEncoderValue).withSlot(0)); // .withLimitReverseMotion(true));
        SmartDashboard.putNumber("Desired elevator position", desiredEncoderValue);
        // SmartDashboard.putNumber("Motor Voltage",
        // setVoltage.withPosition(desiredEncoderValue).withSlot(0).getFeedForwardMeasure());
        // @SuppressWarnings("unused")
        // StatusCode val5 =
        // m_elevatorRightMotor.setControl(setVoltage.withPosition(desiredEncoderValue).withSlot(0));//
        // .withLimitReverseMotion(true));
        // SmartDashboard.putNumber("Left Elevator Encoder Bottom Value",
        // m_leftElevatorEncoder.get());
        // SmartDashboard.putNumber("Right Elevator Encoder Bottom Value",
        // m_rightElevatorEncoder.get());
        // SmartDashboard.putNumber("Desired Encoder Value", desiredEncoderValue);

    }

    public void elevatorMaintainPositionMM() {
        // TOOD: NEED FEEDFORWARD

        @SuppressWarnings("unused")
        StatusCode val = m_elevatorLeftMotor
                .setControl(setVoltage.withPosition(m_elevatorLeftMotor.getPosition().getValueAsDouble()).withSlot(0)); // .withLimitReverseMotion(true));
        SmartDashboard.putNumber("Desired elevator position", m_elevatorLeftMotor.getPosition().getValueAsDouble());
        // @SuppressWarnings("unused")
        // StatusCode val5 =
        // m_elevatorRightMotor.setControl(setVoltage.withPosition(desiredEncoderValue).withSlot(0));//
        // .withLimitReverseMotion(true));

    }

    public void elevatorMoveToPresetMM(ElevatorPresets desiredPreset) {
        // TOOD: NEED FEEDFORWARD
        // SmartDashboard.putNumber("Left Elevator Encoder Bottom Value",
        // m_leftElevatorEncoder.get());
        // SmartDashboard.putNumber("Right Elevator Encoder Bottom Value",
        // m_rightElevatorEncoder.get());
        switch (desiredPreset) {
        case Level1:
            SmartDashboard.putString("DesiredPreset", "Level1");
            SmartDashboard.putNumber("Desired elevator position", preset1EncoderValue);
            @SuppressWarnings("unused")
            StatusCode val = m_elevatorLeftMotor.setControl(setVoltage.withPosition(preset1EncoderValue).withSlot(0));// .withLimitReverseMotion(true));
            break;
        case Level2:
            SmartDashboard.putString("DesiredPreset", "Level2");
            SmartDashboard.putNumber("Desired elevator position", preset2EncoderValue);
            @SuppressWarnings("unused")
            StatusCode val2 = m_elevatorLeftMotor.setControl(setVoltage.withPosition(preset2EncoderValue).withSlot(0));// .withLimitReverseMotion(true));
            break;
        case Level3:
            SmartDashboard.putString("DesiredPreset", "Level3");
            SmartDashboard.putNumber("Desired elevator position", preset3EncoderValue);
            @SuppressWarnings("unused")
            StatusCode val3 = m_elevatorLeftMotor.setControl(setVoltage.withPosition(preset3EncoderValue).withSlot(0));// .withLimitReverseMotion(true));
            break;
        case Level4:
            SmartDashboard.putString("DesiredPreset", "Level4");
            SmartDashboard.putNumber("Desired elevator position", preset4EncoderValue);
            @SuppressWarnings("unused")
            StatusCode val4 = m_elevatorLeftMotor.setControl(setVoltage.withPosition(preset4EncoderValue).withSlot(0));// .withLimitReverseMotion(true));
            break;
        } // @SuppressWarnings("unused")
    }

    public void sysIdTest() {
        final SysIdRoutine m_sysIdRoutine = new SysIdRoutine(new SysIdRoutine.Config(null, // Use default ramp rate
                // (1 V/s)
                Volts.of(1), // Reduce dynamic step voltage to 4 to prevent brownout
                null, // Use default timeout (10 s)
                // Log state with Phoenix SignalLogger class
                (state) -> SignalLogger.writeString("state", state.toString())),
                new SysIdRoutine.Mechanism(
                        (volts) -> m_elevatorLeftMotor.setControl(voltageOut.withOutput(volts.in(Volts))), null, this));
    }

    public void debuggingMethod() {
        SmartDashboard.putNumber("Left Elevator Encoder", m_leftElevatorEncoder.get());
        // SmartDashboard.putNumber("Right Elevator Encoder",
        // m_rightElevatorEncoder.get());
        SmartDashboard.putBoolean("Elevator Top Limit Switch", m_elevatorTopLimitSwitch.get());
        SmartDashboard.putBoolean("Elevator Bottom Limit Switch", m_elevatorBottomLimitSwitch.get());
        SmartDashboard.putNumber("Left Motor Position", m_elevatorLeftMotor.getPosition().getValueAsDouble());
        SmartDashboard.putNumber("Right Motor Position", m_elevatorRightMotor.getPosition().getValueAsDouble());
    }

    public boolean isElevatorAtTop() {
        // Placeholder
        if ((m_elevatorLeftMotor.getPosition().getValueAsDouble() >= leftElevatorEncoderTopValue
                && m_elevatorRightMotor.getPosition().getValueAsDouble() >= rightElevatorEncoderTopValue)
                && !m_elevatorTopLimitSwitch.get()) {
            SmartDashboard.putBoolean("Elevator Thinks its at top", false);
            return false;
        } else if ((m_elevatorLeftMotor.getPosition().getValueAsDouble() >= leftElevatorEncoderTopValue
                && m_elevatorRightMotor.getPosition().getValueAsDouble() >= rightElevatorEncoderTopValue)
                || !m_elevatorTopLimitSwitch.get()) {
            // System.out.println("Error: Encoder And Top Limit Switch Mismatched");
            elevatorLogger.error("Encoder And Top Limit Switch Mismatched");
            SmartDashboard.putBoolean("Bottom Sensor and Encoder is mismatched", true);
            return true;
        } else {
            SmartDashboard.putBoolean("Elevator Thinks its at top", true);
            return true;

        }
    }

    public boolean isElevatorAtBottom() {
        // Placeholder
        if ((m_elevatorLeftMotor.getPosition().getValueAsDouble() <= leftElevatorEncoderBottomValue
                && m_elevatorRightMotor.getPosition().getValueAsDouble() <= rightElevatorEncoderBottomValue)
                && !m_elevatorBottomLimitSwitch.get()) {
            SmartDashboard.putBoolean("Elevator thinks it is at bottom", false);
            return false;
        } else if ((m_elevatorLeftMotor.getPosition().getValueAsDouble() <= leftElevatorEncoderBottomValue
                && m_elevatorRightMotor.getPosition().getValueAsDouble() <= rightElevatorEncoderBottomValue)
                || !m_elevatorBottomLimitSwitch.get()) {
            // System.out.println("Error: Encoder And Bottom Limit Switch Mismatched");
            SmartDashboard.putBoolean("Bottom Sensor and Encoder is mismatched", true);
            elevatorLogger.error("Encoder And Bottom Limit Switch Mismatched");
            return true;
        } else {

            SmartDashboard.putBoolean("Elevator thinks it is at bottom", true);
            return true;
        }
    }

    // public void setMotorElevatorSpeed(boolean isGoingUp) {
    // // Placeholder
    // debuggingMethod();
    // if (isGoingUp && !isElevatorAtTop()) {// if motor is moving and elevators at
    // the top, make both move up
    // m_elevatorLeftMotor.set(motorMaxElevatorSpeed);
    // m_elevatorRightMotor.set(-motorMaxElevatorSpeed);
    // Utility.printLn("Elevator is moving up");
    // } else if (!isGoingUp && !isElevatorAtBottom()) {// or if its at bottom make
    // move down
    // m_elevatorLeftMotor.set(-motorMaxElevatorSpeed);
    // m_elevatorRightMotor.set(-motorMaxElevatorSpeed);
    // Utility.printLn("Elevator is moving down");
    // } else {// otherwise just no speed
    // m_elevatorLeftMotor.set(0);
    // m_elevatorRightMotor.set(0);
    // System.out.println("Motor Voltage Value: " +
    // m_elevatorLeftMotor.getMotorVoltage()
    // + "\n desired Voltage Value: " + motorMaxElevatorSpeed + "\n is elevator
    // going up: " + isGoingUp
    // + "\n Elevator Encoder Value: " + m_leftElevatorEncoder.get()
    // + "\n Elevator Top Limit Switch Value: " + m_elevatorTopLimitSwitch.get()
    // + "\n Elevator Bottom Limit Switch Value: " +
    // m_elevatorBottomLimitSwitch.get());
    // System.out.println("Elevator is at the top, bottom, or desired position.
    // motors have been stopped");
    // }
    // }

    // public void moveElevatorToPreset(ElevatorPresets desiredPreset) {// tells
    // elevator where to go based on certain
    // // presets using distgusting switch case statements
    // switch (desiredPreset) {
    // case Level1:
    // SmartDashboard.putString("DesiredPreset", "Level1");
    // moveElevatorToSpecificPreset(preset1EncoderValue, desiredPreset);
    // break;
    // case Level2:
    // SmartDashboard.putString("DesiredPreset", "Level2");
    // moveElevatorToSpecificPreset(preset2EncoderValue, desiredPreset);
    // break;
    // case Level3:
    // SmartDashboard.putString("DesiredPreset", "Level3");
    // moveElevatorToSpecificPreset(preset3EncoderValue, desiredPreset);
    // break;
    // case Level4:
    // SmartDashboard.putString("DesiredPreset", "Level4");
    // moveElevatorToSpecificPreset(preset4EncoderValue, desiredPreset);
    // break;
    // }
    // }

    /**
     * The moveElevatorToPosition method takes a double as a parameter. This value
     * represents the number of rotations necessary to reach the target position.
     * 
     * fun fact: 6!! is 6 * 4 * 2 while 7!! is 7 * 5 * 3 * 1. Kind of like a
     * selective factorial. Yeah idk why I said that. Moving on
     * 
     * Oooooo thats a really fun fact! I didn't know you could have selective
     * factorials! - H
     * 
     * @param position
     * @param desiredPreset
     */
    // public void moveElevatorToSpecificPreset(double position, ElevatorPresets
    // desiredPreset) {// moves elevator to a
    // // certain
    // // position based on the encoder
    // // value
    // // WILL HAVE TO REVERSE ONE MOTOR DEPENDING ON ORIENTATION!!!!
    // debuggingMethod();
    // if (m_leftElevatorEncoder.get() <= (position + 5) && !isElevatorAtBottom()) {
    // // checking to see if the motor
    // // wants to move
    // // down and makes sure the elevator isn't at
    // // the bottom
    // m_elevatorLeftMotor.set(-motorMaxElevatorSpeed);
    // m_elevatorRightMotor.set(motorMaxElevatorSpeed);
    // Utility.printLn("Elevator is moving down");
    // } else if (m_leftElevatorEncoder.get() >= (position - 5) &&
    // !isElevatorAtTop()) { // checking to see if the
    // // motor wants to
    // // move up and makes sure the elevator
    // // isn't at the top
    // m_elevatorLeftMotor.set(motorMaxElevatorSpeed);
    // m_elevatorRightMotor.set(-motorMaxElevatorSpeed);
    // Utility.printLn("Elevator is moving up");
    // } else {
    // m_elevatorLeftMotor.set(0);
    // m_elevatorRightMotor.set(0);
    // Utility.printLn(
    // "Elevator is at the top, bottom, desired position, or error occurred and
    // therefore the motors have been stopped");
    // currentElevatorState = desiredPreset;
    // }
    // }

    public ElevatorPresets getElevatorState() {
        return currentElevatorState;
    }

    public BooleanSupplier isElevatorAtDesiredState(ElevatorPresets desiredState) {
        return () -> currentElevatorState == desiredState;
    }

    // public Command commandVoltage(double voltage) {// just exists because just
    // felt like it
    // return runOnce(() -> {
    // setMotorElevatorSpeed(voltage);
    // });
    // }

    // public Command commandMoveToPreset(ElevatorPresets desiredPreset) {// The
    // first function in a very tall dependancy
    // // tree
    // return runOnce(() -> {
    // moveElevatorToPreset(desiredPreset);
    // });
    // }
}
// a comment because why not :)
// This is a very cool comment *wink* *wink* *nudge* *nudge* - H