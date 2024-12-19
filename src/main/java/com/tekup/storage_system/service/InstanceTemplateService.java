package com.tekup.storage_system.service;

import com.tekup.storage_system.model.InstanceTemplate;
import com.tekup.storage_system.model.Template;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.repository.InstanceTemplateRepository;
import com.tekup.storage_system.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstanceTemplateService {
    @Autowired
    public InstanceTemplateRepository instanceTemplateRepository;

    public InstanceTemplate saveInstanceTemplate(InstanceTemplate instanceTemplate) {
        return instanceTemplateRepository.save(instanceTemplate);
    }

    public Optional<InstanceTemplate> getInstanceTemplateById(Long id) {
        return instanceTemplateRepository.findById(id);
    }

    public void deleteInstanceTemplate(Long id) {
        instanceTemplateRepository.deleteById(id);
    }

    public List<InstanceTemplate> getInstancesByTemplate(Template template){
        return instanceTemplateRepository.findByTemplate(template);
    }
}
