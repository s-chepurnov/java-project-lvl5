package hexlet.code.dtos.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ResponseError {

    private final String status;
    private final String message;

}
