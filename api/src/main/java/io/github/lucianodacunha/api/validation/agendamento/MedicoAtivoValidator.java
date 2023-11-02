package io.github.lucianodacunha.api.validation.agendamento;

import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import io.github.lucianodacunha.api.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicoAtivoValidator implements AgendamentoDeConsultaValidator{

    @Autowired
    private MedicoRepository medicoRepository;

    public void validar(DadosAgendamentoConsulta dados){

        if (dados.idMedico() == null) {
            return;
        }

        var medicoEstaAtivo = medicoRepository.findAtivoById(dados.idMedico());

        if (!medicoEstaAtivo) {
            throw new ValidacaoException("Consulta não pode ser agendada com médico excluído");
        }
    }
}
