package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Produto;
import com.cupcakeshop.projeto.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VitrineController {

    private final ProdutoService produtoService;

    public VitrineController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/vitrine")
    public String exibirVitrine(
            @RequestParam(value = "tag", required = false) String tag,
            Model model) {

        List<Produto> produtos;

        if (tag != null && !tag.isEmpty()) {
            produtos = produtoService.findByTagIgnoreCase(tag);
            model.addAttribute("tagAtiva", tag);
        } else {
            produtos = produtoService.buscarTodosAtivos();
        }

        model.addAttribute("produtos", produtos);
        return "cliente/vitrine";
    }

    @GetMapping("/")
    public String redirecionarParaVitrine() {
        return "redirect:/vitrine";
    }
}