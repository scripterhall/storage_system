package com.tekup.storage_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String renamedFile;
    private String size;
    private String type;
    private String path;

    @ManyToOne
    @ToString.Exclude
    private Folder folder;


}