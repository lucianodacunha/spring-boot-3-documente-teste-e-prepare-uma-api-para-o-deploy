package io.github.lucianodacunha.api.validation.cancelamento;

import io.github.lucianodacunha.api.model.DadosCancelamentoConsulta;

public interface CancelamentoDeConsultaValidator {

    void validar(DadosCancelamentoConsulta dados);

}