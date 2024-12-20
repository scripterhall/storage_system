package com.tekup.storage_system.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekup.storage_system.model.EnumFieldType;
import com.tekup.storage_system.model.FieldTypeTO;
import com.tekup.storage_system.model.InstanceTemplate;
import com.tekup.storage_system.model.InstanceTemplateTO;
import com.tekup.storage_system.model.Template;
import com.tekup.storage_system.model.TemplateTO;
import com.tekup.storage_system.service.InstanceTemplateService;
import com.tekup.storage_system.service.TemplateService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/instances")
public class InstanceTemplateController {

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private final UserController userController;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InstanceTemplateService instanceTemplateService;

    @GetMapping("/{templateId}")
    public String listInstances(Authentication authentication, @PathVariable Long templateId, Model model) {
        Optional<Template> optionalTemplate = templateService.getTemplateById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();
            if (!template.getUser().equals(userController.getCurrentUser(authentication))) {
                return "redirect:/error";
            }

            try {
                TemplateTO templateTO = new TemplateTO();
                List<FieldTypeTO> templateFields = objectMapper.readValue(template.getFields(), new TypeReference<List<FieldTypeTO>>() {
                });
                templateTO.setFields(templateFields);
                // Fetch instances of the template
                List<InstanceTemplate> instances = instanceTemplateService.getInstancesByTemplate(template);

                // Deserialize fields of each instance from JSON
                ObjectMapper objectMapper = new ObjectMapper();
                List<InstanceTemplateTO> instanceTemplateTOs = new ArrayList<>();
                for (InstanceTemplate instance : instances) {
                    // Deserialize the JSON string in fields into a list of TemplateField objects
                    List<FieldTypeTO> fields = objectMapper.readValue(instance.getFields(), new TypeReference<List<FieldTypeTO>>() {
                    });
                    InstanceTemplateTO instanceTemplateTO = new InstanceTemplateTO();
                    instanceTemplateTO.setFields(fields);
                    instanceTemplateTO.setTemplate(template);
                    instanceTemplateTO.setId(instance.getId());
                    instanceTemplateTOs.add(instanceTemplateTO);
                }

                // Add the template and instances to the model
                model.addAttribute("template", templateTO);
                model.addAttribute("templateFields", templateTO.getFields());
                model.addAttribute("templateId", template.getId());
                model.addAttribute("instances", instanceTemplateTOs);
                model.addAttribute("content", "instanceTemplate/list");
                model.addAttribute("user", userController.getCurrentUser(authentication));

                return "base";
            } catch (IOException e) {
                e.printStackTrace(); // Handle error while deserializing
            }
        }
        return "redirect:/error";
    }

    @GetMapping("/new/{templateId}")
    public String newInstance(Authentication authentication, @PathVariable Long templateId, Model model) {
        Optional<Template> optionalTemplate = templateService.getTemplateById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();
            if (!template.getUser().equals(userController.getCurrentUser(authentication))) {
                return "redirect:/error";
            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<FieldTypeTO> templateFields = null;
            try {
                // Convert the 'fields' JSON String to a List<TemplateField>
                templateFields = objectMapper.readValue(template.getFields(), new TypeReference<List<FieldTypeTO>>() {
                });
            } catch (IOException e) {
                e.printStackTrace(); // Handle exception
            }
            InstanceTemplate instanceTemplate = new InstanceTemplate();
            instanceTemplate.setTemplate(template);

            model.addAttribute("instanceTemplate", instanceTemplate);
            model.addAttribute("fields", templateFields);
            model.addAttribute("content", "instanceTemplate/form");
            model.addAttribute("user", userController.getCurrentUser(authentication));

            return "base"; // Render the "template/newInstance.html" page
        }
        return "redirect:/error";
    }

    // Handle form submission for creating a new instance
    @PostMapping("/new/{templateId}")
    public String createInstance(Authentication authentication, @PathVariable Long templateId, @ModelAttribute InstanceTemplateTO instanceTemplateTO) {
        Optional<Template> optionalTemplate = templateService.getTemplateById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();
            if (!template.getUser().equals(userController.getCurrentUser(authentication))) {
                return "redirect:/error";
            }
            try {
                InstanceTemplate instanceTemplate = new InstanceTemplate();
                instanceTemplate.setTemplate(template);

                TemplateTO templateTO=new TemplateTO();
                templateTO.setFields(objectMapper.readValue(template.getFields(), new TypeReference<List<FieldTypeTO>>() {}));
                // Handle file upload for fields of type 'file'
                for (int i = 0; i < instanceTemplateTO.getFields().size(); i++) {
                    FieldTypeTO field = instanceTemplateTO.getFields().get(i);

                    if (templateTO.getFields().get(i).getType().equals(EnumFieldType.file.toString())) {
                        System.out.println("field valuee: "+field.getValue());
                        MultipartFile file = (MultipartFile) field.getValue(); // Get the corresponding file for this field

                        if (!file.isEmpty()) {
                            // Get the original filename
                            String filename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                            Path targetLocation = Paths.get(uploadDir).resolve(filename);

                            // Save the file to the upload directory
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                            // You can store the file path or filename in the database or in fieldsJson
                            field.setValue(filename);  // Update the field's value with the file name or path
                        }
                    }
                }
                String fieldsJson = objectMapper.writeValueAsString(instanceTemplateTO.getFields());
                instanceTemplate.setFields(fieldsJson);
                instanceTemplateService.saveInstanceTemplate(instanceTemplate);
                return "redirect:/instances/" + templateId; // Redirect to the list of instances for the template
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/error";
            }
        }
        return "redirect:/error";
    }

    @GetMapping("/delete/{id}")
    public String deleteInstanceTemplate(Authentication authentication, @PathVariable Long id) {
        Optional<InstanceTemplate> instanceTemplateOptional = instanceTemplateService.getInstanceTemplateById(id);
        if (instanceTemplateOptional.isPresent()) {
            Long templateId = instanceTemplateOptional.get().getTemplate().getId();
            instanceTemplateService.deleteInstanceTemplate(id);
            return "redirect:/instances/" + templateId;
        }
        return "redirect:/error";
    }

}
