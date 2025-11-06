package br.com.alura.domain;

import jakarta.persistence.*;

@Entity
public class Agencia {

    Agencia() {

    }

    Agencia(Long id, String nome, String razaoSocial, String cnpj, String situacaoCadastral) {
        this.id = id;
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.situacaoCadastral = situacaoCadastral;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(name = "razao_social")
    private String razaoSocial;
    private String cnpj;

    @Column(name = "situacao_cadastral")
    private String situacaoCadastral;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }
}