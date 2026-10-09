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

    private BattleListener listener;
    private YgoApiClient api;

    public Duel() {
        playerCards = new ArrayList<>();
        aiCards = new ArrayList<>();

        playerScore = 0;
        aiScore = 0;

        playerTurn = new Random().nextBoolean();

        duelEnded = false;
        api = new  YgoApiClient();
    }

    // Permite conectar la lógica del juego con la interfaz gráfica
    public void setListener(BattleListener listener){
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

    public List<Card> getPlayerCards(){
        return playerCards;
    }

    public List<Card> getAiCards(){
        return aiCards;
    }
    public int getPlayerScore(){
        return playerScore;
    }
    public int getAiScore(){
        return aiScore;
    }
    public boolean isPlayerTurn(){
        return playerTurn;
    }
    public boolean isDuelEnded(){
        return duelEnded;
    }

    public void jugarRonda(int indiceCarta, boolean modoAtaque){
        if(duelEnded || !playerTurn){
            return;
        }
        if(indiceCarta < 0 || indiceCarta >= playerCards.size()){
            return;
        }
        Card playerCard = playerCards.remove(indiceCarta);

        // La máquina selecciona una carta al azar
        Random random = new Random();
        int indiceAI = random.nextInt(aiCards.size());
        Card aiCard = aiCards.remove(indiceAI);

        // Calcula el resultado de la ronda
        String winner = determinarGanador(playerCard, aiCard, modoAtaque);

        if (winner.equals("Jugador")) {
            playerScore++;
        }
        else if (winner.equals("Máquina")) {
            aiScore++;
        }

        // Notifica el resultado de la ronda
        if (listener != null) {
            listener.onTurn(
                    playerCard.getName(),
                    aiCard.getName(),
                    winner
            );

            listener.onScoreChanged(playerScore, aiScore);
        }

        // Comprueba si alguien ganó el duelo
        if (playerScore == 2 || aiScore == 2) {
            duelEnded = true;

            if (listener != null) {
                listener.onDuelEnded(
                        playerScore == 2 ? "Jugador" : "Máquina"
                );
            }
        }
        else if (playerCards.isEmpty() || aiCards.isEmpty()) {
            // Si se agotan las cartas, gana quien tenga más puntos
            duelEnded = true;

            String ganador;

            if (playerScore > aiScore) {
                ganador = "Jugador";
            }
            else if (aiScore > playerScore) {
                ganador = "Máquina";
            }
            else {
                ganador = "Empate";
            }

            if (listener != null) {
                listener.onDuelEnded(ganador);
            }
        }
        else {

            playerTurn = false;
        }
    }

    private String determinarGanador(Card playerCard, Card aiCard, boolean modoAtaque) {
        int playerValue;
        int aiValue;

        if (modoAtaque) {
            playerValue = playerCard.getAtk();
            aiValue = aiCard.getAtk();
        }
        else {
            playerValue = playerCard.getDef();
            aiValue = aiCard.getAtk();
        }

        if (playerValue > aiValue) {
            return "Jugador";
        }
        else if (aiValue > playerValue) {
            return "Máquina";
        }
        else {
            return "Empate";
        }
    }
    public void terminarTurno() {
        if (duelEnded || playerTurn) {
            return;
        }
        playerTurn = true;
    }
}
