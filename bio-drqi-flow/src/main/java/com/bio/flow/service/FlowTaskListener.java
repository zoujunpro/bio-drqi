package com.bio.flow.service;

import com.bio.flow.enums.EventType;

import java.util.function.Supplier;

public interface FlowTaskListener<T> {

    void notice(EventType eventType, Supplier<T> supplier);

}
