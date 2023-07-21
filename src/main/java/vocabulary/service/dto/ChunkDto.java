package vocabulary.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vocabulary.service.enums.ChunkType;

@Getter
@Setter
@ToString
public class ChunkDto {
    private String value;
    private ChunkType type;
}
