import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.Math.*;

public class PatternMatching {

    public static void main(String[] args) throws FileNotFoundException {
        String txtFile = args[0];
        String ptnFile = args[1];

        String text = readFile(txtFile);
        String pattern = readFile(ptnFile);
        if(text.equals(pattern)) {
            System.out.println("Pattern and text are the same!");
        }
        char[] alphabet = constructAlphabet(pattern);
        State[] states = constructStates(alphabet, pattern.length()+1);
        constructAutomata(pattern, states);
//        automataMatching(text, states, pattern.length());
//        printAutomata(states);
        karpRabin(pattern, text, 256, 524287);
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
    //TODO Need to create an additional end state.
    public static State[] constructStates(char[] alphabet, int numOfStates) {
        State[] states = new State[numOfStates];
        int i;
        for(i = 0; i < numOfStates; i++) {
            states[i] = new State(i, alphabet);
        }
        for(int j = 0; j < states[i-1].alphabet.length; j++) {
            states[i-1].alphabet[j].nextState = 0;
        }
        return states;
    }
    // Not taking the final letter in the pattern to the accepting state.
    // Need to make it so that state 0 is before any letters are even evaluated...if pattern starts with
    // m then state 0 should have 1 for m not 0.
    public static State[] constructAutomata(String pattern, State[] states) {
        int m = pattern.length();
        int k;
        pattern = " " + pattern;
        String suffix = "";
        String prefix;
        for(int q = 0; q < m+1; q++) {
            for (int j = 0; j < states[q].alphabet.length; j++) {
                k = min(m+1, q + 2);
                do{

                    prefix = pattern.substring(0, k);
                    suffix = pattern.substring(0, k-1) + states[q].alphabet[j].symbol;
                    // This works for the h in hehe but not the e...
                    if(q == m && suffix.length() == pattern.length()) { // might need to add an additional condition here states[0].alphabet.length.....q == m && (k >= (min(m+1,q+2)-states[0].alphabet.length))
                        suffix = pattern.substring(0,1) + pattern.substring(2) ;
                        prefix = pattern.substring(0, k);
                        suffix = suffix.substring(0, k-1) + states[q].alphabet[j].symbol;
                        k = k - 1;

                    }
                    else {
                        prefix = pattern.substring(0, k);
                        suffix = pattern.substring(0, k-1) + states[q].alphabet[j].symbol;
                        k = k - 1;
                    }
               }
                while(!(prefix.equals(suffix)) && k > 0); // needs to compare the pattern from 0 to k length to the current letter combination
                states[q].alphabet[j].nextState = k;
            }
        }
         return states;
    }

    private static void automataMatching(String text, State[] states, int m) {
        int n = text.length();
        int q = 0;
        for(int i = 1; i < n+1; i++) {
            q = State.getNextState(states[q], text.charAt(i-1));
            if( q == m) {
                System.out.println("Pattern is matched at text location: " + (i-m));
            }
        }
    }

    public static void printAutomata(State[] states) {
        for (State state : states) {
            for (int j = 0; j < state.alphabet.length; j++) {
                System.out.println("State: " + state.ID + " Letter In State: " + state.alphabet[j].symbol + " Letters next state: " + state.alphabet[j].nextState);
            }
            System.out.println();
        }
    }

    public static void karpRabin(String ptn, String txt, int alphabetSize, int primeNum) {
        int n = txt.length();
        int m = ptn.length();
        int alphaModPrime = 1;//(int)Math.pow(alphabetSize,m-1) % primeNum;
        int p = 0;
        int t = 0;

        for(int i = 1; i < m; i++) {
            alphaModPrime = (alphabetSize * (int)(Math.pow(alphaModPrime, m-i) % primeNum) % primeNum);
        }

        for(int i = 0; i < m; i++) {
            p += ((int)Math.pow(alphabetSize, i) * (int)ptn.charAt(m-i-1)) % primeNum;
            t += ((int)Math.pow(alphabetSize, i) * (int)txt.charAt(m-i-1)) % primeNum;
            //alphaModPrime += (alphabetSize * (Math.pow(alphabetSize, m-(i+1)) % primeNum) % primeNum);
        }
        for(int j = 0; j < (n-m); j++) {
            char currT = txt.charAt(m+j);
            if(p == t) {
                System.out.println("Pattern occurs with shift: " + j);
            }
            if(j < n-m) {
                t = ((alphabetSize * (t - (int)txt.charAt(j) * alphaModPrime) + (int)txt.charAt(j+m+1)) % primeNum);
                if( t < 0) {
                    t = t + primeNum;
                }
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

    int ID; // What number the state is (0, 1, 2, 3)
    charNode[] alphabet;  // The characters in the alphabet as well as what the number of the next state
    // that character will take you to.

    public State(int ID, char[] input) {
        this.ID = ID;
        this.alphabet = new charNode[input.length];
        for(int i = 0; i < input.length; i++) {
            alphabet[i] = new charNode(input[i], 0);
        }
    }

    public static int getNextState(State state, char symbol) {
        for(int i = 0; i < state.alphabet.length; i++) {
            if(symbol == state.alphabet[i].symbol) {
                return state.alphabet[i].nextState;
                // return nextState value here
            }
        }
        return 0;
    }



}
