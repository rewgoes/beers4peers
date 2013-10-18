package Interface;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 * This thread is responsible for enabling/disabling buttons in the interface.
 * The class has a number of buttons saved in it's attributes, and an array of
 * booleans. Every second, it checks the array and sets the corresponding
 * buttons to enabled or disabled, according to the values in the array.
 */
public class ButtonsControlThread extends Thread{

    // The boolean array used to determine which buttons are enabled, and which
    // are disabled
    private boolean[] controlButton;

    // The actual buttons to be enabled/disabled
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;

    public ButtonsControlThread(boolean[] controlString, JButton jButton1, JButton jButton2,
            JButton jButton3, JButton jButton4, JButton jButton5){

        // Just save the arguments in the class attributes
        this.controlButton = controlString;
        this.jButton1 = jButton1;
        this.jButton2 = jButton2;
        this.jButton3 = jButton3;
        this.jButton4 = jButton4;
        this.jButton5 = jButton5;
    }

    @Override
    public void run() {
        while(true){
            try {
                // Set all buttons to the value in the corresponding array position
                // Button "n" => controlButton[n-1]
                this.jButton1.setEnabled(controlButton[0]);
                this.jButton2.setEnabled(controlButton[1]);
                this.jButton3.setEnabled(controlButton[2]);
                this.jButton4.setEnabled(controlButton[3]);
                this.jButton5.setEnabled(controlButton[4]);

                // sleep 1 second (1000 ms)
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ButtonsControlThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
