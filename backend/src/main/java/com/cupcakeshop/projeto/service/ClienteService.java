package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.model.Endereco;
import com.cupcakeshop.projeto.repository.ClienteRepository;
import com.cupcakeshop.projeto.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Cliente cadastrarNovoCliente(Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        cliente.setStatusConta("TEMPORARIA");
        cliente.setDataCadastro(LocalDateTime.now());

        return clienteRepository.save(cliente);
    }

    public Cliente atualizarSenha(Long clienteId, String novaSenha) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if (!novaSenha.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new IllegalArgumentException("Nova senha não atende aos requisitos de segurança.");
        }

        cliente.setSenha(passwordEncoder.encode(novaSenha));
        cliente.setStatusConta("ATIVA");
        return clienteRepository.save(cliente);
    }

    public boolean iniciarRecuperacaoSenha(String email) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            System.out.println("DEBUG: Token de recuperação enviado para " + email);
            return true;
        }
        return false;
    }

    public Cliente cadastrarEndereco(Long clienteId, Endereco novoEndereco) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if (cliente.getEnderecoPrincipal() != null) {
            Endereco enderecoExistente = cliente.getEnderecoPrincipal();
            enderecoExistente.setCep(novoEndereco.getCep());
            enderecoExistente.setLogradouro(novoEndereco.getLogradouro());
            enderecoExistente.setNumero(novoEndereco.getNumero());
            enderecoExistente.setComplemento(novoEndereco.getComplemento());
            enderecoExistente.setBairro(novoEndereco.getBairro());
            enderecoExistente.setCidade(novoEndereco.getCidade());

            enderecoRepository.save(enderecoExistente);
        } else {
            Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);
            cliente.setEnderecoPrincipal(enderecoSalvo);
        }

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> obterPorId(Long clienteId) {
        return clienteRepository.findById(clienteId);
    }
}