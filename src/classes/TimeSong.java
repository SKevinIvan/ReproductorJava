package classes;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class TimeSong extends Thread {

    private JLabel lblTimeSong;
    private int minutos, segundos, aux;
    private int duration;

    public TimeSong() {
    }

    public TimeSong(int duration) {
        minutos = segundos = 0;
        this.duration = duration;
    }

    public TimeSong(int minutos, int segundos, int duration, int aux) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.duration = duration;
        this.aux = aux;
    }

    public JLabel getLblTimeSong() {
        return lblTimeSong;
    }

    public void setLblTimeSong(JLabel lblTimeSong) {
        this.lblTimeSong = lblTimeSong;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public int getAux() {
        return aux;
    }

    public void setAux(int aux) {
        this.aux = aux;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void recibeJLabel(JLabel lblTimeSong) {
        this.lblTimeSong = lblTimeSong;
    }

    @Override
    public void run() {
        while (true) {
            if (segundos == 60) {
                segundos = 0;
                minutos++;
                if (minutos == 60) {
                    minutos = 0;
                }
            }
            if (duration == aux) {
                break;
            }
            segundos++;
            aux++;
            lblTimeSong.setText((minutos >= 10 ? minutos : "0" + minutos) + ":" + (segundos >= 10 ? segundos : "0" + segundos));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(null, "ERROR",
                        "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
                Thread.currentThread().interrupt();
            }
        }
    }
}
