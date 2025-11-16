package com.cupcakeshop.projeto.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pagamentoKey;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(unique = true, length = 100)
    private String idMp;

    @Column(nullable = false, length = 50)
    private String statusPagamento;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dataTransacao;

    public Long getPagamentoKey() {
        return pagamentoKey;
    }

    public void setPagamentoKey(Long pagamentoKey) {
        this.pagamentoKey = pagamentoKey;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getIdMp() {
        return idMp;
    }

    public void setIdMp(String idMp) {
        this.idMp = idMp;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
