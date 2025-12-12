let todosProdutos = [];
let carrinho = [];
let todosClientes = []; // Nova lista global para clientes
let historicoCache = [];

document.addEventListener("DOMContentLoaded", () => {
    carregarProdutos();
    carregarClientes();
});

function carregarProdutos() {
    fetch('/produtos')
        .then(r => r.json())
        .then(lista => {
            todosProdutos = lista;
            renderizarCatalogo(lista);
        });
}

function carregarClientes() {
    fetch('/clientes')
        .then(r => r.json())
        .then(lista => {
            todosClientes = lista; // Guarda na memória para pesquisa rápida
        });
}
// 1. Carregar produtos ao iniciar
fetch('/produtos')
    .then(r => r.json())
    .then(lista => {
        todosProdutos = lista;
        renderizarCatalogo(lista);
    });

function filtrarClientesUI() {
    const termo = document.getElementById("inputNomeCliente").value.toLowerCase();
    const box = document.getElementById("listaSugestoes");

    // Se digitou pouco, esconde
    if (termo.length < 1) {
        box.style.display = 'none';
        return;
    }

    // Filtra por Nome, CPF ou CNPJ
    const filtrados = todosClientes.filter(c =>
        c.nome.toLowerCase().includes(termo) ||
        (c.cpf && c.cpf.includes(termo)) ||
        (c.cnpj && c.cnpj.includes(termo))
    );

    box.innerHTML = "";
    if (filtrados.length === 0) {
        box.innerHTML = '<div class="item-sugestao" style="cursor: default; color: #999;">Nenhum cliente encontrado</div>';
    } else {
        filtrados.forEach(c => {
            let doc = c.tipo === 'FISICA' ? c.cpf : c.cnpj;
            let div = document.createElement("div");
            div.className = "item-sugestao";
            div.innerHTML = `<strong>${c.nome}</strong><small>${doc || 'Sem Documento'}</small>`;

            // Ao clicar na sugestão
            div.onclick = () => selecionarCliente(c);

            box.appendChild(div);
        });
    }

    box.style.display = 'block';
}

function selecionarCliente(cliente) {
    // Preenche os campos
    document.getElementById("inputNomeCliente").value = cliente.nome;
    document.getElementById("idClienteSelecionado").value = cliente.id;

    // UI: Trava o campo para parecer selecionado e mostra botão limpar
    document.getElementById("inputNomeCliente").disabled = true;
    document.getElementById("inputNomeCliente").style.backgroundColor = "#e9ecef";
    document.getElementById("btnLimparCliente").style.display = "block";

    // Esconde a lista
    document.getElementById("listaSugestoes").style.display = 'none';
    document.getElementById("btnVerHistorico").style.display = "inline-block";
}

function limparSelecaoCliente() {
    document.getElementById("inputNomeCliente").value = "";
    document.getElementById("idClienteSelecionado").value = "";
    document.getElementById("inputNomeCliente").disabled = false;
    document.getElementById("inputNomeCliente").style.backgroundColor = "white";
    document.getElementById("btnLimparCliente").style.display = "none";
    document.getElementById("inputNomeCliente").focus();
    document.getElementById("btnVerHistorico").style.display = "none";
}

document.addEventListener('click', function(e) {
    if (!document.getElementById('inputNomeCliente').contains(e.target)) {
        document.getElementById('listaSugestoes').style.display = 'none';
    }
});


