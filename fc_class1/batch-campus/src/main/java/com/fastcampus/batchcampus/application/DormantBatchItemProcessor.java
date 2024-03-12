package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.batch.ItemProcessor;
import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DormantBatchItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) {
        final boolean isDormantTarget = LocalDateTime.now()
                .minusDays(365)
                .isAfter(item.getLoginAt().toLocalDate().atStartOfDay());

        if (isDormantTarget) {
            item.setStatus(Customer.Status.DORMANT);
            return item;
        } else {
            return null;
        }
    }
}
