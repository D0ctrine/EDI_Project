package com.edi.domain.model.user;

import com.edi.domain.common.model.AbstractBaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.text.SimpleDateFormat;

@Entity
@SequenceGenerator(
  name = "EDI_USER_SEQUENCE",
  sequenceName = "edi_user_seq",
  initialValue = 1,
  allocationSize=1
)
@Table(name = "EDI_USER")
public class User extends AbstractBaseEntity {

  //private static final long serialVersionUID = -538781580460070724L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_USER_SEQUENCE")
  private Long id;

  @Column(name = "username", length = 100, unique = true)
  private String username;

  @Column(name = "email_address", length = 100, unique = true)
  private String emailAddress;

  @Column(name = "password", length = 100)
  private String password;

  @Column(name = "usernumber", length = 10)
  private String usernumber;

  @Column(name = "auth", length = 5)
  private String auth;

  @Column(name = "type", length = 30)
  private String type;

  @Column(name = "useflag", length = 3)
  private String useflag;

  @Column(name = "created_date")
  private String createdDate;

  /**
   * Create new user
   */
  public static User create(String username, String emailAddress, String password) {

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    User user = new User();
    user.username = username;
    user.emailAddress = emailAddress;
    user.password = password;
    user.useflag = "Y";
    user.auth = "10";
    user.type = "user";
    user.createdDate = sqldate;
    return user;
  }

  public UserId getId() {
    return new UserId(id);
  }

  public String getUsername() {
    return username;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getPassword() {
    return password;
  }

  public String getUsernumber() {
    return usernumber;
  }

  public String getAuth() {
    return auth;
  }

  public String getType() {
    return type;
  }

  public String getUseflag() {
    return useflag;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  @Override
  public String toString() {
    return "User [auth=" + auth + ", createdDate=" + createdDate + ", emailAddress=" + emailAddress + ", id=" + id
        + ", password=" + password + ", type=" + type + ", useflag=" + useflag + ", username=" + username
        + ", usernumber=" + usernumber + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((auth == null) ? 0 : auth.hashCode());
    result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
    result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((useflag == null) ? 0 : useflag.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    result = prime * result + ((usernumber == null) ? 0 : usernumber.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    if (auth == null) {
      if (other.auth != null)
        return false;
    } else if (!auth.equals(other.auth))
      return false;
    if (createdDate == null) {
      if (other.createdDate != null)
        return false;
    } else if (!createdDate.equals(other.createdDate))
      return false;
    if (emailAddress == null) {
      if (other.emailAddress != null)
        return false;
    } else if (!emailAddress.equals(other.emailAddress))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (useflag == null) {
      if (other.useflag != null)
        return false;
    } else if (!useflag.equals(other.useflag))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    if (usernumber == null) {
      if (other.usernumber != null)
        return false;
    } else if (!usernumber.equals(other.usernumber))
      return false;
    return true;
  }



}
