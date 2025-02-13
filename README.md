# Credit Simulation API
## Descrição 📖
A **Credit Simulation API** é uma aplicação backend desenvolvida em Kotlin, utilizando Spring Boot, 
para realizar simulações de crédito e cálculos relacionados a taxas de juros com base em perfis etários. 
A aplicação processa entradas de simulação, calcula valores baseados em regras predefinidas e retorna a resposta estruturada.

## Tecnologias Utilizadas 🛠️

* [![Kotlin][Kotlin]][Kotlin-url]
* [![Java][Java]][Java-url]
* [![Gradle][Gradle]][Gradle-url]
* [![Swagger][Swagger]][Swagger-url]
* [![Postgres][Postgres]][Postgres-url]
* [![Spring][Spring]][Spring-url]

---

## Funcionalidades ⚙️
- **Simulação de Credito:** Calcula os valores com base nas taxas de juros associadas a idade do cliente ou taxa variável.
- **Processamento de Arquivos CSV:** Importa e processa simulações de crédito em lote.


## Estrutura do Projeto 🏛
O projeto segue uma estrutura modular e escalável:

- **Controller:** Camada responsável pela comunicação entre o cliente e os serviços da aplicação (ex.: `CreditSimulationController`).
- **Service:** Implementa a lógica de negócios, como o cálculo de empréstimos (`LoanCalculationService`) e a busca de taxas de juros por faixa etária (`FindAgeInterestRateService`).
- **Repository:** Gerencia os dados da aplicação, com abstração para fontes de dados (ex.: `AgeInterestRateRepository`).
- **Models:** Define as classes de entrada e saída (requests/responses), organizando os dados de forma clara.
- **Exception:** Gerencia erros personalizados para tratamento de validações específicas.


## Justificativa da Arquitetura 📐
1. **Modularidade:** Separação de responsabilidades por camadas, promovendo um design limpo e fácil de entender.
2. **Escalabilidade:** Permite a adição de novas funcionalidades sem impactar outras áreas da aplicação.
3. **Manutenibilidade:** A organização facilita a identificação e correção de problemas.
4. **Testabilidade:** Estrutura clara que permite testes unitários e de integração de forma isolada.

---

## Como Iniciar 🚀

Siga os passos abaixo para configurar seu ambiente.

### Pré-requisitos 📋

* Docker e plugin docker compose v2
* Java 21
* Gradle 8.11.1

### Executando a Aplicação

1. Inicie a aplicação rodando esse script
   ```sh
   sh run.sh
   ```
2. Pare a aplicação rodando esse script
    ```sh
   sh stop.sh
   ```

## Utilizando a API ⚡

### Acessando via Swagger
http://localhost:8080/api/webjars/swagger-ui/index.html

### Collection postman
No endpoint `POST /v1/credit-simulations` possui um pre request script que gera um header Idempotency-Key em SHA256, 
para simular um client
<br>
[credit-simulation-api.postman_collection.json](collection/credit-simulation-api.postman_collection.json)

### Arquivo CSV com 10k para teste de endpoint

[testFile.csv](src/test/resources/testFile.csv)

### Endpoints Disponíveis:

* `POST /v1/credit-simulations` - Realiza a simulação de crédito de forma unitária.
<br>

Para simulação fixa passar o **interestRateType** FIXED e o **annualVariableInterestRate** deve ser null

```shell
curl --location 'http://localhost:8080/api/v1/credit-simulations' \
--header 'Idempotency-Key: any' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "email@email.com",
    "loanAmount": "800000",
    "customerDateOfBirth": "01/01/1995",
    "paymentTermInMonths": 12,
    "interestRateType": "FIXED"
}'
```

Para simulação variavel passar o **interestRateType** VARIABLE e o **annualVariableInterestRate** deve estar preenchido

```shell
curl --location 'http://localhost:8080/api/v1/credit-simulations' \
--header 'Idempotency-Key: any-variable' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "email@email.com",
    "loanAmount": "800000",
    "customerDateOfBirth": "01/01/1995",
    "paymentTermInMonths": 12,
    "interestRateType": "VARIABLE",
    "annualVariableInterestRate": 5
}'
  ```

* `POST /v1/credit-simulations/batch` - Realiza a simulação de crédito em lote por arquivo csv.
```shell
curl --location 'http://localhost:8080/api/v1/credit-simulations/batch' \
--form 'file=@"caminho-do-file-csv"'
```

[Kotlin]: https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[Java]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/
[Spring]: https://img.shields.io/badge/spring_boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[Postgres]: https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white
[Postgres-url]: https://www.postgresql.org/
[Spring-url]: https://spring.io/projects/spring-boot
[Gradle]: https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white
[Gradle-url]: https://gradle.org/
[Swagger]: https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white
[Swagger-url]: https://swagger.io/