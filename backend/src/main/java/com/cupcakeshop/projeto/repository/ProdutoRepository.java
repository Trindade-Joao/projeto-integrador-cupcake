package com.cupcakeshop.projeto.repository;

import com.cupcakeshop.projeto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatusOrderByNomeAsc(String status);

    List<Produto> findByTagsContainingIgnoreCase(String tag);
}