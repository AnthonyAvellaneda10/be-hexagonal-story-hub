package com.uni.pe.storyhub.infrastructure.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ToastIdGenerator {
    private final AtomicInteger counter = new AtomicInteger(0);

    public int nextId() {
        return counter.incrementAndGet();
    }
}
