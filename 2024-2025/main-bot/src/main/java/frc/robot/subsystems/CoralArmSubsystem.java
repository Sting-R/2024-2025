package frc.robot.subsystems;

//"A good programmers code needs not any comments, for it comments on itself" - IDK who I made it up
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CoralArmConstants;
import frc.robot.Constants.ElevatorConstants;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CoralArmSubsystem extends SubsystemBase {
    // Motor(s)
    private final TalonFX m_CoralArmMotor;
    private final TalonSRX m_LeftCoralIntakeMotor;
    private final TalonSRX m_RightCoralIntakeMotor;
    // Encoder
    private final Encoder m_CoralArmEncoder;
    private final Encoder m_CoralIntakeEncoder;

    // Light Sensor
    private final DigitalInput m_CoralArmLightSensor;
    // Current state
    private CoralArmLevels currentCoralArmState;

    // Constants
    private double motorMaxCoralSpeed;

    // Test values for motion magic
    private MotionMagicVoltage setVoltage;
    // private StateManager stateManager;
    public static final double MOTION_MAGIC_ACCELERATION = 80;// 120
    public static final double MOTION_MAGIC_VELOCITY = 100;// 170
    private VoltageOut voltageOut = new VoltageOut(0.0);

    public CoralArmSubsystem() {
        // Initialized Stuff
        m_CoralArmEncoder = new Encoder(CoralArmConstants.kCoralArmEncoderID1, CoralArmConstants.kCoralArmEncoderID2);
        m_CoralIntakeEncoder = new Encoder(CoralArmConstants.kCoramIntakeEncoderID1,
                CoralArmConstants.kCoralIntakeEncoderID2);
        m_CoralArmMotor = new TalonFX(CoralArmConstants.coralArmMotorID);
        m_LeftCoralIntakeMotor = new TalonSRX(CoralArmConstants.kLeftCoralMotorID);
        m_RightCoralIntakeMotor = new TalonSRX(CoralArmConstants.kRightCoralMotorID);
        motorMaxCoralSpeed = ElevatorConstants.kMaxMotorElevatorSpeed;
        currentCoralArmState = CoralArmLevels.Down;
        m_CoralArmLightSensor = new DigitalInput(CoralArmConstants.kCoralLightSensorID);
        SetUpCoralArmMotor();

        setVoltage = new MotionMagicVoltage(0).withSlot(0);
        setVoltage.UpdateFreqHz = 1000;
    }

    private void SetUpCoralArmMotor() {
        // This function is courtesy of the Team 1764 codebase
        TalonFXConfiguration coralArmMotorConfig = new TalonFXConfiguration();

        coralArmMotorConfig.Slot0.kP = 0.1; // p pid //4.1
        coralArmMotorConfig.Slot0.kD = 0;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
        coralArmMotorConfig.Slot0.kV = 0;
        coralArmMotorConfig.Slot0.kA = 0;
        coralArmMotorConfig.Slot0.kG = 0.65;

        coralArmMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        coralArmMotorConfig.MotorOutput.PeakForwardDutyCycle = motorMaxCoralSpeed;
        coralArmMotorConfig.MotorOutput.PeakReverseDutyCycle = -0.3; // can bump up to 12 or something
        coralArmMotorConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        coralArmMotorConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        coralArmMotorConfig.CurrentLimits.StatorCurrentLimit = 60;

        MotionMagicConfigs motionMagicConfigs = new MotionMagicConfigs();
        motionMagicConfigs.withMotionMagicAcceleration(MOTION_MAGIC_ACCELERATION)
                .withMotionMagicCruiseVelocity(MOTION_MAGIC_VELOCITY);

        m_CoralArmMotor.getConfigurator().apply(coralArmMotorConfig);
        m_CoralArmMotor.getConfigurator().apply(motionMagicConfigs);

    }

    public enum CoralArmLevels {
        intake, outtake, Down
    }

    public void coralArmIntake() {
        m_CoralArmMotor.setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderIntakePosition).withSlot(0));
        if (coralGrabbed()) {
            SmartDashboard.putBoolean("Coral grabbed?", true);
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        } else {
            SmartDashboard.putBoolean("Coral grabbed?", false);
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0.5);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, -0.5);
        }
        // .withLimitReverseMotion(true));
    }

    public void coralArmOuttake() {
        m_CoralArmMotor.setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakePosition).withSlot(0));
        m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, -0.5);
        m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0.5);
        // .withLimitReverseMotion(true));
    }

    public void coralArmDefaultState() {
        m_CoralArmMotor.setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderDefaultPosition).withSlot(0));
        m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        // .withLimitReverseMotion(true));
    }

    public boolean coralGrabbed() {
        return m_CoralArmLightSensor.get();
    }

    public CoralArmLevels getCurrentCoralArmState() {
        if (m_CoralArmEncoder.get() > CoralArmConstants.kCoralEncoderIntakePosition - 0.1
                && m_CoralArmEncoder.get() < CoralArmConstants.kCoralEncoderIntakePosition + 0.1) {
            currentCoralArmState = CoralArmLevels.intake;
        } else if (m_CoralArmEncoder.get() > CoralArmConstants.kCoralEncoderOuttakePosition - 0.1
                && m_CoralArmEncoder.get() < CoralArmConstants.kCoralEncoderOuttakePosition + 0.1) {
            currentCoralArmState = CoralArmLevels.outtake;
        } else {
            currentCoralArmState = CoralArmLevels.Down;
        }
        return currentCoralArmState;
    }

    // public Command CommandsetCoralArmVoltage(double voltage, CoralArmLevels
    // desiredState) {
    // return runOnce(() -> {
    // if (voltage > 0) {
    // if (m_CoralEncoder.get() < CoralArmConstants.kCoralEncoderTopValue) {
    // setCoralArmVoltage(voltage);
    // } else if (m_CoralEncoder.get() > CoralArmConstants.kCoralEncoderTopBuffer) {
    // System.out.println("How is the arm not broken yet? Amazingly, you didn't
    // break it.");
    // setCoralArmVoltage(0);
    // } else {
    // setCoralArmVoltage(0);
    // currentCoralArmState = desiredState;
    // }
    // } else if (voltage < 0) {
    // if (m_CoralEncoder.get() > CoralArmConstants.kCoralEncoderBottomValue) {
    // setCoralArmVoltage(voltage);
    // } else if (m_CoralEncoder.get() <
    // CoralArmConstants.kCoralEncoderBottomBuffer) {
    // System.out.println("How is the arm not broken yet? Amazingly, you didn't
    // break it.");
    // setCoralArmVoltage(0);
    // } else {
    // setCoralArmVoltage(0);
    // currentCoralArmState = desiredState;
    // }
    // } else {
    // setCoralArmVoltage(0);
    // currentCoralArmState = desiredState;
    // }
    // });
    // }

    // public void setCoralArmVoltage(double voltage) {
    // m_CoralArmMotor.setControl(setVoltage.withPosition(voltage).withSlot(0)); //
    // .withLimitReverseMotion(true));
    // // Add more motors here if neccessary
    // }

    public BooleanSupplier isCoralArmAtDesiredState(CoralArmLevels desiredState) {
        return () -> currentCoralArmState == desiredState;
    }

    // public Command emergencyStop() {// Just in case the driver wants to stop the
    // arm without stopping the whole
    // // program (and for some reason the Command Voltage function doesn't stop the
    // // arm after it gets above a certain point).
    // return runOnce(() -> {
    // setCoralArmVoltage(0);
    // System.out.println("Emergency Stop Pressed! Stay Still And Don't Move The
    // Arm...");
    // if (m_CoralArmEncoder.get() > CoralArmConstants.kCoralEncoderTopPosition) {
    // do {// Hey look at that, a do while loop has a use!
    // setCoralArmVoltage(-0.25);
    // } while (m_CoralArmEncoder.get() >
    // CoralArmConstants.kCoralEncoderTopPosition);
    // setCoralArmVoltage(0);
    // } else if (m_CoralArmEncoder.get() <
    // CoralArmConstants.kCoralEncoderTopPosition) {
    // do {
    // setCoralArmVoltage(0.25);
    // } while (m_CoralArmEncoder.get() <
    // CoralArmConstants.kCoralEncoderTopPosition);
    // setCoralArmVoltage(0);
    // } else {
    // System.out.println("Arm is already at top");
    // }
    // System.out.println("Arm Is Now Reset. Carry On.");
    // });
    // }
}
