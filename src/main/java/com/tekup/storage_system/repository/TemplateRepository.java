package com.tekup.storage_system.repository;

import com.tekup.storage_system.model.Template;
import com.tekup.storage_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    public List<Template> findByUser(User user);
}
