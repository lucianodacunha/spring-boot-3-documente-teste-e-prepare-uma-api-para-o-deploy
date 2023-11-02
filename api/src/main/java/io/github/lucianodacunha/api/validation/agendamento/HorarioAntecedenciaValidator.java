package io.github.lucianodacunha.api.validation.agendamento;

import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

// Necessário nomear o componente pois existem dois com o mesmo nome.
@Component("HorarioAntecedenciaValidatorAgendamento")
public class HorarioAntecedenciaValidator  implements AgendamentoDeConsultaValidator{
    public void validar(DadosAgendamentoConsulta dados){
        var dataConsulta = dados.data();

        var agora = LocalDateTime.now();
        var diferencaEmMinutos = Duration.between(agora, dataConsulta).toMinutes();

        if (diferencaEmMinutos < 30){
            throw new ValidacaoException("Consulta deve ser agendada com antecedencia mínima de 30 minutos");
        }

        var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var antesDaAberturaDaClinia = dataConsulta.getHour() < 7;
        var depoisDoEncerramentoDaClinia = dataConsulta.getHour() > 18;

        if (domingo || antesDaAberturaDaClinia || depoisDoEncerramentoDaClinia){
            throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica");
        }

    }

}
