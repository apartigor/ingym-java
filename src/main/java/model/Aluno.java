package model;

public class Aluno {

    private Integer id;
    private String nome;
    private String email;
    private String criadoEm;
    private Integer planoId;
    private Plano plano;

    // campos preenchidos automaticamente pela consulta ao ViaCEP
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;

    public Aluno() {}

    public Aluno(Integer id, String nome, String email, String criadoEm, Integer planoId, Plano plano,
                 String cep, String logradouro, String bairro, String cidade, String uf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.criadoEm = criadoEm;
        this.planoId = planoId;
        this.plano = plano;
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }

    public Integer getPlanoId() { return planoId; }
    public void setPlanoId(Integer planoId) { this.planoId = planoId; }

    public Plano getPlano() { return plano; }
    public void setPlano(Plano plano) { this.plano = plano; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}
