package com.ej.restaurant.enums;

public enum DLExceptionType {
  SUCCESS(100000, "ok"),
  COMMON_SERVER_ERROR(100001, "网络错误", ELogType.ERROR),
  COMMON_ILLEGAL_ARGUMENT(100002, "参数错误"),
  NOT_LOGIN(100003, "请登录"),
  USER_REGISTERED(100005, "您已经注册过, 请直接登录"),
  ARGUMENT_MISS(100006, "填写的信息有误或不全, 请再次检查"),
  ADMIN_PERMISSION_DENY(100008, "没有权限"),
  USER_MANUAL_REGISTERED(100009, "请到登录页面重置密码"),
  VCODE_MISMATCH(100011, "验证码错误"),
  LOGINNAME_PW_ERROR(100013, "用户名、密码错误"),
  CUST_HAVENOT_REGISTED(1000011, "请您先注册");



  private long code;
  private String msg;
  private ELogType eLogType;

  DLExceptionType(long c, String m) {
    code = c;
    msg = m;
    this.eLogType = ELogType.WARNING;
  }

  DLExceptionType(long c, String m, ELogType eLogType) {
    code = c;
    msg = m;
    this.eLogType = eLogType;
  }

  public long getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public ELogType geteLogType() {
    return eLogType;
  }

  public static enum ELogType {
    WARNING,
    ERROR
  }
}
