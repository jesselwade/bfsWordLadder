import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import java.util.stream.Collectors;

/**
 * Provides an implementation of the WordLadderGame interface. The lexicon
 * is stored as a HashSet of Strings.
 *
 * @author Jesse Wade (jzw0169@auburn.edu)
 * @author Dean Hendrix (dh@auburn.edu)
 * @version 2019-11-25
 */
public class Doublets implements WordLadderGame {

   // The word list used to validate words.
   // Must be instantiated and populated in the constructor.
   private HashSet<String> lexicon;


   /**
    * Instantiates a new instance of Doublets with the lexicon populated with
    * the strings in the provided InputStream. The InputStream can be formatted
    * in different ways as long as the first string on each line is a word to be
    * stored in the lexicon.
    */
   public Doublets(InputStream in) {
      try {
         lexicon = new HashSet<String>();
         Scanner s =
            new Scanner(new BufferedReader(new InputStreamReader(in)));
         while (s.hasNext()) {
            String str = s.next();
            lexicon.add(str.toUpperCase());
            s.nextLine();
         }
         in.close();
      }
      catch (java.io.IOException e) {
         System.err.println("Error reading from InputStream.");
         System.exit(1);
      }
   }


 /**
    * Returns the Hamming distance between two strings, str1 and str2. The
    * Hamming distance between two strings of equal length is defined as the
    * number of positions at which the corresponding symbols are different. The
    * Hamming distance is undefined if the strings have different length, and
    * this method returns -1 in that case. See the following link for
    * reference: https://en.wikipedia.org/wiki/Hamming_distance
    *
    * @param  str1 the first string
    * @param  str2 the second string
    * @return      the Hamming distance between str1 and str2 if they are the
    *                  same length, -1 otherwise
    */
   public int getHammingDistance(String str1, String str2) {
      if (str1.length() != str2.length()) {
         return -1;
      }
      int ham = 0;
      for (int i = 0; i < str1.length(); i++) {
         if (str1.toUpperCase().charAt(i) != str2.toUpperCase().charAt(i)) {
            ham++;
         }
      }
      return ham; 
   }


  /**
   * Returns a minimum-length word ladder from start to end. If multiple
   * minimum-length word ladders exist, no guarantee is made regarding which
   * one is returned. If no word ladder exists, this method returns an empty
   * list.
   *
   * Breadth-first search must be used in all implementing classes.
   *
   * @param  start  the starting word
   * @param  end    the ending word
   * @return        a minimum length word ladder from start to end
   */
   public List<String> getMinLadder(String start, String end) {
      if (start == null || end == null) {
         throw new IllegalArgumentException();
      }
      
      // declarations
      ArrayList<String> result = new ArrayList<String>();
      HashSet<String> visited = new HashSet<String>();
      LinkedList<Node> q = new LinkedList<Node>();
      int minLength = 0;
           
      // check corner case   
      if (start.equals(end)) {
         result.add(start);   
         return result;
      }
      
      // starter node
      Node first = new Node(start, 0, null);
      q.offer(first);
      
      // if queue is empty we've exhausted all possible
      // hamming 1's from start to end of lexicon (for valid same length strings).
      while (!q.isEmpty()) {
         Node curr = q.poll();
         
         // check for valid result
         if (result.size() > 0 && curr.ham > minLength) {
            return result;
         }
         
         // get hamm distance 1 possibles
         ArrayList<String> n = getNeighbors(curr.word);
         
         for (int i = 0; i < n.size(); i++) {
            // check for end word and populate linked list
            // all of the work to build output is here
            if (n.get(i).equals(end)) {
               ArrayList<String> potential = new ArrayList<>();
               potential.add(end);
               
               // set p to node that found End.
               Node p = curr;
               
               // while p hasn't traversed back to start
               while (p != null) {
                  potential.add(p.word);
                  
                  // set p to previous node
                  p = p.prev;
               }
               
               // since we're stepping back, flip order
               Collections.reverse(potential);
               result.addAll(potential);
               
               // change min length based on hamming
               if (curr.ham <= minLength) {
                  minLength = curr.ham;
               }
               else {
                  return result;
               }
            }
            
            // create new nodes to populate queue if not visited
            // visited nodes represent duplicate paths so no need
            // to process
            if (!visited.contains(n.get(i))) {
            
               Node nd = new Node(n.get(i), curr.ham + 1, curr);
               q.offer(nd);
               visited.add(n.get(i));
            }
         }
      }
   
      return result;
   }


   /**
    * Returns all the words that have a Hamming distance of one relative to the
    * given word.
    *
    * @param  word the given word
    * @return      the neighbors of the given word
    */
   public ArrayList<String> getNeighbors(String word) {
      if (word == null) {
         throw new IllegalArgumentException();
      }
      ArrayList<String> result = new ArrayList<String>();
      
      // per prof. hendrix video, lets refactor this to not be iterative
      // over the lexicon.
      
      // break word into array to create variations of hamming 1.
      char[] chars = word.toCharArray();
      
      // for each place in the char array
      for (int i = 0; i < chars.length; i++) {
         // change the value to every character a-z (checking for duplicate)
         for (char c = 'a'; c <= 'z'; c++) {
            char t = chars[i];
            if (chars[i] != c) {
               chars[i] = c;
            }
               
            String nextWord = new String(chars);
            
            // look for word in lexicon and make sure its not the same word.   
            if (isWord(nextWord) && !nextWord.equals(word)) {
               result.add(nextWord);
            }
            
            // set character back to original value for next iteration
            chars[i] = t;
         }
      }
      
      return result;
   }


   /**
    * Returns the total number of words in the current lexicon.
    *
    * @return number of words in the lexicon
    */
   public int getWordCount() {
      return lexicon.size();
   }


   /**
    * Checks to see if the given string is a word.
    *
    * @param  str the string to check
    * @return     true if str is a word, false otherwise
    */
   public boolean isWord(String str) {
      if (str == null) {
         throw new IllegalArgumentException();
      }
      return lexicon.contains(str.toUpperCase());
   }


   /**
    * Checks to see if the given sequence of strings is a valid word ladder.
    *
    * @param  sequence the given sequence of strings
    * @return          true if the given sequence is a valid word ladder,
    *                       false otherwise
    */
   public boolean isWordLadder(List<String> sequence) {
      if (sequence.isEmpty()) {
         return false;
      }
      Iterator<String> itr = sequence.iterator();
      
      // setup first comparison and test it
      String str1 = itr.next();
      if (!isWord(str1)) {
         return false;
      }
      
      // test against 2nd comparison and iterate.
      while (itr.hasNext()) {
         String str2 = itr.next();
         if (getHammingDistance(str1,str2) != 1 || !isWord(str2)) {
            return false;
         }
         str1 = str2;
      }
      return true;   
   }
   
   /***
   * A generic node structure for building word linked lists.
   */
   public class Node {
      public String word;
      public int ham;
      public Node prev;
      
      /**
      * Constructor.
      */
      public Node(String word, int ham, Node prev) {
         this.word = word;
         this.ham = ham;
         this.prev = prev;
      }
   }


}

