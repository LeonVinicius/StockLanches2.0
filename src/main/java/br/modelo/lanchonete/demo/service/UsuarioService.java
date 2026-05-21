package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.model.Usuario;
import br.modelo.lanchonete.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioLogado = null;

    public boolean autenticar(String email, String senha) {
       
        Usuario usuarioDb = usuarioRepository.findByEmail(email);

        if (usuarioDb != null && usuarioDb.getSenha().equals(senha)) {
            this.usuarioLogado = usuarioDb;
            return true;
        }
        return false;
    }

    public void cadastrar(Usuario novoUsuario) {
       
        usuarioRepository.save(novoUsuario);
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isUserLogged() {
        return usuarioLogado != null;
    }
}