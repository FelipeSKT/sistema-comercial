let todosProdutos = [];
let carrinho = [];

// 1. Carregar produtos ao iniciar
fetch('/produtos')
    .then(r => r.json())
    .then(lista => {
        todosProdutos = lista;
        renderizarCatalogo(lista);
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
function finalizarVenda() {
    if (carrinho.length === 0) {
        alert("O carrinho está vazio!");
        return;
    }

    if (!confirm("Confirmar a venda e dar baixa no estoque?")) return;

    // Prepara os dados para o Java
    const dadosParaEnvio = carrinho.map(item => ({
        produtoId: item.produto.id,
        quantidade: item.quantidade,
        porEmbalagem: item.porEmbalagem,
        precoVendido: item.precoUnitario // Envia o preço (original ou editado)
    }));

    fetch('/venda/finalizar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dadosParaEnvio)
    })
        .then(r => r.text())
        .then(msg => {
            alert(msg);
            carrinho = []; // Limpa carrinho
            atualizarCarrinhoVisual();
        })
        .catch(err => console.error(err));
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