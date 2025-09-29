package classes;

import java.io.File;
import javax.swing.JOptionPane;
import javazoom.jlgui.basicplayer.BasicPlayer;

public class ControllerSong {

    public BasicPlayer player;

    public ControllerSong() {
        player = new BasicPlayer();
    }

    public void play() {
        try {
            player.play();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void open(String ruta) {
        try {
            player.open(new File(ruta));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void pause() {
        try {
            player.pause();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void resume() {
        try {
            player.resume();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void stop() {
        try {
            player.stop();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
