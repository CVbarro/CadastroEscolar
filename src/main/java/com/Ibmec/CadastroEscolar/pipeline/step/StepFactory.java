package com.Ibmec.CadastroEscolar.pipeline.step;

import com.Ibmec.CadastroEscolar.model.Usuario;
import com.Ibmec.CadastroEscolar.pipeline.core.Step;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class StepFactory {

    private final Step<Usuario> padronizarNome;
    private final Step<Usuario> validarEmail;
    private final Step<Usuario> gerarMatricula;
    private final Step<Usuario> calcularIdade;

    public StepFactory(
            Step<Usuario> padronizarNome,
            Step<Usuario> validarEmail,
            Step<Usuario> gerarMatricula,
            Step<Usuario> calcularIdade
    ) {
        this.padronizarNome = padronizarNome;
        this.validarEmail = validarEmail;
        this.gerarMatricula = gerarMatricula;
        this.calcularIdade = calcularIdade;
    }

    public <T> Step<T> createStep(Class<T> clazz, String name) {
        if (clazz.equals(Usuario.class)) {
            switch (name) {
                case "padronizarNome": return (Step<T>) padronizarNome;
                case "validarEmail":   return (Step<T>) validarEmail;
                case "gerarMatricula": return (Step<T>) gerarMatricula;
                case "calcularIdade":  return (Step<T>) calcularIdade;
                default:
                    throw new RuntimeException("Etapa desconhecida: " + name);
            }
        }
        throw new RuntimeException("Classe não suportada: " + clazz.getSimpleName());
    }
}

