package org.example;

import java.util.*;

public class ChomskyNormalForm {

    private static final int MAX = 20;
    private static String[][] gram = new String[MAX][MAX];
    private static String[] dpr = new String[MAX];
    private static int p; //np-> number of productions
    private static int np;
    private String input;
    private int lineCount;
    private String epselonFound = "";

    static private Map<String, List<String>> mapVariableProduction = new LinkedHashMap<>();

    public static void main(String args[]) {
        int line_count = 3;
        String final_string = """
                S->ASA|aB
                A->B|S
                B->b|e""";

//        String final_string = """
//                S->ASB
//                A->aAS|a|e
//                B->SbS|A|bb""";

//        String final_string = """
//                S->abb|AaB
//                A->aA|bA|e
//                B->aa|bb""";

        ChomskyNormalForm chomskyNormalForm = new ChomskyNormalForm();
        chomskyNormalForm.setInputandLineCount(final_string, line_count);
        chomskyNormalForm.convertCFGtoCNF();
    }

    public void setInputandLineCount(String input, int lineCount) {
        this.input = input;
        this.lineCount = lineCount;
    }


    public Map<String, List<String>> getMapVariableProduction() {
        return mapVariableProduction;
    }

    public void convertCFGtoCNF() {
        insertNewStartSymbol();
        convertStringtoMap();
        printMap();
        eliminateEpselon();
        removeDuplicateKeyValue();
        eliminateSingleVariable();
        onlyTwoTerminalandOneVariable();
        eliminateThreeTerminal();
    }

    private void eliminateSingleVariable() {
        System.out.println("Remove single variable in every production ... ");
        for (int i = 0; i < lineCount; i++) {
            removeSingleVariable();
        }
        printMap();
    }

    private void eliminateThreeTerminal() {
        System.out.println("Replace two terminal variable with new variable ... ");
        for (int i = 0; i < lineCount; i++) {
            removeThreeTerminal();
        }
        printMap();
    }

    private void eliminateEpselon() {
        System.out.println("\nRemove epsilon....");
        for (int i = 0; i < lineCount; i++) {
            removeEpselon();
        }
        printMap();
    }

    private String[] splitEnter(String input) {
        String[] tmpArray = new String[lineCount];
        for (int i = 0; i < lineCount; i++) {
            tmpArray = input.split("\\n");
        }
        return tmpArray;
    }

