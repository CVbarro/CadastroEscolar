package com.Ibmec.CadastroEscolar.pipeline;

import com.Ibmec.CadastroEscolar.model.Usuario;
import com.Ibmec.CadastroEscolar.pipeline.core.Pipeline;
import com.Ibmec.CadastroEscolar.pipeline.core.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PipelineConfig {

    @Bean
    public Pipeline<Usuario> usuarioPipeline(List<Step<Usuario>> stepsDeUsuario) {
        return new Pipeline<>(stepsDeUsuario);
    }
}
