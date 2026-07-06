# sistema de pescd

api rest para controlar ofertas, alunos, professores, documentos e encerramento do pescd.

## tecnologias

- java 17
- spring boot 4
- spring security
- spring data jpa
- mysql
- maven

## como rodar

suba o mysql local e confira as credenciais em:

```text
src/main/resources/application.properties
```

por padrao:

```text
banco: db_pescd
usuario: root
senha: root
```

rode:

```bash
./mvnw spring-boot:run
```

a api fica em:

```text
http://localhost:8080
```

## autenticacao

a api usa http basic.

exemplo:

```bash
curl -u pedro.aluno:pedro1 http://localhost:8080/api/me
```

## usuarios de exemplo

```text
mario.admin:mario1
lucas.sec:lucas1
luis.prof:luis1
maria.sup:maria1
pedro.aluno:pedro1
ana.aluno:ana1
carlos.aluno:carlos1
leonardo.aluno:leonardo1
```

## rotas principais

```text
get  /api/ofertas-publicas
get  /api/me
get  /api/aluno/ofertas
get  /api/aluno/progresso
post /api/aluno/ofertas/{ofertaid}/plano
post /api/aluno/ofertas/{ofertaid}/documentacao
post /api/aluno/ofertas/{ofertaid}/relatorio
get  /api/professores
get  /api/professor/ofertas
get  /api/professor/responsavel/ofertas
get  /api/secretario/ofertas
post /api/secretario/ofertas
get  /api/admin/usuarios
post /api/admin/usuarios
```

## swagger

```text
http://localhost:8080/swagger-ui.html
```

## observacao

o banco e recriado ao iniciar porque a configuracao usa:

```text
spring.jpa.hibernate.ddl-auto=create
```
