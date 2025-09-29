package classes;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellView extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList jlist, Object valueObj, int index, boolean isSelected, boolean hasFocusCell) {

        String valor = valueObj.toString();
        if (isSelected) {
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/play.png")));
            setForeground(jlist.getSelectionForeground());
            setBackground(jlist.getSelectionBackground());
        } else {
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/song.png")));
            setForeground(jlist.getForeground());
            setBackground(jlist.getBackground());
        }
        setOpaque(true);
        setEnabled(jlist.isEnabled());
        setFont(jlist.getFont());

        setText(valor);

        return this;
    }
}
