package frc.robot.subsystems;

//"A good programmers code needs not any comments, for it comments on itself" - IDK who I made it up
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.CoralArmConstants;
import java.util.function.BooleanSupplier;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CoralArmSubsystem extends SubsystemBase {
    // Motor(s)
    private final TalonFX m_CoralArmMotor;
    private final TalonSRX m_LeftCoralIntakeMotor;
    private final TalonSRX m_RightCoralIntakeMotor;
    // Light Sensor
    private final DigitalInput m_CoralArmLightSensor;
    // Current state
    private CoralArmLevels currentCoralArmState;
    private CoralArmLevels desiredCoralArmState;

    // Constants
    private double motorMaxCoralSpeed;
    private double intakePosition = CoralArmConstants.kCoralEncoderIntakePosition;

    // Test values for motion magic
    private MotionMagicVoltage setVoltage;
    // private StateManager stateManager;
    public static final double MOTION_MAGIC_ACCELERATION = 80;// 120
    public static final double MOTION_MAGIC_VELOCITY = 100;// 170
    private VoltageOut voltageOut = new VoltageOut(0.0);

    public CoralArmSubsystem() {
        m_CoralArmMotor = new TalonFX(CoralArmConstants.coralArmMotorID);
        m_LeftCoralIntakeMotor = new TalonSRX(CoralArmConstants.kLeftCoralIntakeMotorID);
        m_RightCoralIntakeMotor = new TalonSRX(CoralArmConstants.kRightCoralIntakeMotorID);
        motorMaxCoralSpeed = CoralArmConstants.kMaxArmSpeed;
        currentCoralArmState = CoralArmLevels.defaultState;
        desiredCoralArmState = CoralArmLevels.defaultState;
        m_CoralArmLightSensor = new DigitalInput(CoralArmConstants.kCoralLightSensorID);
        SetUpCoralArmMotor();

        setVoltage = new MotionMagicVoltage(0).withSlot(0);
        setVoltage.UpdateFreqHz = 1000;
    }

    private void SetUpCoralArmMotor() {
        // This function is courtesy of the Team 1764 codebase
        TalonFXConfiguration coralArmMotorConfig = new TalonFXConfiguration();

        // Coral Arm Going up
        coralArmMotorConfig.Slot0.kP = 3; // p pid //4.1
        coralArmMotorConfig.Slot0.kI = 0.1;
        coralArmMotorConfig.Slot0.kD = 0.15;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
        coralArmMotorConfig.Slot0.kV = 0;
        coralArmMotorConfig.Slot0.kA = 0;
        coralArmMotorConfig.Slot0.kG = 0.45;

        // Coral arm going down
        coralArmMotorConfig.Slot1.kP = 1; // p pid //4.1
        coralArmMotorConfig.Slot1.kD = 0.15;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
        coralArmMotorConfig.Slot1.kV = 0;
        coralArmMotorConfig.Slot1.kA = 0;
        coralArmMotorConfig.Slot1.kG = 0.1;

        coralArmMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        coralArmMotorConfig.MotorOutput.PeakForwardDutyCycle = motorMaxCoralSpeed;
        coralArmMotorConfig.MotorOutput.PeakReverseDutyCycle = -4; // can bump up to 12 or something
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
        intake, lvl1, lvl2, lvl3, lvl4, kickLowerAlgaeOff, kickUpperAlgaeOff, defaultState, inbetween
    }

    public void intake() {
        SmartDashboard.putBoolean("Default State Active", false);
        SmartDashboard.putNumber("Desired Coral Arm Position", intakePosition);
        desiredCoralArmState = CoralArmLevels.intake;
        m_CoralArmMotor.setControl(setVoltage.withPosition(intakePosition).withSlot(0));
        if (coralGrabbed()) {
            SmartDashboard.putBoolean("Coral grabbed?", true);
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        } else {
            SmartDashboard.putBoolean("Coral grabbed?", false);
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0.5);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0.5);
        }
        // .withLimitReverseMotion(true));
    }

    /**
     * Outtake method going to a desired preset
     * 
     * @param desiredLevel
     * @param ejectCoral
     */
    public void outtake(CoralArmLevels desiredLevel, boolean ejectCoral) {
        SmartDashboard.putBoolean("Default State Active", false);

        desiredCoralArmState = desiredLevel;
        int inversed = 1;
        switch (desiredCoralArmState) {
        case lvl1:
            m_CoralArmMotor.setControl(
                    setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakelvl1Position).withSlot(0));
            SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderOuttakelvl1Position);
            break;
        case lvl2:
            m_CoralArmMotor.setControl(
                    setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakelvl2Position).withSlot(0));
            SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderOuttakelvl2Position);
            break;
        case lvl3:
            m_CoralArmMotor.setControl(
                    setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakelvl3Position).withSlot(0));
            SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderOuttakelvl3Position);
            break;
        case lvl4:
            m_CoralArmMotor.setControl(
                    setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakelvl4Position).withSlot(0));
            inversed *= -1;
            SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderOuttakelvl4Position);
            break;
        }
        double intakePower = -0.5 * inversed;
        if (ejectCoral) {
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, intakePower);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, intakePower);
        } else {
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    /**
     * Outtake method going to a desired position WARNING!!! Assumes that the coral
     * will be ejected in the opposite way
     * 
     * @param desiredPosition
     */
    public void outtake(double desiredPosition, boolean ejectCoral) {
        m_CoralArmMotor.setControl(setVoltage.withPosition(desiredPosition).withSlot(0));

        SmartDashboard.putNumber("Desired Coral Arm Position", desiredPosition);
        double inversed = 1;
        double intakePower = 0.5 * inversed;

        if (ejectCoral) {
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, intakePower);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, intakePower);
        } else {
            m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
            m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        }

    }

    public void kickAlgaeOff() {
        SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderOuttakelvl4Position);
        // I'm just using level 4 as a placeholder for the kickUpperAlgaeOff position
        m_CoralArmMotor
                .setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderOuttakelvl4Position).withSlot(0));
        m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);

    }

    public void defaultState() {
        // SmartDashboard.putBoolean("Default State Active", true);
        SmartDashboard.putNumber("Desired Coral Arm Position", CoralArmConstants.kCoralEncoderDefaultPosition);
        desiredCoralArmState = CoralArmLevels.defaultState;
        m_CoralArmMotor.setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderDefaultPosition).withSlot(1));
        m_LeftCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        m_RightCoralIntakeMotor.set(ControlMode.PercentOutput, 0);
        // .withLimitReverseMotion(true));
    }

    public boolean coralGrabbed() {
        return m_CoralArmLightSensor.get();
    }

    public void changeCoralIntakeEncoderPosition(double change) {
        intakePosition += change;
        System.out.println("new coral arm encoder intake position " + intakePosition);

    }

    public CoralArmLevels getCurrentCoralArmState() {
        double bufferValue = 0.2;
        double currentPosition = m_CoralArmMotor.getPosition().getValueAsDouble();
        SmartDashboard.putNumber("TEST CURRENT POSITION VALUE", currentPosition);

        if (Math.abs(currentPosition - CoralArmConstants.kCoralEncoderOuttakelvl1Position) < bufferValue) {
            currentCoralArmState = CoralArmLevels.lvl1;
        } else if (Math.abs(currentPosition - CoralArmConstants.kCoralEncoderOuttakelvl2Position) < bufferValue) {
            currentCoralArmState = CoralArmLevels.lvl2;
        } else if (Math.abs(currentPosition - CoralArmConstants.kCoralEncoderOuttakelvl3Position) < bufferValue) {
            currentCoralArmState = CoralArmLevels.lvl3;
        } else if (Math.abs(currentPosition - CoralArmConstants.kCoralEncoderOuttakelvl4Position) < bufferValue) {
            currentCoralArmState = CoralArmLevels.lvl4;
        } else if (Math.abs(currentPosition - intakePosition) < bufferValue) {
            currentCoralArmState = CoralArmLevels.intake;
        } else if (Math.abs(currentPosition - CoralArmConstants.kCoralEncoderDefaultPosition) < bufferValue) {
            currentCoralArmState = CoralArmLevels.defaultState;
        } else {
            currentCoralArmState = CoralArmLevels.inbetween;
        }
        // else if (Math.abs(currentPosition -
        // CoralArmConstants.kCoralEncoderKickLowerAlgaeOff) < bufferValue) {
        // currentCoralArmState = CoralArmLevels.kickLowerAlgaeOff;
        // } else if (Math.abs(currentPosition -
        // CoralArmConstants.kCoralEncoderKickUpperAlgaeOff) < bufferValue) {
        // currentCoralArmState = CoralArmLevels.kickUpperAlgaeOff;
        // }

        // if (currentPosition > CoralArmConstants.kCoralEncoderIntakePosition -
        // bufferValue
        // && currentPosition < CoralArmConstants.kCoralEncoderIntakePosition +
        // bufferValue) {
        // currentCoralArmState = CoralArmLevels.intake;
        // } else if (currentPosition >
        // CoralArmConstants.kCoralEncoderOuttakelvl1Position - bufferValue
        // && currentPosition < CoralArmConstants.kCoralEncoderOuttakelvl1Position +
        // bufferValue) {
        // currentCoralArmState = CoralArmLevels.lvl1;
        // } else if (currentPosition >
        // CoralArmConstants.kCoralEncoderOuttakelvl2Position - bufferValue
        // && currentPosition < CoralArmConstants.kCoralEncoderOuttakelvl2Position +
        // bufferValue) {
        // currentCoralArmState = CoralArmLevels.lvl2;
        // } else if (currentPosition >
        // CoralArmConstants.kCoralEncoderOuttakelvl3Position - bufferValue
        // && currentPosition < CoralArmConstants.kCoralEncoderOuttakelvl3Position +
        // bufferValue) {
        // currentCoralArmState = CoralArmLevels.lvl3;
        // } else if (currentPosition >
        // CoralArmConstants.kCoralEncoderOuttakelvl4Position - bufferValue
        // && currentPosition < CoralArmConstants.kCoralEncoderOuttakelvl4Position +
        // bufferValue) {
        // currentCoralArmState = CoralArmLevels.lvl4;
        // } else {
        // currentCoralArmState = CoralArmLevels.defaultState;
        // }
        return currentCoralArmState;
    }

    public BooleanSupplier isCoralArmAtDesiredState(CoralArmLevels desiredState) {
        return () -> currentCoralArmState == desiredState;
    }

    public BooleanSupplier isCoralInArm() {
        return () -> m_CoralArmLightSensor.get();
    }

    public void sysIdTest() {
        final SysIdRoutine m_sysIdRoutine = new SysIdRoutine(new SysIdRoutine.Config(null, // Use default ramp rate
                // (1 V/s)
                Volts.of(1), // Reduce dynamic step voltage to 4 to prevent brownout
                null, // Use default timeout (10 s)
                // Log state with Phoenix SignalLogger class
                (state) -> SignalLogger.writeString("state", state.toString())),
                new SysIdRoutine.Mechanism(
                        (volts) -> m_CoralArmMotor.setControl(voltageOut.withOutput(volts.in(Volts))), null, this));
    }

    public void debuggingMethod() {
        SmartDashboard.putBoolean("is Coral In Arm", m_CoralArmLightSensor.get());
        SmartDashboard.putString("Current Coral Arm State", " " + getCurrentCoralArmState());
        SmartDashboard.putString("Desired Coral Arm State", " " + desiredCoralArmState);
        SmartDashboard.putNumber("Coral Arm Motor position", m_CoralArmMotor.getPosition().getValueAsDouble());
        // SmartDashboard.putNumber("Left Coral Arm Intake Power",
        // m_LeftCoralIntakeMotor.getMotorOutputVoltage());
        // SmartDashboard.putNumber("Right Coral Arm Intake Power",
        // m_RightCoralIntakeMotor.getMotorOutputVoltage());
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
