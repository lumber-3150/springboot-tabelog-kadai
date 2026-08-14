package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	@Transactional
	public void create(Restaurant restaurant, User user, ReviewRegisterForm reviewRegisterFrom) {
		Review review = new Review();
		review.setScore(reviewRegisterFrom.getScore());
		review.setContent(reviewRegisterFrom.getContent());
		review.setRestaurant(restaurant);
		review.setUser(user);
		
		reviewRepository.save(review);
	}
	
	@Transactional
	public void update(ReviewEditForm reviewEditForm, Review review) {
//		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		review.setScore(reviewEditForm.getScore());
		review.setContent(reviewEditForm.getContent());
		
		reviewRepository.save(review);
	}
	
	@Transactional
	public boolean hasUserAlreadyReviewed(Restaurant restaurant, User user) {
		if(reviewRepository.findByRestaurantAndUser(restaurant, user) != null) {
			return true;
		}else {
			return false;
		}
	}
}
