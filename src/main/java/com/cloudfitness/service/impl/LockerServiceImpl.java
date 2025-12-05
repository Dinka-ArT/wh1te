package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Locker;
import com.cloudfitness.mapper.LockerMapper;
import com.cloudfitness.service.LockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LockerServiceImpl implements LockerService {
    @Autowired
    private LockerMapper lockerMapper;

    @Override
    public List<Locker> getLockersByUserId(Integer userId) {
        return lockerMapper.selectByUserId(userId);
    }
}


