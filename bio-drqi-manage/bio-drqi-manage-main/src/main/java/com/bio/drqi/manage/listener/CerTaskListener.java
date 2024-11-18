package com.bio.drqi.manage.listener;

import java.util.function.Supplier;

public interface CerTaskListener<T> {

    void notice(EventType eventType, Supplier<T> supplier);

}
