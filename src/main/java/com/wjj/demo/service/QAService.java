package com.wjj.demo.service;


import com.wjj.demo.vo.Answer;
import com.wjj.demo.vo.Person;
import com.wjj.demo.vo.Question;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class QAService {
    private static Map<String,Question> questionMap = new ConcurrentHashMap<>();
    private static Map<String,Person> personMap = new ConcurrentHashMap<>();

    public boolean addPerson(Person person){
        if (personMap.get(person.getAccount())==null){
            personMap.put(person.getAccount(),person);
            return true;
        }
        return false;
    }

    public Person hasPerson(String acount){
        return personMap.get(acount)==null?null:personMap.get(acount);
    }

    public void addQuestion(Question question){
        questionMap.put(question.getId(),question);
    }

    public String questionList(){
        StringBuilder sb =  new StringBuilder();
        for (String key :questionMap.keySet()){
            Question question = questionMap.get(key);
            ConcurrentHashMap<String, Answer> answerMap = question.getAnswerMap();

            sb.append(";;;;").append("questionId:").append(key).append("--").append(question.getContent()).append("--提出者：").append(question.getAuthor()).append(";;;;");
            for (String answerKey : answerMap.keySet()){
                sb.append(answerKey).append(",");
            }
            if (sb.lastIndexOf(",")==sb.length()-1){
                String substring = sb.substring(0, sb.length() - 1);
                sb.delete(0,sb.length());
                sb.append(substring);
            }

        }
        return sb.toString();
    }

    public void addAnswer(Answer answer){
        Question question = questionMap.get(answer.getQuestionId());
        question.getAnswerMap().put(answer.getId(),answer);
        questionMap.put(question.getId(),question);
    }

    public Question lookQuestion(String id){
        return questionMap.get(id);
    }

    public String lookAnswer(String account,String questionId,String answerId){
        Answer answer = questionMap.get(questionId).getAnswerMap().get(answerId);
        if (answer==null){
            return "请核对问题id和答案id";
        }
        if (answer.getAuthor().equals(account)){
            return answer.getContent();
        }
        if (answer.getPersonMap()!=null){
            if (answer.getPersonMap().get(account)!=null){
                return answer.getContent();
            }
            return "未对答案进行付款，请先购买答案";
        }
        return "未对答案进行付款，请先购买答案";

    }

    public String buyAnswer(String account,String questionId,String answerId){
        Answer answer = questionMap.get(questionId).getAnswerMap().get(answerId);
        if (answer.getPersonMap().get(account)!=null){
            return "你已经购买过该答案，无需重复购买";
        }
        Person person = personMap.get(account);
        if (person.getMoney()-answer.getPrice()>=0){
            answer.getPersonMap().put(account,person);
            return answer.getContent();
        }
        return "余额不足，请充值";
    }

    public double recharge(String account,double money){
        Person person = personMap.get(account);
        person.setMoney(person.getMoney()+money);
        personMap.put(account,person);
        return person.getMoney();
    }

    public String lookPerson(){
        StringBuilder sb = new StringBuilder();
        for (String key:personMap.keySet()){
            Person person = personMap.get(key);
            sb.append(person.getAccount()).append("--").append(person.getName()).append("--").append(person.getMoney()).append(";;;;");
        }
        return sb.toString();
    }
}
