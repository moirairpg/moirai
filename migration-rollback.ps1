param (
    [string]$amount
)

echo "Executing rollback for the last ${amount} migrations..."

mvn liquibase:rollback `-Dliquibase.rollbackCount=${amount} `-Dliquibase.password=$env:POSTGRES_PASSWORD `-Dliquibase.url=jdbc:postgresql://$env:POSTGRES_HOST/$env:POSTGRES_DB `-Dliquibase.username=$env:POSTGRES_USER `-Dliquibase.driver="org.postgresql.Driver"

echo "The last ${amount} migrations have been rolled back."