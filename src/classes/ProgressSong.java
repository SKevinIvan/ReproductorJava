package classes;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class ProgressSong extends Thread {

    private JProgressBar progressBarSong;
    private JLabel lblAlbumImage;
    private int delay = 1000;
    private boolean running = true;
    private int porcent = 0;
    private int duration;

    public ProgressSong() {
    }

    public ProgressSong(int delay, int dura) {
        this.delay = delay;
        this.duration = dura;
    }

    public ProgressSong(int delay, int dura, int porcent) {
        this.delay = delay;
        this.porcent = porcent;
        this.duration = dura;
        this.porcent = porcent;

    }

    public ProgressSong(int delay, boolean repe) {
        this.delay = delay;
        this.running = repe;
        porcent = 0;
    }

    public JLabel getLblAlbumImage() {
        return lblAlbumImage;
    }

    public void setLblAlbumImage(JLabel lblAlbumImage) {
        this.lblAlbumImage = lblAlbumImage;
    }

    public JProgressBar getProgressBarSong() {
        return progressBarSong;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRunning() {
        return running;
    }

    public int getPorcent() {
        return porcent;
    }

    public int getDuration() {
        return duration;
    }

    public void recibirBarraDeProgreso(JProgressBar progressBar, JLabel albumImage) {
        this.progressBarSong = progressBar;
        this.lblAlbumImage = albumImage;
        this.lblAlbumImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/sphere.gif")));

    }

    @Override
    public void run() {
        while (running) {
            if (porcent == 100) {
                lblAlbumImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/gota.gif")));
                running = false;
                break;
            }
            porcent++;
            progressBarSong.setValue(porcent);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
                Thread.currentThread().interrupt();
            }
        }
    }
}
