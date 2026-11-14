package com.co.support.it.support_ticket_system.business.requester.entity;

import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requesters")
@Getter
@Setter
public class RequesterEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requester_id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String surname;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;
}
