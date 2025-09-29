package classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class ControllerReproductor {

    /**
     * Metodo que recibe un archivo para calcular duracion
     *
     * @param file
     * @return el valor en segundos
     */
    public static int calcularDuracion(File file) {
        int r = 0;
        int a = 0;
        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(file); //se crea el objeto mandandole un archivo de audio 
            Map<String, Object> properties = ((TAudioFileFormat) audioFileFormat).properties(); //entra a las propiedades del archivo
            Long micro = (Long) properties.get("duration");//Obtiene la duracion en microsegundos
            a = (int) (micro / 1000); //comvierte de micro a milisegundos
            r = (a / 1000); //combierte milisegundos a segundos
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR  ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
        }
        return r;
    }

    public static void seleccionaLista(javax.swing.JLabel etiquetaNombre, ArrayList listaArchivos, javax.swing.JList jlistaReproduccion) {

        if (!listaArchivos.isEmpty()) {
            try {
                int indice = 0;
                String nombre = etiquetaNombre.getText();

                for (int i = 0; i < listaArchivos.size(); i++) {
                    File archivo = (File) listaArchivos.get(i);
                    if (nombre.equals(archivo.getName())) {
                        indice = i;
                        break;
                    }
                }
                jlistaReproduccion.setSelectedIndex(indice);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "ERROR  ", "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void listaReproduccion(DefaultListModel lista, ArrayList listaArchivos, javax.swing.JList jlistaReproduccion) {

        lista.clear();

        String nombres[] = new String[listaArchivos.size()];

        for (int i = 0; i < listaArchivos.size(); i++) {
            File archivo = (File) listaArchivos.get(i);
            nombres[i] = archivo.getName();
        }
        for (int i = 0; i < nombres.length; i++) {
            for (int j = 0; j < nombres.length - 1; j++) {
                if (nombres[j].compareTo(nombres[j + 1]) > 0) {
                    String temp = nombres[j];
                    nombres[j] = nombres[j + 1];
                    nombres[j + 1] = temp;
                }
            }
        }
        ArrayList<Object> nuevaLista = new ArrayList<>();

        int index;
        int indiceNombre = -1;
        while (nuevaLista.size() != listaArchivos.size()) {
            indiceNombre++;
            boolean bandera = true;
            index = -1;
            while (bandera == true) {
                index++;
                File archivo = (File) listaArchivos.get(index);
                if (nombres[indiceNombre].equals(archivo.getName())) {
                    File nuevoArchivo = (File) listaArchivos.get(index);
                    nuevaLista.add(nuevoArchivo);
                    bandera = false;
                }
            }
        }
        listaArchivos.clear();

        for (int i = 0; i < nuevaLista.size(); i++) {
            listaArchivos.add(nuevaLista.get(i));
        }

        for (String nombre : nombres) {
            lista.addElement(nombre);
        }
        jlistaReproduccion.setModel(lista);
    }

}
