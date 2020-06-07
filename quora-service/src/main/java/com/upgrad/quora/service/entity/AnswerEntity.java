package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer", schema = "quora")
@NamedQueries(
        {
                @NamedQuery(name = "getAnswerById", query = "select a from AnswerEntity a where a.uuid=:answerId"),
                @NamedQuery(name = "getAllAnswersToAQuestion", query = "select a from AnswerEntity a where a.questionEntity=:questionEntity")
        }
)
public class AnswerEntity implements Serializable {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="uuid")
    @NotNull
    @Size(max=200)
    private String uuid;


    @Column(name="ans")
    @NotNull
    @Size(max=255)
    private String answer;

    @Column(name="date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne(fetch=FetchType.EAGER)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="user_id")
    @NotNull
    private UserEntity userEntity;

    @ManyToOne(fetch=FetchType.EAGER)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="question_id")
    @NotNull
    private QuestionEntity questionEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public QuestionEntity getQuestionEntity() {
        return questionEntity;
    }

    public void setQuestionEntity(QuestionEntity questionEntity) {
        this.questionEntity = questionEntity;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    /*
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }*/
}
