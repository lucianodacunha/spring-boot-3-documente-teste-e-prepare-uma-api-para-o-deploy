# Spring Boot 3: documente, teste e prepare uma API para o deploy

## Aula 1. Agendamento de consultas

- Implementar uma nova funcionalidade no projeto;
- Avaliar quando é necessário criar uma classe Service na aplicação;
- Criar uma classe Service, com o objetivo de isolar códigos de regras de negócio, utilizando para isso a anotação @Service;
- Implementar um algoritmo para a funcionalidade de agendamento de consultas;
- Realizar validações de integridade das informações que chegam na API;
- Implementar uma consulta JPQL (Java Persistence Query Language) complexa em uma interface repository, utilizando para isso a anotação @Query.

> https://trello.com/b/O0lGCsKb/api-voll-med

### Códigos para novas funcionalidades

- Controller
- DTO's
- Entidade JPA
- Repository
- Migration
- _Regras de negócios_

### Anotação @JsonAlias

Aprendemos que os nomes dos campos enviados no JSON para a API devem ser idênticos aos nomes dos atributos das classes DTO, pois assim o Spring consegue preencher corretamente as informações recebidas.

Entretanto, pode acontecer de um campo ser enviado no JSON com um nome diferente do atributo definido na classe DTO. Por exemplo, imagine que o seguinte JSON seja enviado para a API:
```
{
    “produto_id” : 12,
    “data_da_compra” : “01/01/2022”
}
```
E a classe DTO criada para receber tais informações seja definida da seguinte maneira:
```
public record DadosCompra(
    Long idProduto,
    LocalDate dataCompra
){}
```
Se isso ocorrer, teremos problemas, pois o Spring vai instanciar um objeto do tipo DadosCompra, mas seus atributos não serão preenchidos e ficarão como null em razão de seus nomes serem diferentes dos nomes dos campos recebidos no JSON.

Temos duas possíveis soluções para essa situação:

1. Renomear os atributos no DTO para terem o mesmo nome dos campos no JSON;

2. Solicitar que a aplicação cliente, que está disparando requisições para a API, altere os nomes dos campos no JSON enviado.

A primeira alternativa citada anteriormente não é recomendada, pois os nomes dos campos no JSON não estão de acordo com o padrão de nomenclatura de atributos utilizado na linguagem Java.

A segunda alternativa seria a mais indicada, porém, nem sempre será possível “obrigar” os clientes da API a alterarem o padrão de nomenclatura utilizado nos nomes dos campos no JSON.

Para essa situação existe ainda uma terceira alternativa, na qual nenhum dos lados (cliente e API) precisam alterar os nomes dos campos/atributos. Basta, para isso, utilizar a anotação `@JsonAlias`:
```
public record DadosCompra(
    @JsonAlias(“produto_id”) Long idProduto,
    @JsonAlias(“data_da_compra”) LocalDate dataCompra
){}
```
A anotação `@JsonAlias` serve para mapear “apelidos” alternativos para os campos que serão recebidos do JSON, sendo possível atribuir múltiplos alias:
```
public record DadosCompra(
    @JsonAlias({“produto_id”, “id_produto”}) Long idProduto,
    @JsonAlias({“data_da_compra”, “data_compra”}) LocalDate dataCompra
){}
```
Dessa forma resolvemos o problema, pois o Spring, ao receber o JSON na requisição, vai procurar os campos considerando todos os alias declarados na anotação `@JsonAlias`.


### Formatação de datas

Como foi demonstrado no vídeo anterior, o Spring tem um padrão de formatação para campos do tipo data quando esses são mapeados em atributos do tipo LocalDateTime. Entretanto, é possível personalizar tal padrão para utilizar outras formatações de nossa preferência.

Por exemplo, imagine que precisamos receber a data/hora da consulta no seguinte formato: `dd/mm/yyyy hh:mm`. Para que isso seja possível, precisamos indicar ao Spring que esse será o formato ao qual a data/hora será recebida na API, sendo que isso pode ser feito diretamente no DTO, com a utilização da anotação @JsonFormat:

```
@NotNull
@Future
@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
LocalDateTime data
```

