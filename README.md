# Sistema de Feedback - PósTech (Azure Serverless)

Este projeto consiste em um ecossistema de feedback para cursos on-line, construído com Java 21 e Quarkus. A arquitetura é totalmente baseada no modelo Serverless, utilizando Azure Functions para processamento orientado a eventos, garantindo escalabilidade automática e baixo custo operacional.


## 🏗️ Arquitetura e Fluxo de Dados
O sistema opera de forma orientada a eventos, integrando serviços nativos da Microsoft Azure localizados no grupo de recursos rg-fiap-postech:
- Ingestão (FeedbackHttpFunction): Recebe feedbacks via API REST (HTTP Trigger).
- Mensageria (Azure Queue Storage): Se um feedback é classificado como crítico (nota ≤ 3), ele é enviado para a conta de armazenamento tc4postech na fila feedback-critico.
- Processamento de Alerta (NotificationQueueFunction): Esta função é disparada automaticamente ao detectar uma nova mensagem na fila. Ela processa o feedback e realiza o envio imediato de um e-mail via SMTP para a equipe de suporte.
- Relatórios (ReportTimerFunction): Uma função temporizada (Timer Trigger) que, semanalmente, consolida os dados do banco e envia um relatório gerencial por e-mail via SMTP.

## 🛠️ Tecnologias e Recursos Azure
O projeto utiliza os seguintes recursos configurados no grupo de recursos rg-fiap-postech:
- Runtime: Java 21 (Quarkus Framework).
- Serviço de Computação: Azure Function App functions-tc-postech.
- Banco de Dados: Azure Database for PostgreSQL (Servidor Flexível) db-postech.
- Mensageria/Storage: Azure Storage Account tc4postech com a fila feedback-critico.
- CI/CD: Autodeploy configurado via GitHub Actions.

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven (opcional, o wrapper `mvnw` está incluído)
- Azure CLI (opcional, para deploys manuais).

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
O sistema possui uma tarefa agendada que roda a cada 7 dias e disdara o envio desse relatório para o email do administrador.


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
- **Queue Function (a cada 1 semana)**: [NotificationQueueFunction.java](src/main/java/com/feedback/functions/NotificationQueueFunction.java)
- Aciona `NotificationService.notify`.
- A implementação atual utiliza fila da azure (Azure Queue Storage) para envio de notificações.
- **Porta HTTP**: respeita `PORT` quando definida pelo ambiente (ex.: Azure App Service/Functions).
