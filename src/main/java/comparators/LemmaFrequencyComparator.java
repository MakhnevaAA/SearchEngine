package comparators;

import searchengine.model.Lemma;

import java.util.Comparator;

public class LemmaFrequencyComparator implements Comparator<Lemma> {

    @Override
    public int compare(Lemma o1, Lemma o2) {
        return Integer.compare(o1.getFrequency(), o2.getFrequency());
    }
}