No atributo pattern indicamos o padrão de formatação esperado, seguindo as regras definidas pelo padrão de datas do Java. Você pode encontrar mais detalhes nesta página do JavaDoc.

Essa anotação também pode ser utilizada nas classes DTO que representam as informações que a API devolve, para que assim o JSON devolvido seja formatado de acordo com o pattern configurado. Além disso, ela não se restringe apenas à classe LocalDateTime, podendo também ser utilizada em atributos do tipo LocalDate e LocalTime.

### Service Pattern

O Padrão Service é muito utilizado na programação e seu nome é muito comentado. Mas apesar de ser um nome único, Service pode ser interpretado de várias maneiras: pode ser um Use Case (Application Service); um Domain Service, que possui regras do seu domínio; um Infrastructure Service, que usa algum pacote externo para realizar tarefas; etc.

Apesar da interpretação ocorrer de várias formas, a ideia por trás do padrão é separar as regras de negócio, as regras da aplicação e as regras de apresentação para que elas possam ser facilmente testadas e reutilizadas em outras partes do sistema.

Existem duas formas mais utilizadas para criar Services. Você pode criar Services mais genéricos, responsáveis por todas as atribuições de um Controller; ou ser ainda mais específico, aplicando assim o S do SOLID: Single Responsibility Principle (Princípio da Responsabilidade Única). Esse princípio nos diz que uma classe/função/arquivo deve ter apenas uma única responsabilidade.

Pense em um sistema de vendas, no qual provavelmente teríamos algumas funções como: Cadastrar usuário, Efetuar login, Buscar produtos, Buscar produto por nome, etc. Logo, poderíamos criar os seguintes Services: CadastroDeUsuarioService, EfetuaLoginService, BuscaDeProdutosService, etc.

Mas é importante ficarmos atentos, pois muitas vezes não é necessário criar um Service e, consequentemente, adicionar mais uma camada e complexidade desnecessária à nossa aplicação. Uma regra que podemos utilizar é a seguinte: se não houverem regras de negócio, podemos simplesmente realizar a comunicação direta entre os controllers e os repositories da aplicação.

### Novas versões do Spring Boot

**ATENÇÃO!**

No vídeo anterior foi utilizada a seguinte query para escolher um médico aleatório:
```
select m from Medico m
where
m.ativo = 1
and
m.especialidade = :especialidade
and
m.id not in(
    select c.medico.id from Consulta c
    where
    c.data = :data
)
order by rand()
limit 1
```
Entretanto, nas versões mais recentes do Hibernate, utilizadas nas versões mais recentes do Spring Boot, a comparação m.ativo = 1 não mais funciona.

Embora o atributo ativo seja do tipo Boolean e não Integer, o Hibernate fazia automaticamente a conversão de 1 para true. Porém, essa conversão deixou de ser feita pelo Hibernate, e com isso a query deve ser alterada para:
```
select m from Medico m
where
m.ativo = true
and
m.especialidade = :especialidade
and
m.id not in(
    select c.medico.id from Consulta c
    where
    c.data = :data
)
order by rand()
limit 1
```

## Aula 2. Regras de Negócio

- Isolar os códigos de validações de regras de negócio em classes separadas, utilizando nelas a anotação @Component do Spring;
- Finalizar a implementação do algoritmo de agendamento de consultas;
- Utilizar os princípios SOLID para deixar o código da funcionalidade de agendamento de consultas mais fácil de entender, evoluir e testar.

### Princípios SOLID

SOLID é uma sigla que representa cinco princípios de programação:

- Single Responsibility Principle (Princípio da Responsabilidade Única)
- Open-Closed Principle (Princípio Aberto-Fechado)
- Liskov Substitution Principle (Princípio da Substituição de Liskov)
- Interface Segregation Principle (Princípio da Segregação de Interface)
- Dependency Inversion Principle (Princípio da Inversão de Dependência)

