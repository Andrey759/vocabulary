package vocabulary.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RowInnerDto {
    private List<ChunkDto> chunks;
    private int line;
}
