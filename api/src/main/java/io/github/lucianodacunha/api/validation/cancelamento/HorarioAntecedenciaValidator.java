package io.github.lucianodacunha.api.validation.cancelamento;

import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosCancelamentoConsulta;
import io.github.lucianodacunha.api.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

// Necessário nomear o componente pois existem dois com o mesmo nome.
@Component("HorarioAntecedenciaValidatorCancelamento")
public class HorarioAntecedenciaValidator implements CancelamentoDeConsultaValidator {

    @Autowired
    private ConsultaRepository repository;

    @Override
    public void validar(DadosCancelamentoConsulta dados) {
        var consulta = repository.getReferenceById(dados.idConsulta());
        var agora = LocalDateTime.now();
        var diferencaEmHoras = Duration.between(agora, consulta.getData()).toHours();

        if (diferencaEmHoras < 24) {
            throw new ValidacaoException("Consulta somente pode ser cancelada com antecedência mínima de 24h!");
        }
    }
}