package vocabulary.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DiffDto {
    private List<RowDto> rows;
    private int added;
    private int removed;
}
