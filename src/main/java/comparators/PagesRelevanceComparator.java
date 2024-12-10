package comparators;

import searchengine.dto.RelevanceDto;

import java.util.Comparator;

public class PagesRelevanceComparator implements Comparator<RelevanceDto> {
    @Override
    public int compare(RelevanceDto o1, RelevanceDto o2) {
        return o2.getRelativeRelevance().compareTo(o1.getRelativeRelevance());
    }
}
