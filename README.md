# Sistema de Feedback - PósTech

Este projeto é um sistema de feedback para cursos on-line, desenvolvido com Java, Quarkus e Docker. Ele foi projetado para ser executado localmente e preparado para implantação em ambiente Serverless (Azure Functions).

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven (opcional, o wrapper `mvnw` está incluído)

## Executando Localmente

1. **Inicie o Banco de Dados:**
   Execute o comando abaixo para subir o container do PostgreSQL via Docker Compose.
   ```bash
   docker-compose up -d feedback-db
   ```

2. **Execute a Aplicação:**
   Utilize o Maven Wrapper para iniciar a aplicação em modo de desenvolvimento (com Hot Reload).
   ```bash
   ./mvnw quarkus:dev
   ```
   A aplicação estará disponível em `http://localhost:8080`.

### Configuração de Ambiente
- Banco: `jdbc:postgresql://localhost:5433/feedback_db`
- Usuário/Senha padrão: `feedback_user` / `feedback_password`
- Variáveis suportadas:
  - `DB_URL`, `DB_USER`, `DB_PASSWORD`
  - `PORT` para definir a porta HTTP em ambientes gerenciados (ex.: Azure)

## Endpoints

### 1. Enviar Feedback
**POST** `/feedbacks`
```json
{
  "descricao": "O curso está excelente, mas o áudio do módulo 2 está baixo.",
  "nota": 8
}
```
*Se a nota for menor ou igual a 3, o feedback é marcado como URGENTE e uma notificação é simulada no log.*

### 2. Listar Feedbacks
**GET** `/feedbacks`

### 3. Dashboard
**GET** `/feedbacks/dashboard`

### Health
- **GET** `/q/health` (geral)
- **GET** `/q/health/live` (liveness)
- **GET** `/q/health/ready` (readiness)

## Relatórios Periódicos
O sistema possui uma tarefa agendada que roda a cada 1 minuto (para fins de demonstração) e imprime um relatório no console da aplicação.

## Arquitetura e Cloud
- **Quarkus**: Framework Java Cloud Native.
- **PostgreSQL**: Banco de dados relacional.
- **Azure Functions**: O projeto utiliza a extensão `quarkus-azure-functions-http`, permitindo que a mesma aplicação JAX-RS seja implantada como uma Azure Function sem alterações no código.

## Testes com Postman
1. Importe a coleção: `feedback-system.postman_collection.json`.
2. Execute:
   - "Criar Feedback (Normal)" → valida `status=PROCESSADO` e `urgencia=false`.
   - "Criar Feedback (Crítico)" → valida `status=NOTIFICADO` e `urgencia=true`.
3. "Listar Feedbacks" e "Dashboard" conferem listagem e estatísticas.

## Azure Functions (Serverless)
- **HTTP Function (POST /feedbacks)**: [FeedbackHttpFunction.java](src/main/java/com/feedback/functions/FeedbackHttpFunction.java)
  - Recebe JSON do feedback e delega para `FeedbackService.processar`.
  - Retorna `201` com o feedback processado.
- **Timer Function (a cada 1 semana)**: [ReportTimerFunction.java](src/main/java/com/feedback/functions/ReportTimerFunction.java)
  - Aciona `ReportService.gerarRelatorioPeriodico`.
- **Porta HTTP**: respeita `PORT` quando definida pelo ambiente (ex.: Azure App Service/Functions).
- **Notificações**: abstraídas em `NotificationService`. A implementação atual utiliza fila da azure (Azure Queue Storage) para envio de notificações.


