package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	private final RestaurantRepository restaurantRepository;        
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final FavoriteRepository favoriteRepository;
	private final CategoryRepository categoryRepository;
    
    public RestaurantController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
			ReviewService reviewService, FavoriteRepository favoriteRepository, CategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.favoriteRepository = favoriteRepository;
		this.categoryRepository = categoryRepository;
    }     
  
    @GetMapping
    public String index(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "area", required = false) String area,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "price", required = false) Integer lowestprice,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Direction.ASC
            ) Pageable pageable,
            Model model) {

        Page<Restaurant> restaurantPage;

        if (keyword != null && !keyword.isEmpty()) {
            restaurantPage =
                    restaurantRepository.findByNameLikeOrAddressLike(
                            "%" + keyword + "%",
                            "%" + keyword + "%",
                            pageable);
        } else if (area != null && !area.isEmpty()) {
            restaurantPage =
                    restaurantRepository.findByAddressLike(
                            "%" + area + "%",
                            pageable);
        } else if (categoryId != null) {
            restaurantPage =
                    restaurantRepository.findByCategoryId(
                            categoryId,
                            pageable);
        } else if (lowestprice != null) {
            restaurantPage =
                    restaurantRepository.findByLowestPriceLessThanEqual(
                            lowestprice,
                            pageable);
        } else {
            restaurantPage = restaurantRepository.findAll(pageable);
        }

        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("price", lowestprice);

        return "restaurants/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        
//        model.addAttribute("restaurant", restaurant);         
//        model.addAttribute("reservationInputForm", new ReservationInputForm());
        
        boolean reviewFlag = false;
        boolean favoriteFlag = false;
        
        List<Review> reviewList = reviewRepository.findTop6ByRestaurantOrderByCreatedAtDesc(restaurant);
        
        Long totalCount = reviewRepository.countByRestaurant(restaurant);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("restaurant", restaurant);
        
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		
		
		if(userDetailsImpl != null) {
		Favorite favorite = favoriteRepository.findByRestaurantAndUser(restaurant, userDetailsImpl.getUser());
		if(favorite != null) {
        	favoriteFlag = true;
        }
		
		Review review = reviewRepository.findByRestaurantAndUser(restaurant, userDetailsImpl.getUser());
        if(review != null) {
        	reviewFlag = true;
        }
        model.addAttribute("reviewFlag", reviewFlag);        
        
        model.addAttribute("favoriteFlag", favoriteFlag);
        model.addAttribute("favorite", favorite);
        
		}
        
        return "restaurants/show";
    }
    
}