    private void printMap() {
        Iterator it = mapVariableProduction.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " -> " + pair.getValue());
        }
        System.out.println(" ");
    }

    private void convertStringtoMap() {
        String[] splitedEnterInput = splitEnter(input);

        for (int i = 0; i < splitedEnterInput.length; i++) {
            String[] tempString = splitedEnterInput[i].split("->|\\|");
            String variable = tempString[0].trim();
            String[] production = Arrays.copyOfRange(tempString, 1, tempString.length);
            List<String> productionList = new ArrayList<String>();
            for (int k = 0; k < production.length; k++) {
                production[k] = production[k].trim();
            }
            Collections.addAll(productionList, production);
            mapVariableProduction.put(variable, productionList);
        }
    }

    private void insertNewStartSymbol() {
        String newStart = "S0";
        ArrayList<String> newProduction = new ArrayList<>();
        newProduction.add("S");
        mapVariableProduction.put(newStart, newProduction);
    }


    private void removeEpselon() {
        Iterator itr = mapVariableProduction.entrySet().iterator();
        Iterator itr2 = mapVariableProduction.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();
            if (productionRow.contains("e")) {
                if (productionRow.size() > 1) {
                    productionRow.remove("e");
                    epselonFound = entry.getKey().toString();
                } else {
                    // remove if less than 1
                    epselonFound = entry.getKey().toString();
                    mapVariableProduction.remove(epselonFound);
                }
            }
        }

        while (itr2.hasNext()) {
            Map.Entry entry = (Map.Entry) itr2.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
            for (int i = 0; i < productionList.size(); i++) {
                String temp = productionList.get(i);
                for (int j = 0; j < temp.length(); j++) {
                    if (epselonFound.equals(Character.toString(productionList.get(i).charAt(j)))) {
                        if (temp.length() == 2) {
                            // remove specific character in string
                            temp = temp.replace(epselonFound, "");
                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(temp);
                            }
                        } else if (temp.length() == 3) {
                            String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();
                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
                            }
                        } else if (temp.length() == 4) {
                            String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();
                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
                            }
                        } else {
                            if (!mapVariableProduction.get(entry.getKey().toString()).contains("e")) {
                                mapVariableProduction.get(entry.getKey().toString()).add("e");
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeDuplicateKeyValue() {
        System.out.println("Remove Duplicate Key Value ... ");
        Iterator itr3 = mapVariableProduction.entrySet().iterator();
        while (itr3.hasNext()) {
            Map.Entry entry = (Map.Entry) itr3.next();
            ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();
            for (int i = 0; i < productionRow.size(); i++) {
                if (productionRow.get(i).contains(entry.getKey().toString())) {
                    productionRow.remove(entry.getKey().toString());
                }
            }
        }
        printMap();
    }

    private void removeSingleVariable() {
        Iterator itr4 = mapVariableProduction.entrySet().iterator();
        String key = null;
        while (itr4.hasNext()) {
            Map.Entry entry = (Map.Entry) itr4.next();
            Set set = mapVariableProduction.keySet();
            ArrayList<String> keySet = new ArrayList<String>(set);
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
            for (int i = 0; i < productionList.size(); i++) {
                String temp = productionList.get(i);
                for (int j = 0; j < temp.length(); j++) {
                    for (int k = 0; k < keySet.size(); k++) {
                        if (keySet.get(k).equals(temp)) {
                            key = entry.getKey().toString();
                            List<String> productionValue = mapVariableProduction.get(temp);
                            productionList.remove(temp);
                            for (int l = 0; l < productionValue.size(); l++) {
                                mapVariableProduction.get(key).add(productionValue.get(l));
                            }
                        }
                    }
                }
            }
        }
    }

    private Boolean checkDuplicateInProductionList(Map<String, List<String>> map, String key) {
        Boolean notFound = true;
        Iterator itr = map.entrySet().iterator();
        outerloop:
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
            for (int i = 0; i < productionList.size(); i++) {
                if (productionList.size() < 2) {
                    if (productionList.get(i).equals(key)) {
                        notFound = false;
                        break outerloop;
                    } else {
                        notFound = true;
                    }
                }
            }
        }
        return notFound;
    }

    private void onlyTwoTerminalandOneVariable() {
        System.out.println("Assign new variable for two non-terminal or one terminal ... ");
        Iterator itr5 = mapVariableProduction.entrySet().iterator();
        String key = null;
        int asciiBegin = 71; //G
        Map<String, List<String>> tempList = new LinkedHashMap<>();
        while (itr5.hasNext()) {
            Map.Entry entry = (Map.Entry) itr5.next();
            Set set = mapVariableProduction.keySet();
            ArrayList<String> keySet = new ArrayList<String>(set);
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
            Boolean found1 = false;
            Boolean found2 = false;
            Boolean found = false;
            for (int i = 0; i < productionList.size(); i++) {
                String temp = productionList.get(i);
                for (int j = 0; j < temp.length(); j++) {
                    if (temp.length() == 3) {
                        String newProduction = temp.substring(1, 3); // SA
                        if (checkDuplicateInProductionList(tempList, newProduction) && checkDuplicateInProductionList(mapVariableProduction, newProduction)) {
                            found = true;
                        } else {
                            found = false;
                        }
                        if (found) {
                            ArrayList<String> newVariable = new ArrayList<>();
                            newVariable.add(newProduction);
                            key = Character.toString((char) asciiBegin);
                            tempList.put(key, newVariable);
                            asciiBegin++;
                        }

                    } else if (temp.length() == 2) { // if only two substring
                        for (int k = 0; k < keySet.size(); k++) {
                            if (!keySet.get(k).equals(Character.toString(productionList.get(i).charAt(j)))) { // if substring not equals to keySet
                                found = false;
                            } else {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            String newProduction = Character.toString(productionList.get(i).charAt(j));
                            if (checkDuplicateInProductionList(tempList, newProduction) && checkDuplicateInProductionList(mapVariableProduction, newProduction)) {
                                ArrayList<String> newVariable = new ArrayList<>();
                                newVariable.add(newProduction);
                                key = Character.toString((char) asciiBegin);
                                tempList.put(key, newVariable);
                                asciiBegin++;
                            }
                        }
                    } else if (temp.length() == 4) {
                        String newProduction1 = temp.substring(0, 2); // SA
                        String newProduction2 = temp.substring(2, 4); // SA
                        if (checkDuplicateInProductionList(tempList, newProduction1) && checkDuplicateInProductionList(mapVariableProduction, newProduction1)) {
                            found1 = true;
                        } else {
                            found1 = false;
                        }
                        if (checkDuplicateInProductionList(tempList, newProduction2) && checkDuplicateInProductionList(mapVariableProduction, newProduction2)) {
                            found2 = true;
                        } else {
                            found2 = false;
                        }
                        if (found1) {
                            ArrayList<String> newVariable = new ArrayList<>();
                            newVariable.add(newProduction1);
                            key = Character.toString((char) asciiBegin);
                            tempList.put(key, newVariable);
                            asciiBegin++;
                        }
                        if (found2) {
                            ArrayList<String> newVariable = new ArrayList<>();
                            newVariable.add(newProduction2);
                            key = Character.toString((char) asciiBegin);
                            tempList.put(key, newVariable);
                            asciiBegin++;
                        }
                    }
                }
            }
        }
        mapVariableProduction.putAll(tempList);
        printMap();
    }

    private void removeThreeTerminal() {
        Iterator itr = mapVariableProduction.entrySet().iterator();
        ArrayList<String> keyList = new ArrayList<>();
        Iterator itr2 = mapVariableProduction.entrySet().iterator();
        // obtain key that use to eliminate two terminal and above
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();
            if (productionRow.size() < 2) {
                keyList.add(entry.getKey().toString());
            }
        }
        // find more than three terminal or combination of variable and terminal to eliminate them
        while (itr2.hasNext()) {
            Map.Entry entry = (Map.Entry) itr2.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
            if (productionList.size() > 1) {
                for (int i = 0; i < productionList.size(); i++) {
                    String temp = productionList.get(i);
                    for (int j = 0; j < temp.length(); j++) {
                        if (temp.length() > 2) {
                            String stringToBeReplaced1 = temp.substring(j, temp.length());
                            String stringToBeReplaced2 = temp.substring(0, temp.length() - j);
                            for (String key : keyList) {
                                List<String> keyValues = new ArrayList<>();
                                keyValues = mapVariableProduction.get(key);
                                String[] values = keyValues.toArray(new String[keyValues.size()]);
                                String value = values[0];
                                if (stringToBeReplaced1.equals(value)) {
                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(stringToBeReplaced1, key);
                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                } else if (stringToBeReplaced2.equals(value)) {
                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(stringToBeReplaced2, key);

                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                }
                            }
                        } else if (temp.length() == 2) {
                            for (String key : keyList) {
                                List<String> keyValues;
                                keyValues = mapVariableProduction.get(key);
                                String[] values = keyValues.toArray(new String[keyValues.size()]);
                                String value = values[0];
                                for (int pos = 0; pos < temp.length(); pos++) {
                                    String tempChar = Character.toString(temp.charAt(pos));
                                    if (value.equals(tempChar)) {
                                        mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                        temp = temp.replace(tempChar, key);
                                        if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                            mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            } else if (productionList.size() == 1) {
                for (int i = 0; i < productionList.size(); i++) {
                    String temp = productionList.get(i);
                    if (temp.length() == 2) {
                        for (String key : keyList) {
                            List<String> keyValues = new ArrayList<>();
                            keyValues = mapVariableProduction.get(key);
                            String[] values = keyValues.toArray(new String[keyValues.size()]);
                            String value = values[0];
                            for (int pos = 0; pos < temp.length(); pos++) {
                                String tempChar = Character.toString(temp.charAt(pos));
                                if (value.equals(tempChar)) {
                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(tempChar, key);
                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}