package com.cupcakeshop.projeto.config;

import com.cupcakeshop.projeto.model.Carrinho;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class CarrinhoConfig {

    @Bean
    @SessionScope
    public Carrinho carrinho() {
        return new Carrinho();
    }
}