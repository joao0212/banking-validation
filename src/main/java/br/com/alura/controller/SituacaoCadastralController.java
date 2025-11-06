package br.com.alura.controller;

import br.com.alura.domain.Agencia;
import br.com.alura.repository.SituacaoCadastralRepository;
import br.com.alura.service.SituacaoCadastralService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/situacao-cadastral")
public class SituacaoCadastralController {

    private final SituacaoCadastralRepository situacaoCadastralRepository;
    private final SituacaoCadastralService situacaoCadastralService;

    SituacaoCadastralController(SituacaoCadastralRepository situacaoCadastralRepository, SituacaoCadastralService situacaoCadastralService) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.situacaoCadastralService = situacaoCadastralService;
    }

    @POST
    @WithTransaction
    @NonBlocking
    public Uni<Void> cadastrar(Agencia agencia) {
        return this.situacaoCadastralRepository.persist(agencia).replaceWithVoid();
    }

    @GET
    @WithSession
    public Uni<List<Agencia>> buscarTodos() {
        return this.situacaoCadastralRepository.findAll().list();
    }

    @GET
    @WithSession
    @Path("{cnpj}")
    public Uni<RestResponse<Agencia>> buscarPorCnpj(String cnpj) {
        Uni<Agencia> agencia = this.situacaoCadastralRepository.findByCnpj(cnpj);
        return agencia
                .onItem().ifNotNull().transform(RestResponse::ok)
                .onItem().ifNull().continueWith(RestResponse::noContent);
    }

    @PUT
    public Uni<RestResponse<Void>> alterar(Agencia agencia) {
        return situacaoCadastralService.alterar(agencia).replaceWith(RestResponse.ok());
    }
}