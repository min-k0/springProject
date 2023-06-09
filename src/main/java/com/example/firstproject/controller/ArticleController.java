package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
public class ArticleController {

    @Autowired // spring boot가 미리 생성해놓은 객체를 가져다가 자동 연결
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form){
        log.info(form.toString());

        // 1. Dto를 entity로 변환
        Article article = form.toEntity();
        log.info(article.toString());

        // 2. Repository에게 entity를 DB안에 저장하도록
        Article saved = articleRepository.save(article);
        log.info(saved.toString());

        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}") // 해당 URL요청을 처리 선언
    public String show(@PathVariable Long id, Model model){ // URL에서 id를 변수로 가져옴
        log.info("id = " + id);

        // 1: DB에서 id로 데이터를 찾기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 2: 가져온 데이터를 모델에 등록
        model.addAttribute("article", articleEntity);

        // 3: 보여줄 페이지를 설정
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model){
        // 1: 모든 Article을 가져온다
        List<Article> articleEntityList = articleRepository.findAll();

        // 2: 가져온 Article 묶음을 뷰로 전달
        model.addAttribute("articleList", articleEntityList);

        // 3: 뷰 페이지를 설정
        return "articles/index"; // templates/articles/index.mustache
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        // 수정할 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 모델에 데이터 등록
        model.addAttribute("article", articleEntity);

        // 뷰 페이지 설정
        return "/articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());

        // 1. DTO를 entity로 변환
        Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());

        // 2. entity를 DB로 저장
        // 2-1. DB에서 기존 데이터를 가져온다
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);

        // 2-2. 기존 데이터의 값을 갱신
        if(target != null){
            articleRepository.save(articleEntity);
        }

        // 3. 수정 결과 페이지로 redirect
        return "redirect:/articles/" + articleEntity.getId();
    }
}
