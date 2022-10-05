package org.budget.tracker.gateway.builder;

import com.google.firebase.auth.UserRecord;
import org.budget.tracker.gateway.db.JUser;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;

import java.time.LocalDateTime;

public class UserBuilder {

  public static JUser with(CreateUserRequest request, UserRecord userRecord) {

    var jUser = new JUser();
    jUser.setEmail(request.getEmail());
    jUser.setFirstName(request.getFirstName());
    jUser.setLastName(request.getLastName());
    jUser.setFirebaseId(userRecord.getUid());
    jUser.setRegistered(Boolean.TRUE);
    jUser.setCreatedOn(LocalDateTime.now());
    return jUser;
  }
}
