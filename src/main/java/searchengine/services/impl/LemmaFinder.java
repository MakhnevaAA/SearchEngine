package searchengine.services.impl;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LemmaFinder {

    private static final String REGEXP_WORD = "[a-zA-Zа-яА-ЯёЁ]+";
    private static final String[] PARTICLES_NAMES = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ", "ВВОДН", "ЧАСТ", "МС",
                                                                 "CONJ", "PART"};
    private static final Logger log = LoggerFactory.getLogger(LemmaFinder.class);

    public ConcurrentHashMap<String, Integer> findAllLemmas(String text) throws IOException {
        LuceneMorphology luceneMorphRus = new RussianLuceneMorphology();
        LuceneMorphology luceneMorphEng = new EnglishLuceneMorphology();
        ConcurrentHashMap<String, Integer> words = new ConcurrentHashMap<>();
        text = deleteTags(text);
        String wordBaseForms = "";
        String lemma = "";
        Pattern pattern = Pattern.compile(REGEXP_WORD);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            try {
                lemma = matcher.group().toLowerCase();
                lemma = luceneMorphRus.getNormalForms(lemma).get(0);
                wordBaseForms = luceneMorphRus.getMorphInfo(lemma).get(0);
            } catch (WrongCharaterException exception) {
                try {
                    lemma = luceneMorphEng.getNormalForms(lemma).get(0);
                    wordBaseForms = luceneMorphEng.getMorphInfo(lemma).get(0);
                } catch (WrongCharaterException wce) {
                    log.error("Слово " + lemma + " нельзя привести к нормализованной форме");
                }
            }
            if (isParticle(wordBaseForms)) {
                continue;
            }
            if (words.get(lemma) != null) {
                words.put(lemma, words.get(lemma) + 1);
            } else {
                words.put(lemma, 1);
            }
        }
        return words;
    }

    public boolean isParticle(String wordBaseForms) {
        for (String form : PARTICLES_NAMES) {
            if (wordBaseForms.contains(form)) {
                return true;
            }
        }
        return false;
    }

    public String deleteTags(String text) {
        return Jsoup.clean(text, Safelist.none());
    }

    public String getSnippet(String text, List<String> lemmas) throws IOException {
        LuceneMorphology luceneMorphRus = new RussianLuceneMorphology();
        LuceneMorphology luceneMorphEng = new EnglishLuceneMorphology();
        text = deleteTags(text);
        StringBuilder textNearLemma = new StringBuilder();
        TreeMap<Integer, String> positionsWords = new TreeMap<>();
        String lemma = "";
        List<String> addedLemmas = new ArrayList<>();

        StringBuilder word = new StringBuilder();
        for (char symbol : text.toCharArray()) {
            textNearLemma.append(symbol);
            if (Character.isLetter(symbol)) {
                word.append(symbol);
            } else {
                try {
                    lemma = !word.isEmpty() ? word.toString().toLowerCase() : ".";
                    lemma = luceneMorphRus.getNormalForms(lemma).get(0);
                    if (lemmas.contains(lemma)) {
                        insertInPositionsWords(textNearLemma, word, positionsWords, addedLemmas, lemma);
                    }
                    word = new StringBuilder();
                } catch (WrongCharaterException wce) {
                    try {
                        lemma = luceneMorphEng.getNormalForms(lemma).get(0);
                        if (lemmas.contains(lemma)) {
                            insertInPositionsWords(textNearLemma, word, positionsWords, addedLemmas, lemma);
                        }
                        word = new StringBuilder();
                    } catch (WrongCharaterException w) {
                        word = new StringBuilder();
                    }
                }
            }
        }
        return createSnippet(positionsWords, textNearLemma);
    }

    public String createSnippet(TreeMap<Integer, String> positionsWords, StringBuilder textNearLemma) {
        String resultText = "";
        int countSymbolsInPartSnippet = Math.max(150 / positionsWords.size(), 20);
        int prevPosition = -countSymbolsInPartSnippet;
        for (Map.Entry<Integer, String> positionLemma: positionsWords.entrySet()) {
            if (prevPosition + countSymbolsInPartSnippet <= positionLemma.getKey() + positionLemma.getValue().length()) {
                int startPosition = Math.max(positionLemma.getKey() - countSymbolsInPartSnippet, 0);
                int endPosition = Math.min(positionLemma.getKey() + positionLemma.getValue().length()
                        + countSymbolsInPartSnippet, textNearLemma.length() - 1);
                String partText = textNearLemma.substring(startPosition, endPosition);
                if (startPosition != 0) {
                    String split = partText.split("\\s", 2)[0];
                    partText = partText.substring(split.length()).trim();
                }
                if (endPosition != textNearLemma.length() - 1) {
                    int index = partText.lastIndexOf('\s');
                    partText = partText.substring(0, index);
                    resultText = resultText + (". . .") + partText;
                }
                prevPosition = positionLemma.getKey() + positionLemma.getValue().length() - 1;
            }
        }
        return resultText + (". . .");
    }

    public void insertInPositionsWords(StringBuilder textNearLemma, StringBuilder word, TreeMap<Integer,
            String> positionsWords, List<String> addedLemmas, String lemma) {
        String stringBuilder = textNearLemma.substring(0, textNearLemma.length() - word.length() - 1);
        textNearLemma.delete(0, textNearLemma.length()).append(stringBuilder);
        word.insert(0, "<b>").append("</b> ");
        textNearLemma.append(word);
        if (!addedLemmas.contains(lemma)) {
            addedLemmas.add(lemma);
            positionsWords.put(textNearLemma.indexOf(word.toString()), word.toString());
        }
    }
}
