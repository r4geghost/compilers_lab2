package org.example;

import java.util.*;

public class Recursion {
    static Map<String, List<String>> rules = new LinkedHashMap<>();
    static Map<String, List<String>> newRules = new LinkedHashMap<>();
    static ArrayList<String> alpha = new ArrayList<>();
    static ArrayList<String> beta = new ArrayList<>();

    public static void main(String[] args) {
        rules.put("S", List.of("Sa", "Sb", "c", "d"));
//        rules.put("S", List.of("S0S1S", "01"));

        rules.forEach((s, strings) -> System.out.println(s + "->" + strings));
        Iterator<String> it = rules.keySet().iterator();
        while (it.hasNext()) {
            int pos = 0;
            String key = it.next();
            List<String> prods = rules.get(key);

            for (String p : prods) {
                if (p.charAt(0) == key.charAt(0)) alpha.add(p.substring(1));
                else beta.add(p);
                pos++;
            }

            System.out.println(alpha.toString());
            System.out.println(beta.toString());

            if (!alpha.isEmpty()) {
                List<String> alphaProds = new ArrayList<>();
                for (String al : alpha) {
                    alphaProds.add(al + key + "'");
                }
                alphaProds.add("e");
                newRules.put(key + "'", alphaProds);
                List<String> betaProds = new ArrayList<>();
                for (String be : beta) {
                    betaProds.add(be + key + "'");
                }
                newRules.put(key, betaProds);
            } else {
                newRules.put(key, rules.get(key));
            }
        }

        newRules.forEach((s, strings) -> System.out.println(s + "->" + strings));
    }
}
