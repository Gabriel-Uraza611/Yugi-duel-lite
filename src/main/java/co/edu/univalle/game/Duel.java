
package co.edu.univalle.game;

import co.edu.univalle.api.YgoApiClient;
import co.edu.univalle.model.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Duel {
    private List<Card> playerCards;
    private List<Card> aiCards;

    private int playerScore;
    private int aiScore;

    private boolean playerTurn;
    private boolean duelEnded;

    private Card playerCard;
    private boolean playerAttack;

    private BattleListener listener;
    private YgoApiClient api;

    public Duel() {
        playerCards = new ArrayList<>();
        aiCards = new ArrayList<>();

        playerScore = 0;
        aiScore = 0;

        playerTurn = true;
        duelEnded = false;

        api = new YgoApiClient();
    }

    public void setListener(BattleListener listener) {
        this.listener = listener;
    }

    // Carga tres cartas para cada jugador
    public void cargarCartas() throws IOException, InterruptedException {
        playerCards.clear();
        aiCards.clear();

        for (int i = 0; i < 3; i++) {
            playerCards.add(api.obtenerCartaAleatoria());
            aiCards.add(api.obtenerCartaAleatoria());
        }
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public List<Card> getAiCards() {
        return aiCards;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getAiScore() {
        return aiScore;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public boolean isDuelEnded() {
        return duelEnded;
    }

    // El jugador selecciona su carta y su modo de combate
    public void jugarRonda(int indiceCarta, boolean modoAtaque) {
        if (duelEnded || !playerTurn) {
            return;
        }

        if (indiceCarta < 0 || indiceCarta >= playerCards.size()) {
            return;
        }

        // Guarda la carta seleccionada por el jugador
        playerCard = playerCards.remove(indiceCarta);
        playerAttack = modoAtaque;

        // Ahora le corresponde jugar a la máquina
        playerTurn = false;
    }

    // La máquina selecciona una carta y realiza su jugada
    public void jugarTurnoMaquina() {
        if (duelEnded || playerTurn || playerCard == null) {
            return;
        }

        // La máquina selecciona una carta al azar
        Random random = new Random();
        int indiceAI = random.nextInt(aiCards.size());
        Card aiCard = aiCards.remove(indiceAI);

        // Compara las estadísticas de ambas cartas
        String winner = determinarGanador(playerCard, aiCard);

        if (winner.equals("Jugador")) {
            playerScore++;
        } else if (winner.equals("Máquina")) {
            aiScore++;
        }

        // Muestra el resultado de la ronda
        if (listener != null) {
            listener.onTurn(
                    playerCard.getName(),
                    aiCard.getName(),
                    winner
            );

            listener.onScoreChanged(playerScore, aiScore);
        }

        playerCard = null;

        // Comprueba si terminó el duelo
        if (playerScore >= 2 || aiScore >= 2
                || playerCards.isEmpty() || aiCards.isEmpty()) {
            terminarDuelo();
        } else {
            // El jugador puede comenzar la siguiente ronda
            playerTurn = true;
        }
    }

    // Compara el valor elegido por el jugador contra el ATK de la máquina
    private String determinarGanador(Card playerCard, Card aiCard) {
        int playerValue;

        if (playerAttack) {
            playerValue = playerCard.getAtk();
        } else {
            playerValue = playerCard.getDef();
        }

        int aiValue = aiCard.getAtk();

        if (playerValue > aiValue) {
            return "Jugador";
        } else if (aiValue > playerValue) {
            return "Máquina";
        } else {
            return "Empate";
        }
    }

    // Finaliza el duelo y anuncia al ganador
    private void terminarDuelo() {
        duelEnded = true;

        String winner;

        if (playerScore > aiScore) {
            winner = "Jugador";
        } else if (aiScore > playerScore) {
            winner = "Máquina";
        } else {
            winner = "Empate";
        }

        if (listener != null) {
            listener.onDuelEnded(winner);
        }
    }
}