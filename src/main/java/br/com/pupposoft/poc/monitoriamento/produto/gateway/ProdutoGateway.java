package br.com.pupposoft.poc.monitoriamento.produto.gateway;

import java.util.Optional;

import br.com.pupposoft.poc.monitoriamento.produto.domain.Produto;

public interface ProdutoGateway {

	Optional<Produto> obterPorId(Long id);

}
