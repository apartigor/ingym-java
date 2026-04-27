package model;

// cada aluno esta vinculado a um plano pelo planoId
public class Aluno {

    private Integer id;
    private String nome;
    private String email;
    private String criadoEm;
    private Integer planoId;  // chave estrangeira pro plano
    private Plano plano;      // objeto do plano, preenchido nas buscas

    // construtor vazio necessario pro jackson
    public Aluno() {}

    // construtor completo usado quando montamos o objeto com dados do banco
    public Aluno(Integer id, String nome, String email, String criadoEm, Integer planoId, Plano plano) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.criadoEm = criadoEm;
        this.planoId = planoId;
        this.plano = plano;
    }

    // getters e setters
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
}
