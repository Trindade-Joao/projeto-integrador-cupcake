package com.cupcakeshop.projeto.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Carrinho {
    public static final String VALOR_DO_FRETE = "10.00"; //inicialmente vai ser um valor fixo
    private Map<Produto, Integer> itens = new HashMap<>();

    public void adicionarItem(Produto produto) {
        // HU 010: Inclusão de produtos no carrinho
        this.itens.merge(produto, 1, Integer::sum);
    }

    public void removerItem(Long produtoId) {
        // HU 011: Exclusão de produtos do carrinho
        this.itens.keySet().removeIf(p -> p.getProdutoKey().equals(produtoId));
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
            BigDecimal precoTotal = entry.getKey().getPreco().multiply(BigDecimal.valueOf(entry.getValue()));
            subtotal = subtotal.add(precoTotal);
        }
        return subtotal;
    }

    // Usaremos um valor fixo de frete para simplificar (HU 014 Simplificada)
    public BigDecimal getValorFrete() {
        return new BigDecimal(VALOR_DO_FRETE);
    }

    public BigDecimal calcularTotal() {
        return calcularSubtotal().add(getValorFrete());
    }

    // Getters and Setters para 'itens'
    public Map<Produto, Integer> getItens() {
        return itens;
    }
    // ...
}