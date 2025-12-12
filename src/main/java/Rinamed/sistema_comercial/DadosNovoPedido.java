package Rinamed.sistema_comercial;

import java.util.List;

public record DadosNovoPedido(
        Long clienteId,
        List<DadosItemVenda> itens // Reaproveitamos o record que já tens
) {}