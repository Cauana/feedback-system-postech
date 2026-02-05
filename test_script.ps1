# Espera um pouco para garantir que o servidor subiu
Start-Sleep -Seconds 5

Write-Host "--- Criando Feedback Normal ---"
$body = @{
    descricao = "Curso muito bom!"
    nota = 5
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/feedbacks" -Method Post -Body $body -ContentType "application/json"

Write-Host "`n--- Criando Feedback Crítico ---"
$bodyCritical = @{
    descricao = "Curso péssimo, áudio ruim"
    nota = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/feedbacks" -Method Post -Body $bodyCritical -ContentType "application/json"

Write-Host "`n--- Listando Feedbacks ---"
Invoke-RestMethod -Uri "http://localhost:8080/feedbacks" -Method Get

Write-Host "`n--- Dashboard ---"
Invoke-RestMethod -Uri "http://localhost:8080/feedbacks/dashboard" -Method Get
