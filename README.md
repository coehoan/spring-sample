### 도커에서 mysql 실행 명령어

```docker run -d --name sample_db -e MYSQL_ROOT_PASSWORD=sample_password -e MYSQL_DATABASE=sample_db -e MYSQL_USER=sample_user -e MYSQL_PASSWORD=sample_password -p 3306:3306 -v mysql_data:/var/lib/mysql mysql:latest```