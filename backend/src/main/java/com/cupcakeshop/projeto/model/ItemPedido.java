package com.cupcakeshop.projeto.model;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ITEM_PEDIDO")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemPedidoKey;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioNaCompra;

    public Long getItemPedidoKey() {
        return itemPedidoKey;
    }

    public void setItemPedidoKey(Long itemPedidoKey) {
        this.itemPedidoKey = itemPedidoKey;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioNaCompra() {
        return precoUnitarioNaCompra;
    }

    public void setPrecoUnitarioNaCompra(BigDecimal precoUnitarioNaCompra) {
        this.precoUnitarioNaCompra = precoUnitarioNaCompra;
    }
}
