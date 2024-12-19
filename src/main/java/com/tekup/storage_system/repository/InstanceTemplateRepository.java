package com.tekup.storage_system.repository;

import com.tekup.storage_system.model.InstanceTemplate;
import com.tekup.storage_system.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceTemplateRepository  extends JpaRepository<InstanceTemplate, Long> {
    public List<InstanceTemplate> findByTemplate(Template template);
}
