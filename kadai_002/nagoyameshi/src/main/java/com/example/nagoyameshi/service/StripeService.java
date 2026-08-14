package com.example.nagoyameshi.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	
	@Value("${stripe.api-key}")
    private String stripeApiKey;

    /**
     * Stripe Checkout Sessionを作成する
     *
     * @param user ログインユーザー
     * @param httpServletRequest リクエスト
     * @return Stripe Checkout Session ID
     */
    public String createSubscriptionSession(
            User user,
            HttpServletRequest httpServletRequest) {
        // Stripeのシークレットキー
    	Stripe.apiKey = stripeApiKey;
        try {
            String requestUrl =
                    httpServletRequest.getRequestURL().toString();

            /*
             * Checkout Sessionを作成
             */
            SessionCreateParams params =
                    SessionCreateParams.builder()

                    // サブスクリプション契約
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    // UserIdをStripe側へ渡す
                    .putMetadata(
                        "userId",
                        user.getId().toString()
                    )
                    // Stripe Checkout完了後
                    .setSuccessUrl(
                        requestUrl.replace(
                            "/subscription",
                            ""
                        )
                        + "/subscription/success?session_id={CHECKOUT_SESSION_ID}"
                    )
                    // キャンセル時
                    .setCancelUrl(
                        requestUrl.replace(
                            "/subscription",
                            ""
                        )
                        + "/subscription"
                    )
                    // 商品
                    .addLineItem(
                        SessionCreateParams.LineItem.builder()
                        /*
                         * Stripe Dashboardで作成した
                         * Price IDを指定
                         */
                        .setPrice(
                            "price_1U3RsaQU6sJSufQNLJmdWsPW"
                        )
                        .setQuantity(1L)
                        .build()
                    )
                    
                    .build();
            /*
             * StripeにCheckout Sessionを作成
             */
            Session session = Session.create(params);

            /*
             * Checkout Session IDを返す
             */
//            return session.getId();
            return session.getUrl();

        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public boolean cancelSubscription(String subscriptionId) {

        Stripe.apiKey = stripeApiKey;

        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);

            subscription.cancel();

            return true;

        } catch (StripeException e) {
            e.printStackTrace();

            return false;
        }
    }
    
    public String createCustomerPortalSession(String customerId) {

        Stripe.apiKey = stripeApiKey;

        try {

            com.stripe.param.billingportal.SessionCreateParams params =
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(customerId)
                            .setReturnUrl("http://localhost:8080/")
                            .build();

            com.stripe.model.billingportal.Session session =
                    com.stripe.model.billingportal.Session.create(params);

            return session.getUrl();

        } catch (StripeException e) {
            e.printStackTrace();

            return "";
        }
    }
    
    public boolean deletePaymentMethods(String customerId) {

        Stripe.apiKey = stripeApiKey;

        try {
            PaymentMethodListParams params =
                    PaymentMethodListParams.builder()
                            .setCustomer(customerId)
                            .setType(PaymentMethodListParams.Type.CARD)
                            .build();

            PaymentMethodCollection paymentMethods =
                    PaymentMethod.list(params);

            for (PaymentMethod paymentMethod : paymentMethods.getData()) {
                paymentMethod.detach();
            }

            return true;

        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
