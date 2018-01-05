package com.palmtree.salesforce.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for response for login call
 */
public class LoginResponse {
  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("instance_url")
  private String instanceUrl;

  private String id;

  @SerializedName("token_type")
  private String tokenType;

  @SerializedName("issued_at")
  private String issuedAt;

  private String signature;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getInstanceUrl() {
    return instanceUrl;
  }

  public void setInstanceUrl(String instanceUrl) {
    this.instanceUrl = instanceUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getIssuedAt() {
    return issuedAt;
  }

  public void setIssuedAt(String issuedAt) {
    this.issuedAt = issuedAt;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
}
