package net.demilich.metastone;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardParseException;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.utils.UserHomeMetastone;
import net.demilich.nittygrittymvc.interfaces.INotification;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.demilich.metastone.NoGuiRunSimulation.getHeroCardForClass;
import static net.demilich.metastone.game.logic.GameLogic.logger;

/**
 *
 * @author rubens Classe utilizada para gerir o serviço SOA para testes
 * totalmente observáveis.
 */
public class LeitorMatchsHearthstone {

    public static void main(String args[]) throws Exception {
        //String pathSOA = args[0];
        //String pathLog = args[1];
        String pathSOA = "/home/igor/Área de Trabalho/DeckEvaluation/configSOA";
        String pathLog = "/home/igor/Área de Trabalho/DeckEvaluation/logs";

        System.out.println(pathSOA);
        System.out.println(pathLog);

        while (!finalizarSOA(pathSOA)) {
            //procuro a existencia dos arquivos a serem processados.
            ArrayList<String> mathSOA = buscarAquivos(pathSOA);
            for (String arquivo : mathSOA) {
                System.out.println("Processando arquivo " + arquivo);
                try {
                    if (processarMatch(pathLog, arquivo)) {
                        //remove o arquivo da pasta
                        File remove = new File(arquivo);
                        remove.delete();
                        System.gc();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("Waiting...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Irá chamar a classe que faz processará o arquivo do confronto.
     *
     * @param pathLog Stringo do caminho onde será salvo o resultado
     * @param arquivo String com o nome do arquivo que contém a configuração
     * @return True se processado corretamente
     */
    private static boolean processarMatch(String pathLog, String arquivo) {
        try {
            // ensure that the user home metastone dir exists
            Files.createDirectories(Paths.get(UserHomeMetastone.getPath()));
        } catch (IOException e) {
            logger.error("Trouble creating " +  Paths.get(UserHomeMetastone.getPath()));
            e.printStackTrace();
        }
        //ler o arquivo e pegar a linha com dados
        String config = getLinha(arquivo);
        String[] itens = config.split("#");
        String[] deck1 = itens[0].split(";");
        String[] deck2 = itens[1].split(";");
       // System.out.println(itens[0].split(";")[0]);



        INotification<GameNotification> notification = new INotification<GameNotification>() {
            @Override
            public Object getBody() {
                GameConfig gameConfig = new GameConfig();
                // gameConfig.setNumberOfGames(Integer.valueOf(args[0]));
                gameConfig.setNumberOfGames(1);
                DeckProxy deckProxy = new DeckProxy();



                CardCatalogue cardCatalogue = new CardCatalogue();

                //deckProxy.saveActiveDeck();
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
                //novo = deckProxy.getActiveDeck();
                PlayerConfig playerConfig1 = new PlayerConfig();
                PlayerConfig playerConfig2 = new PlayerConfig();
                playerConfig1.setDeck(deckProxy.create_deck_from_string(deck1));
                playerConfig2.setDeck(deckProxy.create_deck_from_string(deck2));
                playerConfig1.setName("Igor");
                playerConfig2.setName("Igor");
                playerConfig1.setHeroCard(getHeroCardForClass(HeroClass.DRUID));
                playerConfig2.setHeroCard(getHeroCardForClass(HeroClass.DRUID));
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
        //NoGuiSimulation noGuiSimulation = new NoGuiSimulation();
        facade.sendNotification(GameNotification.NOGUISIMULATE_GAMES, notification.getBody());




        //incluir o processador das batalhas!
        //fazer com que ele receba apenas deck1, deck2 e o caminho para salvar
        //como parametro
        //RoundRobinClusterLeve control = new RoundRobinClusterLeve();
        /*try {
            return control.run(itens[0].trim(),
                    itens[1].trim(),
                    pathLog);
        } catch (Exception ex) {
            Logger.getLogger(LeitorMatchsHearthStone.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return false;
    }

    /**
     * Retorna todos os arquivos da pasta SOA para serem processados
     *
     * @param pathSOA String com o caminho da pasta gerenciada
     * @return Lista de caminhos para os arquivos que serão processados.
     */
    private static ArrayList<String> buscarAquivos(String pathSOA) {
        ArrayList<String> arquivos = new ArrayList<>();
        File diretorio = new File(pathSOA);
        buscarParcial(diretorio, ".txt", arquivos);

        return arquivos;
    }

    /**
     * Irá verificar se existe o arquivo com o nome exit na pasta SOA.
     *
     * @param pathSOA Caminho da pasta de controle de serviços.
     * @return true se encontrar o arquivo exit
     */
    private static boolean finalizarSOA(String pathSOA) {
        ArrayList<String> arquivos = new ArrayList<>();
        File diretorio = new File(pathSOA);
        buscarParcial(diretorio, ".txt", arquivos);

        if (arquivos.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Realiza a busca (recursiva) de todos os arquivos com o nome informado
     *
     * @param arquivo = File contendo o caminho que se deseja procurar.
     * @param palavra = String com o nome que se deseja buscar
     * @param lista = ArrayList<String> que retornará os arquivos encontrados
     * @return Lista de Strings com todos os caminhos absolutos dos arquivos com
     * o nome encontrado
     */
    public static ArrayList<String> buscar(File arquivo, String palavra, ArrayList<String> lista) {
        if (arquivo.isDirectory()) {
            File[] subPastas = arquivo.listFiles();
            for (int i = 0; i < subPastas.length; i++) {
                lista = buscar(subPastas[i], palavra, lista);
                if (arquivo.getName().equalsIgnoreCase(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                } else if (arquivo.getName().indexOf(palavra) > -1) {
                    lista.add(arquivo.getAbsolutePath());
                }
            }
        } else if (arquivo.getName().equalsIgnoreCase(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        }
        //else if (arquivo.getName().indexOf(palavra) > -1) lista.add(arquivo.getAbsolutePath());
        return lista;
    }

    public static ArrayList<String> buscarParcial(File arquivo, String palavra, ArrayList<String> lista) {
        if (arquivo.isDirectory()) {
            File[] subPastas = arquivo.listFiles();
            for (int i = 0; i < subPastas.length; i++) {
                lista = buscarParcial(subPastas[i], palavra, lista);
                if (arquivo.getName().equalsIgnoreCase(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                } else if (arquivo.getName().contains(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                }
            }
        } else if (arquivo.getName().equalsIgnoreCase(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        } else if (arquivo.getName().contains(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        }
        return lista;
    }

    private static String getLinha(String arquivo) {
        File file = new File(arquivo);
        String linha = "";
        try {
            FileReader arq = new FileReader(file);
            java.io.BufferedReader learArq = new BufferedReader(arq);
            linha = learArq.readLine();

            arq.close();
        } catch (Exception e) {
            System.err.printf("Erro na leitura da linha de configuração");
            System.out.println(e.toString());
        }
        return linha;
    }


    private void gravarLog(ArrayList<String> log, String tupleAi1, String tupleAi2, String IDMatch, Integer Generation, String pathLog) throws IOException {
        if (!pathLog.endsWith("/")) {
            pathLog += "/";
        }
        String nameArquivo = pathLog + "Deck_Eval" + tupleAi1 + "_" + tupleAi2 + "_" + IDMatch + "_" + Generation + ".txt";
        File arqLog = new File(nameArquivo);
        if (!arqLog.exists()) {
            arqLog.createNewFile();
        }
        //abre o arquivo e grava o log
        try {
            FileWriter arq = new FileWriter(arqLog, false);
            PrintWriter gravarArq = new PrintWriter(arq);
            for (String l : log) {
                gravarArq.println(l);
            }

            gravarArq.flush();
            gravarArq.close();
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }













}
