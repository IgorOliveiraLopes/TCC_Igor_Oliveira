package net.demilich.metastone.gui.simulationmode;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.mcts.MonteCarloTreeSearch;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardSet;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.cards.desc.HeroCardDesc;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.game.statistics.Statistic;
import net.demilich.metastone.gui.cards.CardProxy;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.utils.Tuple;
import net.demilich.nittygrittymvc.Notification;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.CacheRequest;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static net.demilich.metastone.game.logic.GameLogic.logger;

public class NoGuiSimulation extends SimpleCommand<GameNotification> {



    private class PlayGameTask implements Callable<Void> {

        private final GameConfig gameConfig;

        public PlayGameTask(GameConfig gameConfig) {
            this.gameConfig = gameConfig;
        }

        @Override
        public Void call() throws Exception {
            PlayerConfig playerConfig1 = gameConfig.getPlayerConfig1();
            PlayerConfig playerConfig2 = gameConfig.getPlayerConfig2();

            Player player1 = new Player(playerConfig1);
            Player player2 = new Player(playerConfig2);

            DeckFormat deckFormat = gameConfig.getDeckFormat();

            GameContext newGame = new GameContext(player1, player2, new GameLogic(), deckFormat);
            newGame.play();

            onGameComplete(gameConfig, newGame);
            newGame.dispose();

            return null;
        }

    }

    private static Logger logger = LoggerFactory.getLogger(SimulateGamesCommand.class);
    private int gamesCompleted;
    private long lastUpdate;

    private SimulationResult result;

    protected static HeroCard getHeroCardForClass(HeroClass heroClass) {
        for (Card card : CardCatalogue.getHeroes()) {
            HeroCard heroCard = (HeroCard) card;
            if (heroCard.getHeroClass() == heroClass) {
                return heroCard;
            }
        }
        return null;
    }


    @Override
    public void execute(INotification<GameNotification> notification) {

        final GameConfig gameConfig = (GameConfig) notification.getBody();
        result = new SimulationResult(gameConfig);
        gamesCompleted = 0;

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                int cores = Runtime.getRuntime().availableProcessors();
                logger.info("Starting simulation on " + cores + " cores");
                ExecutorService executor = Executors.newFixedThreadPool(cores);
                // ExecutorService executor =
                // Executors.newSingleThreadExecutor();

                List<Future<Void>> futures = new ArrayList<Future<Void>>();
                // send initial status update
                Tuple<Integer, Integer> progress = new Tuple<>(0, gameConfig.getNumberOfGames());
                getFacade().sendNotification(GameNotification.SIMULATION_PROGRESS_UPDATE, progress);

                // queue up all games as tasks
                lastUpdate = System.currentTimeMillis();
                //System.out.println(gameConfig.getNumberOfGames());
                for (int i = 0; i < gameConfig.getNumberOfGames(); i++) {
                    PlayGameTask task = new PlayGameTask(gameConfig);
                    Future<Void> future = executor.submit(task);
                    futures.add(future);
                   // System.out.println("Entrou for");
                }

                executor.shutdown();
                boolean completed = false;
                while (!completed) {
                    completed = true;
                    for (Future<Void> future : futures) {
                        if (!future.isDone()) {
                            completed = false;
                            continue;
                        }
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    }
                    futures.removeIf(future -> future.isDone());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                result.calculateMetaStatistics();
                getFacade().sendNotification(GameNotification.NOGUI_RESULT, result);
                /*logger.info("Simulation finished");


                logger.info("Resultados da simulacao para o jogador 1: \n" + result.getPlayer1Stats().toString());
                logger.info("Resultados da simulacao para o jogador 2: \n" + result.getPlayer2Stats().toString());

                */

            }
        });
        t.setDaemon(false);
        t.start();
    }

    String pathLog = "/home/igor/Documentos/logs";
    private void onGameComplete(GameConfig gameConfig, GameContext context) {
        long timeStamp = System.currentTimeMillis();
        gamesCompleted++;
        if (timeStamp - lastUpdate > 100) {
            lastUpdate = timeStamp;
            Tuple<Integer, Integer> progress = new Tuple<>(gamesCompleted, gameConfig.getNumberOfGames());
            Notification<GameNotification> updateNotification = new Notification<>(GameNotification.SIMULATION_PROGRESS_UPDATE, progress);
            getFacade().notifyObservers(updateNotification);
            //System.out.println("Perdeu");

        }
        synchronized (result) {
            System.out.println("Terminou a simulacao " + gamesCompleted );

            result.getPlayer1Stats().merge(context.getPlayer1().getStatistics());
            result.getPlayer2Stats().merge(context.getPlayer2().getStatistics());

            ArrayList<String> log = new ArrayList<>();
            log.add("Tupla A1 = " + 1);

            log.add("Winner " + Integer.toString(result.getPlayer1Stats().getLong(Statistic.GAMES_WON) > result.getPlayer2Stats().getLong(Statistic.GAMES_WON) ? 0 : 1 ));
            log.add("Game Over");
            //String stMatch = Integer.toString(IDMatch) + "" + Integer.toString(iMap);


            try {
                gravarLog(log, context.getPlayer1().getDeck().toString(), context.getPlayer2().toString(), pathLog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


    private void gravarLog(  ArrayList<String> log,String tupleAi1, String tupleAi2, String pathLog) throws IOException {
        if (!pathLog.endsWith("/")) {
            pathLog += "/";
        }
        String nameArquivo = pathLog + "Deck_Eval" + "_" + "_" + ".txt";
        File arqLog = new File(nameArquivo);
        if (!arqLog.exists()) {
            arqLog.createNewFile();
        }
        //abre o arquivo e grava o log

        FileWriter arq = new FileWriter(arqLog, false);
        PrintWriter gravarArq = new PrintWriter(arq);
        for (String l : log) {
            gravarArq.println(l);

        }
        gravarArq.flush();
        gravarArq.close();
        arq.close();
    }
}
