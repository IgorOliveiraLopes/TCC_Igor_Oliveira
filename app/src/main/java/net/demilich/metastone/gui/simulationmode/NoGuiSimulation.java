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
import net.demilich.metastone.utils.Tuple;
import net.demilich.nittygrittymvc.Notification;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.CacheRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static net.demilich.metastone.game.logic.GameLogic.logger;

public class NoGuiSimulation extends SimpleCommand<GameNotification> {

//    private static Notification notification;
//    private static int gamesCompleted;
//    private static SimulationResult result;
//    private static GameConfig gameConfig = new GameConfig();
//    @Override
//    public void execute(INotification<GameNotification> iNotification) {
//
//    }
//
//    private static class PlayGameTask implements Callable<Void> {
//
//
//
//        public PlayGameTask() {
//
//            gameConfig.setNumberOfGames(3);
//
//
//
//            PlayerConfig playerConfig1 = new PlayerConfig();
//            PlayerConfig playerConfig2 = new PlayerConfig();
//            playerConfig1.setDeck(new Deck(HeroClass.WARRIOR));
//            playerConfig2.setDeck(new Deck(HeroClass.WARRIOR));
//            playerConfig1.setName("Igor");
//            playerConfig2.setName("Igor");
//            playerConfig1.setBehaviour(new GameStateValueBehaviour());
//            playerConfig2.setBehaviour(new GameStateValueBehaviour());
//
//
//
//            gameConfig.setDeckFormat(new DeckFormat());
//            gameConfig.setPlayerConfig1(playerConfig1);
//            gameConfig.setPlayerConfig2(playerConfig2);
//
//
//
//
//        }
//
//
//        @Override
//        public Void call() throws Exception {
//            gameConfig.setNumberOfGames(5);
//            //DeckFormat deckFormat = new DeckFormat(CardSet.ONE_NIGHT_IN_KARAZHAN);
//            // deckFormat.setName();
//            System.out.println("Call");
//
//            PlayerConfig a1 = new PlayerConfig();
//            PlayerConfig a2 = new PlayerConfig();
//            a1.setDeck(new Deck(HeroClass.DRUID));
//            a2.setDeck(new Deck(HeroClass.DRUID));
//            a1.setName("Igor");
//            a2.setName("Igor2");
//            a1.setBehaviour(new GameStateValueBehaviour());
//            a2.setBehaviour(new MonteCarloTreeSearch());
//
//
//
//            gameConfig.setDeckFormat(new DeckFormat());
//            gameConfig.setPlayerConfig1(a1);
//            gameConfig.setPlayerConfig2(a2);
//
//            PlayerConfig playerConfig1 = gameConfig.getPlayerConfig1();
//            PlayerConfig playerConfig2 = gameConfig.getPlayerConfig2();
//
//            Player player1 = new Player(playerConfig1);
//            Player player2 = new Player(playerConfig2);
//
//            DeckFormat deckFormat = gameConfig.getDeckFormat();
//
//            GameContext newGame = new GameContext(player1, player2, new GameLogic(), deckFormat);
//
//            newGame.play();
//
//            onGameComplete(gameConfig, newGame);
//            newGame.dispose();
//
//            return null;
//        }
//
//    }
//
//
//    private static Logger logger = LoggerFactory.getLogger(SimulateGamesCommand.class);
//    private static long lastUpdate;
//    public static void main(String[] args) {
//
//        File file = new File("Directory1");
//        if (!file.exists()) {
//            if (file.mkdir()) {
//                System.out.println("Directory is created!");
//            } else {
//                System.out.println("Failed to create directory!");
//            }
//        }else{
//            System.out.println("Directory is created!");
//        }
//
//
//       // final GameConfig gameConfig = null;
//        //gameConfig = (GameConfig) notification.getBody();
//        gameConfig.setNumberOfGames(5);
//        //DeckFormat deckFormat = new DeckFormat(CardSet.ONE_NIGHT_IN_KARAZHAN);
//        // deckFormat.setName();
//
//
//        PlayerConfig playerConfig1 = new PlayerConfig();
//        PlayerConfig playerConfig2 = new PlayerConfig();
//        playerConfig1.setDeck(new Deck(HeroClass.WARRIOR));
//        playerConfig2.setDeck(new Deck(HeroClass.DRUID));
//        playerConfig1.setName("Igor");
//        playerConfig2.setName("Igor2");
//        playerConfig1.setBehaviour(new GameStateValueBehaviour());
//        playerConfig2.setBehaviour(new MonteCarloTreeSearch());
//
//
//
//        gameConfig.setDeckFormat(new DeckFormat());
//        gameConfig.setPlayerConfig1(playerConfig1);
//        gameConfig.setPlayerConfig2(playerConfig2);
//
//        result = new SimulationResult(gameConfig);
//
//
//
//        Thread t = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//               int cores = Runtime.getRuntime().availableProcessors();
//                logger.info("Starting simulation on " + cores + " cores");
//                ExecutorService executor = Executors.newFixedThreadPool(cores);
//                // ExecutorService executor =
//                // Executors.newSingleThreadExecutor();
//                System.out.println("Entrou Run");
//
//                List<Future<Void>> futures = new ArrayList<Future<Void>>();
//                // send initial status update
//                //Tuple<Integer, Integer> progress = new Tuple<>(0, gameConfig.getNumberOfGames());
//                //getFacade().sendNotification(GameNotification.SIMULATION_PROGRESS_UPDATE, progress);
//
//                // queue up all games as tasks
//                lastUpdate = System.currentTimeMillis();
//                for (int i = 0; i < 5/*gameConfig.getNumberOfGames()*/; i++) {
//                    System.out.println("Entrou For");
//                    WriteStatsInFile.PlayGameTask task = new PlayGameTask();
//                    Future<Void> future = executor.submit(task);
//                    futures.add(future);
//                }
//
//                executor.shutdown();
//                boolean completed = false;
//                while (!completed) {
//                    System.out.println("Entrou while");
//                    completed = true;
//                    for (Future<Void> future : futures) {
//                        if (!future.isDone()) {
//                            completed = false;
//                            continue;
//                        }
//                        try {
//                            future.get();
//                        } catch (InterruptedException | ExecutionException e) {
//                            logger.error(ExceptionUtils.getStackTrace(e));
//                            e.printStackTrace();
//                            System.exit(-1);
//                        }
//                    }
//                    futures.removeIf(future -> future.isDone());
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                result.calculateMetaStatistics();
//                //getFacade().sendNotification(GameNotification.SIMULATION_RESULT, result);
//                logger.info("Simulation finished");
//                logger.info("Resultado"+result.getPlayer1Stats().toString());
//               logger.info("Resultado"+result.getPlayer2Stats().toString());
//
//
//            }
//        });
//        t.setDaemon(false);
//        t.start();
//    }
//
//
//    private static void onGameComplete(GameConfig gameConfig, GameContext context) {
//        long timeStamp = System.currentTimeMillis();
//        gamesCompleted++;
//        if (timeStamp - lastUpdate > 100) {
//            lastUpdate = timeStamp;
//            Tuple<Integer, Integer> progress = new Tuple<>(gamesCompleted, gameConfig.getNumberOfGames());
//            Notification<GameNotification> updateNotification = new Notification<>(GameNotification.SIMULATION_PROGRESS_UPDATE, progress);
//
//            //getFacade().notifyObservers(updateNotification);
//        }
//        synchronized (result) {
//            System.out.println("Perdeu "+result.getNumberOfGames());
//
//            result.getPlayer1Stats().merge(context.getPlayer1().getStatistics());
//            result.getPlayer2Stats().merge(context.getPlayer2().getStatistics());
//        }
//    }


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
        notification = new INotification<GameNotification>() {
            @Override
            public Object getBody() {
                GameConfig gameConfig = new GameConfig();
                gameConfig.setNumberOfGames(10);


                PlayerConfig playerConfig1 = new PlayerConfig();
                PlayerConfig playerConfig2 = new PlayerConfig();
                playerConfig1.setDeck(new Deck(HeroClass.DRUID));
                playerConfig2.setDeck(new Deck(HeroClass.WARRIOR));
                playerConfig1.setName("Igor");
                playerConfig2.setName("Igor");
                playerConfig1.setHeroCard(getHeroCardForClass(HeroClass.WARRIOR));
                playerConfig2.setHeroCard(getHeroCardForClass(HeroClass.WARRIOR));

                playerConfig1.setBehaviour(new GameStateValueBehaviour());
                playerConfig2.setBehaviour(new GameStateValueBehaviour());


                gameConfig.setDeckFormat(new DeckFormat());
                gameConfig.setPlayerConfig1(playerConfig1);
                gameConfig.setPlayerConfig2(playerConfig2);
                return gameConfig;
            }

            @Override
            public GameNotification getId() {
                return null;
            }
        };

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
                    System.out.println("Executando a simulacao " + i);
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
                logger.info("Simulation finished");


                logger.info("Resultados da simulacao para o jogador 1: \n" + result.getPlayer1Stats().toString());
                logger.info("Resultados da simulacao para o jogador 2: \n" + result.getPlayer2Stats().toString());



            }
        });
        t.setDaemon(false);
        t.start();
    }

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
            result.getPlayer1Stats().merge(context.getPlayer1().getStatistics());
            result.getPlayer2Stats().merge(context.getPlayer2().getStatistics());

        }

    }
}
