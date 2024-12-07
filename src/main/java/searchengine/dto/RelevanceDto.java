package searchengine.dto;

import lombok.*;
import searchengine.model.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelevanceDto {
    private Page page;
    private Float absolutRelevance = 0f;
    private Float relativeRelevance = 0f;
}
