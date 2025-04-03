package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.CoralArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class moveToPreset extends Command {
    private CommandSwerveDrivetrain m_Drivetrain;
    private ElevatorSubsystem m_Elevator;
    private CoralArmSubsystem m_CoralArm;
    private CoralArmLevels desiredCoralLevel;
    private ElevatorPresets desiredElevatorLevel;
    private CommandXboxController driveController;
    private CommandXboxController operatorController;
    private boolean isLvl3or4;
    private boolean isAuto = false;
    private Timer outtakeTimer;
    private boolean timerStarted = false;
    private double lvl = 0;

    public moveToPreset(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController driveController,
            CommandXboxController operatorController, int desiredLvl, boolean isAuto) {
        addRequirements(elevator, coralArm);
        // System.out.println("DOES THIS FRIGGING COMMAND WORK????");
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        this.driveController = driveController;
        this.operatorController = operatorController;
        this.isAuto = isAuto;
        this.lvl = desiredLvl;
        switch (desiredLvl) {
        case 1:
            desiredCoralLevel = CoralArmLevels.lvl1;
            desiredElevatorLevel = ElevatorPresets.Level1;
            break;
        case 2:
            // testing because lvl1 and lvl2 are very similar
            desiredCoralLevel = CoralArmLevels.lvl1;
            desiredElevatorLevel = ElevatorPresets.Level2;
            break;
        case 3:
            desiredCoralLevel = CoralArmLevels.lvl3;
            desiredElevatorLevel = ElevatorPresets.Level3;
            isLvl3or4 = true;
            break;
        case 4:
            desiredCoralLevel = CoralArmLevels.lvl4;
            // Level 3 because the values are very similar
            desiredElevatorLevel = ElevatorPresets.Level4;
            isLvl3or4 = true;
            break;
        default:
            desiredCoralLevel = CoralArmLevels.lvl1;
            desiredElevatorLevel = ElevatorPresets.Level1;
            break;
        }
    }

    public void initialize() {
        // System.out.println("Command Started");

    }

    public void execute() {

        m_Elevator.elevatorMoveToPresetMM(desiredElevatorLevel);
        // Checks if in positon and intake is pressed, checks if override is pressed and
        // in desired state, and checks if auto is pressed
        if ((isInDesiredState(desiredElevatorLevel, desiredCoralLevel)
                && operatorController.getHID().getLeftStickButton())
                || (operatorController.getHID().getLeftStickButton()
                        && operatorController.getHID().getRightStickButton())
                || (isAuto && isInDesiredState(desiredElevatorLevel, desiredCoralLevel))) {
            // Checks if Kickback is needed (level 3 and 4)
            if (desiredElevatorLevel == ElevatorPresets.Level3) {
                // double position = CoralArmConstants.kCoralEncoderOuttakelvl3Position + 1;
                m_CoralArm.outtake(CoralArmConstants.kCoralEncoderOuttakelvl3Position - 2, true);
            } else if (desiredElevatorLevel == ElevatorPresets.Level4) {
                m_CoralArm.outtake(CoralArmConstants.kCoralEncoderOuttakelvl4Position - 2, true);
            }
            // otherwise outtakes normally
            else {
                m_CoralArm.outtake(desiredCoralLevel, true);
            }
            if (!m_CoralArm.coralGrabbed() && outtakeTimer == null) {
                outtakeTimer = new Timer();
                // System.out.println("Outtake Timer Started");
                outtakeTimer.start();
            }
        } else {
            // Basically checks if it is past the level 3 position, and if so, then raises
            // coral arm
            if (m_Elevator.getElevatorPosition() > ElevatorConstants.ElevatorPreset.level4EncoderValue - 4
                    && desiredElevatorLevel == ElevatorPresets.Level4) {
                m_CoralArm.outtake(desiredCoralLevel, false);
            } else if (desiredElevatorLevel == ElevatorPresets.Level4) {
                // Do nothing
            } else {
                m_CoralArm.outtake(desiredCoralLevel, false);
            }
        }
    }

    @Override
    public boolean isFinished() {
        if (isAuto) {
            if (outtakeTimer == null) {
                return false;
            } else {
                // SmartDashboard.putNumber("Command Timer", outtakeTimer.get());
                return outtakeTimer.hasElapsed(0.5) && !m_CoralArm.coralGrabbed();
            }
            // return m_CoralArm.coralGrabbed() && outtakeTimer.hasElapsed(3);
        } else {
            return false;
        }
    }

    public void end(boolean interrupted) {
        m_CoralArm.outtake(desiredCoralLevel, false);
        // System.out.println("Move to preset level finished: " + coralLevel);

        outtakeTimer = null;
        // m_Elevator.stop();
    }

    public boolean isInDesiredState(ElevatorPresets elevatorPreset, CoralArmLevels coralArmLevel) {
        return m_Elevator.isElevatorAtDesiredState(elevatorPreset).getAsBoolean()
                && m_CoralArm.isCoralArmAtDesiredState(coralArmLevel).getAsBoolean();

    }

}
