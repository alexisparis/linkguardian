package org.blackdog.linkguardian.domain.transfer;

public class CountPerUser {
    private String userLogin;
    private String mail;
    private Long   count;

    public CountPerUser(String userLogin, String mail, Long count) {
        this.userLogin = userLogin;
        this.count  = count;
        this.mail = mail;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getMail() {
        return this.mail;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "CountPerUser{" + "userLogin='" + userLogin + '\'' + ", count=" + count + '}';
    }
}