// 2. Renderizar o Catálogo em "Pastas"
function renderizarCatalogo(lista) {
    const container = document.getElementById("containerCatalogo");
    container.innerHTML = "";

    const grupos = lista.reduce((acc, produto) => {
        const cat = produto.mainCategory || "Sem Categoria";
        if (!acc[cat]) acc[cat] = [];
        acc[cat].push(produto);
        return acc;
    }, {});

    // Para cada categoria, criar o HTML
    for (const [categoria, produtosDaCategoria] of Object.entries(grupos)) {

        // Botão da Pasta
        const btnPasta = document.createElement("button");
        btnPasta.className = "pasta-categoria";
        btnPasta.innerHTML = `📂 ${categoria} <span>(${produtosDaCategoria.length} itens)</span>`;

        // Área de Conteúdo (inicialmente escondida)
        const divConteudo = document.createElement("div");
        divConteudo.className = "conteudo-pasta";

        // Ordenar produtos dentro da pasta pela Categoria Secundária (para organizar melhor)
        produtosDaCategoria.sort((a, b) => (a.secondCategory || "").localeCompare(b.secondCategory || ""));

        // Criar cartões dos produtos
        produtosDaCategoria.forEach(p => {
            let precoUn = p.unitPrice ? p.unitPrice.toLocaleString('pt-BR', {style:'currency', currency:'BRL'}) : 'R$ 0,00';
            let precoEmb = p.packPrice ? p.packPrice.toLocaleString('pt-BR', {style:'currency', currency:'BRL'}) : 'R$ 0,00';
            let tamanhoEmb = p.unitPackSize || 1;
            let imagemSrc = p.imageUrl && p.imageUrl.trim() !== "" ? p.imageUrl : "https://placehold.co/100x100?text=Sem+Foto";

            divConteudo.innerHTML += `
                <div class="card-produto">
                    <div>
                    
                        <div style="margin-right: 15px;">
                            <img src="${imagemSrc}" alt="${p.name}"style="width: 80px; height: 80px; object-fit: cover; border-radius: 5px; border: 1px solid #ddd;">
                        </div>
                    
                        <strong>${p.name}</strong><br>
                        <small style="color: #666;">${p.manufacturer} | ${p.secondCategory}</small><br>
                        
                        <div style="margin-top: 5px; font-size: 14px;">
                            <label>
                                <input type="radio" name="tipo_${p.id}" value="unidade" checked onchange="atualizarPrecoDisplay(${p.id}, 'u')"> 
                                Unid: <b>${precoUn}</b>
                            </label>
                            &nbsp;&nbsp;
                            <label>
                                <input type="radio" name="tipo_${p.id}" value="embalagem" onchange="atualizarPrecoDisplay(${p.id}, 'e')"> 
                                Emb (${tamanhoEmb}x): <b>${precoEmb}</b>
                            </label>
                        </div> 
                    </div>
                    <button class="btn-add" onclick="adicionarAoCarrinho(${p.id})">➕ Adicionar</button>
                </div>
            `;
        });

        // Evento de clique para abrir/fechar a pasta
        btnPasta.onclick = function() {
            // Alterna entre mostrar e esconder (Toggle)
            if (divConteudo.style.display === "block") {
                divConteudo.style.display = "none";
            } else {
                divConteudo.style.display = "block";
            }
        };

        container.appendChild(btnPasta);
        container.appendChild(divConteudo);
    }
}

// 3. Função de Busca
function filtrarCatalogo() {
    const termo = document.getElementById("campoBusca").value.toLowerCase();

    if (termo === "") {
        renderizarCatalogo(todosProdutos);
        return;
    }

    // Filtra produtos
    const filtrados = todosProdutos.filter(p =>
        p.name.toLowerCase().includes(termo) ||
        p.mainCategory.toLowerCase().includes(termo) ||
        p.secondCategory.toLowerCase().includes(termo)
    );

    // Renderiza apenas os filtrados (mas mantendo a lógica de pastas!)
    renderizarCatalogo(filtrados);

    // Dica UX: Se estiver filtrando, abre todas as pastas automaticamente
    const pastas = document.querySelectorAll(".conteudo-pasta");
    pastas.forEach(div => div.style.display = "block");
}

// 4. Lógica do Carrinho
function adicionarAoCarrinho(idProduto) {
    const produto = todosProdutos.find(p => p.id === idProduto);

    // Verifica qual radio button está marcado (Unidade ou Embalagem)
    const radios = document.getElementsByName(`tipo_${idProduto}`);
    let tipoVenda = 'unidade'; // valor padrão
    for (const r of radios) {
        if (r.checked) tipoVenda = r.value;
    }

    const ehEmbalagem = (tipoVenda === 'embalagem');
    const preco = ehEmbalagem ? produto.packPrice : produto.unitPrice;

    // Verifica se já existe no carrinho com O MESMO tipo
    const itemExistente = carrinho.find(item => item.produto.id === idProduto && item.porEmbalagem === ehEmbalagem);

    if (itemExistente) {
        itemExistente.quantidade++;
    } else {
        carrinho.push({
            produto: produto,
            quantidade: 1,
            porEmbalagem: ehEmbalagem,
            precoUnitario: preco
        });
    }

    atualizarCarrinhoVisual();
}

