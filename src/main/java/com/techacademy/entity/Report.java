package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;

import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {


	public static enum Role {
        GENERAL("一般"), ADMIN("管理者");

        private String name;

        private Role(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }



    // ID
    @Id //主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer id;

    // 日付
    @NotNull(message = "値を入力してください")
    @Column(name = "report_date",nullable = false)
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate reportDate;




    // タイトル
    @NotEmpty(message ="値を入力してください")
    @Size(max = 100,message = "100文字以下で入力してください")
    @Column(length = 600, nullable = false)
    private String title;

    //　内容
    @NotEmpty(message = "値を入力してください")
    @Size(max = 600,message = "600文字以下で入力してください")
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String content;

    /* 社員番号
    @Column
    private String employee_code;
    */

    // 権限
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.GENERAL;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;





    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;







}