package com.wjj.demo.controller;

import com.wjj.demo.service.QAService;
import com.wjj.demo.vo.Answer;
import com.wjj.demo.vo.Person;
import com.wjj.demo.vo.Question;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Api(description = "一系列接口")
public class QAController {
    @Autowired
    private QAService service;

    @PostMapping("register")
    @ApiOperation("注册")
    public String register(@RequestParam String name, @RequestParam String account,
                           @RequestParam double money, @RequestParam String password,
                           HttpServletRequest request) {
        Person person = new Person();
        person.setName(name);
        person.setAccount(account);
        person.setMoney(money);
        person.setPassword(password);
        service.addPerson(person);
        request.getSession().setAttribute("account",person);
        return "注册成功";
    }

    @PostMapping("login")
    @ApiOperation("登陆")
    public String login(@RequestParam String account, @RequestParam String password,HttpServletRequest request) {
        if (request.getSession().getAttribute("account")!=null){
            return "登陆成功";
        }
        if (!account.equals("")) {
            Person person = service.hasPerson(account);
            if (person == null) {
                return "帐号不存在";
            } else if (person != null && person.getPassword().equals(password)) {
                request.getSession().setAttribute("account",person);
                return "登陆成功";
            } else {
                return "密码错误";
            }
        }
        return "请输入正确的帐号";
    }

    @PostMapping("logout")
    @ApiOperation("登出")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("account");
        return "登出成功";
    }


    @GetMapping("addQuestion")
    @ApiOperation("提交问题")
    public String addQuestion(@RequestParam String content, HttpServletRequest request) {
        String account = getAccount(request);
        if (!account.equals("")) {
            Question question = new Question();
            question.setAuthor(account);
            question.setContent(content);
            question.setId(UUID.randomUUID().toString());
            question.setAnswerMap(new ConcurrentHashMap<>());
            service.addQuestion(question);
            return service.questionList();
        }
        return "登陆异常，请重新登陆";
    }

    @GetMapping("getAllQuestion")
    @ApiOperation("获取问题列表")
    public String getAllQuestion() {
        return service.questionList();
    }

    @GetMapping("lookQuestion")
    @ApiOperation("查看问题")
    public String lookQuestion(@RequestParam String id) {
        return service.lookQuestion(id).getContent();
    }

    @GetMapping("addAnswer")
    @ApiOperation("提交答案")
    public void addAnswer(@RequestParam String questionId, @RequestParam String content,
                          @RequestParam double price, HttpServletRequest request) {
        String account = getAccount(request);
        Person person = service.hasPerson(account);
        Answer answer = new Answer();
        answer.setId(UUID.randomUUID().toString());
        answer.setQuestionId(questionId);
        answer.setContent(content);
        answer.setAuthor(person.getAccount());
        answer.setPrice(price);
        service.addAnswer(answer);
    }

    @GetMapping("buyAnswer")
    @ApiOperation("购买答案")
    public String buyAnswer(@RequestParam String questionId, @RequestParam String answerId,
                            HttpServletRequest request) {
        String account = getAccount(request);
        return service.buyAnswer(account, questionId, answerId);
    }

    @GetMapping("lookAnswer")
    @ApiOperation("查看答案")
    public String lookAnswer(@RequestParam String questionId, @RequestParam String answerId,
                             HttpServletRequest request) {
        String account = getAccount(request);
        return service.lookAnswer(account, questionId, answerId);
    }

    private String getAccount(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object person = session.getAttribute("account");
        if (person instanceof Person){
            Person person1 = (Person) person;
            return person1.getAccount();
        }
        return "";
    }
}
