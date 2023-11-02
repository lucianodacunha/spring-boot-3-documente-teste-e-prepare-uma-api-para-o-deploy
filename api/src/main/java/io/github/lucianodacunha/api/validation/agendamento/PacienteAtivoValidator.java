package io.github.lucianodacunha.api.validation.agendamento;

import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import io.github.lucianodacunha.api.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PacienteAtivoValidator implements AgendamentoDeConsultaValidator{

    @Autowired
    private PacienteRepository pacienteRepository;

    public void validar(DadosAgendamentoConsulta dados){

        boolean pacienteEstaAtivo = pacienteRepository.findAtivoById(dados.idPaciente());

        if (!pacienteEstaAtivo) {
            throw new ValidacaoException("Paciente informado não está inativo");
        }


    }
}
