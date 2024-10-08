package br.com.alura;

public class Agencia {

    Agencia(String nome, String razaoSocial, String cnpj, String situacaoCadastral) {
        this.id = 1L;
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.situacaoCadastral = SituacaoCadastral.valueOf(situacaoCadastral);
    }

    private Long id;
    private final String nome;
    private final String razaoSocial;
    private final String cnpj;
    private final SituacaoCadastral situacaoCadastral;

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

    public SituacaoCadastral getSituacaoCadastral() {
        return situacaoCadastral;
    }
}
