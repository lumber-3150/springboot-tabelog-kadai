package com.example.nagoyameshi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StripeService stripeService;

    public SubscriptionController(UserRepository userRepository, RoleRepository roleRepository, StripeService stripeService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.stripeService = stripeService;
    }

    /**
     * サブスクリプション契約ページ
     */
    @GetMapping
    public String showSubscriptionPage(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            HttpServletRequest request) {

        User user = userDetailsImpl.getUser();

        if (user.getSubscriptionId() != null) {
            return "redirect:/";
        }

        String checkoutUrl =
                stripeService.createSubscriptionSession(
                    user,
                    request
                );

        return "redirect:" + checkoutUrl;
    }

    /**
     * Stripe決済完了後
     */
    @GetMapping("/success")
    public String subscriptionSuccess(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            String session_id,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            /*
             * Checkout Sessionを取得
             */
            Session session =
                    Session.retrieve(session_id);
            /*
             * Stripeが発行したSubscription ID
             */
            String subscriptionId =
                    session.getSubscription();
            
            String customerId =
                    session.getCustomer();
            /*
             * ログインユーザー
             */
            User user = userDetailsImpl.getUser();

            /*
             * Userテーブルへ保存
             */
            user.setSubscriptionId(subscriptionId);
            user.setCustomerId(customerId);
            
            // Role ID = 3 のRoleを取得
            Role paidMemberRole = roleRepository.findById(3)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Role ID 3 が存在しません。"
                    ));

            // 有料会員のRoleを設定
            user.setRole(paidMemberRole);
            
            userRepository.save(user);
            
            //強制ログアウト
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            new SecurityContextLogoutHandler()
                    .logout(request, response, authentication);

            /*
             * ログイン画面へ
             */
            return "redirect:/login";
//            return "redirect:/";

        } catch (StripeException e) {
            e.printStackTrace();

            return "redirect:/subscription";
        }
    }
    
    @PostMapping("/cancel")
    public String cancelSubscription(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {

        User user = userDetailsImpl.getUser();

        // Subscription IDが存在しない場合
        if (user.getSubscriptionId() == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "サブスクリプション契約がありません。"
            );

            return "redirect:/subscription";
        }

        // StripeのSubscriptionを解約
        boolean canceled =
                stripeService.cancelSubscription(
                        user.getSubscriptionId()
                );

        if (!canceled) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "サブスクリプションの解約に失敗しました。"
            );
            
            boolean deleted =
                    stripeService.deletePaymentMethods(
                            user.getCustomerId()
                    );

            if (!deleted) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "サブスクリプションは解約されましたが、カード情報の削除に失敗しました。"
                );

                return "redirect:/";
            }

            return "redirect:/subscription";
        }

        // 通常会員のRoleを取得
        Role normalMemberRole =
                roleRepository.findById(1)
                        .orElseThrow(() ->
                            new IllegalArgumentException(
                                "Role ID 1 が存在しません。"
                            )
                        );

        // 通常会員に戻す
        user.setRole(normalMemberRole);
        // Subscription IDを削除
        user.setSubscriptionId(null);

        // DB更新
        userRepository.save(user);
        
        /* 強制ログアウト */
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        new SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "サブスクリプションを解約しました。"
        );

        return "redirect:/login";
//        redirectAttributes.addFlashAttribute(
//                "successMessage",
//                "サブスクリプションを解約しました。"
//        );
//
//        return "redirect:/";
    }
    
    @GetMapping("/card")
    public String redirectToCustomerPortal(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        // ログイン中のユーザーを取得
        User user = userDetailsImpl.getUser();

        // Stripe Customer IDを取得
        String customerId = user.getCustomerId();

        // Customer PortalのURLを作成
        String portalUrl =
                stripeService.createCustomerPortalSession(customerId);

        // Stripeのカード情報編集画面へ移動
        return "redirect:" + portalUrl;
    }
    
}