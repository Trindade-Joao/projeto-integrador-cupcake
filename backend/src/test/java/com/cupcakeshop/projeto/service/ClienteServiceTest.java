package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.model.Endereco;
import com.cupcakeshop.projeto.repository.ClienteRepository;
import com.cupcakeshop.projeto.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteValido;
    private Endereco enderecoValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente();
        clienteValido.setClienteKey(1L);
        clienteValido.setNome("João Silva");
        clienteValido.setEmail("joao.silva@teste.com");
        clienteValido.setSenha("Senha123");

        enderecoValido = new Endereco();
        enderecoValido.setCep("12345-678");
        enderecoValido.setLogradouro("Rua Teste");
        enderecoValido.setNumero("100");
        enderecoValido.setCidade("Cidade Teste");
    }

    @Test
    @DisplayName("Deve cadastrar um novo cliente com sucesso")
    void cadastrarNovoCliente_Sucesso() {
        when(clienteRepository.findByEmail(clienteValido.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        Cliente clienteSalvo = clienteService.cadastrarNovoCliente(clienteValido);

        assertNotNull(clienteSalvo);
        assertEquals("TEMPORARIA", clienteSalvo.getStatusConta());
        assertTrue(passwordEncoder.matches("Senha123", clienteSalvo.getSenha()));
        verify(clienteRepository, times(1)).findByEmail(clienteValido.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar cliente com email já existente")
    void cadastrarNovoCliente_EmailExistente_LancaExcecao() {
        when(clienteRepository.findByEmail(clienteValido.getEmail())).thenReturn(Optional.of(clienteValido));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrarNovoCliente(clienteValido);
        });

        assertEquals("Email já cadastrado.", thrown.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve atualizar a senha do cliente com sucesso para uma senha forte")
    void atualizarSenha_Sucesso_SenhaForte() {
        String novaSenhaForte = "NovaSenha123";
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        Cliente clienteAtualizado = clienteService.atualizarSenha(1L, novaSenhaForte);

        assertNotNull(clienteAtualizado);
        assertEquals("ATIVA", clienteAtualizado.getStatusConta());
        assertTrue(passwordEncoder.matches(novaSenhaForte, clienteAtualizado.getSenha()));
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o cliente não for encontrado ao atualizar senha")
    void atualizarSenha_ClienteNaoEncontrado_LancaExcecao() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizarSenha(2L, "NovaSenha123");
        });

        assertEquals("Cliente não encontrado.", thrown.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se a nova senha for fraca (sem maiúscula)")
    void atualizarSenha_SenhaFraca_SemMaiuscula_LancaExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        String senhaFraca = "novasenha123";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizarSenha(1L, senhaFraca);
        });

        assertEquals("Nova senha não atende aos requisitos de segurança.", thrown.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se a nova senha for fraca (sem número)")
    void atualizarSenha_SenhaFraca_SemNumero_LancaExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        String senhaFraca = "NovaSenhaFraca";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizarSenha(1L, senhaFraca);
        });

        assertEquals("Nova senha não atende aos requisitos de segurança.", thrown.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar true se o cliente for encontrado para recuperação de senha")
    void iniciarRecuperacaoSenha_ClienteEncontrado_RetornaTrue() {
        when(clienteRepository.findByEmail(clienteValido.getEmail())).thenReturn(Optional.of(clienteValido));

        assertTrue(clienteService.iniciarRecuperacaoSenha(clienteValido.getEmail()));
        verify(clienteRepository, times(1)).findByEmail(clienteValido.getEmail());
    }

    @Test
    @DisplayName("Deve retornar false se o cliente não for encontrado para recuperação de senha")
    void iniciarRecuperacaoSenha_ClienteNaoEncontrado_RetornaFalse() {
        String emailNaoCadastrado = "naoexiste@teste.com";
        when(clienteRepository.findByEmail(emailNaoCadastrado)).thenReturn(Optional.empty());

        assertFalse(clienteService.iniciarRecuperacaoSenha(emailNaoCadastrado));
        verify(clienteRepository, times(1)).findByEmail(emailNaoCadastrado);
    }

    @Test
    @DisplayName("Deve cadastrar um novo endereço principal se o cliente ainda não tiver um")
    void cadastrarEndereco_NovoEndereco_Sucesso() {
        clienteValido.setEnderecoPrincipal(null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoValido);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        Cliente clienteAtualizado = clienteService.cadastrarEndereco(1L, enderecoValido);

        assertNotNull(clienteAtualizado.getEnderecoPrincipal());
        assertEquals(enderecoValido.getCep(), clienteAtualizado.getEnderecoPrincipal().getCep());
        verify(enderecoRepository, times(1)).save(enderecoValido);
        verify(clienteRepository, times(1)).save(clienteValido);
    }

    @Test
    @DisplayName("Deve atualizar o endereço principal existente do cliente")
    void cadastrarEndereco_AtualizarExistente_Sucesso() {
        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setEnderecoKey(10L);
        enderecoExistente.setCep("99999-999");
        clienteValido.setEnderecoPrincipal(enderecoExistente);

        Endereco novoEndereco = new Endereco();
        novoEndereco.setCep("11111-111");
        novoEndereco.setLogradouro("Nova Rua");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoExistente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        Cliente clienteAtualizado = clienteService.cadastrarEndereco(1L, novoEndereco);

        assertNotNull(clienteAtualizado.getEnderecoPrincipal());
        assertEquals("11111-111", clienteAtualizado.getEnderecoPrincipal().getCep());
        assertEquals(10L, clienteAtualizado.getEnderecoPrincipal().getEnderecoKey());
        verify(enderecoRepository, times(1)).save(enderecoExistente);
        verify(clienteRepository, times(1)).save(clienteValido);
    }

    @Test
    @DisplayName("Deve lançar exceção se o cliente não for encontrado ao cadastrar endereço")
    void cadastrarEndereco_ClienteNaoEncontrado_LancaExcecao() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrarEndereco(2L, enderecoValido);
        });

        assertEquals("Cliente não encontrado.", thrown.getMessage());
        verify(enderecoRepository, never()).save(any(Endereco.class));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar Optional com Cliente quando encontrado")
    void obterPorId_ClienteEncontrado_RetornaOptionalComCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        Optional<Cliente> resultado = clienteService.obterPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(clienteValido.getEmail(), resultado.get().getEmail());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o cliente não for encontrado")
    void obterPorId_ClienteNaoEncontrado_RetornaOptionalVazio() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.obterPorId(2L);

        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(2L);
    }
}