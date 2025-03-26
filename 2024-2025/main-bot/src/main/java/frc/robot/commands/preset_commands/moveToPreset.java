package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class moveToPreset extends Command {
    CommandSwerveDrivetrain m_Drivetrain;
    ElevatorSubsystem m_Elevator;
    CoralArmSubsystem m_CoralArm;
    CoralArmLevels coralLevel;
    ElevatorPresets elevatorLevel;
    CommandXboxController driveController;
    CommandXboxController operatorController;
    boolean isAuto = false;
    Timer outtakeTimer;
    boolean timerStarted = false;

    public moveToPreset(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController driveController,
            CommandXboxController operatorController, int lvl, boolean isAuto) {
        addRequirements(elevator, coralArm);
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        this.driveController = driveController;
        this.operatorController = operatorController;
        switch (lvl) {
        case 1:
            coralLevel = CoralArmLevels.lvl1;
            elevatorLevel = ElevatorPresets.Level1;
            break;
        case 2:
            // testing because lvl1 and lvl2 are very similar
            coralLevel = CoralArmLevels.lvl1;
            elevatorLevel = ElevatorPresets.Level2;
            break;
        case 3:
            coralLevel = CoralArmLevels.lvl3;
            elevatorLevel = ElevatorPresets.Level3;
            break;
        case 4:
            coralLevel = CoralArmLevels.lvl4;
            // Level 3 because the values are very similar
            elevatorLevel = ElevatorPresets.Level3;
            break;
        default:
            coralLevel = CoralArmLevels.lvl1;
            elevatorLevel = ElevatorPresets.Level1;
            break;
        }
    }

    public void execute() {
        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        // Checks if in positon and intake is pressed, checks if override is pressed and
        // in desired state,
        // and checks if auto is pressed
        if (isInDesiredState(elevatorLevel, coralLevel) && operatorController.getHID().getLeftStickButton()
                || (operatorController.getHID().getLeftStickButton()
                        && operatorController.getHID().getRightStickButton())
                || isAuto && isInDesiredState(elevatorLevel, coralLevel)) {
            m_CoralArm.outtake(coralLevel, true);
            if (!m_CoralArm.coralGrabbed() && outtakeTimer == null) {
                outtakeTimer = new Timer();
                // System.out.println("Outtake Timer Started");
                outtakeTimer.start();
            }
        } else {
            m_CoralArm.outtake(coralLevel, false);
        }
    }

    @Override
    public boolean isFinished() {
        if (outtakeTimer == null) {
            return false;
        } else {
            return outtakeTimer.hasElapsed(2) && !m_CoralArm.coralGrabbed();
        }
        // return m_CoralArm.coralGrabbed() && outtakeTimer.hasElapsed(3);
    }

    public void end(boolean interrupted) {
        m_CoralArm.outtake(coralLevel, false);

        outtakeTimer = null;
        // m_Elevator.stop();
    }

    public boolean isInDesiredState(ElevatorPresets elevatorPreset, CoralArmLevels coralArmLevel) {
        return m_Elevator.isElevatorAtDesiredState(elevatorPreset).getAsBoolean()
                && m_CoralArm.isCoralArmAtDesiredState(coralArmLevel).getAsBoolean();

    }

}
