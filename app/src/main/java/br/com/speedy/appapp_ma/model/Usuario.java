package br.com.speedy.appapp_ma.model;

public class Usuario {

	private Long id;
	private String nome;
	private String login;
	private String senha;
	
	public Usuario(){}


	public Long getId() {

        return id;
	}


	public void setId(Long id) {

        this.id = id;
	}


	public String getNome() {

        return nome;
	}


	public void setNome(String nome) {

        this.nome = nome;
	}


	public String getLogin(){
		return login;
	}


	public void setLogin(String login) {

        this.login = login;
	}


	public String getSenha() {

        return senha;
	}


	public void setSenha(String senha) {

        this.senha = senha;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((senha == null) ? 0 : senha.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (senha == null) {
			if (other.senha != null)
				return false;
		} else if (!senha.equals(other.senha))
			return false;
		return true;
	}
	
	
	
}
