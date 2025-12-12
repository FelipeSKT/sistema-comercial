package Rinamed.sistema_comercial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueService estoqueService; // Para dar baixa depois

    // 1. Cria o pedido (NÃO mexe no estoque ainda)
    @PostMapping("/criar")
    @Transactional
    public String criarPedido(@RequestBody DadosNovoPedido dados) {
        if (dados.clienteId() == null) return "Erro: Cliente não selecionado!";

        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pedido pedido = new Pedido(cliente);
        BigDecimal total = BigDecimal.ZERO;

        for (DadosItemVenda itemDados : dados.itens()) {
            Produto produto = produtoRepository.findById(itemDados.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            ItemPedido item = new ItemPedido(
                    produto,
                    itemDados.quantidade(),
                    itemDados.porEmbalagem(),
                    itemDados.precoVendido()
            );

            // Calcula total do item para somar no pedido
            BigDecimal totalItem = itemDados.precoVendido().multiply(new BigDecimal(itemDados.quantidade()));
            total = total.add(totalItem);

            pedido.adicionarItem(item);
        }

        pedido.setValorTotal(total);
        pedidoRepository.save(pedido);

        return "Pedido criado com sucesso! (Aguardando pagamento)";
    }

    // 2. Lista apenas os pendentes
    @GetMapping("/pendentes")
    public List<Pedido> listarPendentes() {
        return pedidoRepository.findByStatus("PENDENTE");
    }

    @GetMapping("/cliente/{id}")
    public List<Pedido> listarPorCliente(@PathVariable Long id) {
        // Busca todos os pedidos daquele cliente
        return pedidoRepository.findByClienteId(id);
    }

    // 3. CONFIRMA O PAGAMENTO e Dá baixa no estoque
    @PostMapping("/{id}/confirmar")
    @Transactional
    public String confirmarRecebimento(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if ("FINALIZADO".equals(pedido.getStatus())) {
            return "Este pedido já foi finalizado!";
        }

        try {
            // Agora sim, percorremos os itens e tiramos do estoque
            for (ItemPedido item : pedido.getItens()) {
                estoqueService.registrarMovimentacao(
                        item.getProduto().getId(),
                        TipoMovimentacao.SAIDA,
                        item.getQuantidade(),
                        "PEDIDO FINALIZADO #" + pedido.getId() + " - " + pedido.getCliente().getNome(),
                        item.getPorEmbalagem(),
                        item.getPrecoVendido()
                );
            }

            pedido.setStatus("FINALIZADO");
            pedidoRepository.save(pedido);
            return "Pedido confirmado e estoque atualizado!";

        } catch (Exception e) {
            return "Erro ao finalizar pedido: " + e.getMessage(); // Provavelmente estoque insuficiente
        }
    }

    // 4. Excluir pedido (Cancelamento)
    @DeleteMapping("/{id}")
    public void cancelarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }
}