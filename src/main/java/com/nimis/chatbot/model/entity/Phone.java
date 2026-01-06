package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;


    private String phone_1;
    private String phone_2;
    private String phone_3;
    private String phone_4;
    private String phone_5;
    private String phone_6;
    private String phone_7;
    private String phone_8;
    private String phone_9;
    private String phone_10;

}
