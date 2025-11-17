package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Pedido;
import com.cupcakeshop.projeto.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/detalhes-pedido/{id}")
    public String exibirDetalhesPedido(@PathVariable Long id, Model model) {

        Pedido pedido = pedidoService.buscarPedidoPorId(id);

        model.addAttribute("pedido", pedido);
        model.addAttribute("pagamento", pedido.getPagamentos().get(0));

        return "cliente/detalhes-pedido.html";
    }

}