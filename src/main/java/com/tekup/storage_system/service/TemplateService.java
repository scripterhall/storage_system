package com.tekup.storage_system.service;

import com.tekup.storage_system.model.Template;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {
    @Autowired
    public TemplateRepository templateRepository;

    public List<Template> getTemplates(User user) {
        return templateRepository.findByUser(user);
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public Optional<Template> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    public boolean deleteTemplate(Long id) {
        Optional<Template> template = templateRepository.findById(id);
        if (template.isPresent()) {
            templateRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
