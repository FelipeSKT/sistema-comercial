package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class VendaController {

    @Autowired
    private EstoqueService estoqueService;
    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @PostMapping("/venda/finalizar")
    public String finalizarVenda(@RequestBody List<DadosItemVenda> itens) {
        try {
            for (DadosItemVenda item : itens) {
                estoqueService.registrarMovimentacao(
                        item.produtoId(),
                        TipoMovimentacao.SAIDA,
                        item.quantidade(),
                        "VENDA VAREJO", // Esta tag permite filtrarmos depois
                        item.porEmbalagem(),
                        item.precoVendido() // Passamos o preço editado pelo usuário!
                );
            }
            return "Venda realizada com sucesso!";
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    @GetMapping("/vendas/historico")
    public List<MovimentacaoEstoque> historicoVendas(
            @RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim
    ) {
        // Se as datas vierem preenchidas, filtramos
        if (inicio != null && fim != null) {
            // Ajustamos para pegar o dia inteiro (00:00:00 até 23:59:59)
            LocalDateTime dataInicio = inicio.atStartOfDay();
            LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

            return movimentacaoEstoqueRepository.findByDataHoraBetweenAndDescricaoContainingOrderByDataHoraDesc(
                    dataInicio, dataFim, "VENDA VAREJO"
            );
        }

        // Comportamento padrão: Se não informar datas, retorna TUDO (como era antes)
        // DICA: Se o sistema ficar lento no futuro, podes mudar aqui para retornar só o mês atual
        return movimentacaoEstoqueRepository.findByDescricaoContainingOrderByDataHoraDesc("VENDA VAREJO");
    }
}