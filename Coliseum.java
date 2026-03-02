package games;

import java.util.ArrayList;

import edu.rutgers.cs112.BST.BSTNode;

/**
 * This class contains methods to represent a Coliseum in Gladiators.
 * It manages cities, people, and the duel process.
 * 
 * @author Maksims Kurjanovics Kravcenko
 * @author Pranay Roni
 */
public class Coliseum {

    private ArrayList<City> cities; // all cities in Gladiators.
    private BSTNode<City> game; // root of the BST. The BST contains cities that are still in the game. 

    /**
     * Default constructor, initializes an empty list of cities.
     */
    public Coliseum() {
        cities = new ArrayList<>();
        game = null; 
    }

    /**
     * Sets up Gladiators, the universe in which the duels takes place.
     * Reads cities and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupGladiators(String filename) {
        StdIn.setFile(filename); // open the file - done only once here
        setupCities();
        setupPeople();
    }

    /**
     * Reads the following from input file:
     * - Number of cities
     * - city IDs
     * Add cities into the cities ArrayList in order of appearance. 
     */
    public void setupCities() {
        int n = StdIn.readInt(); //numCities
        cities = new ArrayList<City>(); //make sure you spell ArrayList correctly
        for (int i = 0; i < n; i++){
            int cityID = StdIn.readInt();
            City c = new City(cityID);
            cities.add(c); //add city to your new list
        }

    }

    /**
     * Reads the following from input file:
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, city id, effectiveness 
     * 
     * You should add each person to the corresponding city based on their city id.
     * If the person's birth month is odd, add to oddPopulation, else add to evenPopulation.
     */
    public void setupPeople() { 
        int p = StdIn.readInt(); //numPeople; will continue reading from where setupCities left off
        for (int i = 0; i < p; i++){
            String firstName = StdIn.readString();
            String lastName = StdIn.readString();
            int birthMonth = StdIn.readInt();
            int age = StdIn.readInt();
            int cityID = StdIn.readInt();
            int effectiveness = StdIn.readInt();

            Person person = new Person (firstName, lastName, birthMonth, 
            age, cityID, effectiveness);

            //find city object in cities ArraList
            City target = null;
            for (City c : cities){ //iterate through cities ArrayList
                if (c.getCityNumber() == cityID){ //diff cityID; talking abt one used in this method
                    target = c;
                    break;
                }
            }

            //add person to their corresponding population
            if (birthMonth % 2 == 0){
                target.addEvenPerson(person);
            } else {
                target.addOddPerson(person);
            }
        }
 
    }

    /**
     * Adds a city to the game BST.
     * If the city is already added, do nothing
     *  
     * @param newCity the city we wish to add
     */
    public void addCityToGame(City newCity) {
        BSTNode<City> node = new BSTNode<City>(newCity);
        if (game == null){ //if root equal to null
            game = node;
            return; //CHECK: why can't I use return instead of break?!!
        }
        BSTNode<City> current = game;
        while (true) { //infinite loop until return
            int currID = current.getData().getCityNumber();
            int newID = newCity.getCityNumber();
            if (newID > currID){
                //go right
                if (current.getRight() == null){
                    current.setRight(node);
                    return;
                } else {
                    current = current.getRight();
                }
            } else {
                if (current.getLeft() == null){
                    current.setLeft(node);
                    return;
                } else {
                    current = current.getLeft();
                }
            }
        }
    }


    /**
     * Searches for a city inside of the BST given the city id.
     * 
     * @param id the city to search
     * @return the city if found, null if not found
     */
    public City findCity(int id) {
        return findCityHelper(game, id);
    }
    private City findCityHelper(BSTNode<City> node, int targetID){
        if (node == null){
            return null;
        }
        int id = node.getData().getCityNumber();
        if (id == targetID){
            return node.getData();
        }
        if (targetID > id){
            return findCityHelper(node.getRight(), targetID);
        } else {
            return findCityHelper(node.getLeft(), targetID);
        }
    } //go over it!!
   
