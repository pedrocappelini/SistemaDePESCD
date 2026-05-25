# Projeto PESCD

Projeto da Atividade Avaliativa 2 de DSW1

## Tecnologias Utilizadas:

* **Java com Spring Boot:** Framework base da aplicação.
* **Spring Data JPA:** Usado para a persistência de dados e criação automática das tabelas no banco.
* **MySQL:** Banco de dados relacional.
* **Lombok:** Biblioteca utilizada para reduzir a verbosidade do código.
  
## Configurando banco de dados

### Passo 1: Instalação
Instalar e rodar o mysql na maquina (porta 3306)

### Passo 2: Configuração das Credenciais
Em:
`src/main/resources/application.properties`

Atualiza as linhas abaixo com suas proprias credenciais locais:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_pescd?createDatabaseIfNotExist=true
spring.datasource.username=SEU_USUARIO_AQUI
spring.datasource.password=SUA_SENHA_AQUI
