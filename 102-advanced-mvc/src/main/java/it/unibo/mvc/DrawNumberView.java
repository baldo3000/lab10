package it.unibo.mvc;

/**
 *
 */
public interface DrawNumberView {

    /**
     * @param observer the controller to attach
     */
    void setObserver(DrawNumberViewObserver observer);

    /**
     * This method is called before the UI is used. It should finalize its status
     * (if needed).
     */
    void start();

    /**
     * This method is called before quitting the program to stop the view from
     * running.
     */
    void stop();

    /**
     * Informs the user that the inserted number is not correct.
     */
    void numberIncorrect();

    /**
     * @param res the result of the last draw
     */
    void result(DrawResult res);

    /**
     * @param message the message to display
     */
    void displayError(String message);
}
