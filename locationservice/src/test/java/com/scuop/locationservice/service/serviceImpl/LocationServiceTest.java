package com.scuop.locationservice.service.serviceImpl;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scuop.locationservice.domain.Location;
import com.scuop.locationservice.service.ILocationService;

@SpringBootTest
public class LocationServiceTest {
    @Autowired
    private ILocationService locationService;

    @Test
    void testGetRoutesByRouteId() {
        ArrayList<Long> routes = new ArrayList<>();
        routes.add((long) 1);
        routes.add((long) 2);
        System.out.println(routes);
        System.out.println(locationService.getRoutesByRouteId(routes));
    }

    @Test
    void testGetRoutesId() throws Exception {
        System.out.println(locationService.getRoutesId("test", 120));
    }

    @Test
    void testInsertBatch() {
        Location location1 = new Location((long) 4, (long) 2, 60, 120, "test", 2, "test", "test");
        Location location2 = new Location((long) 5, (long) 2, 60, 120, "test", 3, "test", "test");
        Location location3 = new Location((long) 6, (long) 2, 60, 120, "test", 4, "test", "test");
        Location location4 = new Location((long) 7, (long) 3, 60, 120, "test", 5, "test", "test");
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location1);
        locations.add(location2);
        locations.add(location3);
        locations.add(location4);
        try {
            locationService.saveBatch(locations);
        } catch (Exception e) {
            System.out.println(false);
        }

    }

    @Test
    void testGetLastOrder() {
        try {
            System.out.println(locationService.getLastOrder((long) 2));
        } catch (Exception e) {
            System.out.println(false);
        }
    }

    @Test
    void testDeleteLastOrder() {
        locationService.deleteLastOrder((long) 1);
    }

    @Test
    void test() {
        System.out.println(Location.DESCRIPTION);
    }

    @Test
    void testExistRoute() {
        System.out.println(locationService.existRoute((long) 2));
    }
}
