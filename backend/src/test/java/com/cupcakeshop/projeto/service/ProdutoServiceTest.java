package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.model.Produto;
import com.cupcakeshop.projeto.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produtoBase;
    private Administrador adminResponsavel;
    private final Long PRODUTO_ID = 1L;

    @BeforeEach
    void setUp() {
        adminResponsavel = new Administrador();
        adminResponsavel.setAdminstradorKey(10L);
        adminResponsavel.setNome("Admin Teste");

        produtoBase = new Produto();
        produtoBase.setProdutoKey(PRODUTO_ID);
        produtoBase.setNome("Bolo de Fubá");
        produtoBase.setDescricao("Bolo caseiro de fubá com goiabada.");
        produtoBase.setPreco(new BigDecimal("15.00"));
        produtoBase.setEstoque(50);
        produtoBase.setTags("bolo, fubá, goiabada");
        produtoBase.setStatus("Ativo");
    }


    @Test
    @DisplayName("Deve salvar um novo produto com status 'Ativo' e administrador responsável")
    void salvarNovoProduto_Sucesso() {
        Produto novoProduto = new Produto();
        novoProduto.setNome("Novo Cupcake");

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoBase);

        Produto produtoSalvo = produtoService.salvarNovoProduto(novoProduto, adminResponsavel);

        assertNotNull(produtoSalvo);
        assertEquals("Ativo", produtoSalvo.getStatus());
        verify(produtoRepository, times(1)).save(novoProduto);
    }

    @Test
    @DisplayName("Deve atualizar as informações de um produto existente")
    void atualizarProduto_Sucesso() {
        Produto produtoComNovosDados = new Produto();
        produtoComNovosDados.setNome("Bolo de Fubá Premium");
        produtoComNovosDados.setDescricao("Nova Descrição");
        produtoComNovosDados.setPreco(new BigDecimal("25.00"));
        produtoComNovosDados.setEstoque(100);
        produtoComNovosDados.setTags("bolo, fubá, premium");

        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produtoBase));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoBase);

        Produto produtoAtualizado = produtoService.atualizarProduto(PRODUTO_ID, produtoComNovosDados);

        assertNotNull(produtoAtualizado);
        assertEquals("Bolo de Fubá Premium", produtoAtualizado.getNome());
        assertEquals(new BigDecimal("25.00"), produtoAtualizado.getPreco());
        assertEquals(100, produtoAtualizado.getEstoque());
        assertEquals("bolo, fubá, premium", produtoAtualizado.getTags());
        // Garante que o status original foi mantido
        assertEquals("Ativo", produtoAtualizado.getStatus());

        verify(produtoRepository, times(1)).findById(PRODUTO_ID);
        verify(produtoRepository, times(1)).save(produtoBase);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar produto não encontrado")
    void atualizarProduto_NaoEncontrado_LancaExcecao() {
        Produto produtoInvalido = new Produto();

        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.atualizarProduto(PRODUTO_ID, produtoInvalido);
        });

        assertEquals("Produto não encontrado.", thrown.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }


    @Test
    @DisplayName("Deve desativar um produto alterando seu status para 'Descontinuado'")
    void desativarProduto_Sucesso() {
        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produtoBase));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoBase);

        produtoService.desativarProduto(PRODUTO_ID);

        assertEquals("Descontinuado", produtoBase.getStatus());
        verify(produtoRepository, times(1)).findById(PRODUTO_ID);
        verify(produtoRepository, times(1)).save(produtoBase);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar produto não encontrado")
    void desativarProduto_NaoEncontrado_LancaExcecao() {
        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.desativarProduto(PRODUTO_ID);
        });

        assertEquals("Produto não encontrado.", thrown.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }


    @Test
    @DisplayName("Deve retornar a lista de todos os produtos com status 'Ativo'")
    void buscarTodosAtivos_Sucesso() {
        Produto produto2 = new Produto();
        produto2.setNome("Cupcake de Cenoura");
        List<Produto> produtosAtivos = Arrays.asList(produtoBase, produto2);

        when(produtoRepository.findByStatusOrderByNomeAsc("Ativo")).thenReturn(produtosAtivos);

        List<Produto> resultado = produtoService.buscarTodosAtivos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Bolo de Fubá", resultado.get(0).getNome());
        verify(produtoRepository, times(1)).findByStatusOrderByNomeAsc("Ativo");
    }


    @Test
    @DisplayName("Deve retornar um Optional contendo o produto quando encontrado")
    void buscarPorId_Encontrado_RetornaOptional() {
        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.of(produtoBase));

        Optional<Produto> resultado = produtoService.buscarPorId(PRODUTO_ID);

        assertTrue(resultado.isPresent());
        assertEquals(PRODUTO_ID, resultado.get().getProdutoKey());
        verify(produtoRepository, times(1)).findById(PRODUTO_ID);
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio quando o produto não for encontrado")
    void buscarPorId_NaoEncontrado_RetornaOptionalVazio() {
        when(produtoRepository.findById(PRODUTO_ID)).thenReturn(Optional.empty());

        Optional<Produto> resultado = produtoService.buscarPorId(PRODUTO_ID);

        assertFalse(resultado.isPresent());
        verify(produtoRepository, times(1)).findById(PRODUTO_ID);
    }


    @Test
    @DisplayName("Deve retornar a lista de todos os produtos no banco de dados")
    void findAll_Sucesso() {
        Produto produto2 = new Produto();
        List<Produto> todosProdutos = Arrays.asList(produtoBase, produto2);

        when(produtoRepository.findAll()).thenReturn(todosProdutos);

        List<Produto> resultado = produtoService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("Deve retornar produtos que contenham a tag especificada, ignorando case")
    void findByTagIgnoreCase_Sucesso() {
        Produto produtoTag1 = new Produto();
        produtoTag1.setNome("Produto com Morango");
        Produto produtoTag2 = new Produto();
        produtoTag2.setNome("Torta de Morango");

        List<Produto> produtosPorTag = Arrays.asList(produtoTag1, produtoTag2);
        String tagBusca = "morango";

        when(produtoRepository.findByTagsContainingIgnoreCase(tagBusca)).thenReturn(produtosPorTag);

        List<Produto> resultado = produtoService.findByTagIgnoreCase(tagBusca);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Produto com Morango", resultado.get(0).getNome());
        verify(produtoRepository, times(1)).findByTagsContainingIgnoreCase(tagBusca);
    }

    @Test
    @DisplayName("Deve retornar lista vazia se nenhuma tag corresponder")
    void findByTagIgnoreCase_NenhumaCorrespondencia() {
        String tagBusca = "tag_inexistente";

        when(produtoRepository.findByTagsContainingIgnoreCase(tagBusca)).thenReturn(Collections.emptyList());

        List<Produto> resultado = produtoService.findByTagIgnoreCase(tagBusca);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).findByTagsContainingIgnoreCase(tagBusca);
    }
}