function atualizarCarrinhoVisual() {
    const divLista = document.getElementById("listaCarrinho");
    const spanTotal = document.getElementById("valorTotal");

    divLista.innerHTML = "";
    let totalGeral = 0;

    if (carrinho.length === 0) {
        divLista.innerHTML = '<p style="text-align: center; color: #777;">O carrinho está vazio.</p>';
        spanTotal.innerText = "R$ 0,00";
        return;
    }

    carrinho.forEach((item, index) => {
        let subtotal = item.quantidade * item.precoUnitario;
        totalGeral += subtotal;

        let descTipo = item.porEmbalagem ? "📦 Emb" : "👤 Unid";

        divLista.innerHTML += `
            <div class="item-carrinho">
                <div style="display:flex; justify-content:space-between;">
                    <strong>${item.produto.name}</strong>
                    <span style="color:red; cursor:pointer;" onclick="removerDoCarrinho(${index})">🗑️</span>
                </div>
                
                <div style="margin-top: 8px; font-size: 14px; display: flex; align-items: center; justify-content: space-between;">
                    
                    <div style="margin-right: 10px;">
                        <label style="font-size: 10px; display:block;">Qtd:</label>
                        <input type="number" min="1" value="${item.quantidade}" 
                               style="width: 40px; padding: 5px;" 
                               onchange="alterarQtd(${index}, this.value)">
                    </div>

                    <div>
                        <label style="font-size: 10px; display:block;">Preço (${descTipo}):</label>
                        <input type="number" step="0.01" value="${item.precoUnitario}" 
                               style="width: 70px; padding: 5px; border: 1px solid #17a2b8; color: #17a2b8; font-weight: bold;" 
                               onchange="alterarPreco(${index}, this.value)">
                    </div>

                </div>
                
                <div style="text-align: right; margin-top: 5px; border-top: 1px solid #eee; padding-top: 5px;">
                   Subtotal: <strong>${subtotal.toLocaleString('pt-BR', {style:'currency', currency:'BRL'})}</strong>
                </div>
            </div>
        `;
    });

    spanTotal.innerText = totalGeral.toLocaleString('pt-BR', {style:'currency', currency:'BRL'});
}

function removerDoCarrinho(index) {
    carrinho.splice(index, 1);
    atualizarCarrinhoVisual();
}

function alterarQtd(index, novaQtd) {
    if (novaQtd < 1) {
        removerDoCarrinho(index);
        return;
    }
    carrinho[index].quantidade = parseInt(novaQtd);
    atualizarCarrinhoVisual();
}

// 5. Finalizar Venda
// No início do arquivo, adicione:
fetch('/clientes').then(r => r.json()).then(lista => {
    let select = document.getElementById("selectCliente");
    lista.forEach(c => {
        let opt = document.createElement("option");
        opt.value = c.id;
        opt.innerText = c.nome;
        select.appendChild(opt);
    });
});

// Modifique a função finalizarVenda:
function finalizarVenda(tipo) {
    if (carrinho.length === 0) {
        alert("O carrinho está vazio!");
        return;
    }

    // MUDANÇA AQUI: Pegamos o valor do input hidden, não mais do select
    let clienteId = document.getElementById("idClienteSelecionado").value;
    let nomeCliente = document.getElementById("inputNomeCliente").value;

    // Se for Pedido, EXIGE cliente
    if (tipo === 'PEDIDO') {
        if (!clienteId) {
            alert("Para criar um pedido, você DEVE pesquisar e selecionar um cliente no topo da página!");
            document.getElementById("inputNomeCliente").focus();
            return;
        }

        if (!confirm(`Salvar pedido para o cliente abaixo?\n\n👤 ${nomeCliente}\n\nO estoque NÃO será baixado agora.`)) return;

        const dadosPedido = {
            clienteId: clienteId,
            itens: carrinho.map(item => ({
                produtoId: item.produto.id,
                quantidade: item.quantidade,
                porEmbalagem: item.porEmbalagem,
                precoVendido: item.precoUnitario
            }))
        };

        fetch('/pedidos/criar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosPedido)
        }).then(r => r.text()).then(msg => {
            alert(msg);
            carrinho = [];
            atualizarCarrinhoVisual();
            limparSelecaoCliente(); // Limpa o cliente selecionado após o pedido
        });

    } else {
        // Venda Varejo (Igual ao anterior)
        if (!confirm("Confirmar a venda e dar baixa no estoque IMEDIATAMENTE?")) return;

        const dadosParaEnvio = carrinho.map(item => ({
            produtoId: item.produto.id,
            quantidade: item.quantidade,
            porEmbalagem: item.porEmbalagem,
            precoVendido: item.precoUnitario
        }));

        fetch('/venda/finalizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosParaEnvio)
        }).then(r => r.text()).then(msg => {
            alert(msg);
            carrinho = [];
            atualizarCarrinhoVisual();
            // Não limpamos o cliente aqui necessariamente, pois pode ser venda de balcão sem cliente
        });
    }
}

