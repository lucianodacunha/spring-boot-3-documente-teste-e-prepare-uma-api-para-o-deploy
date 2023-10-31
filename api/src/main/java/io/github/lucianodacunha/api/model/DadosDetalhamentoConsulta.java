package io.github.lucianodacunha.api.model;

import java.time.LocalDateTime;

public record DadosDetalhamentoConsulta(Long id, Long idMedico, Long idPaciente,
                                        LocalDateTime data) {}
