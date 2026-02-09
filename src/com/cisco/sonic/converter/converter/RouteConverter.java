package com.cisco.sonic.converter.converter;

import com.cisco.sonic.converter.model.*;
import com.cisco.sonic.converter.util.NetworkUtils;
import java.util.*;

/**
 * Converts Cisco routing configurations to SONiC format
 */
public class RouteConverter {
    
    /**
     * Convert static routes from Cisco to SONiC
     */
    public void convert(CiscoConfig ciscoConfig, SonicConfig sonicConfig) {
        for (RouteConfig route : ciscoConfig.getStaticRoutes()) {
            String prefix = NetworkUtils.toCidr(route.getNetwork(), route.getNetmask());
            String routeKey = "0.0.0.0/0|" + prefix + "|" + route.getNextHop();
            
            Map<String, Object> routeData = new LinkedHashMap<>();
            routeData.put("nexthop", route.getNextHop());
            routeData.put("ifname", "");
            
            if (route.getAdminDistance() != null) {
                routeData.put("distance", String.valueOf(route.getAdminDistance()));
            }
            
            sonicConfig.addStaticRoute(routeKey, routeData);
        }
    }
}

