package net.demilich.metastone;

import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.simulationmode.NoGuiSimulation;
import net.demilich.metastone.utils.UserHomeMetastone;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.nittygrittymvc.interfaces.INotifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static javafx.application.Application.launch;
import static net.demilich.metastone.game.logic.GameLogic.logger;

public class NoGuiRunSimulation {
    public static void main(String[] args) {
        //DevCardTools.formatJsons();

        try {
            // ensure that the user home metastone dir exists
            Files.createDirectories(Paths.get(UserHomeMetastone.getPath()));
        } catch (IOException e) {
            logger.error("Trouble creating " +  Paths.get(UserHomeMetastone.getPath()));
            e.printStackTrace();
        }
        NoGuiSimulation simulation = new NoGuiSimulation();
        INotification<GameNotification> notification = new INotification<GameNotification>() {
            @Override
            public Object getBody() {
                GameConfig gameConfig = new GameConfig();
                gameConfig.setNumberOfGames(3);



            PlayerConfig playerConfig1 = new PlayerConfig();
            PlayerConfig playerConfig2 = new PlayerConfig();
            playerConfig1.setDeck(new Deck(HeroClass.WARRIOR));
            playerConfig2.setDeck(new Deck(HeroClass.DRUID));
            playerConfig1.setName("Igor");
            playerConfig2.setName("Igor");
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

        ApplicationFacade facade = (ApplicationFacade) ApplicationFacade.getInstance();
        facade.startUp();


       // NotificationProxy.sendNotification(GameNotification.COMMIT_SIMULATIONMODE_CONFIG,notification);
        NoGuiSimulation noGuiSimulation = new NoGuiSimulation();
        facade.sendNotification(GameNotification.NOGUISIMULATE_GAMES, notification.getBody());

        //noGuiSimulation.execute(notification);
    }
}
