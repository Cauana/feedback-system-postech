@echo off
echo --- Create Feedback Normal ---
curl -v -X POST -H "Content-Type: application/json" -d "{\"descricao\": \"Curso bom\", \"nota\": 5}" http://localhost:8080/feedbacks
echo.
echo.
echo --- Create Feedback Critical ---
curl -v -X POST -H "Content-Type: application/json" -d "{\"descricao\": \"Curso ruim\", \"nota\": 1}" http://localhost:8080/feedbacks
echo.
echo.
echo --- List Feedbacks ---
curl -v http://localhost:8080/feedbacks
echo.
echo.
echo --- Dashboard ---
curl -v http://localhost:8080/feedbacks/dashboard
