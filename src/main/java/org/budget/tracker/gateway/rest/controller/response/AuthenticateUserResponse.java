package org.budget.tracker.gateway.rest.controller.response;

public class AuthenticateUserResponse extends BaseResponse {

  private String kind;
  private String email;
  private String profilePicture;
  private String localId;
  private String displayName;
  private String idToken;
  private Boolean registered;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getLocalId() {
    return localId;
  }

  public void setLocalId(String localId) {
    this.localId = localId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getIdToken() {
    return idToken;
  }

  public void setIdToken(String idToken) {
    this.idToken = idToken;
  }

  public Boolean getRegistered() {
    return registered;
  }

  public void setRegistered(Boolean registered) {
    this.registered = registered;
  }
}
