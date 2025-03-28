package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.CoralArmConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class moveToPreset extends Command {
    private CommandSwerveDrivetrain m_Drivetrain;
    private ElevatorSubsystem m_Elevator;
    private CoralArmSubsystem m_CoralArm;
    private CoralArmLevels coralLevel;
    private ElevatorPresets elevatorLevel;
    private CommandXboxController driveController;
    private CommandXboxController operatorController;
    private boolean isLvl3or4;
    private boolean isAuto = false;
    private Timer outtakeTimer;
    private boolean timerStarted = false;
    private double lvl = 0;

    public moveToPreset(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController driveController,
            CommandXboxController operatorController, int lvl, boolean isAuto) {
        addRequirements(elevator, coralArm);
        // System.out.println("DOES THIS FRIGGING COMMAND WORK????");
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        this.driveController = driveController;
        this.operatorController = operatorController;
        this.isAuto = isAuto;
        this.lvl = lvl;
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
            isLvl3or4 = true;
            break;
        case 4:
            coralLevel = CoralArmLevels.lvl4;
            // Level 3 because the values are very similar
            elevatorLevel = ElevatorPresets.Level4;
            isLvl3or4 = true;
            break;
        default:
            coralLevel = CoralArmLevels.lvl1;
            elevatorLevel = ElevatorPresets.Level1;
            break;
        }
    }

    public void initialize() {
        // System.out.println("Command Started");
    }

    public void execute() {

        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        // Checks if in positon and intake is pressed, checks if override is pressed and
        // in desired state,
        // and checks if auto is pressed
        if ((isInDesiredState(elevatorLevel, coralLevel) && operatorController.getHID().getLeftStickButton())

                || (operatorController.getHID().getLeftStickButton()
                        && operatorController.getHID().getRightStickButton())
                || (isAuto && isInDesiredState(elevatorLevel, coralLevel))) {
            // Checks if Kickback is needed (level 3 and 4)

            if (elevatorLevel == ElevatorPresets.Level3) {
                // double position = CoralArmConstants.kCoralEncoderOuttakelvl3Position + 1;
                m_CoralArm.outtake(CoralArmConstants.kCoralEncoderOuttakelvl3Position - 2, true);
            } else if (elevatorLevel == ElevatorPresets.Level4) {
                m_CoralArm.outtake(CoralArmConstants.kCoralEncoderOuttakelvl4Position - 2, true);
            } else {
                m_CoralArm.outtake(coralLevel, true);
            }

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
            // SmartDashboard.putNumber("Command Timer", outtakeTimer.get());
            return outtakeTimer.hasElapsed(2) && !m_CoralArm.coralGrabbed();
        }
        // return m_CoralArm.coralGrabbed() && outtakeTimer.hasElapsed(3);
    }

    public void end(boolean interrupted) {
        m_CoralArm.outtake(coralLevel, false);
        // System.out.println("Move to preset level finished: " + coralLevel);

        outtakeTimer = null;
        // m_Elevator.stop();
    }

    public boolean isInDesiredState(ElevatorPresets elevatorPreset, CoralArmLevels coralArmLevel) {
        return m_Elevator.isElevatorAtDesiredState(elevatorPreset).getAsBoolean()
                && m_CoralArm.isCoralArmAtDesiredState(coralArmLevel).getAsBoolean();

    }

}
