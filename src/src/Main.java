package src;

import classes.ControllerReproductor;
import classes.ReproductorUI;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import models.PlayerModel;

/**
 *
 * @author Kevin Ivan Sanchez Valdin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, "ERROR ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PlayerModel model = new PlayerModel();
                ReproductorUI view = new ReproductorUI();
                ControllerReproductor controllerReproductor = new ControllerReproductor(view, model);
                view.setVisible(true);
            }
        });
    }
}
