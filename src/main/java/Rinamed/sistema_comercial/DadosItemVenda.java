package Rinamed.sistema_comercial;

import java.math.BigDecimal;

public record DadosItemVenda(
        Long produtoId,
        Integer quantidade,
        Boolean porEmbalagem,
        BigDecimal precoVendido // Novo campo
) {}