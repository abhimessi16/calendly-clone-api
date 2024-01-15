package me.abhilasbhyrava.model.exception;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseException extends RuntimeException{

    private int code;
    private String message;

}
