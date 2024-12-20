package com.tekup.storage_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InstanceTemplateTO {
    private Template template;

    private List<FieldTypeTO> fields;

}