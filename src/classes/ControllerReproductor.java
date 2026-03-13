package classes;

import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import models.PlayerModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class ControllerReproductor {

    private ReproductorUI view;
    private PlayerModel model;

    ProgressSong thrProgressBar = new ProgressSong();
    TimeSong thrTime = new TimeSong();//hilo tiempo o duracion

    ArrayList<Object> playlist = new ArrayList<>();

    int porcent, aux;//guarda el percentaje | guarda el tiempo donde se quedo la cancion
    double durationSong;//guarda lo que durationSong la cancion en segundos
    double delayCalcProgress;
    int minutos, segundos;
    String songNameCurrent = "...";

    public ControllerReproductor(ReproductorUI view, PlayerModel model) {
        this.view = view;
        this.model = model;

        initController();
    }

    private void initController() {
        view.getBtnReanuarPista().addActionListener(e -> reanuarCancion());
        view.getBtnMostrarLista().addActionListener(e -> mostrarLista());
        view.getBtnAgregarPista().addActionListener(e -> agregarPista());
        view.getBtnAnteriorPista().addActionListener(e -> previewPista());
        view.getBtnPausarPista().addActionListener(e -> pausarPista());
        view.getBtnSiguientePista().addActionListener(e -> nextPista());
        view.getBtnStop().addActionListener(e -> stopPista());
        view.getBtnEliminarLista().addActionListener(e -> eliminarLista());
        view.getBtnAbrirLista().addActionListener(e -> abrirLista());
        view.getBtnPlay().addActionListener(e -> playSong());

        view.getLstPlaylist().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarPistaLista();
                }

            }
        });

        view.getTxtBuscaCancion().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                buscarCancion();
            }
        });

        thrTime = new TimeSong();

        //
        view.getBtnAnteriorPista().setEnabled(false);
        view.getBtnAgregarPista().setEnabled(false);
        view.getBtnEliminarLista().setEnabled(false);
        view.getBtnSiguientePista().setEnabled(false);
        view.getBtnStop().setEnabled(false);
        view.getBtnPausarPista().setEnabled(false);
        view.getBtnPlay().setEnabled(false);
        view.getBtnReanuarPista().setEnabled(false);

      
        view.getPnlPlayList().setVisible(false);
    }

    private void mostrarLista() {
        if (view.getPnlPlayList().isVisible()) {
            view.getPnlPlayList().setVisible(false);
        } else {
            view.getPnlPlayList().setVisible(true);
        }
    }

    private void agregarPista() {
        if (!playlist.isEmpty()) {
            try {
                JFileChooser selecciona = new JFileChooser();
                FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivo MP3", "mp3");
                selecciona.setFileFilter(filtro);

                selecciona.setMultiSelectionEnabled(true);

                int resultado = selecciona.showOpenDialog(view);

                if (resultado == JFileChooser.APPROVE_OPTION) {
                    File[] archivos = selecciona.getSelectedFiles();

                    if (archivos != null && archivos.length > 0) {
                        for (File archivo : archivos) {
                            playlist.add(archivo);
                        }
                    }

                    ControllerReproductor.createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
                }
            } catch (HeadlessException e) {
                JOptionPane.showMessageDialog(null, "ERROR ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void previewPista() {
        if (!playlist.isEmpty()) {
            String nombre = "";
            try {

                int indice = 0;
                nombre = view.getLblNameSong().getText();

                for (int i = 0; i < playlist.size(); i++) {
                    File archivo = (File) playlist.get(i);
                    if (nombre.equals(archivo.getName())) {
                        indice = i;
                        break;
                    }
                }

                if ((indice - 1) == -1) {
                    indice = playlist.size() - 1;
                } else {
                    indice--;
                }

                File archivo = (File) playlist.get(indice);

                String ruta = archivo.getCanonicalPath();

                model.stop();
                model.open(ruta);
                model.play();
                view.getLblNameSong().setText(archivo.getName());
                view.getBtnPlay().setEnabled(true);

                durationSong = ControllerReproductor.calcularDuracion(archivo);

                if (thrTime.isAlive()) {
                    thrTime.stop();
                }
                if (thrProgressBar.isAlive()) {

                    thrProgressBar.stop();

                }

                delayCalcProgress = ((1 * durationSong) / 100) * 1000; //calcula la delayCalcProgress entre el porcentaje de la barra para que dure lo mismo que el hilo tiempo de musica
                minutos = segundos = 0;
                porcent = 0;
                thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux); //crea nuevos objetos de hilos con los valores en los que se encontraban al momento de la pausa
                thrTime.recibeJLabel(view.getLblTimeSong());

                thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
                thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

                thrProgressBar.start();
                thrTime.start();

                ControllerReproductor.createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
                view.getBtnPlay().setVisible(false);
                view.getBtnReanuarPista().setVisible(false);
                view.getBtnPausarPista().setVisible(true);
            } catch (IOException e) {
                ControllerReproductor.createPlaylist(view.getLstModel(), ControllerReproductor.quitarCancionPlaylist(playlist, nombre), view.getLstPlaylist());
                JOptionPane.showMessageDialog(null, "ERROR ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pausarPista() {
        if (!playlist.isEmpty()) {

            model.pause();  //pausa hilo musica

            view.getLblAlbumImage().setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/gota.gif"))); //cambia a imagen inicial
            minutos = thrTime.getMinutos(); //guarda segundos
            segundos = thrTime.getSegundos(); //guarda minutos
            aux = thrTime.getAux();                  //guarda la duracion de la cancion actual
            porcent = thrProgressBar.getPorcent();         //guarda el porcentaje de la barra

            thrProgressBar.stop();
            thrTime.stop();

            view.getBtnPlay().setVisible(false);
            view.getBtnReanuarPista().setVisible(true);
            view.getBtnPausarPista().setVisible(false);
            view.getBtnStop().setVisible(true);
        }
    }

    private void nextPista() {
        if (!playlist.isEmpty()) {
            String nombre = "";
            try {
                int indice = 0;
                nombre = view.getLblNameSong().getText();

                for (int i = 0; i < playlist.size(); i++) {
                    File archivo = (File) playlist.get(i);
                    if (nombre.equals(archivo.getName())) {
                        indice = i;
                        break;
                    }
                }
                if ((indice + 1) == playlist.size()) {
                    indice = 0;
                } else {
                    indice++;
                }
                File archivo = (File) playlist.get(indice);

                String ruta = archivo.getCanonicalPath();

                model.stop();

                model.open(ruta);
                model.play();
                view.getLblNameSong().setText(archivo.getName());
                view.getBtnPlay().setEnabled(true);

                durationSong = ControllerReproductor.calcularDuracion(archivo);

                if (thrTime.isAlive()) {
                    thrTime.stop();
                }
                if (thrProgressBar.isAlive()) {

                    thrProgressBar.stop();
                }

                delayCalcProgress = ((1 * durationSong) / 100) * 1000; //calcula la delayCalcProgress entre el porcentaje de la barra para que dure lo mismo que el hilo tiempo de musica
                minutos = segundos = 0;
                porcent = 0;
                thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux); //crea nuevos objetos de hilos con los valores en los que se encontraban al momento de la pausa
                thrTime.recibeJLabel(view.getLblTimeSong());

                thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
                thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

                thrProgressBar.start();
                thrTime.start();

                ControllerReproductor.createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
                view.getBtnPlay().setVisible(false);
                view.getBtnReanuarPista().setVisible(false);
                view.getBtnPausarPista().setVisible(true);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "ERROR ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
                ControllerReproductor.createPlaylist(view.getLstModel(), ControllerReproductor.quitarCancionPlaylist(playlist, nombre), view.getLstPlaylist());
            }
        }
    }

    private void stopPista() {
        if (!playlist.isEmpty()) {

            model.stop();//pausa hilo musica

            view.getLblAlbumImage().setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/gota.gif"))); //cambia a imagen inicial
            view.getProgressBarSong().setValue(0);
            view.getLblTimeSong().setText("00:00");
            minutos = 0; //guarda segundos
            segundos = 0; //guarda minutos
            aux = 0;                  //guarda la duracion de la cancion actual
            porcent = 0;         //guarda el porcentaje de la barra

            if (thrTime.isAlive()) {
                thrTime.stop();
            }
            if (thrProgressBar.isAlive()) {
                thrProgressBar.stop();
            }

            view.getBtnPlay().setVisible(false);
            view.getBtnReanuarPista().setVisible(true);
            view.getBtnPausarPista().setVisible(false);
            view.getBtnStop().setVisible(true);
            selectedSongPlaylist(songNameCurrent, playlist, view.getLstPlaylist());
        }

    }

    private void eliminarLista() {
        if (!playlist.isEmpty()) {
            try {

                playlist.clear(); //lo mismo pero para la lstModel de archivos
                view.getLblAlbumImage().setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/gota.gif")));
                view.getLblNameSong().setText("...");
                view.getProgressBarSong().setValue(0);
                view.getLblTimeSong().setText("00:00");

                model.stop();

                if (thrTime.isAlive()) {
                    thrTime.stop();
                }
                if (thrProgressBar.isAlive()) {

                    thrProgressBar.stop();
                }

                ControllerReproductor.createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());

                //
                view.getBtnPausarPista().setVisible(false);
                view.getBtnReanuarPista().setVisible(false);
                view.getBtnPlay().setVisible(true);
                //Muestra los botones que se pueden utilizar

                view.getBtnAnteriorPista().setEnabled(false);
                view.getBtnAgregarPista().setEnabled(false);
                view.getBtnEliminarLista().setEnabled(false);
                view.getBtnSiguientePista().setEnabled(false);
                view.getBtnStop().setEnabled(false);
                view.getBtnPausarPista().setEnabled(false);
                view.getBtnPlay().setEnabled(false);
                view.getBtnReanuarPista().setEnabled(false);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ERROR ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void playSong() {
        if (!playlist.isEmpty()) {
            try {

                int indice = 0;
                String nombre = view.getLblNameSong().getText();

                for (int i = 0; i < playlist.size(); i++) {
                    File archivo = (File) playlist.get(i);
                    if (nombre.equals(archivo.getName())) {
                        indice = i;
                        break;
                    }
                }

                File archivo = (File) playlist.get(indice);

                String ruta = archivo.getCanonicalPath();

                model.stop();
                model.open(ruta);
                model.play();
                view.getLblNameSong().setText(archivo.getName());
                view.getBtnPlay().setEnabled(true);

                durationSong = calcularDuracion(archivo);

                if (thrTime.isAlive()) {
                    thrTime.stop();
                }
                if (thrProgressBar.isAlive()) {

                    thrProgressBar.stop();
                }

                delayCalcProgress = ((1 * durationSong) / 100) * 1000; //calcula la delayCalcProgress entre el porcentaje de la barra para que dure lo mismo que el hilo tiempo de musica
                minutos = segundos = 0;
                porcent = 0;
                thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux); //crea nuevos objetos de hilos con los valores en los que se encontraban al momento de la pausa
                thrTime.recibeJLabel(view.getLblTimeSong());

                thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
                thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

                thrProgressBar.start();
                thrTime.start();

                createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
                view.getBtnPlay().setVisible(false);
                view.getBtnReanuarPista().setVisible(false);
                view.getBtnPausarPista().setVisible(true);
                view.getBtnStop().setEnabled(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage() + ": btnPlayActionPerformed", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reanuarCancion() {
        if (!playlist.isEmpty()) {

            model.resume(); //reanuda el hilo musica

            thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
            thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux);

            thrTime.recibeJLabel(view.getLblTimeSong());
            thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

            thrProgressBar.start(); //inicio de la continuacion de los hilos (play)
            thrTime.start();

            view.getBtnPlay().setVisible(false);
            view.getBtnReanuarPista().setVisible(false);
            view.getBtnPausarPista().setVisible(true);
            view.getBtnStop().setVisible(true);
        }
    }

    private void abrirLista() {
        try {
            JFileChooser selecciona = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivo MP3", "mp3", "mp3");
            selecciona.setFileFilter(filtro);
            selecciona.setMultiSelectionEnabled(true);

            int resultado = selecciona.showOpenDialog(view);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File[] archivos = selecciona.getSelectedFiles();
                if (archivos != null && archivos.length > 0) {
                    playlist.clear();
                    for (File archivo : archivos) {
                        playlist.add(archivo);
                    }
                }
                reproducirCancion((File) playlist.get(0));
            }

        } catch (IOException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + ": btnAbrirListaActionPerformed", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void seleccionarPistaLista() {
        if (!playlist.isEmpty()) {
            int opcion;
            String nombre = "";
            ImageIcon imagenMensaje = new javax.swing.ImageIcon(getClass().getResource("/imagenes/song.png"));
            String menu[]
                    = {
                        "Reproducir pista", "Quitar de la lista"
                    };
            opcion = JOptionPane.showOptionDialog(view, "Selecciona una opción", "Opciones ", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, imagenMensaje, menu, menu[0]);

            switch (opcion) {
                case 0:
                    //REPROCIR AUDIO SELECCIONADO
                    try {
                    int indice = 0;

                    if (view.getLstPlaylist().getSelectedIndex() != -1) {
                        nombre = view.getLstPlaylist().getSelectedValue();
                    }

                    for (int i = 0; i < playlist.size(); i++) {
                        File archivo = (File) playlist.get(i);
                        if (nombre.equals(archivo.getName())) {
                            indice = i;
                            break;
                        }
                    }

                    File archivo = (File) playlist.get(indice);

                    String ruta = archivo.getCanonicalPath();

                    model.stop();
                    model.open(ruta);
                    model.play();
                    view.getLblNameSong().setText(archivo.getName());
                    view.getBtnPlay().setEnabled(true);

                    durationSong = calcularDuracion(archivo);

                    if (thrTime.isAlive()) {
                        thrTime.stop();
                    }
                    if (thrProgressBar.isAlive()) {

                        thrProgressBar.stop();
                    }

                    delayCalcProgress = ((1 * durationSong) / 100) * 1000; //calcula la delayCalcProgress entre el porcentaje de la barra para que dure lo mismo que el hilo tiempo de musica
                    minutos = segundos = 0;
                    porcent = 0;
                    thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux); //crea nuevos objetos de hilos con los valores en los que se encontraban al momento de la pausa
                    thrTime.recibeJLabel(view.getLblTimeSong());

                    thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
                    thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

                    thrProgressBar.start();
                    thrTime.start();

                    view.getBtnPlay().setVisible(false);
                    view.getBtnReanuarPista().setVisible(false);
                    view.getBtnPausarPista().setVisible(true);

                    view.getBtnStop().setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage() + ": lstPlaylistMouseClicked", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
                    createPlaylist(view.getLstModel(), quitarCancionPlaylist(playlist, nombre),
                            view.getLstPlaylist());
                }
                break;
                case 1:

                    if (view.getLstPlaylist().getSelectedIndex() != -1) {
                        nombre = view.getLstPlaylist().getSelectedValue();
                    }

                    createPlaylist(view.getLstModel(), quitarCancionPlaylist(playlist, nombre), view.getLstPlaylist());
                    break;
                default:
                    selectedSongPlaylist(view.getLblNameSong().getText(), playlist, view.getLstPlaylist());
                    break;
            }
        }
    }

    private void buscarCancion() {
        if (view.getTxtBuscaCancion().getText().isEmpty()) {
            createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
        } else {
            if (!playlist.isEmpty()) {
                String cancion = view.getTxtBuscaCancion().getText();
                view.getLstModel().clear();
                for (int i = 0; i < playlist.size(); i++) {

                    File archivo = (File) playlist.get(i);
                    if (archivo.getName().contains(cancion)) {
                        view.getLstModel().addElement(archivo.getName());
                    }
                }
                view.getLstPlaylist().setModel(view.getLstModel());
                if (view.getLstModel().isEmpty()) {
                    createPlaylist(view.getLstModel(), playlist, view.getLstPlaylist());
                }
            }
        }
    }

    /**
     * Metodo que recibe un archivo para calcular duracion
     *
     * @param file
     * @return el valor en segundos
     */
    public static int calcularDuracion(File file) {
        int segundos = 0;
        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(file); //se crea el objeto mandandole un archivo de audio 
            Map<String, Object> properties = ((TAudioFileFormat) audioFileFormat).properties(); //entra a las propiedades del archivo

            System.out.println("Propieties: " + properties.toString());
            Long microsegundos = (Long) properties.get("duration");//Obtiene la duracion en microsegundos
            segundos = (int) (microsegundos / 1000) / 1000; //comvierte de micro a segundos
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + ": calcularDuracion", "Mensaje de error", JOptionPane.ERROR_MESSAGE);
        }
        return segundos;
    }

    public static void selectedSongPlaylist(String nombre, ArrayList playlist, JList jList) {
        int index = 0;
        if (!playlist.isEmpty()) {
            try {
                for (int i = 0; i < playlist.size(); i++) {
                    File archivo = (File) playlist.get(i);
                    if (nombre.equals(archivo.getName())) {
                        index = i;
                        break;
                    }
                }
                jList.setSelectedIndex(index);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage() + ": seleccionaLista", "Mensaje de error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void createPlaylist(DefaultListModel jLstModel, ArrayList playlist, JList jList) {
        jLstModel.clear();
        ArrayList<Object> nvaPlaylist = new ArrayList<>();
        String nombres[] = new String[playlist.size()];
        int index;
        int indiceNombre = -1;

        for (int i = 0; i < playlist.size(); i++) {
            File archivo = (File) playlist.get(i);
            nombres[i] = archivo.getName();
        }

        for (int j = 0; j < nombres.length - 1; j++) {
            if (nombres[j].compareTo(nombres[j + 1]) > 0) {
                String temp = nombres[j];
                nombres[j] = nombres[j + 1];
                nombres[j + 1] = temp;
            }
        }

        while (nvaPlaylist.size() != playlist.size()) {
            indiceNombre++;
            boolean bandera = true;
            index = -1;
            while (bandera == true) {
                index++;
                File archivo = (File) playlist.get(index);
                if (nombres[indiceNombre].equals(archivo.getName())) {
                    File nuevoArchivo = (File) playlist.get(index);
                    nvaPlaylist.add(nuevoArchivo);
                    bandera = false;
                }
            }
        }
        playlist.clear();

        for (int i = 0; i < nvaPlaylist.size(); i++) {
            playlist.add(nvaPlaylist.get(i));
        }

        for (String nombre : nombres) {
            jLstModel.addElement(nombre);
        }
        jList.setModel(jLstModel);
    }

  
/*
    public void nextSong() {
        if (!playlist.isEmpty()) {
            String nombre = "";
            try {
                int indice = Integer.parseInt(lblNameSong.getName());

                if ((indice) == playlist.size()) {
                    indice = 0;
                }
                File archivo = (File) playlist.get(indice);
                nombre = archivo.getName();
                String ruta = archivo.getCanonicalPath();

                thrMusic.stop();
                thrMusic.open(ruta);
                thrMusic.play();
                lblNameSong.setText(nombre);
                btnPlay.setEnabled(true);

                durationSong = calcularDuracion(archivo);

                if (thrTime.isAlive()) {
                    thrTime.stop();
                }
                if (thrProgressBar.isAlive()) {

                    thrProgressBar.stop();
                }

                delayCalcProgress = ((1 * durationSong) / 100) * 1000; //calcula la delayCalcProgress entre el porcentaje de la barra para que dure lo mismo que el hilo tiempo de musica
                minutos = segundos = 0;
                porcent = 0;
                thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux); //crea nuevos objetos de hilos con los valores en los que se encontraban al momento de la pausa
                thrTime.recibeJLabel(lblTimeSong);

                thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
                thrProgressBar.recibirBarraDeProgreso(progressBarSong, lblAlbumImage);

                thrProgressBar.start();
                thrTime.start();

                createPlaylist(lstModel, playlist, lstPlaylist);
                selectedSongPlaylist(lblNameSong.getText(), playlist, lstPlaylist);
                btnPlay.setVisible(false);
                btnReanuarPista.setVisible(false);
                btnPausarPista.setVisible(true);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + ": nextSong", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
                createPlaylist(lstModel, quitarCancionPlaylist(playlist, nombre), lstPlaylist);
            }
        }
    }
*/
    public void reproducirCancion(File archivo) throws IOException {
        model.stop();
        if (thrTime.isAlive()) {
            thrTime.stop();
        }
        if (thrProgressBar.isAlive()) {
            thrProgressBar.stop();
        }
        minutos = segundos = 0;
        String ruta = archivo.getCanonicalPath();
        view.getLblNameSong().setText(archivo.getName());
        durationSong = calcularDuracion(archivo);

        delayCalcProgress = ((durationSong) * 1000) / 100;

        porcent = 0;
        thrTime = new TimeSong(minutos, segundos, (int) durationSong, aux);
        thrTime.recibeJLabel(view.getLblTimeSong());

        thrProgressBar = new ProgressSong((int) delayCalcProgress, (int) durationSong, porcent);
        thrProgressBar.recibirBarraDeProgreso(view.getProgressBarSong(), view.getLblAlbumImage());

        view.getBtnAnteriorPista().setEnabled(true);
        view.getBtnAgregarPista().setEnabled(true);
        view.getBtnEliminarLista().setEnabled(true);
        view.getBtnSiguientePista().setEnabled(true);
        view.getBtnStop().setEnabled(true);
        view.getBtnPausarPista().setEnabled(true);
        view.getBtnPlay().setEnabled(true);
        view.getBtnReanuarPista().setEnabled(true);

        view.getBtnPlay().setVisible(false);
        view.getBtnReanuarPista().setVisible(false);
        view.getBtnPausarPista().setVisible(true);

        selectedSongPlaylist(archivo.getName(), playlist, view.getLstPlaylist());
        model.open(ruta);
        model.play();
        thrProgressBar.start();
        thrTime.start();

    }

    public static ArrayList quitarCancionPlaylist(ArrayList playlist, String nombre) {
        int index = 0;
        for (int i = 0; i < playlist.size(); i++) {
            File archivo = (File) playlist.get(i);
            if (nombre.equals(archivo.getName())) {
                index = i;
                break;
            }
        }

        playlist.remove(index);
        return playlist;
    }

}
