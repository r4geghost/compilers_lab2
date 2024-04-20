package org.example;

import java.util.ArrayList;

public class Recursion {

    public static void main(String[] args) {
        Grammar grammar = new Grammar();

        // непосредственная (прямая)
//        grammar.addRule("S -> Sa | Sb | c | d");

//        grammar.addRule("E -> E+T | T");
//        grammar.addRule("T -> T*F | F");
//        grammar.addRule("F -> (E) | id");

//        grammar.addRule("T -> T,S | S");
//        grammar.addRule("S -> a | ^ | (T)");

        // не непосредственная (непрямая)
        grammar.addRule("S -> Aa | b");
        grammar.addRule("A -> Ac | Sd");
//
//        grammar.addRule("B -> Ab | y");
//        grammar.addRule("A -> Ba");

//        grammar.addRule("A -> Cd");
//        grammar.addRule("B -> Ce");
//        grammar.addRule("C -> A | B | f");

//        grammar.addRule("S -> Bw | aB");
//        grammar.addRule("B -> S | zx");

        grammar.deleteLeftRecursion();

        grammar.nonTerminals.forEach(NonTerminal::printRule);
    }

    static class NonTerminal {
        private String name;
        private ArrayList<String> rules;

        public NonTerminal(String name) {
            this.name = name;
            rules = new ArrayList<>();
        }

        public void addRule(String rule) {
            rules.add(rule);
        }

        public void setRules(ArrayList<String> rules) {
            this.rules = rules;
        }

        public String getName() {
            return name;
        }

        public ArrayList<String> getRules() {
            return rules;
        }

        public void printRule() {
            System.out.print(name + " -> ");
            for (int i = 0; i < rules.size(); i++) {
                System.out.print(rules.get(i));
                if (i != rules.size() - 1)
                    System.out.print(" | ");
            }
            System.out.println();
        }
    }


    static class Grammar {
        private final ArrayList<NonTerminal> nonTerminals;

        public Grammar() {
            nonTerminals = new ArrayList<>();
        }

        public void addRule(String rule) {
            boolean nt = false;
            StringBuilder parse = new StringBuilder();

            for (int i = 0; i < rule.length(); i++) {
                char c = rule.charAt(i);
                if (c == ' ') {
                    if (!nt) {
                        NonTerminal newNonTerminal = new NonTerminal(parse.toString());
                        nonTerminals.add(newNonTerminal);
                        nt = true;
                        parse = new StringBuilder();
                    } else if (!parse.isEmpty()) {
                        nonTerminals.get(nonTerminals.size() - 1).addRule(parse.toString());
                        parse = new StringBuilder();
                    }
                } else if (c != '|' && c != '-' && c != '>') {
                    parse.append(c);
                }
            }
            if (!parse.isEmpty()) {
                nonTerminals.get(nonTerminals.size() - 1).addRule(parse.toString());
            }
        }

        public void solveNonImmediateRecursion(NonTerminal A, NonTerminal B) {
            String nameB = B.getName();
            ArrayList<String> newRulesA = new ArrayList<>();
            for (String ruleA : A.getRules()) {
                if (ruleA.startsWith(nameB)) {
                    for (String ruleB : B.getRules()) {
                        newRulesA.add(ruleB + ruleA.substring(nameB.length()));
                    }
                } else {
                    newRulesA.add(ruleA);
                }
            }
            A.setRules(newRulesA);
        }

        public void solveImmediateRecursion(NonTerminal A) {
            String name = A.getName();
            String newName = name + "'";

            ArrayList<String> alphas = new ArrayList<>();
            ArrayList<String> betas = new ArrayList<>();
            ArrayList<String> newRulesA = new ArrayList<>();
            ArrayList<String> newRulesA1 = new ArrayList<>();

            // Check for left recursion
            for (String rule : A.getRules()) {
                if (rule.startsWith(name)) {
                    alphas.add(rule.substring(name.length()));
                } else {
                    betas.add(rule);
                }
            }
            if (alphas.isEmpty())
                return;
            if (betas.isEmpty())
                newRulesA.add(newName);
            for (String beta : betas)
                newRulesA.add(beta + newName);
            for (String alpha : alphas)
                newRulesA1.add(alpha + newName);

            A.setRules(newRulesA);
            newRulesA1.add("eps");

            NonTerminal newNonTerminal = new NonTerminal(newName);
            newNonTerminal.setRules(newRulesA1);
            nonTerminals.add(newNonTerminal);
        }

        public void deleteLeftRecursion() {
            for (int i = 0; i < nonTerminals.size(); i++) {
                for (int j = 0; j < i; j++) {
                    solveNonImmediateRecursion(nonTerminals.get(i), nonTerminals.get(j));
                }
                solveImmediateRecursion(nonTerminals.get(i));
            }
        }
    }
}
