package Rinamed.sistema_comercial;

public record DadosMovimentacao(
        Long produtoId,
        TipoMovimentacao tipo,
        Integer quantidade,
        String descricao,
        Boolean porEmbalagem
) {}