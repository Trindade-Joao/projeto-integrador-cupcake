package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.*;
import com.cupcakeshop.projeto.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;


    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, PagamentoRepository pagamentoRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Pedido finalizarPedido(Carrinho carrinho, Long clienteId, String idTransacaoMP, String statusPagamentoMP) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("O carrinho não pode estar vazio.");
        }

        if (cliente.getEnderecoPrincipal() == null) {
            throw new IllegalStateException("Cliente não possui endereço principal cadastrado.");
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);

        novoPedido.setEnderecoEntrega(cliente.getEnderecoPrincipal());

        novoPedido.setValorFrete(carrinho.getValorFrete());
        novoPedido.setValorTotal(carrinho.calcularTotal());

        novoPedido.setStatusPedido("RECEBIDO");
        novoPedido.setDataPedido(LocalDateTime.now());

        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        for (Map.Entry<Produto, Integer> entry : carrinho.getItens().entrySet()) {
            Produto produtoSessao = entry.getKey();
            Integer quantidade = entry.getValue();

            Produto produtoBD = produtoRepository.findById(produtoSessao.getProdutoKey())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no catálogo."));

            if (produtoBD.getEstoque() < quantidade) {
                throw new IllegalStateException("Produto '" + produtoBD.getNome() + "' não tem estoque suficiente (" + produtoBD.getEstoque() + ").");
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedidoSalvo);
            item.setProduto(produtoBD);
            item.setQuantidade(quantidade);
            item.setPrecoUnitarioNaCompra(produtoBD.getPreco());
            itemPedidoRepository.save(item);

            produtoBD.setEstoque(produtoBD.getEstoque() - quantidade);
            produtoRepository.save(produtoBD);
        }

        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setPedido(pedidoSalvo);
        novoPagamento.setIdMp(idTransacaoMP);
        novoPagamento.setStatusPagamento(statusPagamentoMP);
        novoPagamento.setDataTransacao(LocalDateTime.now());
        pagamentoRepository.save(novoPagamento);

        carrinho.getItens().clear();

        return pedidoSalvo;
    }

    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
    }
}