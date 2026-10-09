package co.edu.univalle.ui;

import co.edu.univalle.game.BattleListener;
import co.edu.univalle.game.Duel;
import co.edu.univalle.model.Card;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class DuelFrame extends JFrame implements BattleListener
{
    private Duel duel;

    private JPanel playerCardsPanel;
    private JPanel aiCardsPanel;

    private JLabel scoreLabel;
    private JLabel turnLabel;

    private JTextArea battleLog;

    private JButton attackButton;
    private JButton defenseButton;

    public DuelFrame()
    {
        setTitle("Yu-Gi-Oh! Duel Lite");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        duel = new Duel();
        duel.setListener(this);

        // Panel superior con el marcador y el turno
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        scoreLabel = new JLabel("Jugador 0 - 0 Máquina", SwingConstants.CENTER);
        turnLabel = new JLabel("Cargando cartas...", SwingConstants.CENTER);

        topPanel.add(scoreLabel);
        topPanel.add(turnLabel);

        add(topPanel, BorderLayout.NORTH);

        // Panel de cartas de la máquina
        aiCardsPanel = new JPanel(new FlowLayout());
        aiCardsPanel.setBorder(BorderFactory.createTitledBorder("Cartas de la máquina"));

        // Panel de cartas del jugador
        playerCardsPanel = new JPanel(new FlowLayout());
        playerCardsPanel.setBorder(BorderFactory.createTitledBorder("Tus cartas"));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 1));
        cardsPanel.add(aiCardsPanel);
        cardsPanel.add(playerCardsPanel);

        add(cardsPanel, BorderLayout.CENTER);

        // Registro de las rondas
        battleLog = new JTextArea(6, 30);
        battleLog.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registro del duelo"));

        // Botones de combate
        attackButton = new JButton("Atacar");
        defenseButton = new JButton("Defender");

        attackButton.setEnabled(false);
        defenseButton.setEnabled(false);

        attackButton.addActionListener(e -> jugarCarta(true));
        defenseButton.addActionListener(e -> jugarCarta(false));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(attackButton);
        buttonsPanel.add(defenseButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Carga las cartas sin congelar la ventana
        cargarCartas();
    }

    private void cargarCartas()
    {
        turnLabel.setText("Cargando cartas de la API...");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                duel.cargarCartas();
                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();

                    mostrarCartas();
                    actualizarTurno();

                    battleLog.append("¡El duelo ha comenzado!\n");
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(
                            DuelFrame.this,
                            "No se pudieron cargar las cartas: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    turnLabel.setText("Error al cargar las cartas.");
                }
            }
        };

        worker.execute();
    }

    private void mostrarCartas()
    {
        aiCardsPanel.removeAll();
        playerCardsPanel.removeAll();

        List<Card> aiCards = duel.getAiCards();
        List<Card> playerCards = duel.getPlayerCards();

        for (Card card : aiCards)
        {
            aiCardsPanel.add(crearPanelCarta(card, false));
        }

        for (int i = 0; i < playerCards.size(); i++)
        {
            playerCardsPanel.add(crearPanelCarta(playerCards.get(i), true));
        }

        aiCardsPanel.revalidate();
        aiCardsPanel.repaint();

        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    private JPanel crearPanelCarta(Card card, boolean esJugador)
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(150, 230));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel nameLabel = new JLabel(
                card.getName(),
                SwingConstants.CENTER
        );

        JLabel imageLabel = new JLabel("Cargando imagen...", SwingConstants.CENTER);

        try
        {
            ImageIcon icon = new ImageIcon(new URL(card.getImageUrl()));

            Image image = icon.getImage().getScaledInstance(
                    120, 160, Image.SCALE_SMOOTH
            );

            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(image));
        }
        catch (Exception e)
        {
            imageLabel.setText("Imagen no disponible");
        }

        JLabel statsLabel = new JLabel(
                "ATK: " + card.getAtk() + " | DEF: " + card.getDef(),
                SwingConstants.CENTER
        );

        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(statsLabel, BorderLayout.SOUTH);

        return panel;
    }


    private void jugarCarta(boolean modoAtaque) {
        if (!duel.isPlayerTurn() || duel.isDuelEnded()) {
            return;
        }

        List<Card> cartas = duel.getPlayerCards();

        if (cartas.isEmpty()) {
            return;
        }

        String[] opciones = new String[cartas.size()];

        for (int i = 0; i < cartas.size(); i++) {
            Card card = cartas.get(i);

            opciones[i] = card.getName()
                    + " (ATK: " + card.getAtk()
                    + ", DEF: " + card.getDef() + ")";
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona la carta que quieres jugar:",
                "Elegir carta",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == null) {
            return;
        }

        int indice = -1;

        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(seleccion)) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            return;
        }

        attackButton.setEnabled(false);
        defenseButton.setEnabled(false);

        // El jugador juega su carta
        duel.jugarRonda(indice, modoAtaque);
        mostrarCartas();

        if (!duel.isDuelEnded()) {
            turnLabel.setText("Turno de la máquina...");
            battleLog.append("La máquina está preparando su jugada...\n");

            // La máquina juega después de un segundo
            Timer timer = new Timer(1000, e -> {
                duel.jugarTurnoMaquina();

                mostrarCartas();
                actualizarTurno();
            });

            timer.setRepeats(false);
            timer.start();
        }
    }

    private void actualizarTurno()
    {
        if (duel.isDuelEnded())
        {
            attackButton.setEnabled(false);
            defenseButton.setEnabled(false);
            return;
        }

        if (duel.isPlayerTurn())
        {
            turnLabel.setText("Tu turno: elige ataque o defensa");
            attackButton.setEnabled(true);
            defenseButton.setEnabled(true);
        }
        else
        {
            turnLabel.setText("Turno de la máquina");
            attackButton.setEnabled(false);
            defenseButton.setEnabled(false);
        }
    }

    @Override
    public void onTurn(String playerCard, String aiCard, String winner)
    {
        battleLog.append(
                "Jugador: " + playerCard
                        + " | Máquina: " + aiCard
                        + " | Ganador: " + winner + "\n"
        );
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore)
    {
        scoreLabel.setText(
                "Jugador " + playerScore + " - " + aiScore + " Máquina"
        );
    }

    @Override
    public void onDuelEnded(String winner) {
        turnLabel.setText("Duelo terminado");

        attackButton.setEnabled(false);
        defenseButton.setEnabled(false);

        String[] opciones = {"Jugar de nuevo", "Cerrar"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "El ganador es " + winner,
                "Fin del duelo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (respuesta == 0) {
            reiniciarDuelo();
        } else {
            dispose();
        }

    }
    private void reiniciarDuelo() {
        duel = new Duel();
        duel.setListener(this);

        battleLog.setText("");

        onScoreChanged(0, 0);
        turnLabel.setText("Cargando cartas...");
        cargarCartas();

    }

}