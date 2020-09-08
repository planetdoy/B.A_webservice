package com.breakaway.admin.BA_website.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

/*
    JpaRepository<Entity클래스 ,PK 타입>를 상속하면
    기본적은 CRUD 메소드가 자동으로 생성됩니다.
* */

public interface PostsRepository extends JpaRepository<Posts,Long> {
}
