import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.Math.min;

public class PatternMatching {

    public static void main(String[] args) throws FileNotFoundException {
        String txtFile = args[0];
        String ptnFile = args[1];

        String text = readFile(txtFile);
        String pattern = readFile(ptnFile);
        char[] alphabet = constructAlphabet(pattern);
        State[] states = constructStates(alphabet, pattern.length());
        printAutomata(states);
        System.out.println();
        states = constructAutomata(pattern, states);
        printAutomata(states);
//        System.out.println(alphabet);
//        System.out.println(text);
//        System.out.println(pattern);
    }

    public static String readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner fin = new Scanner(file);
        String res = "";
        while(fin.hasNextLine()) {
            res = res + fin.nextLine();
        }

        return res;
    }

    public static char[] constructAlphabet(String pattern) {
        String res = "";
        for(int i = 0; i < pattern.length(); i++) {
            if(!(res.contains(pattern.charAt(i)+""))) {
                res += pattern.charAt(i);
            }
        }
        return res.toCharArray();
    }

    public static State[] constructStates(char[] alphabet, int numOfStates) {
        State[] States = new State[numOfStates];
        for(int i = 0; i < numOfStates; i++) {
            States[i] = new State(i, alphabet);
        }
        return States;
    }

    public static State[] constructAutomata(String pattern, State[] states) {
        int m = pattern.length();
        int k;
        String suffix = "";
        String prefix;
        for(int q = 0; q < m-1; q++) {
            for (int j = 0; j < states[q].alphabet.length; j++) {
                k = min(m+1, q + 2);
                //prefix = pattern.substring(0,k);
                //suffix = pattern.substring(0,k-1) + states[q].alphabet[j];
                do{
                    prefix = pattern.substring(0,k);
                    suffix = pattern.substring(0,k-1) + states[q].alphabet[j].symbol; // Need to have an edge case when we are analyzing just the first letter.

                    k = k - 1;
                                   }
                while(!(prefix.equals(suffix))); // needs to compare the pattern from 0 to k length to the current letter combination
                states[q].alphabet[j].nextState = k;
            }
        }
        return states;
    }

    public static void printAutomata(State[] states) {
        for(int i = 0; i < states.length; i++) {
            for(int j = 0; j < states[i].alphabet.length; j++) {
                System.out.println("State: " + states[i].ID + " Letter In State: " +states[i].alphabet[j].symbol +" Letters next state: "+ states[i].alphabet[j].nextState);
            }
        }
    }
}

class State {

    public class charNode {
        int nextState;
        char symbol;
        public charNode(char input, int nextState) {
            this.symbol = input;
            this.nextState = nextState;
        }
    }

    static int ID; // What number the state is (0, 1, 2, 3)
    static charNode[] alphabet;  // The characters in the alphabet as well as what the number of the next state
    // that character will take you to.

    public State(int ID, char[] input) {
        this.ID = ID;
        this.alphabet = new charNode[input.length];
        for(int i = 0; i < input.length; i++) {
            alphabet[i] = new charNode(input[i], 0);
        }
    }

}
