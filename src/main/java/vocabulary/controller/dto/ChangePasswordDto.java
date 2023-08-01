package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ChangePasswordDto {
    private final String passwordOld;
    private final String passwordNew;
    private final String passwordConfirm;
}
