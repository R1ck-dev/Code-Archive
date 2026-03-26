## Serviços disponíveis

- **frontend**: React + Nginx em `http://localhost:3000`
- **backend**: Spring Boot em `http://localhost:8080`
- **db**: PostgreSQL em `localhost:5432`

O frontend já está configurado para encaminhar chamadas `/api/*` para o backend.

## Como subir o projeto

```bash
docker compose up --build
```

## Como parar o projeto

```bash
docker compose down
```

## Como limpar também os dados do banco

```bash
docker compose down -v
```

> O volume `postgres_data` mantém os dados entre reinicializações.
