package model;

// classe que representa um plano da academia
public class Plano {

    private Integer id;
    private String nome;
    private String descricao;
    private Double preco;
    private String criadoEm;

    // construtor vazio pro jackson funcionar
    public Plano() {}

    // construtor usado quando montamos o objeto com dados do banco
    public Plano(Integer id, String nome, String descricao, Double preco, String criadoEm) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.criadoEm = criadoEm;
    }

    // getters e setters pra acessar os atributos privados
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }
}
