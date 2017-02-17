package com.example;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Baz implements Serializable {

  int count;


  Date sendDate = new Date();
  Date recvDate;
}