package com.ecommerce.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.r2dbc.spi.ConnectionFactories;

/**
 * Configuración de la conexión R2DBC a MySQL.
 */
@Configuration
public class DatabaseConfig {
    @Value("${spring.r2dbc.url:r2dbc:mysql://localhost:3306/ecommerce_db}")
    private String url;
    @Value("${spring.r2dbc.username:root}")
    private String username;
    @Value("${spring.r2dbc.password:password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        // Usar solo la url, username y password, sin valores por defecto
        return ConnectionFactories.get(
                String.format("r2dbc:mysql://%s:%s@localhost:3306/ecommerce_db", username, password));
    }
}
