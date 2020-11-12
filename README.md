# reactiveposts

Caches forums posts in a postgres database

# Run on docker 

eg `docker run --link postgis -e REACTIVEPOSTS_POSTS-UPDATE-INTERVAL=5m -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgis:5432/postsdb -e SPRING_PROFILES_ACTIVE=postgres -d -p8080:8080 gcr.io/badinko/reactiveposts:0.1.3-SNAPSHOT`

### maven release plugin
`./mvnw --batch-mode release:prepare && ./mvnw --batch-mode release:perform`