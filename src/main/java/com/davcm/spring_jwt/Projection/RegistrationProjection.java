package com.davcm.spring_jwt.Projection;

import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.User;

public interface RegistrationProjection {
    Long getId();
    User getUser();
    Event getEvent();
    String getAprobado();
    boolean isScan();
}
