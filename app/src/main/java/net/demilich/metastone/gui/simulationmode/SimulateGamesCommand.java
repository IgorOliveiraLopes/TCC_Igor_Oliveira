package net.demilich.metastone.gui.simulationmode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.demilich.nittygrittymvc.Notification;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.utils.Tuple;

public class SimulateGamesCommand extends SimpleCommand<GameNotification> {


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
		/*notification = new INotification<GameNotification>() {
			@Override
			public Object getBody() {
				GameConfig gameConfig = new GameConfig();
				gameConfig.setNumberOfGames(3);



				PlayerConfig playerConfig1 = new PlayerConfig();
				PlayerConfig playerConfig2 = new PlayerConfig();
				playerConfig1.setDeck(new Deck(HeroClass.WARRIOR));
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
		};*/

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
				System.out.println(gameConfig.getNumberOfGames());
				for (int i = 0; i < gameConfig.getNumberOfGames(); i++) {
					// System.out.println(gameConfig.getDeckFormat().print());
					PlayGameTask task = new PlayGameTask(gameConfig);
					Future<Void> future = executor.submit(task);
					futures.add(future);
					System.out.println("Entrou for");
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
				getFacade().sendNotification(GameNotification.SIMULATION_RESULT, result);
				logger.info("Simulation finished");
				System.out.println(result.getPlayer1Stats().toString());

			}
		});
		t.setDaemon(true);
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
			System.out.println("Perdeu");

		}
		synchronized (result) {
			result.getPlayer1Stats().merge(context.getPlayer1().getStatistics());
			result.getPlayer2Stats().merge(context.getPlayer2().getStatistics());
			logger.info(result.toString());
			System.out.println();

		}
	}

}
