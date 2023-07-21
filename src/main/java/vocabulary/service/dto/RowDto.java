package vocabulary.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RowDto {
    private boolean end;
    private RowInnerDto left;
    private RowInnerDto right;
    private boolean insideChanged;
    private boolean start;
}
