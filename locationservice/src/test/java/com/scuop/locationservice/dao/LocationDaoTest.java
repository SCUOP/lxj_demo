package com.scuop.locationservice.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LocationDaoTest {
    @Autowired
    private LocationDao locationDao;

    @Test
    void testSelectRouteById() {
        System.out.println(locationDao.selectRouteById((long) 3));
    }
}
