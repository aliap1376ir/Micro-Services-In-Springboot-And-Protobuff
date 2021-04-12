package ir.aliap1376ir.source.microservices.controller;

import ir.aliap1376ir.source.microservices.models.db.Category;
import ir.aliap1376ir.source.microservices.models.db.CategoryRepository;
import ir.aliap1376ir.source.microservices.models.transfer.Models;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class Controller {

    @Autowired
    private CategoryRepository categoryRepository;


    @GetMapping(path = "/categories")
    private void init() {
        newData("رمان");
        newData("داستان");
        newData("علمی");
        newData("تخیلی");
    }

    private void newData(String name) {
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
    }

    @PostMapping(path = "/categories/new/json")
    private Category newCategoryJson(@RequestBody Category categoryJson) {
        categoryJson = categoryRepository.save(categoryJson);
        return categoryJson;
    }

    @PostMapping(path = "/categories/new/proto")
    private Models.Category newCategoryProto(@RequestBody Models.Category categoryProto) {

        Category categoryJson = new Category();
        categoryJson.setId(categoryProto.getId());
        categoryJson.setName(categoryProto.getName());

        categoryJson = categoryRepository.save(categoryJson);

        categoryProto = Models.Category.newBuilder()
                .setId(categoryJson.getId())
                .setName(categoryJson.getName())
                .build();

        return categoryProto;
    }


    @GetMapping(path = "/categories/all/json")
    private ArrayList<Category> getAllCategoriesJson() {
        ArrayList<Category> categoriesJson = categoryRepository.findAll();

        return categoriesJson;
    }

    @GetMapping(path = "/categories/all/proto")
    private Models.Categories getAllCategoriesProto() {
        Models.Categories categoriesProto = Models.Categories.newBuilder().build();

        ArrayList<Category> categoriesJson = categoryRepository.findAll();
        for (Category categoryJson : categoriesJson) {

            Models.Category categoryProto = Models.Category.newBuilder()
                    .setId(categoryJson.getId())
                    .setName(categoryJson.getName())
                    .build();

            categoriesProto = categoriesProto.toBuilder().addArray(categoryProto).build();
        }

        return categoriesProto;
    }

    @GetMapping(path = "/categories/categories/findById")
    private Models.Category findCategoryById(@RequestParam long id){

        Category categoryJson = categoryRepository.findById(id);

        Models.Category categoryProto = Models.Category.newBuilder()
                .setId(categoryJson.getId())
                .setName(categoryJson.getName())
                .build();

        return categoryProto;
    }

}