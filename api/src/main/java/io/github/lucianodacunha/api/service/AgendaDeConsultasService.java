package io.github.lucianodacunha.api.service;

import io.github.lucianodacunha.api.entity.Consulta;
import io.github.lucianodacunha.api.entity.Medico;
import io.github.lucianodacunha.api.exception.ValidacaoException;
import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import io.github.lucianodacunha.api.model.DadosCancelamentoConsulta;
import io.github.lucianodacunha.api.model.DadosDetalhamentoConsulta;
import io.github.lucianodacunha.api.repository.ConsultaRepository;
import io.github.lucianodacunha.api.repository.MedicoRepository;
import io.github.lucianodacunha.api.repository.PacienteRepository;
import io.github.lucianodacunha.api.validation.agendamento.AgendamentoDeConsultaValidator;
import io.github.lucianodacunha.api.validation.cancelamento.CancelamentoDeConsultaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultasService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<AgendamentoDeConsultaValidator> agendamentoValidators;
    @Autowired
    private List<CancelamentoDeConsultaValidator> cancelamentoValidators;

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
        if (!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ValidacaoException("Id do paciente informado não existe!");
        }

        if (dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
            throw new ValidacaoException("Id do médico informado não existe!");
        }

        agendamentoValidators.forEach(v -> v.validar(dados));

        var paciente = pacienteRepository.findById(dados.idPaciente()).get();
        var medico = escolherMedico(dados);
        if (medico == null){
            throw new ValidacaoException("Não existe médico disponível nessa data.");
        }
        var consulta = new Consulta(null, medico, paciente, dados.data(), null);
        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);
    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando médico não for escolhido!");
        }

        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
    }

    public void cancelar(DadosCancelamentoConsulta dados) {
        if (!consultaRepository.existsById(dados.idConsulta())) {
            throw new ValidacaoException("Id da consulta informado não existe!");
        }

        cancelamentoValidators.forEach(v -> v.validar(dados));

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());
        consulta.cancelar(dados.motivo());
    }

}
