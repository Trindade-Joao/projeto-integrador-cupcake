package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Carrinho;
import com.cupcakeshop.projeto.model.Produto;
import com.cupcakeshop.projeto.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final Carrinho carrinho;
    private final ProdutoService produtoService;

    public CarrinhoController(Carrinho carrinho, ProdutoService produtoService) {
        this.carrinho = carrinho;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String exibirCarrinho(Model model) {
        model.addAttribute("carrinho", carrinho);
        return "cliente/carrinho";
    }

    @GetMapping("/adicionar/{id}")
    public String adicionarAoCarrinho(@PathVariable Long id, RedirectAttributes ra) {
        Produto produto = produtoService.buscarPorId(id)
                .orElse(null);

        if (produto == null || "Descontinuado".equals(produto.getStatus())) {
            ra.addFlashAttribute("erro", "Produto não disponível.");
            return "redirect:/vitrine";
        }

        if (produto.getEstoque() <= 0) {
            ra.addFlashAttribute("erro", "Produto esgotado.");
            return "redirect:/vitrine";
        }

        carrinho.adicionarItem(produto);
        ra.addFlashAttribute("sucesso", "Cupcake " + produto.getNome() + " adicionado!");

        return "redirect:/vitrine";
    }

    @GetMapping("/remover/{id}")
    public String removerDoCarrinho(@PathVariable Long id) {
        carrinho.removerItem(id);
        return "redirect:/carrinho";
    }

    @GetMapping("/limpar")
    public String limparCarrinho() {
        carrinho.getItens().clear();
        return "redirect:/vitrine";
    }

}