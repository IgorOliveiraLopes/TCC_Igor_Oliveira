package net.demilich.metastone;

import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardParseException;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.utils.UserHomeMetastone;
import net.demilich.nittygrittymvc.interfaces.INotification;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static javafx.application.Application.launch;
import static net.demilich.metastone.game.logic.GameLogic.logger;

public class NoGuiRunSimulation {
    protected static HeroCard getHeroCardForClass(HeroClass heroClass) {
        for (Card card : CardCatalogue.getHeroes()) {
            HeroCard heroCard = (HeroCard) card;
            if (heroCard.getHeroClass() == heroClass) {
                return heroCard;
            }
        }
        return null;
    }



    public static void main(String[] args) {

        try {
            // ensure that the user home metastone dir exists
            Files.createDirectories(Paths.get(UserHomeMetastone.getPath()));
        } catch (IOException e) {
            logger.error("Trouble creating " +  Paths.get(UserHomeMetastone.getPath()));
            e.printStackTrace();
        }
        //NoGuiSimulation simulation = new NoGuiSimulation();
        INotification<GameNotification> notification = new INotification<GameNotification>() {
            @Override
            public Object getBody() {
                GameConfig gameConfig = new GameConfig();
               // gameConfig.setNumberOfGames(Integer.valueOf(args[0]));
                gameConfig.setNumberOfGames(1);


                Integer i =  0;
/*                for (Card card : deckProxy.getCards(HeroClass.DRUID)){

                     lista_cartas.put(i,card.getCardId());

                    i++;
                }

                System.out.println(cardCatalogue.getCardById(lista_cartas.get(1)).toString());

                deckProxy.saveActiveDeck();
                try {
                    cardCatalogue.loadLocalCards();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (CardParseException e) {
                    e.printStackTrace();
                }
                //deckProxy.addCardToDeck(cardCatalogue.getCardById("100"));
                novo = deckProxy.getActiveDeck();
                PlayerConfig playerConfig1 = new PlayerConfig();
                PlayerConfig playerConfig2 = new PlayerConfig();
                playerConfig1.setDeck(deck);
                playerConfig2.setDeck(deck);
                playerConfig1.setName("Igor");
                playerConfig2.setName("Igor");
                playerConfig1.setHeroCard(getHeroCardForClass(HeroClass.DRUID));
                playerConfig2.setHeroCard(getHeroCardForClass(HeroClass.DRUID));
                playerConfig1.setBehaviour(new GameStateValueBehaviour());
                playerConfig2.setBehaviour(new GameStateValueBehaviour());

            gameConfig.setDeckFormat(new DeckFormat());
            gameConfig.setPlayerConfig1(playerConfig1);
            gameConfig.setPlayerConfig2(playerConfig2);*/
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
        //NoGuiSimulation noGuiSimulation = new NoGuiSimulation();
        facade.sendNotification(GameNotification.NOGUISIMULATE_GAMES, notification.getBody());

        //noGuiSimulation.execute(notification);

    }
}
