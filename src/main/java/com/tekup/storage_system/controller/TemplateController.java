package com.tekup.storage_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekup.storage_system.model.*;
import com.tekup.storage_system.service.InstanceTemplateService;
import com.tekup.storage_system.service.TemplateService;
import com.tekup.storage_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.descriptor.jdbc.JsonAsStringJdbcType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/templates")
public class TemplateController {
    @Autowired
    private final UserController userController;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InstanceTemplateService instanceTemplateService;

    @GetMapping
    public String listTemplates(Authentication authentication, Model model) {
        List<Template> templates = templateService.getTemplates(userController.getCurrentUser(authentication));

        Map<Integer, List<FieldTypeTO>> fieldsList = new HashMap<>(Collections.emptyMap());
        IntStream.range(0, templates.size()).forEach(index -> {
            List<FieldTypeTO> fields = Collections.emptyList();
            try {
                fields = objectMapper.readValue(templates.get(index).getFields(), new TypeReference<List<FieldTypeTO>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();  // Handle the error properly
            }
            fieldsList.put(index, fields);
        });

        model.addAttribute("fieldsList", fieldsList);
        model.addAttribute("templates", templates);
        return "template/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("template", new Template());
        model.addAttribute("fieldTypes", EnumFieldType.values());
        return "template/form";
    }

    @PostMapping
    public String saveTemplate(Authentication authentication, @ModelAttribute TemplateTO templateTO) {
        try {

            templateTO.getFields().forEach(field -> {
                System.out.println("Field Label: " + field.getLabel());
                System.out.println("Field Type: " + field.getType());
            });
            // Convert the list of fields to JSON if necessary
            ObjectMapper objectMapper = new ObjectMapper();
            String fieldsJson = objectMapper.writeValueAsString(templateTO.getFields());

            // Save the JSON string and other template data
            Template template = new Template();
            template.setTitle(templateTO.getTitle());
            template.setFields(fieldsJson);  // Save the fields as a JSON string
            template.setUser(userController.getCurrentUser(authentication));
            templateService.saveTemplate(template);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }

        return "redirect:/templates";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Template template = templateService.getTemplateById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template introuvable avec l'ID : " + id));
        model.addAttribute("template", template);
        return "template/form"; // Vue : src/main/resources/templates/template/form.html
    }

    @PostMapping("/update/{id}")
    public String updateTemplate(@PathVariable Long id, @ModelAttribute("template") Template updatedTemplate) {
        Template existingTemplate = templateService.getTemplateById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template with ID : " + id + " does not exist."));

        existingTemplate.setTitle(updatedTemplate.getTitle());
        existingTemplate.setFields(updatedTemplate.getFields());
        existingTemplate.setUser(updatedTemplate.getUser());

        templateService.saveTemplate(existingTemplate);
        return "redirect:/templates";
    }

    @GetMapping("/delete/{id}")
    public String deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return "redirect:/templates";
    }

    //get list per template
    @GetMapping("/{templateId}/instances")
    public String getInstancesByTemplate(Authentication authentication,
                                         @PathVariable Long templateId, Model model) {
        Optional<Template> optionalTemplate = templateService.getTemplateById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();
            if (!template.getUser().equals(userController.getCurrentUser(authentication))) {
                return "redirect:/error";
            }
            List<InstanceTemplate> instances = instanceTemplateService.getInstancesByTemplate(template);

            model.addAttribute("template", template);
            model.addAttribute("instances", instances);

            return "instanceTemplate/list";
        }
        return "redirect:/error";
    }

}
