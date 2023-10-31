package io.github.lucianodacunha.api.controller;// Trecho de código suprimido

import io.github.lucianodacunha.api.model.DadosAgendamentoConsulta;
import io.github.lucianodacunha.api.model.DadosCancelamentoConsulta;
import io.github.lucianodacunha.api.model.DadosDetalhamentoConsulta;
import io.github.lucianodacunha.api.service.AgendaDeConsultasService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("consultas")
public class ConsultaController {

    @Autowired
    private AgendaDeConsultasService agenda;

    @PostMapping
    @Transactional
    public ResponseEntity agendar(@RequestBody @Valid DadosAgendamentoConsulta dados) {
        System.out.println(dados);
        agenda.agendar(dados);
        return ResponseEntity.ok(new DadosDetalhamentoConsulta(null, null, null, null));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity cancelar(@RequestBody @Valid DadosCancelamentoConsulta dados) {
        agenda.cancelar(dados);
        return ResponseEntity.noContent().build();
    }

}