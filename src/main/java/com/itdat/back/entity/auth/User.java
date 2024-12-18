package com.itdat.back.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


    @Entity
    @Table(name = "users")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(nullable = false, unique = true)
        private String userId;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String userName;

        private String company;

        private String userPhone;

        @Column(nullable = false, unique = true)
        private String userEmail;

        @Column(name = "user_birth")
        private LocalDate userBirth;

        private String companyRank;
        private String companyDept;
        private String companyFax;
        private String companyAddr;

        @Column(name = "company_addr_detail")
        private String companyAddrDetail;

        private String companyPhone;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserState userState = UserState.ACTIVE;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserType userType;

        @Enumerated(EnumType.STRING)
        private UserStatus status = UserStatus.ACTIVE;

        @Column(updatable = false)
        private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

        private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

        public User() {
        }

        public User(int id, String userId, String password, String userName, String company, String userPhone, String userEmail, LocalDate userBirth, String companyRank, String companyDept, String companyFax, String companyAddr, String companyAddrDetail,String companyPhone, UserState userState, UserType userType, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.userId = userId;
            this.password = password;
            this.userName = userName;
            this.company = company;
            this.userPhone = userPhone;
            this.userEmail = userEmail;
            this.userBirth = userBirth;
            this.companyRank = companyRank;
            this.companyDept = companyDept;
            this.companyFax = companyFax;
            this.companyAddr = companyAddr;
            this.companyAddrDetail = companyAddrDetail;
            this.companyPhone = companyPhone;
            this.userState = userState;
            this.userType = userType;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public LocalDate getUserBirth() {
            return userBirth;
        }

        public void setUserBirth(LocalDate userBirth) {
            this.userBirth = userBirth;
        }

        public String getCompanyRank() {
            return companyRank;
        }

        public void setCompanyRank(String companyRank) {
            this.companyRank = companyRank;
        }

        public String getCompanyDept() {
            return companyDept;
        }

        public void setCompanyDept(String companyDept) {
            this.companyDept = companyDept;
        }

        public String getCompanyFax() {
            return companyFax;
        }

        public void setCompanyFax(String companyFax) {
            this.companyFax = companyFax;
        }

        public String getCompanyAddr() {
            return companyAddr;
        }

        public void setCompanyAddr(String companyAddr) {
            this.companyAddr = companyAddr;
        }

        public String getCompanyAddrDetail() {
            return companyAddrDetail;
        }

        public void setCompanyAddrDetail(String companyAddrDetail) {
            this.companyAddrDetail = companyAddrDetail;
        }

        public String getCompanyPhone() {
            return companyPhone;
        }

        public void setCompanyPhone(String companyPhone) {
            this.companyPhone = companyPhone;
        }

        public UserState getUserState() {
            return userState;
        }

        public void setUserState(UserState userState) {
            this.userState = userState;
        }

        public UserType getUserType() {
            return userType;
        }

        public void setUserType(UserType userType) {
            this.userType = userType;
        }

        public UserStatus getStatus() {
            return status;
        }

        public void setStatus(UserStatus status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", userId='" + userId + '\'' +
                    ", password='" + password + '\'' +
                    ", userName='" + userName + '\'' +
                    ", company='" + company + '\'' +
                    ", userPhone='" + userPhone + '\'' +
                    ", userEmail='" + userEmail + '\'' +
                    ", userBirth=" + userBirth +
                    ", companyRank='" + companyRank + '\'' +
                    ", companyDept='" + companyDept + '\'' +
                    ", companyFax='" + companyFax + '\'' +
                    ", companyAddr='" + companyAddr + '\'' +
                    ", companyAddrDetail='" + companyAddrDetail + '\'' +
                    ", companyPhone='" + companyPhone + '\'' +
                    ", userState=" + userState +
                    ", userType=" + userType +
                    ", status=" + status +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }

    enum UserState { ACTIVE, AWAY, BUSY }
    enum UserType { BUSINESS, PERSONAL }
    enum UserStatus { ACTIVE, BANNED, REPORTED }

