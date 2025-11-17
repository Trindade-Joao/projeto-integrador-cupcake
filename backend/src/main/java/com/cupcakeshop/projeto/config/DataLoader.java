package com.cupcakeshop.projeto.config;

import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.repository.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader {

    // Componente que executa um código assim que o Spring inicializa
    @Bean
    public CommandLineRunner initDatabase(AdministradorRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se o Admin já existe para não duplicar o registro
            if (adminRepository.findByEmail("admin@cupcake.com").isEmpty()) {
                Administrador admin = new Administrador();
                admin.setNome("Admin Master");
                admin.setEmail("admin@cupcake.com");
                // Criptografa a senha "admin1234"
                admin.setSenha(passwordEncoder.encode("admin1234"));
                admin.setNivelAcesso("ADMIN");
                adminRepository.save(admin);
                System.out.println(">>> Admin de teste criado: admin@cupcake.com / admin1234");
            }
        };
    }
    // Você precisará adicionar o método findByEmail no AdministradorRepository
}