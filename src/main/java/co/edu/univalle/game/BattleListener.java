package co.edu.univalle.game;

    public interface BattleListener
    {
        // Se ejecuta cuando termina una ronda
        void onTurn(String playerCard, String aiCard, String winner);

        // Se ejecuta cuando cambia el marcador
        void onScoreChanged(int playerScore, int aiScore);

        // Se ejecuta cuando termina el duelo
        void onDuelEnded(String winner);
    }
