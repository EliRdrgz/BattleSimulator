package com.hooligansofjava;

import com.google.gson.Gson;
import jdk.swing.interop.SwingInterOpUtils;
import net.datafaker.Faker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Game {
    final ArrayList<Character> partyPlayer1 = new ArrayList<>();
    final ArrayList<Character> partyPlayer2 = new ArrayList<>();


    public Game() {

    }

    //generar 2 arrays (player 1 y player 2) con size n
    //llenar esos arrays de random characters
    public void randomParty() {
        Faker fc = new Faker();

        int index = generateRandomNumber(1, 100);
        for (int i = 0; i < index; i++) {
            int random1 = generateRandomNumber(0, 1);
            int random2 = generateRandomNumber(0, 1);
            partyPlayer1.add(createRandomCharacter(TypeOfCharacter.values()[random1], fc));
            partyPlayer2.add(createRandomCharacter(TypeOfCharacter.values()[random2], fc));
        }

    }

    public String generateJson() {
        Gson gson = new Gson();
        ArrayList<Wizard> player1Wizards;
        player1Wizards = partyPlayer1.stream().filter(x -> x instanceof Wizard).map(x -> (Wizard) x).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        ArrayList<Warrior> player1Warriors;
        player1Warriors = partyPlayer1.stream().filter(x -> x instanceof Warrior).map(x -> (Warrior) x).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        ArrayList<Wizard> player2Wizards;
        player2Wizards = partyPlayer2.stream().filter(x -> x instanceof Wizard).map(x -> (Wizard) x).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        ArrayList<Warrior> player2Warriors;
        player2Warriors = partyPlayer2.stream().filter(x -> x instanceof Warrior).map(x -> (Warrior) x).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return "%s Warriors::%s player2::%sWarriors::%s".formatted(gson.toJson(player1Wizards), gson.toJson(player1Warriors), gson.toJson(player2Wizards), gson.toJson(player2Warriors));

    }

    public void parseJson(String JSONText) {
        Gson gson = new Gson();
        String[] player = JSONText.split("player2::");
        String[] player1 = player[0].split("Warriors::");
        String[] player2 = player[1].split("Warriors::");

        String player1Wizards = player1[0];
        String player1Warriors = player1[1];
        String player2Wizards = player2[0];
        String player2Warriors = player2[1];

        Warrior[] loadedWarriors = gson.fromJson(player1Warriors, Warrior[].class);
        Wizard[] loadedWizards = gson.fromJson(player1Wizards, Wizard[].class);
        Warrior[] loadedWarriors2 = gson.fromJson(player2Warriors, Warrior[].class);
        Wizard[] loadedWizards2 = gson.fromJson(player2Wizards, Wizard[].class);

        partyPlayer1.addAll(Arrays.asList(loadedWarriors));
        partyPlayer1.addAll(Arrays.asList(loadedWizards));
        partyPlayer2.addAll(Arrays.asList(loadedWarriors2));
        partyPlayer2.addAll(Arrays.asList(loadedWizards2));


    }

    public Game startConsole() {
        System.out.println("Welcome to the game of Hooligans of JAVA: ");
        Faker faker = new Faker();
        Scanner sc = new Scanner(System.in);
        int playerId = 0;
        int players = 0;
        boolean readPreviousParty = ConsoleQuery.queryToConsole(sc, "Read previous party?");
        if (readPreviousParty) {
            try {
                System.out.println("reading file");
                parseJson(FileReadAndWrite.readFile());
                players = 2;
            } catch (IOException e) {
                System.out.println("error reading file, Initiating normal game...");
            }
        }
        while (players != 2) {
            int player = 0;
            if (playerId == 0) {
                player = ConsoleQuery.queryToConsole(sc, "Select a player to start the set up:", new String[]{"Player 1", "Player 2"}, 1, 2);
            } else if (playerId == 1) {
                player = 2;
            } else if (playerId == 2) {
                player = 1;
            }
            players++;
            playerId = player;
            System.out.println("Hello Player " + player);
            System.out.println("Now, you have to select the number of warriors and wizards.");
            int warriorCount = ConsoleQuery.queryToConsole(sc, "Now, you have to select the number of warriors.", 1, 10);
            generateCharacterLoop(faker, sc, TypeOfCharacter.WARRIOR, player, warriorCount);
            int wizardCount = ConsoleQuery.queryToConsole(sc, "Now, you have to select the number of wizards.", 1, 10);
            generateCharacterLoop(faker, sc, TypeOfCharacter.WIZARD, player, wizardCount);
        }
        System.out.println("end reading data from terminal");

        return null;
    }

    public void startCustomParty() {
        Faker faker = new Faker();
        Scanner sc = new Scanner(System.in);
        int playerId = 0;
        int players = 0;
        while (players != 2) {
            int player = 0;
            if (playerId == 0) {
                player = ConsoleQuery.queryToConsole(sc, "Select a player to start the set up:", new String[]{"Player 1", "Player 2"}, 1, 2);
            } else if (playerId == 1) {
                player = 2;
            } else if (playerId == 2) {
                player = 1;
            }
            players++;
            playerId = player;
            System.out.println("Hello Player " + player);
            System.out.println("Now, you have to select the number of warriors and wizards.");
            int warriorCount = ConsoleQuery.queryToConsole(sc, "Now, you have to select the number of warriors.", 1, 10);
            generateCharacterLoop(faker, sc, TypeOfCharacter.WARRIOR, player, warriorCount);
            int wizardCount = ConsoleQuery.queryToConsole(sc, "Now, you have to select the number of wizards.", 1, 10);
            generateCharacterLoop(faker, sc, TypeOfCharacter.WIZARD, player, wizardCount);
        }
    }

    public void playLastParty() {
        try {
            System.out.println("reading file");
            parseJson(FileReadAndWrite.readFile());
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }


    private void generateCharacterLoop(Faker faker, Scanner sc, TypeOfCharacter type, int player, int count) {
        for (int i = 0; i < count; i++) {
            Character newCharacter;
            boolean customCharacter = ConsoleQuery.queryToConsole(sc, "Do you want to create a customized character? (Y/N)");
            if (customCharacter) {
                newCharacter = createCustomizedCharacter(sc, type);
            } else {
                newCharacter = createRandomCharacter(type, faker);
            }
            System.out.println(newCharacter);
            if (player == 1) {
                this.partyPlayer1.add(newCharacter);
            } else {
                this.partyPlayer2.add(newCharacter);
            }
        }
    }

    public static boolean checkValidNumber(String characterNumber) {
        int number;
        if (characterNumber == null) {
            return false;
        }
        try {
            number = Integer.parseInt(characterNumber);

        } catch (NumberFormatException e) {
            return false;
        }
        return number >= 0 && number <= 50;
    }

    private static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min + 1) + min);
    }

    private static Character createRandomCharacter(TypeOfCharacter type, Faker faker) {
        return CharacterFactory.getCharacter(type, generateRandomNumber(1, 50), faker.name().fullName(), generateRandomNumber(50, 100), generateRandomNumber(10, 100), generateRandomNumber(10, 100));
    }

    private static Character createCharacter(TypeOfCharacter type, String name, int hp, int classFirstAttribute, int classSecondAttribute) {
        // classFirstAttribute and classSecondAttribute corresponds to stamina and strength for Warrior and mana and intelligence for Wizard
        return CharacterFactory.getCharacter(type, generateRandomNumber(1, 50), name, hp, classFirstAttribute, classSecondAttribute);
    }

    private static Character createCustomizedCharacter(Scanner sc, TypeOfCharacter type) {
        String name;
        int health;
        Integer firstAttribute = null;
        Integer secondAttribute = null;

        switch (type) {
            case WARRIOR -> {
                firstAttribute = ConsoleQuery.queryToConsole(sc, "Define how much stamina do you want to set - (Choose a number between 10 - 50)", TypeOfCharacter.WARRIOR.firstParamMin, TypeOfCharacter.WARRIOR.firstParamMax);
                secondAttribute = ConsoleQuery.queryToConsole(sc, "Define how much strength do you want to set - (Choose a number between 10 - 50)", TypeOfCharacter.WARRIOR.secondParamMin, TypeOfCharacter.WARRIOR.secondParamMax);
            }
            case WIZARD -> {
                firstAttribute = ConsoleQuery.queryToConsole(sc, "Define how much mana do you want to set - (Choose a number between 10 - 50)", TypeOfCharacter.WIZARD.firstParamMin, TypeOfCharacter.WIZARD.secondParamMax);
                secondAttribute = ConsoleQuery.queryToConsole(sc, "Define how much intelligence do you want to set - (Choose a number between 10 - 50)", TypeOfCharacter.WIZARD.secondParamMin, TypeOfCharacter.WIZARD.secondParamMax);
            }
        }
        health = ConsoleQuery.queryToConsole(sc, " define ho much health do you want to set - (Choose a number between 1 - 100)", 1, 100);
        name = ConsoleQuery.queryToConsoleText(sc, "Finally, set a funny name for you Hero!");
        System.out.println("Finally, set a funny name for you Hero!");
        return createCharacter(type, name, health, firstAttribute, secondAttribute);
    }

    public void startGame() {
        System.out.println("The game has started!");

        while (getAliveCharacters(partyPlayer1).size() > 0 && getAliveCharacters(partyPlayer2).size() > 0) {
            Character player1 = getAliveCharacters(partyPlayer1).get(0);
            Character player2 = getAliveCharacters(partyPlayer2).get(0);
            System.out.println("player1 " + player1.name + " (" + player1.getHp() + " ) Attacks " + "--> player2 " + player2.name + " (" + player2.getHp() + ")");
            System.out.println("player2 " + player2.name + " Attacks --> player1 " + player1.name);

            if (player1 instanceof Warrior) {
                player2.receiveAttack(((Warrior) player1).attack());
            }
            if (player1 instanceof Wizard) {
                player2.receiveAttack(((Wizard) player1).attack());
            }
            if (player2 instanceof Warrior) {
                player1.receiveAttack(((Warrior) player2).attack());
            }
            if (player2 instanceof Wizard) {
                player1.receiveAttack(((Wizard) player2).attack());
            }
            if (!player1.isAlive) {
                System.out.println("player1 " + player1.name + " is dead");
            }
            if (!player2.isAlive) {
                System.out.println("player2 " + player2.name + " is dead");
            }
        }
        if (getAliveCharacters(partyPlayer1).size() == 0) {
            System.out.println("player2 wins");
        } else {
            System.out.println("player1 wins");
        }
    }

    private ArrayList<Character> getAliveCharacters(ArrayList<Character> playerCharacters) {
        ArrayList<Character> aliveCharacters = new ArrayList<>();
        for (Character playerCharacter : playerCharacters) {
            if (playerCharacter.isAlive()) {
                aliveCharacters.add(playerCharacter);
            }
        }
        return aliveCharacters;
    }


}
