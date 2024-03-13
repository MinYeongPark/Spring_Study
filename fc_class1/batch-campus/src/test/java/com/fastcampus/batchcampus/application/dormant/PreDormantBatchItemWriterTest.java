package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.EmailProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreDormantBatchItemWriterTest {

    private PreDormantBatchItemWriter preDormantBatchItemWriter;

    @Test
    @DisplayName("1주일 뒤에 휴면계정전환 예정자라고 이메일을 전송해야 한다.")
    void test1() {

        // given
        final EmailProvider mockEmailProvider = mock(EmailProvider.class);

        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com");
        this.preDormantBatchItemWriter = new PreDormantBatchItemWriter(mockEmailProvider);

        // when
        preDormantBatchItemWriter.write(customer);

        // then
        verify(mockEmailProvider, atLeastOnce()).send(any(), any(), any()); // send() 가 최소한 한번이라도 불렸는지
    }
}