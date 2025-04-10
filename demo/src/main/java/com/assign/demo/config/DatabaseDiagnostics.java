package com.assign.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseDiagnostics implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDiagnostics.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            log.info("🔍 Testing database connection and checking user_details table...");
            
            // Check if we can connect to the database
            String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            log.info("✅ Connected to database: {}", dbName);
            
            // Check if the user_details table exists
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_name = 'user_details'", 
                Integer.class);
            log.info("✅ user_details table exists: {}", tableCount > 0);
            
            // Check if user with ID 1 exists
            Integer userCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_details WHERE id = 1",
                Integer.class);
            log.info("✅ User with ID 1 exists: {}", userCount > 0);
            
            if (userCount > 0) {
                // Get the user details
                jdbcTemplate.query(
                    "SELECT id, name, email FROM user_details WHERE id = 1",
                    (rs, rowNum) -> {
                        log.info("✅ Found user: ID={}, Name={}, Email={}", 
                            rs.getLong("id"), 
                            rs.getString("name"), 
                            rs.getString("email"));
                        return null;
                    });
            }
        } catch (Exception e) {
            log.error("❌ Database diagnostic failed: {}", e.getMessage(), e);
        }
    }
}