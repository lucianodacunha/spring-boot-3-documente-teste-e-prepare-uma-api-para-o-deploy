package io.github.lucianodacunha.api.validation.agendamento;

import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class HorarioFuncionamentoClinicaValidator implements AgendamentoDeConsultaValidator{

    public void validar(DadosAgendamentoConsulta dados){
        var dataConsulta = dados.data();

        var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var antesDaAberturaDaClinia = dataConsulta.getHour() < 7;
        var depoisDoEncerramentoDaClinia = dataConsulta.getHour() > 18;

        if (domingo || antesDaAberturaDaClinia || depoisDoEncerramentoDaClinia){
            throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica");
        }

    }

}
