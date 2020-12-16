package net.scales.dispatcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import net.scales.dispatcher.model.SubscriptionEntity;
import net.scales.dispatcher.repository.SubscriptionRepository;

@Component
public class SubscriptionInit implements ApplicationRunner {

    private SubscriptionRepository repository;

    @Autowired
    public SubscriptionInit(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long count = repository.count();

        if (count != 0) {
            return;
        }

        repository.save(new SubscriptionEntity("invoice", "http://localhost:10088/webhook"));
        repository.save(new SubscriptionEntity("order", "http://localhost:10088/webhook"));
    }

}