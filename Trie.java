import java.util.*;

public class Trie {
    // Represents a single node in the Trie data structure.  Each node corresponds to a character
    // and can have multiple children representing the next possible characters in a word.
    private class TrieNode {
        // A map to store the children of this node. The key is the character, and the value is the
        // corresponding TrieNode.  Using a HashMap allows for efficient lookups of child nodes.
        Map<Character, TrieNode> children = new HashMap<>();
        // A boolean flag to indicate whether this node represents the end of a valid word.
        // If true, it means the path from the root to this node spells out a word that was
        // inserted into the Trie.
        boolean isEndOfWord;
    }

    // The root node of the Trie.  The Trie starts here.
    private TrieNode root;

    // Constructor for the Trie class. Initializes the root node.
    public Trie() {
        // Create a new TrieNode and assign it to the root variable.  This is the starting point
        // for all words inserted into the Trie.
        root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie.  The word is added character by character, creating new
     * nodes as needed.
     *
     * @param word The word to insert into the Trie.
     */
    public void insert(String word) {
        // Start at the root node.  We'll traverse the Trie from here.
        TrieNode node = root;
        // Convert the word to lowercase to ensure case-insensitive searching later.  Also,
        // convert it to a character array for easy iteration.
        for (char c : word.toLowerCase().toCharArray()) {
            // Check if the current character already exists as a child of the current node.
            // If not, create a new TrieNode for it and add it to the children map.
            node.children.putIfAbsent(c, new TrieNode());
            // Move to the next node, which corresponds to the current character.
            node = node.children.get(c);
        }
        // After processing all characters, mark the current node as the end of a word.
        node.isEndOfWord = true;
    }

    /**
     * Retrieves all words in the Trie that start with the given prefix.  This method traverses
     * the Trie to the node representing the prefix and then collects all words that can be formed
     * from that node.
     *
     * @param prefix The prefix to search for.
     * @return A list of words that start with the given prefix. Returns an empty list if no
     *         words with the prefix are found.
     */
    public List<String> getWordsWithPrefix(String prefix) {
        // Create a list to store the results (the words that match the prefix).
        List<String> results = new ArrayList<>();
        // Start at the root node.
        TrieNode node = root;
        // Convert the prefix to lowercase for case-insensitive searching.
        for (char c : prefix.toLowerCase().toCharArray()) {
            // Check if the current character exists as a child of the current node.
            if (!node.children.containsKey(c)) {
                // If the character doesn't exist, it means there are no words in the Trie
                // that start with the given prefix, so return an empty list.
                return results;
            }
            // Move to the next node, which corresponds to the current character in the prefix.
            node = node.children.get(c);
        }
        // If we reach here, it means we've successfully traversed the Trie to the node
        // representing the prefix.  Now, collect all words that can be formed from this node.
        collectAllWords(node, new StringBuilder(prefix.toLowerCase()), results);
        // Return the list of words that start with the given prefix.
        return results;
    }

    /**
     * Recursively collects all words that can be formed from a given node in the Trie.
     * This method performs a depth-first traversal of the Trie, adding words to the results
     * list as it encounters end-of-word nodes.
     *
     * @param node    The current node being visited.
     * @param prefix  The current prefix being formed.  This is a StringBuilder for efficiency
     *                since we'll be modifying it.
     * @param results The list to store the collected words.
     */
    private void collectAllWords(TrieNode node, StringBuilder prefix, List<String> results) {
        // If the current node represents the end of a word, add the current prefix to the results list.
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        // Iterate through the children of the current node.
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            // Append the character of the child node to the current prefix.
            prefix.append(entry.getKey());
            // Recursively call collectAllWords on the child node.
            collectAllWords(entry.getValue(), prefix, results);
            // After the recursive call returns, remove the last character from the prefix.
            // This is necessary to backtrack and explore other branches of the Trie.
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
}