    /**
     * Selects two duelers from the tree, according to a series of selection rules.
     * View the assignment description for exact implementation details.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
        DuelPair pair = new DuelPair();
        //track chosen city IDs; no duplicates
        ArrayList<Integer> chosenCityIDs = new ArrayList<Integer>(); 
        //call helper methods in order
        pickOddYoung(game, pair, chosenCityIDs);
        
        pickEvenYoung(game, pair, chosenCityIDs);
        
        pickOddRandom(game, pair, chosenCityIDs);
        
        pickEvenRandom(game, pair, chosenCityIDs);
        
        return pair;
    }
    //recursive helper methods for selectDuelers
    private void pickOddYoung(BSTNode<City> node, DuelPair pair, ArrayList<Integer> chosenIDs){
        if (node == null || pair.getPerson1() != null) {
            return;
        }
        
        // Then check current node
        if (pair.getPerson1() == null) {
            City city = node.getData();
            if (!chosenIDs.contains(city.getCityNumber())) {
                ArrayList<Person> odd = city.getOddPopulation();
                for (int i = 0; i < odd.size(); i++){
                    Person p = odd.get(i);
                    if (p.isYoungWarrior()){
                        odd.remove(i);
                        pair.setPerson1(p);
                        chosenIDs.add(city.getCityNumber());
                        return; //stop because p1 chosen
                    }
                }
            }
        }
        // First try left subtree
        pickOddYoung(node.getLeft(), pair, chosenIDs);

        if (pair.getPerson1() != null){
            return;
        }

        // Then try right subtree
        pickOddYoung(node.getRight(), pair, chosenIDs);
        //stop early if person1 and 2 chosen
    }
    private void pickEvenYoung(BSTNode<City> node, DuelPair pair, ArrayList<Integer> chosenIDs){
        if (node == null || pair.getPerson2() != null) {
            return;
        }
        
        // Then check current node
        if (pair.getPerson2() == null) {
            City city = node.getData();
            if (!chosenIDs.contains(city.getCityNumber())) {
                ArrayList<Person> even = city.getEvenPopulation();
                for (int i = 0; i < even.size(); i++){
                    Person p = even.get(i);
                    if (p.isYoungWarrior()) {
                        even.remove(i);
                        pair.setPerson2(p);
                        chosenIDs.add(city.getCityNumber());
                        return;
                    }
                }
            }
        }

        // First try left subtree
        pickEvenYoung(node.getLeft(), pair, chosenIDs);

        if (pair.getPerson2() != null){
            return;
        }
        // Then try right subtree
        pickEvenYoung(node.getRight(), pair, chosenIDs);
    }

    private void pickOddRandom(BSTNode<City> node, DuelPair pair, ArrayList<Integer> chosenIDs){
        if (node == null){
            return;
        }
        if (pair.getPerson1() == null){
            City city = node.getData();
            int cityID = city.getCityNumber();
            if (!chosenIDs.contains(cityID)){
                ArrayList<Person> odd = city.getOddPopulation();
                int size = odd.size();
                if (size > 0){
                    int xIndex = StdRandom.uniform(size);
                    Person p = odd.remove(xIndex);
                    pair.setPerson1(p);
                    chosenIDs.add(cityID);
                    return; //stop because person1 chosen
                }
            }
        }
        pickOddRandom(node.getLeft(), pair, chosenIDs);
        if (pair.getPerson1() != null){
            return;
        }
        pickOddRandom(node.getRight(), pair, chosenIDs);
    }
    private void pickEvenRandom(BSTNode<City> node, DuelPair pair, ArrayList<Integer> chosenIDs){
        if (node == null){
            return;
        }
        if (pair.getPerson2() == null){
            City city = node.getData();
            int cityID = city.getCityNumber();
            if (!chosenIDs.contains(cityID)){
                ArrayList<Person> even = city.getEvenPopulation();
                int size = even.size();
                if (size > 0){
                    int xIndex = StdRandom.uniform(size);
                    Person p = even.remove(xIndex);
                    pair.setPerson2(p);
                    chosenIDs.add(cityID);
                    return;
                }
            }
        }
        pickEvenRandom(node.getLeft(), pair, chosenIDs);
        
        pickEvenRandom(node.getRight(), pair, chosenIDs);
    }



    /**
     * Removes a city from the BST given the city id.
     * 
     * @param id the city to eliminate
     */
    public void eliminateCity(int id) {
        game = eliminateCityHelper(game, id);
    }
    private BSTNode<City> eliminateCityHelper(BSTNode<City> root, int key) {
        if (root == null) {
            return null;
        }
        //empty tree
        
        int cityID = root.getData().getCityNumber();
        
        if (key < cityID) {
            root.setLeft(eliminateCityHelper(root.getLeft(), key));
        } else if (key > cityID) {
            root.setRight(eliminateCityHelper(root.getRight(), key));
        } else {
            // Node with only one child or no child
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }
            // Node with two children: Get the inorder successor (smallest
            // in the right subtree)
            BSTNode<City> successor = findMin(root.getRight());
            BSTNode<City> newRoot = new BSTNode<City>(successor.getData());//new node no duplicates
            //no duplicates or dangling pointers
            newRoot.setRight(removeMin(root.getRight())); //remove min from right subtree
            newRoot.setLeft(root.getLeft()); //keep left subtree
            return newRoot;//new root
        }
        return root;
    }
    
    private BSTNode<City> findMin(BSTNode<City> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    private BSTNode<City> removeMin(BSTNode<City> node){
        if (node == null){
            return null; //sets removed child spot to null
            //remove min will go back to previous method call 
        }
        if (node.getLeft() == null){
            return node.getRight();
        }
        node.setLeft(removeMin(node.getLeft()));
        return node;
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets added back to their city
     * - If a cities odd OR even population is empty, eliminate it from the game 
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        if (pair == null) {
            return;
        }
        Person p1 = pair.getPerson1();
        Person p2 = pair.getPerson2();

        if (p1 == null && p2 == null) {
            return;
        }
        
        // Handle case where only one person is present
        if (p1 == null || p2 == null) {
            Person single = (p1 != null) ? p1 : p2;
            City c = findCity(single.getCityNumber());
            if (c != null) {
                if (single.getBirthMonth() % 2 == 0) {
                    c.getEvenPopulation().add(single);
                } else {
                    c.getOddPopulation().add(single);
                }
            }
            return;
        }

        // Both persons present: have them duel
        Person winner = p1.duel(p2);
        Person loser = (winner == p1) ? p2 : p1;

        // Add winner back to their city
        City winnerCity = findCity(winner.getCityNumber());
        if (winnerCity != null) {
            if (winner.getBirthMonth() % 2 == 0) {
                winnerCity.getEvenPopulation().add(winner);
            } else {
                winnerCity.getOddPopulation().add(winner);
            }
        }

        // Check if either city needs to be eliminated
        checkAndEliminateCity(winnerCity);
        checkAndEliminateCity(findCity(loser.getCityNumber()));
    }
    
    private void checkAndEliminateCity(City city) {
        if (city == null) {
            return;
        }
        // Eliminate city if either population is empty
        if (city.getOddPopulation().isEmpty() || city.getEvenPopulation().isEmpty()) {
            eliminateCity(city.getCityNumber());
        }
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of cities for the Driver.
     * 
     * @return the ArrayList of cities for selection
     */
    public ArrayList<City> getCities() {
        return this.cities;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public BSTNode<City> getRoot() {
        return game;
    }
}