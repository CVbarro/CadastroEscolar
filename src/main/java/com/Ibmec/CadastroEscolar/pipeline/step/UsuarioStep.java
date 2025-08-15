package com.Ibmec.CadastroEscolar.pipeline.step;

import com.Ibmec.CadastroEscolar.model.Usuario;
import com.Ibmec.CadastroEscolar.pipeline.core.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.Date;

@Configuration
public class UsuarioStep {

    @Bean
    public Step<Usuario> padronizarNome() {
        return (usuario, params) -> {
            if (usuario.getNome() != null) {
                usuario.setNome(usuario.getNome().trim().toUpperCase());
            }
            return usuario;
        };
    }

    @Bean
    public Step<Usuario> validarEmail() {
        return (usuario, params) -> {
            if (usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
                throw new RuntimeException("Email inválido para o usuário: " + usuario.getNome());
            }
            return usuario;
        };
    }

    @Bean
    public Step<Usuario> gerarMatricula() {
        return (usuario, params) -> {
            if (usuario.getMatricula() == null) {
                usuario.setMatricula(UUID.randomUUID().toString());
            }
            return usuario;
        };
    }

    @Bean
    public Step<Usuario> calcularIdade() {
        return (usuario, params) -> {
            if (usuario.getDtNascimento() != null) {
                long diff = new Date().getTime() - usuario.getDtNascimento().getTime();
                usuario.setIdade((int) (diff / (1000L * 60 * 60 * 24 * 365)));
            }
            return usuario;
        };
    }
}
