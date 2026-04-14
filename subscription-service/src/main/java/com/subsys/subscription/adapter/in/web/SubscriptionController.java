package com.subsys.subscription.adapter.in.web;

import com.subsys.subscription.api.CreateSubscriptionRequest;
import com.subsys.subscription.api.SubscriptionResponse;
import com.subsys.subscription.application.command.CreateSubscriptionCommand;
import com.subsys.subscription.application.mapper.SubscriptionWebMapper;
import com.subsys.subscription.application.port.in.CancelSubscriptionUseCase;
import com.subsys.subscription.application.port.in.CreateSubscriptionUseCase;
import com.subsys.subscription.application.port.in.GetSubscriptionUseCase;
import com.subsys.subscription.domain.Subscription;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final GetSubscriptionUseCase getSubscriptionUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final SubscriptionWebMapper subscriptionWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SubscriptionResponse create(@Valid @RequestBody CreateSubscriptionRequest request) {
        CreateSubscriptionCommand command = subscriptionWebMapper.toCommand(request);

        Subscription subscription = createSubscriptionUseCase.createSubscription(command);

        return subscriptionWebMapper.toResponse(subscription, "Subscription is being created");
    }

    @GetMapping("/{id}")
    public SubscriptionResponse get(@PathVariable("id") UUID id) {
        Subscription subscription = getSubscriptionUseCase.getSubscription(id);

        return subscriptionWebMapper.toResponse(subscription, "Subscription fetched");
    }

    @PostMapping("/{id}/cancel")
    public SubscriptionResponse cancel(@PathVariable("id") UUID id) {
        Subscription subscription = cancelSubscriptionUseCase.cancelSubscription(id);

        return subscriptionWebMapper.toResponse(subscription, "Subscription cancelled");
    }
}
