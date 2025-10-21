package com.davcm.spring_jwt.Projection;

import com.davcm.spring_jwt.Model.Role;
import com.davcm.spring_jwt.Model.Total;

public interface UserProjection {
    Long getId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getCarrera();
    Role getRole();
}
