package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * [xToOne(ManyToOne, OneToOne) 관계에서 어떻게 성능최적화를 할 수 있을지 실습함]
 * Order 조회
 * Order -> Member 조회 (ManyToOne 관계)
 * Order -> Delivery 조회 (OneToOne 관계)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 주문을 다 돌고 옴
        for (Order order : all) { // 강제로 lazy 로딩하기!
            order.getMember() // 여기까지는 프록시 객체임 (진짜가 아님)
                    .getName(); // 실제 name을 끌고 와야 해서 lazy가 강제 초기화되어서 하이버네이트가 다 가져오게 된다.
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }
}
