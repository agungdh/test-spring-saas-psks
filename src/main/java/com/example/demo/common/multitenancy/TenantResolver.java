package com.example.demo.common.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TenantResolver {

    public String resolveSubdomain(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            return null;
        }

        // Remove port if present
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }

        // Handle localhost / IP — subdomain is first part
        if (host.equals("localhost") || host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            return null;
        }

        String[] parts = host.split("\\.");
        if (parts.length >= 2) {
            return parts[0];
        }

        return null;
    }
}
