package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Usuario;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping({"/", "/login"})
    public String login(Model model,
                        @RequestParam(required = false) String cadastrado,
                        @RequestParam(required = false) String erro) {

        // Se já estiver logado, vai direto para o PDV
        if (usuarioService.isUserLogged()) {
            return "redirect:/pdv";
        }

        if ("true".equals(cadastrado)) {
            model.addAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        }

        if ("true".equals(erro)) {
            model.addAttribute("mensagemErro", "Credenciais inválidas.");
        }

        model.addAttribute("usuario", new Usuario());

        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@RequestParam String email,
                                 @RequestParam String senha,
                                 RedirectAttributes ra) {

        if (usuarioService.autenticar(email, senha)) {

            // LOGIN AGORA ABRE O PDV
            return "redirect:/pdv";

        } else {

            ra.addAttribute("erro", "true");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        usuarioService.logout();
        return "redirect:/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {

        // Se já estiver logado, vai direto para o PDV
        if (usuarioService.isUserLogged()) {
            return "redirect:/pdv";
        }

        model.addAttribute("usuario", new Usuario());

        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@RequestParam String usuario,
                                    @RequestParam String email,
                                    @RequestParam String senha,
                                    @RequestParam String confirm_password,
                                    RedirectAttributes ra) {

        if (!senha.equals(confirm_password)) {
            ra.addFlashAttribute("mensagemErro", "As senhas não coincidem!");
            return "redirect:/cadastro";
        }

        String regexSenhaForte = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{9,20}$";

        if (!senha.matches(regexSenhaForte)) {
            ra.addFlashAttribute(
                "mensagemErro",
                "A senha deve ter entre 9-20 caracteres, contendo números, letras maiúsculas e minúsculas."
            );

            return "redirect:/cadastro";
        }

        usuarioService.cadastrar(new Usuario(usuario, email, senha));

        ra.addAttribute("cadastrado", "true");

        return "redirect:/login";
    }
}