package br.com.alura.domain.audit;

public class Audit {

    public Audit(Long id, String cnpj, String status) {
        this.id = id;
        this.cnpj = cnpj;
        this.status = status;
    }

    private Long id;
    private String cnpj;

    private String status;

    public Long getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getStatus() {
        return status;
    }
}
