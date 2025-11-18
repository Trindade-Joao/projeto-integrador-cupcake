package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.*;
import com.cupcakeshop.projeto.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente clienteValido;
    private Endereco enderecoValido;
    private Produto produto1;
    private Carrinho carrinhoValido;

    @BeforeEach
    void setUp() {
        enderecoValido = new Endereco();
        enderecoValido.setEnderecoKey(1L);
        enderecoValido.setLogradouro("Rua Teste");

        clienteValido = new Cliente();
        clienteValido.setClienteKey(1L);
        clienteValido.setNome("Cliente Teste");
        clienteValido.setEnderecoPrincipal(enderecoValido);

        produto1 = new Produto();
        produto1.setProdutoKey(10L);
        produto1.setNome("Cupcake de Chocolate");
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setEstoque(10);
        carrinhoValido = new Carrinho();
        carrinhoValido.adicionarItem(produto1);
    }

    @Test
    @DisplayName("Deve finalizar o pedido e salvar todas as entidades com sucesso")
    void finalizarPedido_Sucesso() {
        Long clienteId = 1L;
        String idTransacaoMP = "MP12345";
        String statusPagamentoMP = "approved";

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteValido));

        when(produtoRepository.findById(produto1.getProdutoKey())).thenReturn(Optional.of(produto1));

        Pedido pedidoSimulado = new Pedido();
        pedidoSimulado.setPedidoKey(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSimulado);

        when(produtoRepository.save(any(Produto.class))).thenReturn(produto1);

        Pedido pedidoFinalizado = pedidoService.finalizarPedido(carrinhoValido, clienteId, idTransacaoMP, statusPagamentoMP);

        assertNotNull(pedidoFinalizado);
        assertEquals(1L, pedidoFinalizado.getPedidoKey());

        verify(clienteRepository, times(1)).findById(clienteId);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(itemPedidoRepository, times(1)).save(any(ItemPedido.class));
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        assertEquals(9, produto1.getEstoque());
        verify(produtoRepository).findById(produto1.getProdutoKey());


        assertTrue(carrinhoValido.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção se o Cliente não for encontrado")
    void finalizarPedido_ClienteNaoEncontrado_LancaExcecao() {
        Long clienteIdInvalido = 99L;
        when(clienteRepository.findById(clienteIdInvalido)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.finalizarPedido(carrinhoValido, clienteIdInvalido, "MP123", "approved");
        });

        assertEquals("Cliente não encontrado.", thrown.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o carrinho estiver vazio")
    void finalizarPedido_CarrinhoVazio_LancaExcecao() {
        Carrinho carrinhoVazio = new Carrinho();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            pedidoService.finalizarPedido(carrinhoVazio, 1L, "MP123", "approved");
        });

        assertEquals("O carrinho não pode estar vazio.", thrown.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o cliente não tiver endereço principal")
    void finalizarPedido_SemEnderecoPrincipal_LancaExcecao() {
        clienteValido.setEnderecoPrincipal(null);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            pedidoService.finalizarPedido(carrinhoValido, 1L, "MP123", "approved");
        });

        assertEquals("Cliente não possui endereço principal cadastrado.", thrown.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o Produto não for encontrado no catálogo (durante o loop)")
    void finalizarPedido_ProdutoNaoEncontrado_LancaExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        Pedido pedidoSimulado = new Pedido();
        pedidoSimulado.setPedidoKey(1L);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSimulado);

        when(produtoRepository.findById(produto1.getProdutoKey())).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.finalizarPedido(carrinhoValido, 1L, "MP123", "approved");
        });

        assertEquals("Produto não encontrado no catálogo.", thrown.getMessage());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(itemPedidoRepository, never()).save(any(ItemPedido.class));
    }

    @Test
    @DisplayName("Deve retornar o Pedido quando encontrado pelo ID")
    void buscarPedidoPorId_Encontrado_RetornaPedido() {
        Long pedidoId = 1L;
        Pedido pedidoSimulado = new Pedido();
        pedidoSimulado.setPedidoKey(pedidoId);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoSimulado));

        Pedido resultado = pedidoService.buscarPedidoPorId(pedidoId);

        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getPedidoKey());
        verify(pedidoRepository, times(1)).findById(pedidoId);
    }

    @Test
    @DisplayName("Deve lançar exceção se o Pedido não for encontrado pelo ID")
    void buscarPedidoPorId_NaoEncontrado_LancaExcecao() {
        Long pedidoIdInvalido = 99L;

        when(pedidoRepository.findById(pedidoIdInvalido)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.buscarPedidoPorId(pedidoIdInvalido);
        });

        assertEquals("Pedido não encontrado.", thrown.getMessage());
        verify(pedidoRepository, times(1)).findById(pedidoIdInvalido);
    }
}