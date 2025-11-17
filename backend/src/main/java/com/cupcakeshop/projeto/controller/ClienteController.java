package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/cadastro";
    }

    @PostMapping("/cadastro/salvar")
    public String salvarCadastro(Cliente cliente, Model model) {
        try {
            clienteService.cadastrarNovoCliente(cliente);

            return "redirect:/login?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("cliente", cliente);
            return "cliente/cadastro";
        }
    }

    @GetMapping("/login")
    public String exibirFormularioLogin() {
        return "cliente/login";
    }

}
