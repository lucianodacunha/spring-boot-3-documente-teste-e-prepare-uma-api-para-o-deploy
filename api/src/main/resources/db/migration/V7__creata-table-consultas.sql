CREATE TABLE consultas
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    medico_id   BIGINT                NULL,
    paciente_id BIGINT                NULL,
    data        datetime              NULL,
    CONSTRAINT pk_consultas PRIMARY KEY (id)
);

ALTER TABLE consultas
    ADD CONSTRAINT FK_CONSULTAS_ON_MEDICO FOREIGN KEY (medico_id) REFERENCES medicos (id);

ALTER TABLE consultas
    ADD CONSTRAINT FK_CONSULTAS_ON_PACIENTE FOREIGN KEY (paciente_id) REFERENCES pacientes (id);