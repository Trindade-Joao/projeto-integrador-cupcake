package com.cupcakeshop.projeto.repository;

import com.cupcakeshop.projeto.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatusPedido(String status);

    List<Pedido> findByClienteClienteKey(Long clienteKey);
}