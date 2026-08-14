package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>{
	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
	
	public Page<Restaurant> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);    
    public Page<Restaurant> findByAddressLike(String area, Pageable pageable);
    public Page<Restaurant> findByLowestPriceLessThanEqual(Integer lowestprice, Pageable pageable);  
    
    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
    
    @Query("""
            SELECT cr.restaurant
            FROM CategoryRestaurant cr
            WHERE cr.category.id = :categoryId
        """)
        public Page<Restaurant> findByCategoryId(
                @Param("categoryId") Integer categoryId,
                Pageable pageable);
}
