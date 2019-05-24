package com.ej.restaurant.utils;

import com.ej.restaurant.enums.DLExceptionType;

public class DLException extends RuntimeException {
  private DLExceptionType dlExceptionType;
  private String detail = null;

  public DLException(String msg) {
    super(msg);
    this.dlExceptionType = DLExceptionType.COMMON_SERVER_ERROR;
    detail = msg;
  }

  public DLException(DLExceptionType type) {
    super(type.getMsg());
    this.dlExceptionType = type;
  }

  public DLException(DLExceptionType type, String msg) {
    super(msg);
    this.dlExceptionType = type;
    this.detail = msg;
  }

  public DLException(DLExceptionType type, Throwable cause) {
    super(cause);
    this.dlExceptionType = type;
  }

  public DLException(DLExceptionType type, String msg, Throwable cause) {
    super(msg, cause);
    this.dlExceptionType = type;
    this.detail = msg;
  }

  public DLExceptionType getDlExceptionType() {
    return dlExceptionType;
  }

  @Override
  public String getMessage() {
    if (detail != null) {
      return detail;
    }
    return dlExceptionType.getMsg();
  }

  public static DLException wrap(Throwable throwable) {
    if (throwable instanceof DLException) {
      return (DLException) throwable;
    }
    return new DLException(DLExceptionType.COMMON_SERVER_ERROR, throwable);
  }

  public static DLException wrap(Throwable throwable, DLExceptionType type) {
    return new DLException(type, throwable);
  }
}
