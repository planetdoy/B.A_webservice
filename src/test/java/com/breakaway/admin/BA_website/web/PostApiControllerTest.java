package com.breakaway.admin.BA_website.web;

import com.breakaway.admin.BA_website.domain.posts.PostsRepository;
import com.breakaway.admin.BA_website.web.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/*
    HelloController와 달리 @WebMvcTest를 사용하지 않음
    JPA 기능이 작동하지 않기 때문입니다.
    Controller와 ControllerAdvice 등 외부 연동과 관련된 부분만 활성화 되니
    지금같이 JPA 기능까지 한번에 테스트할 때는 @SpringBootTest와 TestRestTemplate을 사용하면 됩니다.
* */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception{

        postsRepository.deleteAll();
    }

    @Test
    public void Posts_be_registered() throws Exception{

        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);


    }
}
