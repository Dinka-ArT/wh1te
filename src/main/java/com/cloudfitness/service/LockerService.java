package com.cloudfitness.service;

import com.cloudfitness.entity.Locker;
import java.util.List;

public interface LockerService {
    List<Locker> getLockersByUserId(Integer userId);
}


