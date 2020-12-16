package net.scales.dispatcher.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import net.scales.dispatcher.model.SubscriptionEntity;

@Repository
public interface SubscriptionRepository extends PagingAndSortingRepository<SubscriptionEntity, Long> {

    public List<SubscriptionEntity> findByEventAndUrl(String event, String url);
    public List<SubscriptionEntity> findByEvent(String event);

}