package com.tekup.storage_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TemplateTO {

    private String title;

    private List<FieldTypeTO> fields = new ArrayList<>();

}
