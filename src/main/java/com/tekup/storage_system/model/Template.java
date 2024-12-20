package com.tekup.storage_system.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;

    @OneToMany(mappedBy = "template")
    private List<InstanceTemplate> instances;

    private String fields;

}
