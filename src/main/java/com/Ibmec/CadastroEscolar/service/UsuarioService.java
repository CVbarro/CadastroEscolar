package com.Ibmec.CadastroEscolar.service;


import com.Ibmec.CadastroEscolar.model.Usuario;
import com.Ibmec.CadastroEscolar.pipeline.core.Pipeline;
import com.Ibmec.CadastroEscolar.pipeline.core.Step;
import com.Ibmec.CadastroEscolar.pipeline.step.StepFactory;
import com.Ibmec.CadastroEscolar.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final StepFactory stepFactory;

    public UsuarioService(UsuarioRepository usuarioRepository, StepFactory stepFactory) {
        this.usuarioRepository = usuarioRepository;
        this.stepFactory = stepFactory;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    private List<Map<String, Object>> getPipelineConfig() {
        return List.of(
                Map.of("step", "padronizarNome", "enabled", true),
                Map.of("step", "validarEmail", "enabled", true),
                Map.of("step", "gerarMatricula", "enabled", true),
                Map.of("step", "calcularIdade", "enabled", true)
        );
    }

    public Usuario processarUsuario(Usuario usuario) {
        Pipeline<Usuario> pipeline = montarPipeline();
        Usuario processado = pipeline.execute(usuario);

        Optional<Usuario> existente = usuarioRepository.findByEmail(processado.getEmail());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um usuário com o email: " + processado.getEmail());
        }

        return usuarioRepository.save(processado);
    }

    @PostConstruct
    public void processarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) return;

        Pipeline<Usuario> pipeline = montarPipeline();
        List<Usuario> processados = pipeline.executeParallel(usuarios);

        usuarioRepository.saveAll(processados);
        processados.forEach(u -> logger.info("Processado: {}", u));
    }

    private Pipeline<Usuario> montarPipeline() {
        Pipeline<Usuario> pipeline = new Pipeline<>();

        for (Map<String, Object> stepConfig : getPipelineConfig()) {
            String stepName = (String) stepConfig.get("step");
            boolean enabled = (Boolean) stepConfig.getOrDefault("enabled", true);
            Map<String, Object> params = (Map<String, Object>) stepConfig.getOrDefault("params", new HashMap<>());
            Step<Usuario> step = stepFactory.createStep(Usuario.class, stepName);

            if (step != null) {
                pipeline.addStep(step, enabled, params);
            } else {
                logger.warn("Step não encontrado: {}", stepName);
            }
        }
        return pipeline;
    }
}
