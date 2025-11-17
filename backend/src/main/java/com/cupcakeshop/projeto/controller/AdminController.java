package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.model.Produto;
import com.cupcakeshop.projeto.repository.AdministradorRepository;
import com.cupcakeshop.projeto.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProdutoService produtoService;
    private final AdministradorRepository administradorRepository;

    public AdminController(ProdutoService produtoService, AdministradorRepository administradorRepository) {
        this.produtoService = produtoService;
        this.administradorRepository = administradorRepository;
    }

    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "admin/lista-produtos";
    }

    @GetMapping("/produtos/novo")
    public String exibirFormularioNovoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "admin/formulario-produto";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(Produto produto) {

        Administrador adminResponsavel = administradorRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Admin ID 1 não encontrado para persistir o produto."));

        produtoService.salvarNovoProduto(produto, adminResponsavel);

        return "redirect:/admin/produtos";
    }

    @GetMapping("/produtos/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        model.addAttribute("produto", produto);
        return "admin/formulario-produto";
    }

    @PostMapping("/produtos/desativar/{id}")
    public String desativarProduto(@PathVariable Long id) {
        produtoService.desativarProduto(id);
        return "redirect:/admin/produtos";
    }
}