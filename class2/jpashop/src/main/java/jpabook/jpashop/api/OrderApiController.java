package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // 강제 초기화
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    private final OrderQueryService orderQueryService;

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return orderQueryService.ordersV3();
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit )
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        for (Order order : orders) {
            System.out.println("order ref= " + order + " id=" + order.getId());;
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        // 이 코드에 Cannot resolve method 'getOrderId' in 'T' 오류가 나서.. 다른 분의 코드로 작업했다.
//        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
//
//        return flats.stream()
//                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
//                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
//                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
//                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
//                        e.getKey().getAddress(), e.getValue()))
//                .collect(toList());


        // 출처 : https://www.inflearn.com/questions/1077515/%EC%A7%88%EB%AC%B8x-orderv6-for%EB%AC%B8%EC%9C%BC%EB%A1%9C-%EB%B0%9C%EB%9D%BC%EB%82%B4%EA%B8%B0
        List<OrderFlatDto> orderFlats = orderQueryRepository.findAllByDto_flat();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = new HashMap<>();
        Map<Long, OrderQueryDto> orderMap = new HashMap<>();

        orderFlats.forEach(orderFlat -> { // OrderFlatDto -> OrderQueryDto 로 변환하는 코드
            Long orderId = orderFlat.getOrderId();
            if(orderMap.get(orderId) == null){
                orderMap.put(orderId,new OrderQueryDto(orderId, orderFlat.getName(), orderFlat.getOrderDate(), orderFlat.getOrderStatus(), orderFlat.getAddress()));
            }

            if(orderItemMap.get(orderId) == null){
                orderItemMap.put(orderId,new ArrayList<OrderItemQueryDto>());
            }
            orderItemMap.get(orderId).add(new OrderItemQueryDto(orderId, orderFlat.getItemName(), orderFlat.getOrderPrice(), orderFlat.getCount()));
        });

        orderItemMap.forEach((orderId, orderItem)->{
            orderMap.get(orderId).setOrderItems(orderItem);
        });

        return new ArrayList<OrderQueryDto>(orderMap.values());
    }
}
