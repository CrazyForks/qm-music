package com.github.chenqimiao.qmmusic.app.exception;

import com.github.chenqimiao.qmmusic.app.enums.EnumSubsonicErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@Getter
@AllArgsConstructor
public class SubsonicCommonErrorException extends RuntimeException{

    private EnumSubsonicErrorCode enumSubsonicErrorCode;

}
