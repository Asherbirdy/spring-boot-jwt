# java-mvc-sample

#### 匯入數據


```sql
CREATE DATABASE mymovie;
  CREATE TABLE member (
      member_id VARCHAR(36) PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL,
      role VARCHAR(50) DEFAULT 'user',
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  );

  CREATE TABLE token (
      token_id VARCHAR(36) PRIMARY KEY,
      refresh_token VARCHAR(255) NOT NULL,
      ip VARCHAR(50),
      user_agent VARCHAR(500),
      is_valid BOOLEAN DEFAULT TRUE,
      member_id VARCHAR(36) NOT NULL,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      FOREIGN KEY (member_id) REFERENCES member(member_id)
  );


```# spring-boot-jwt
