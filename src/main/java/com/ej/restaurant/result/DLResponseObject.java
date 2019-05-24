package com.ej.restaurant.result;

import com.ej.restaurant.enums.DLExceptionType;
import com.ej.restaurant.utils.DLClock;
import com.ej.restaurant.utils.DLException;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ToString
public class DLResponseObject<T> implements Serializable{
  public long code;
  public String msg;
  public long time;
  public T body;

  public DLResponseObject(long code, String msg, long time, T body) {
    this.code = code;
    this.msg = msg;
    this.time = time;
    this.body = body;
  }

  public static <T> DLResponseObject<T> generate(T body) {
    return new DLResponseObject<>(DLExceptionType.SUCCESS.getCode(), DLExceptionType.SUCCESS.getMsg(), DLClock.now(), body);
  }

  public static DLResponseObject<String> success() {
    return generate("");
  }

  public static DLResponseObject<List> emptyList() {
    return generate(Collections.emptyList());
  }

  public static DLResponseObject<String> fromError(DLException ex) {
    return new DLResponseObject<>(ex.getDlExceptionType().getCode(), ex.getMessage(), DLClock.now(), "");
  }

  public static DLResponseObject<String> fromErrorType(DLExceptionType dlExceptionType) {
    return new DLResponseObject<>(dlExceptionType.getCode(), dlExceptionType.getMsg(), DLClock.now(), "");
  }
}
