package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.repository.ClienteRepository;
import com.cupcakeshop.projeto.repository.AdministradorRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;

    public AutenticacaoService(ClienteRepository clienteRepository,
                               AdministradorRepository administradorRepository) {
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Administrador> adminOpt = administradorRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            return buildUserDetails(admin.getEmail(), admin.getSenha(), admin.getNivelAcesso());
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return buildUserDetails(cliente.getEmail(), cliente.getSenha(), "CLIENTE");
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }

    private UserDetails buildUserDetails(String email, String senha, String role) {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );

        return new org.springframework.security.core.userdetails.User(
                email,
                senha,
                authorities
        );
    }
}