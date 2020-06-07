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
@Table(name = "question", schema = "quora")
@NamedQueries(
        {
                @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity q"),
                @NamedQuery(name = "getQuestionById", query = "select q from QuestionEntity q where q.uuid=:questionId"),
                @NamedQuery(name = "getAllQuestionsByUser", query = "select q from QuestionEntity q where q.userEntity=:userEntity")
        }
)
public class QuestionEntity implements Serializable {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="uuid")
    @NotNull
    @Size(max=200)
    private String uuid;


    @Column(name="content")
    @NotNull
    @Size(max=500)
    private String content;

    @Column(name="date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    @NotNull
    private UserEntity userEntity;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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