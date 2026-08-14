package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
public class HomeController {
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    public HomeController(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Restaurant> newRestaurants = restaurantRepository.findTop10ByOrderByCreatedAtDesc();
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("newRestaurants", newRestaurants);
        model.addAttribute("categories", categories);

        return "index";
    }
}
