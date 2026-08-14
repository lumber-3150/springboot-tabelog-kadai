package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

import jakarta.transaction.Transactional;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository =favoriteRepository;
	}
	
	@Transactional
	public void create(Restaurant restaurant, User user) {
		Favorite favorite = new Favorite();
		
		favorite.setRestaurant(restaurant);
		favorite.setUser(user);
		
		favoriteRepository.save(favorite);
	}
	
	public boolean isFavorite(Restaurant restaurant, User user) {
		if(favoriteRepository.findByRestaurantAndUser(restaurant, user) != null) {
			return true;
		}else {
			return false;
		}
	}
}
