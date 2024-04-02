package com.fastcampus.batchcampus.domain.repository;

import com.fastcampus.batchcampus.domain.Customer;
import org.springframework.data.domain.Pageable;


import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface CustomerRepository {

    List<Customer> findAll(Pageable pageable);

    Customer findById(Long id);

    class Fake implements CustomerRepository {
        @Override
        public List<Customer> findAll(Pageable pageable) {
            if (pageable.getPageNumber() == 0) {
                return List.of(
                        new Customer(0L, "A사", "A@compangy.com"),
                        new Customer(1L, "B사", "B@compangy.com"),
                        new Customer(2L, "C사", "C@compangy.com"),
                        new Customer(3L, "D사", "D@compangy.com"),
                        new Customer(4L, "E사", "E@compangy.com"),
                        new Customer(5L, "F사", "F@compangy.com"),
                        new Customer(6L, "G사", "G@compangy.com"),
                        new Customer(7L, "H사", "H@compangy.com"),
                        new Customer(8L, "I사", "I@compangy.com"),
                        new Customer(9L, "J사", "J@compangy.com")
                );
            } else if (pageable.getPageNumber() == 1) {
                return List.of(
                        new Customer(10L, "K사", "K@compangy.com"),
                        new Customer(11L, "L사", "L@compangy.com"),
                        new Customer(12L, "M사", "M@compangy.com"),
                        new Customer(13L, "N사", "N@compangy.com"),
                        new Customer(14L, "O사", "O@compangy.com"),
                        new Customer(15L, "P사", "P@compangy.com"),
                        new Customer(16L, "Q사", "Q@compangy.com"),
                        new Customer(17L, "R사", "R@compangy.com"),
                        new Customer(18L, "S사", "S@compangy.com"),
                        new Customer(19L, "T사", "T@compangy.com")
                );
            } else {
                return Collections.emptyList(); // 마무리
            }
        }

        @Override
        public Customer findById(Long id) {
            return Stream.of(
                    new Customer(0L, "A사", "A@compangy.com"),
                    new Customer(1L, "B사", "B@compangy.com"),
                    new Customer(2L, "C사", "C@compangy.com"),
                    new Customer(3L, "D사", "D@compangy.com"),
                    new Customer(4L, "E사", "E@compangy.com"),
                    new Customer(5L, "F사", "F@compangy.com"),
                    new Customer(6L, "G사", "G@compangy.com"),
                    new Customer(7L, "H사", "H@compangy.com"),
                    new Customer(8L, "I사", "I@compangy.com"),
                    new Customer(9L, "J사", "J@compangy.com"),
                    new Customer(10L, "K사", "K@compangy.com"),
                    new Customer(11L, "L사", "L@compangy.com"),
                    new Customer(12L, "M사", "M@compangy.com"),
                    new Customer(13L, "N사", "N@compangy.com"),
                    new Customer(14L, "O사", "O@compangy.com"),
                    new Customer(15L, "P사", "P@compangy.com"),
                    new Customer(16L, "Q사", "Q@compangy.com"),
                    new Customer(17L, "R사", "R@compangy.com"),
                    new Customer(18L, "S사", "S@compangy.com"),
                    new Customer(19L, "T사", "T@compangy.com")
            ).filter(it -> it.getId().equals(id))
                    .findFirst()
                    .orElseThrow();
        }
    }
}
