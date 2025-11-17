package com.cupcakeshop.projeto.controller;

import com.cupcakeshop.projeto.model.Carrinho;
import com.cupcakeshop.projeto.model.Cliente;
import com.cupcakeshop.projeto.model.Pedido;
import com.cupcakeshop.projeto.service.ClienteService;
import com.cupcakeshop.projeto.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final PedidoService pedidoService;
    private final Carrinho carrinho;
    private final ClienteService clienteService;

    public CheckoutController(PedidoService pedidoService, Carrinho carrinho, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.carrinho = carrinho;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String exibirCheckout(Model model) {
        if (carrinho.getItens().isEmpty()) {
            return "redirect:/carrinho";
        }

        Cliente clienteLogado = clienteService.obterPorId(1L).orElseGet(() -> new Cliente());

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("cliente", clienteLogado); // INJETA o objeto cliente COMPLETO na View

        return "cliente/checkout";
    }

    @PostMapping("/finalizar")
    public String finalizarPedido(RedirectAttributes ra) {
        Long clienteId = 1L;
        String statusPagamento = "APROVADO";
        String idTransacaoMP = "MP" + System.currentTimeMillis();

        try {
            Pedido pedidoFinalizado = pedidoService.finalizarPedido(
                    carrinho,
                    clienteId,
                    idTransacaoMP,
                    statusPagamento
            );

            ra.addFlashAttribute("sucesso", "Pedido #" + pedidoFinalizado.getPedidoKey() + " finalizado com sucesso!");
            return "redirect:/detalhes-pedido.html/" + pedidoFinalizado.getPedidoKey();
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/checkout";
        }
    }
}