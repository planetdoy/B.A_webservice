package com.breakaway.admin.BA_website.web;

import com.breakaway.admin.BA_website.service.posts.PostsService;
import com.breakaway.admin.BA_website.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("posts", postsService.findAllDesc());
        return "index";
        /*
            Model
                서버 템플릿 엔진에서 사용할 수 있는 객체를 저장할 수 있습니다.
                여기서는 posts.Service.findAllDesc()로 가져온 결과를 posts로 index.mustache에 전달합니다.
        * */
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){

        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post",dto);

        return "posts-update";
    }


}
