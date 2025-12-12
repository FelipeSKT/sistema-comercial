package Rinamed.sistema_comercial;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Pedido pedido;

    @ManyToOne
    private Produto produto;

    private Integer quantidade;
    private Boolean porEmbalagem;
    private BigDecimal precoVendido; // Preço no momento do pedido

    public ItemPedido() {}

    public ItemPedido(Produto produto, Integer quantidade, Boolean porEmbalagem, BigDecimal precoVendido) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.porEmbalagem = porEmbalagem;
        this.precoVendido = precoVendido;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Produto getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public Boolean getPorEmbalagem() { return porEmbalagem; }
    public BigDecimal getPrecoVendido() { return precoVendido; }
}