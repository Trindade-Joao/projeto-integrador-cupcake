package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.repository.AdministradorRepository;
import com.cupcakeshop.projeto.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Administrador adminValido;
    private Cliente clienteValido;
    private final String EMAIL_ADMIN = "admin@cupcake.com";
    private final String EMAIL_CLIENTE = "cliente@teste.com";
    private final String EMAIL_NAO_ENCONTRADO = "naoexiste@teste.com";

    @BeforeEach
    void setUp() {
        adminValido = new Administrador();
        adminValido.setAdminstradorKey(1L);
        adminValido.setEmail(EMAIL_ADMIN);
        adminValido.setSenha("{bcrypt}$2a$10$HASHADMIN");
        adminValido.setNivelAcesso("ADMIN");

        clienteValido = new Cliente();
        clienteValido.setClienteKey(2L);
        clienteValido.setEmail(EMAIL_CLIENTE);
        clienteValido.setSenha("{bcrypt}$2a$10$HASHCLIENTE");
    }


    @Test
    @DisplayName("Deve carregar UserDetails para um Administrador existente")
    void loadUserByUsername_AdminEncontrado_RetornaUserDetails() {
        when(administradorRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(adminValido));

        UserDetails userDetails = autenticacaoService.loadUserByUsername(EMAIL_ADMIN);

        assertNotNull(userDetails);
        assertEquals(EMAIL_ADMIN, userDetails.getUsername());
        assertEquals("{bcrypt}$2a$10$HASHADMIN", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        verify(administradorRepository, times(1)).findByEmail(EMAIL_ADMIN);
        verify(clienteRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve carregar UserDetails para um Cliente existente")
    void loadUserByUsername_ClienteEncontrado_RetornaUserDetails() {
        when(administradorRepository.findByEmail(EMAIL_CLIENTE)).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(EMAIL_CLIENTE)).thenReturn(Optional.of(clienteValido));

        UserDetails userDetails = autenticacaoService.loadUserByUsername(EMAIL_CLIENTE);

        assertNotNull(userDetails);
        assertEquals(EMAIL_CLIENTE, userDetails.getUsername());
        assertEquals("{bcrypt}$2a$10$HASHCLIENTE", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLIENTE")));
        verify(administradorRepository, times(1)).findByEmail(EMAIL_CLIENTE);
        verify(clienteRepository, times(1)).findByEmail(EMAIL_CLIENTE);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException se o usuário não for encontrado")
    void loadUserByUsername_NaoEncontrado_LancaExcecao() {
        when(administradorRepository.findByEmail(EMAIL_NAO_ENCONTRADO)).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(EMAIL_NAO_ENCONTRADO)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            autenticacaoService.loadUserByUsername(EMAIL_NAO_ENCONTRADO);
        });

        assertEquals("Usuário não encontrado: " + EMAIL_NAO_ENCONTRADO, thrown.getMessage());
        verify(administradorRepository, times(1)).findByEmail(EMAIL_NAO_ENCONTRADO);
        verify(clienteRepository, times(1)).findByEmail(EMAIL_NAO_ENCONTRADO);
    }
}