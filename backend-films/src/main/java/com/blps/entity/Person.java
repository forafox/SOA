package com.blps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Getter
@Setter

public class Person {


    private String name;


    private Date birthday;


    private double height;


    private long weight;

    private String passportID;
}
