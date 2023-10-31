package io.github.lucianodacunha.api.controller;

import io.github.lucianodacunha.api.entity.Medico;
import io.github.lucianodacunha.api.model.DadosAtualizacaoMedico;
import io.github.lucianodacunha.api.model.DadosCadastroMedico;
import io.github.lucianodacunha.api.model.DadosDetalhamentoMedico;
import io.github.lucianodacunha.api.model.DadosListagemMedico;
import io.github.lucianodacunha.api.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados,
                                    UriComponentsBuilder uriBuilder){
        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));

    }

    /**
     * Para fazer paginação, devemos retornar um objeto Page.
     * O repository tem uma sobrecarga do método find que retorna um Page, e
     * como parâmetro, recebe um objeto Pageable, além da anotação
     * @PageableDefault, que define a quantidade de registros por página e a
     * ordenação.
     * @param paginacao
     * @return
     */
    @GetMapping
    public ResponseEntity <Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        repository.delete(medico);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
}

