package com.bus.chelaile.strategy;

class TrieNode {
	private TrieNode[] children = null;
	
	public void setChildActive(char c) {
		if (children == null) {
			children = new TrieNode[256];
		}
		int index = (int)c;
		if (children[index] == null) {
			children[index] = new TrieNode();
		}
	}
	
	public TrieNode getChild(char c) {
		if (children == null) {
			return null;
		}
		return children[(int)c];
	}
}

public class Trie {
	private TrieNode root;
	
	public Trie() {
		root = new TrieNode();
	}
	
	public void insert(String word) {
        TrieNode itr = root;
        int i = 0;
        while (i < word.length()) {
        	char c = word.charAt(i);
            itr.setChildActive(c);
            itr = itr.getChild(c);
            i++;
        }
        itr.setChildActive('\0');
    }

    private TrieNode iterateGivenStr(String str) {
        if (root == null) {
            return null;
        }
        TrieNode itr = root;
        int i = 0;
        while (i < str.length()) {
            itr = itr.getChild(str.charAt(i));
            if (itr == null) {
                return null;
            }
            i++;
        }
        return itr;
    }

    public String getLongestMatchedPrefix(String str) {
        if (root == null) {
            return null;
        }
        if(str == null) {
        	return null;
        }
        char[] results = new char[200];
        TrieNode itr = root;
        int i = 0;
        while (i < str.length()) {
            itr = itr.getChild(str.charAt(i));
            if (itr == null) {
                break;
            }
            results[i] = str.charAt(i);
            i++;
        }
        return String.copyValueOf(results).trim();
    }
    

    public boolean search(String word) {
        TrieNode itr = iterateGivenStr(word);
        return itr != null && itr.getChild('\0') != null;
    }

    public boolean startsWith(String prefix) {
        return iterateGivenStr(prefix) != null;
    }
}
