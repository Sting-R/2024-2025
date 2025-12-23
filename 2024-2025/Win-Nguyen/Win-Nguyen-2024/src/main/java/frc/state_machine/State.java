package frc.state_machine;

/**
* Abstract class used to create states for the state machine.
*/
public abstract class State {

    // Imported from 2022-2023 code

    /**
    * Method called when a state is entered.
    */
    public void onEnter() {
    }

    /**
    * Method called when a state is exited.
    */
    public void onExit() {
    }

    /**
     * Method called to check if a state is valid.
     * @return  boolean
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Method called to run a state.
     * This method should return true when the state is finished
     *
     * @return  boolean
     */
    public abstract boolean run();
}
