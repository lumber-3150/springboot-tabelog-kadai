package com.example.nagoyameshi.controller;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository; 
	private final RestaurantRepository restaurantRepository;
	private final ReservationService reservationService;
    
    public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, ReservationService reservationService) {        
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.reservationService = reservationService;
    }    

    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        model.addAttribute("reservationPage", reservationPage);         
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        
        return "reservations/index";
    }
    
    @PostMapping("/restaurants/{id}/reservations/create")
    public String create(
            @PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @ModelAttribute @Validated ReservationInputForm reservationInputForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        if (!bindingResult.hasErrors() && reservationInputForm.getReservedDatetime() != null) {

            LocalTime reservationTime 	= reservationInputForm.getReservedDatetime().toLocalTime();
            LocalTime openingTime 		= restaurant.getOpening_time().toLocalTime();
            LocalTime closingTime 		= restaurant.getClosing_time().toLocalTime();

            if (reservationTime.isBefore(openingTime)
                    || reservationTime.isAfter(closingTime)) {

                bindingResult.rejectValue(
                        "reservedDatetime",
                        "error.reservedDatetime",
                        "予約時間は"
                                + openingTime
                                + "～"
                                + closingTime
                                + "の間で選択してください。"
                );
            }
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            return "restaurants/show";
        }

        User user = userDetailsImpl.getUser();

        ReservationRegisterForm reservationRegisterForm =
                new ReservationRegisterForm(
                        restaurant.getId(),
                        user.getId(),
                        reservationInputForm.getReservedDatetime(),
                        reservationInputForm.getNumberOfPeople()
                );

        reservationService.create(reservationRegisterForm);

        redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました。");

        return "redirect:/reservations";
    }
    
    @PostMapping("/reservations/{reservationId}/delete")
    public String delete(
            @PathVariable(name = "reservationId") Integer reservationId,
            RedirectAttributes redirectAttributes) {

        reservationRepository.deleteById(reservationId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "予約をキャンセルしました。"
        );

        return "redirect:/reservations";
	}
}
