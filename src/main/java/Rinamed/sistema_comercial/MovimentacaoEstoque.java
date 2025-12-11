package Rinamed.sistema_comercial;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal; // Importante!

@Entity
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING) // Grava o texto "ENTRADA" ou "SAIDA" no banco
    private TipoMovimentacao tipo;

    private Integer quantidade;
    private String descricao;

    @ManyToOne // Liga esta movimentação ao Produto
    private Produto produto;

    private BigDecimal valorUnitario;
    // Construtor vazio (obrigatório para o JPA)
    public MovimentacaoEstoque() {}

    // Construtor prático para usarmos depois
    public MovimentacaoEstoque(Produto produto, TipoMovimentacao tipo, Integer quantidade, String descricao, BigDecimal valorUnitario) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario; // Recebe o valor
        this.dataHora = LocalDateTime.now();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // --- Getters e Setters ---
    // (Podes gerá-los automaticamente tal como fizeste no Produto)
}