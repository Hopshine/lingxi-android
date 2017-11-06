package me.cl.lingxi.entity;

/**
 * author : bafsj
 * e-mail : bafs.jy@live.com
 * time   : 2017/04/16
 * desc   : 阿里OSS STS token
 * version: 1.0
 */

public class STSToken {

    private String accessKeyId;
    private String securityToken;
    private String accessKeySecret;
    private String expiration;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