// Auxiliar apenas para fins de debug ou UI
function atualizarPrecoDisplay(id, tipo) {
    // Pode ser usada para destacar o preço selecionado no cartão se quiseres evoluir o CSS depois
}

function alterarPreco(index, novoValor) {
    if (!novoValor || novoValor < 0) return;

    // Atualiza o preço no carrinho (convertendo para float para evitar erros de texto)
    carrinho[index].precoUnitario = parseFloat(novoValor);

    // Recalcula o total visualmente
    atualizarCarrinhoVisual();
}

function abrirModalHistorico() {
    let clienteId = document.getElementById("idClienteSelecionado").value;
    let nomeCliente = document.getElementById("inputNomeCliente").value;

    if (!clienteId) return;

    document.getElementById("nomeClienteHist").innerText = nomeCliente;
    document.getElementById("modalHistoricoCliente").style.display = 'block';
    document.getElementById("listaHistoricoBody").innerHTML = '<tr><td colspan="4">Carregando...</td></tr>';

    // Busca no Backend
    fetch(`/pedidos/cliente/${clienteId}`)
        .then(r => r.json())
        .then(lista => {
            historicoCache = lista; // Guarda para usar depois
            renderizarTabelaHistorico(lista);
        })
        .catch(e => {
            console.error(e);
            document.getElementById("listaHistoricoBody").innerHTML = '<tr><td colspan="4">Erro ao carregar.</td></tr>';
        });
}

function renderizarTabelaHistorico(lista) {
    let tbody = document.getElementById("listaHistoricoBody");
    tbody.innerHTML = "";

    if (lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="padding:15px; text-align:center;">Nenhuma compra anterior encontrada.</td></tr>';
        return;
    }

    // Ordena do mais recente para o mais antigo
    lista.sort((a, b) => new Date(b.dataCriacao) - new Date(a.dataCriacao));

    lista.forEach((pedido, index) => {
        let data = new Date(pedido.dataCriacao).toLocaleDateString('pt-BR') + ' ' + new Date(pedido.dataCriacao).toLocaleTimeString('pt-BR', {hour: '2-digit', minute:'2-digit'});
        let total = pedido.valorTotal.toLocaleString('pt-BR', {style:'currency', currency:'BRL'});

        // Resumo dos itens (ex: "Dipirona (2), Paracetamol (1)...")
        let resumoItens = pedido.itens.map(i => `${i.produto.name} (${i.quantidade})`).join(', ');
        if(resumoItens.length > 50) resumoItens = resumoItens.substring(0, 50) + "...";

        tbody.innerHTML += `
            <tr style="border-bottom: 1px solid #ddd;">
                <td style="padding: 10px;">
                    <strong>${data}</strong><br>
                    <small style="color: #666;">${resumoItens}</small>
                </td>
                <td style="padding: 10px;">${pedido.status}</td>
                <td style="padding: 10px; font-weight: bold;">${total}</td>
                <td style="padding: 10px; text-align: center;">
                    <button onclick="repetirPedido(${index})" 
                            style="background-color: #28a745; color: white; border: none; padding: 8px 12px; border-radius: 4px; cursor: pointer;">
                        🔄 Repetir
                    </button>
                </td>
            </tr>
        `;
    });
}

function repetirPedido(index) {
    let pedidoAntigo = historicoCache[index];

    if(!confirm("Deseja adicionar os itens deste pedido ao carrinho atual?")) return;

    // Percorre os itens do pedido antigo e adiciona ao carrinho
    pedidoAntigo.itens.forEach(itemAntigo => {
        carrinho.push({
            produto: itemAntigo.produto, // O objeto produto completo que vem do JSON
            quantidade: itemAntigo.quantidade,
            porEmbalagem: itemAntigo.porEmbalagem,
            precoUnitario: itemAntigo.precoVendido // Usa o preço que foi pago na época (ou itemAntigo.produto.unitPrice se quiser atualizar o preço)
        });
    });

    atualizarCarrinhoVisual();
    fecharModalHistorico();
    alert("Itens adicionados ao carrinho!");
}

function fecharModalHistorico() {
    document.getElementById("modalHistoricoCliente").style.display = 'none';
}

// Fechar se clicar fora
window.onclick = function(event) {
    let modal = document.getElementById('modalHistoricoCliente');
    if (event.target == modal) {
        fecharModalHistorico();
    }
}