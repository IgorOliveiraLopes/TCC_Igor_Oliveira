package net.demilich.metastone;

import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardParseException;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
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

public class MetaStoneUtils {

    String[] string_deck_one;
    String[] string_deck_two;
    String file_name;
    String file_path;

    Deck deck_one;
    Deck deck_two;

    DeckProxy deckProxy = new DeckProxy();


    Deck novo = new Deck(HeroClass.ANY);
    CardCatalogue cardCatalogue = new CardCatalogue();
    HashMap<Integer, String> lista_cartas = new HashMap<>();


    Integer i = 0;

    /*
    */
    public void create_deck_hash(String file_name,String file_path) {

        for (Card card : deckProxy.getCards(HeroClass.DRUID)){

            this.lista_cartas.put(i,card.getCardId());
            i++;
        }

        //DeckProxy.create_decks_from_file("Teste.txt");

    }
    public Deck create_deck_one(){
        deckProxy.clearActiveDeck();
        return  deckProxy.Create_deck(string_deck_one);
    }

    public Deck create_deck_two(){
        deckProxy.clearActiveDeck();

        return deckProxy.Create_deck(string_deck_one);
    }

    public MetaStoneUtils(String[] deck_one, String[] deck_two) {
        this.string_deck_one = deck_one;
        this.string_deck_two = deck_two;


    }

    public String[] getDeck_one() {
        return string_deck_one;
    }

    public void setDeck_one(String[] deck_one) {
        this.string_deck_one = deck_one;
    }

    public String[] getDeck_two() {
        return string_deck_two;
    }

    public void setDeck_two(String[] deck_two) {
        this.string_deck_two = deck_two;
    }


}
