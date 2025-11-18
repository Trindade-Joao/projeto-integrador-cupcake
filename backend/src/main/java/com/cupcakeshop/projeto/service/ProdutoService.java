package com.cupcakeshop.projeto.service;

import com.cupcakeshop.projeto.model.Produto;
import com.cupcakeshop.projeto.model.Administrador;
import com.cupcakeshop.projeto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvarNovoProduto(Produto produto, Administrador adminResponsavel) {
        produto.setStatus("Ativo");
        produto.setAdministrador(adminResponsavel);
        return produtoRepository.save(produto);
    }

    public Produto atualizarProduto(Long idProduto, Produto produtoAtualizado) {
        Produto produtoExistente = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setEstoque(produtoAtualizado.getEstoque());
        produtoExistente.setTags(produtoAtualizado.getTags());

        return produtoRepository.save(produtoExistente);
    }

    public void desativarProduto(Long idProduto) {
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        produto.setStatus("Descontinuado");
        produtoRepository.save(produto);
    }

    public List<Produto> buscarTodosAtivos() {
        return produtoRepository.findByStatusOrderByNomeAsc("Ativo");
    }

    public Optional<Produto> buscarPorId(Long idProduto) {
        return produtoRepository.findById(idProduto);
    }

    public List<Produto> findAll() {
        return  produtoRepository.findAll();
    }

    public List<Produto> findByTagIgnoreCase(String tag) {
        return produtoRepository.findByTagsContainingIgnoreCase(tag);
    }
}