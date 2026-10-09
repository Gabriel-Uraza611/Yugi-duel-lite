package co.edu.univalle;

import co.edu.univalle.ui.DuelFrame;

import javax.swing.SwingUtilities;

public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            DuelFrame frame = new DuelFrame();
            frame.setVisible(true);
        });
    }
}