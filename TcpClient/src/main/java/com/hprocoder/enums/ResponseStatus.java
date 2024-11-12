package com.hprocoder.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {

  OK(1),

  KO(2);

  private final int value;

  public static ResponseStatus fromValue(int value){
    for(ResponseStatus type : values()){
      if(type.getValue() == value){
        return type;
      }
    }
    throw new IllegalArgumentException("UNKOWN ID");
  }
}
