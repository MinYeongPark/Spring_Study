package com.fastcampus.batchcampus.batch;

public interface ItemProcessor<I, O> { // I : input, O : output
    O process(I item);
}
