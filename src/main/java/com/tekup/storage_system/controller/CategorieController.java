package com.tekup.storage_system.controller;

import com.tekup.storage_system.model.Categorie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategorieController {

    @GetMapping("/categories")
    public Categorie[] getCategories() {
        return Categorie.values();
    }
}