Cada princípio representa uma boa prática de programação, que quando aplicadas facilita muito a sua manutenção e extensão. Tais princípios foram criados por Robert Martin, conhecido como Uncle Bob, em seu artigo [Design Principles and Design Patterns](http://staff.cs.utu.fi/~jounsmed/doos_06/material/DesignPrinciplesAndPatterns.pdf).

Estes dois episódios do podcast Hipsters.Tech foram dedicados ao tema SOLID:

- [Hipsters #129 - Práticas de Orientação a Objetos](https://cursos.alura.com.br/extra/hipsterstech/praticas-de-orientacao-a-objetos-hipsters-129-a453)

- [Hipsters #219 - SOLID: Código bom e bonito](https://cursos.alura.com.br/extra/hipsterstech/solid-codigo-bom-e-bonito-hipsters-ponto-tech-219-a649)

## Aula 3. Documentação da API

- Adicionar a biblioteca SpringDoc no projeto para que ela faça a geração automatizada da documentação da API;
- Analisar a documentação do SpringDoc para entender como realizar a sua configuração em um projeto;
- Acessar os endereços que disponibilizam a documentação da API nos formatos yaml e html;
- Utilizar o Swagger UI para visualizar e testar uma API Rest;
- Configurar o JWT na documentação gerada pelo SpringDoc.

### OpenAPI Initiative

A documentação é algo muito importante em um projeto, principalmente se ele for uma API Rest, pois nesse caso podemos ter vários clientes que vão precisar se comunicar com ela, necessitando então de uma documentação que os ensinem como realizar essa comunicação de maneira correta.

Por muito tempo não existia um formato padrão de se documentar uma API Rest, até que em 2010 surgiu um projeto conhecido como Swagger, cujo objetivo era ser uma especificação open source para design de APIs Rest. Depois de um tempo, foram desenvolvidas algumas ferramentas para auxiliar pessoas desenvolvedoras a implementar, visualizar e testar suas APIs, como o Swagger UI, Swagger Editor e Swagger Codegen, tornando-se assim muito popular e utilizado ao redor do mundo.

Em 2015, o Swagger foi comprado pela empresa SmartBear Software, que doou a parte da especificação para a fundação Linux. Por sua vez, a fundação renomeou o projeto para OpenAPI. Após isso, foi criada a OpenAPI Initiative, uma organização focada no desenvolvimento e evolução da especificação OpenAPI de maneira aberta e transparente.

A OpenAPI é hoje a especificação mais utilizada, e também a principal, para documentar uma API Rest. A documentação segue um padrão que pode ser descrito no formato yaml ou JSON, facilitando a criação de ferramentas que consigam ler tais arquivos e automatizar a criação de documentações, bem como a geração de códigos para consumo de uma API.

Você pode obter mais detalhes no [site oficial da OpenAPI Initiative](https://www.openapis.org/).

### Personalizando a documentação

É possível personalizar a documentação gerada pelo SpringDoc para a inclusão do token de autenticação. Além do token, podemos incluir outras informações na documentação que fazem parte da especificação OpenAPI, como, por exemplo, a descrição da API, informações de contato e de sua licença de uso.

Tais configurações devem ser feitas no objeto OpenAPI, que foi configurado na classe SpringDocConfigurations de nosso projeto:
```
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .components(new Components()
                    .addSecuritySchemes("bearer-key",
                            new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")))
                    .info(new Info()
                            .title("Voll.med API")
                            .description("API Rest da aplicação Voll.med, contendo as funcionalidades de CRUD de médicos e de pacientes, além de agendamento e cancelamento de consultas")
                            .contact(new Contact()
                                    .name("Time Backend")
                                    .email("backend@voll.med"))
                    .license(new License()
                            .name("Apache 2.0")
                            .url("http://voll.med/api/licenca")));
}
```
Usando os imports:
```
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
```
No código anterior, repare que após a configuração do token JWT foram adicionadas as informações da API. Ao entrar novamente na página do Swagger UI, tais informações serão exibidas, conforme demonstrado na imagem a seguir:

![](imgs/swagger-UI-doc.png)

alt text: Página do Swagger Ui exibindo as informações da Voll.med API, onde se lê a mensagem “API Rest da aplicação Voll.med, contendo as funcionalidades de CRUD de médicos e de pacientes, além de agendamento e cancelamento de consultas.”

Para saber mais detalhes sobre quais informações podem ser configuradas na [documentação da API](https://spec.openapis.org/oas/latest.html#schema), consulte a especificação OpenAPI no site oficial da iniciativa.

## Aula 4. Testes Automatizados

- Escrever testes automatizados em uma aplicação com Spring Boot;
- Escrever testes automatizados de uma interface Repository, seguindo a estratégia de usar o mesmo banco de dados que a aplicação utiliza;
- Sobrescrever propriedades do arquivo application.properties, criando outro arquivo chamado application-test.properties que seja carregado apenas ao executar os testes, utilizando para isso a anotação @ActiveProfiles;
- Escrever testes automatizados de uma classe Controller, utilizando a classe MockMvc para simular requisições na API;
- Testar cenários de erro 400 e código 200 no teste de uma classe controller.

### Testes com in-memory database

Como citado no vídeo anterior, podemos realizar os testes de interfaces repository utilizando um banco de dados em memória, como o H2, ao invés de utilizar o mesmo banco de dados da aplicação.

Caso você queira utilizar essa estratégia de executar os testes com um banco de dados em memória, será necessário incluir o H2 no projeto, adicionando a seguinte dependência no arquivo pom.xml:
```
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```
E também deve remover as anotações @AutoConfigureTestDatabase e @ActiveProfiles na classe de teste, deixando-a apenas com a anotação @DataJpaTest:
```
@DataJpaTest
class MedicoRepositoryTest {

  //resto do código permanece igual

}
```
Você também pode apagar o arquivo application-test.properties, pois o Spring Boot realiza as configurações de url, username e password do banco de dados H2 de maneira automática.

## Aula 5. Build do projeto

- Funciona o build de uma aplicação com Spring Boot;
- Utilizar arquivos de propriedades específicos para cada profile, alterando em cada arquivo as propriedades que precisam ser modificadas;
- Configurar informações sensíveis da aplicação, como dados de acesso ao banco de dados, via variáveis de ambiente;
- Realizar o build do projeto via Maven;
- Executar a aplicação via terminal, com o comando java -jar, passando as variáveis de ambiente como parâmetro.

### Build com arquivo .war

Projetos que utilizam o Spring Boot geralmente utilizam o formato jar para o empacotamento da aplicação, conforme foi demonstrado ao longo desta aula. Entretanto, o Spring Boot fornece suporte para o empacotamento da aplicação via formato war, que era bastante utilizado em aplicações Java antigamente.

Caso você queira que o build do projeto empacote a aplicação em um arquivo no formato war, vai precisar realizar as seguintes alterações:

1. Adicionar a tag <packaging>war</packaging> no arquivo pom.xml do projeto, devendo essa tag ser filha da tag raiz <project>:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.0.0</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>
  <groupId>med.voll</groupId>
  <artifactId>api</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>api</name>

  <packaging>war</packaging>
```
2. Ainda no arquivo pom.xml, adicionar a seguinte dependência:

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
  <scope>provided</scope>
</dependency>
```
3. Alterar a classe main do projeto (ApiApplication) para herdar da classe SpringBootServletInitializer, bem como sobrescrever o método configure:
```
@SpringBootApplication
public class ApiApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ApiApplication.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }

}
```
Pronto! Agora, ao realizar o build do projeto, será gerado um arquivo com a extensão .war dentro do diretório target, ao invés do arquivo com a extensão .jar.


### GraalVM Native Image

Uma das novidades de mais destaque da versão 3 do Spring Boot é o suporte a imagens nativas, algo que reduz, de maneira muito significativa, o consumo de memória e o tempo de inicialização de uma aplicação, sendo que alguns outros frameworks concorrentes do Spring Boot, como Micronaut e Quarkus, já forneciam suporte a esse recurso.

Na realidade até era possível gerar imagens nativas em aplicações com Spring Boot antes da versão 3, mas para isso se fazia necessário a utilização de um projeto chamado Spring Native, que adicionava suporte a isso. Com a chegada da versão 3 do Spring Boot, tal projeto não é mais necessário.
Native Image

Imagem nativa é uma tecnologia utilizada para compilar uma aplicação Java, incluindo todas as suas dependências, gerando um arquivo binário executável que pode ser executado diretamente no sistema operacional, sem a necessidade de se utilizar a JVM. Mesmo sem executar numa JVM, a aplicação também contará com os recursos dela, como gerenciamento de memória, garbage collector e controle de execução de threads.

Para saber mais detalhes sobre a tecnologia de imagens nativas acesse a documentação no site: https://www.graalvm.org/native-image

### Native Image com Spring Boot 3

Uma maneira bem simples de gerar uma imagem nativa da aplicação é utilizando um plugin do Maven, que deve ser incluído no arquivo pom.xml:
```
<plugin>
  <groupId>org.graalvm.buildtools</groupId>
  <artifactId>native-maven-plugin</artifactId>
</plugin>
```
Pronto! Essa é a única alteração necessária no projeto. Após isso, a geração da imagem deve ser feita via terminal, com o seguinte comando Maven sendo executado no diretório raiz do projeto:
```
./mvnw -Pnative native:compile
```
O comando anterior pode levar vários minutos para finalizar sua execução, sendo totalmente normal essa demora.

Atenção! Para executar o comando anterior e gerar a imagem nativa do projeto, é necessário que você tenha instalado em seu computador o [GraalVM](https://www.graalvm.org/) (máquina virtual Java com suporte ao recurso de Native Image) em uma versão igual ou superior a 22.3.

Após o comando anterior finalizar, será gerado no terminal um log como o seguinte:
```
Top 10 packages in code area:           Top 10 object types in image heap:
   3,32MB jdk.proxy4                      19,44MB byte[] for embedded resources
   1,70MB sun.security.ssl                16,01MB byte[] for code metadata
   1,18MB java.util                        8,91MB java.lang.Class
 936,28KB java.lang.invoke                 6,74MB java.lang.String
 794,65KB com.mysql.cj.jdbc                6,51MB byte[] for java.lang.String
 724,02KB com.sun.crypto.provider          4,89MB byte[] for general heap data
 650,46KB org.hibernate.dialect            3,07MB c.o.s.c.h.DynamicHubCompanion
 566,00KB org.hibernate.dialect.function   2,40MB byte[] for reflection metadata
 563,59KB com.oracle.svm.core.code         1,30MB java.lang.String[]
 544,48KB org.apache.catalina.core         1,25MB c.o.s.c.h.DynamicHu~onMetadata
  61,46MB for 1482 more packages           9,74MB for 6281 more object types
--------------------------------------------------------------------------------
    9,7s (5,7% of total time) in 77 GCs | Peak RSS: 8,03GB | CPU load: 7,27
--------------------------------------------------------------------------------
Produced artifacts:
 /home/rodrigo/Desktop/api/target/api (executable)
 /home/rodrigo/Desktop/api/target/api.build_artifacts.txt (txt)
================================================================================
Finished generating 'api' in 2m 50s.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  03:03 min
[INFO] Finished at: 2023-01-17T12:13:04-03:00
[INFO] ------------------------------------------------------------------------
```
A imagem nativa é gerada no diretório target, juntamente com o arquivo .jar da aplicação, como um arquivo executável de nome api, conforme demonstrado na imagem a seguir:

alt text: Lista de arquivos e diretórios localizados dentro do diretório target do projeto, estando entre eles o arquivo da imagem nativa, cujo nome é **api**

Diferente do arquivo .jar, que é executado pela JVM via comando java -jar, a imagem nativa é um arquivo binário e deve ser executada diretamente pelo terminal:
```
target/api
```
Ao rodar o comando anterior será gerado o log de inicialização da aplicação, que ao final exibe o tempo que levou para a aplicação inicializar:
```
INFO 127815 --- [restartedMain] med.voll.api.ApiApplication : Started ApiApplication in 0.3 seconds (process running for 0.304)
```
Repare que a aplicação levou menos de meio segundo para inicializar, algo realmente impressionante, pois quando a executamos pela JVM, via arquivo .jar, esse tempo sobe para algo em torno de 5 segundos.

Para saber mais detalhes sobre a geração de uma imagem nativa com Spring Boot 3 acesse a documentação no site:

- [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)