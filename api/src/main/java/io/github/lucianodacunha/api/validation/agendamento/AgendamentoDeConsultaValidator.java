package io.github.lucianodacunha.api.validation.agendamento;


import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;

public interface AgendamentoDeConsultaValidator {

    void validar(DadosAgendamentoConsulta dados);

}