package com.edi.domain.model.category;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import com.edi.domain.model.user.UserId;

@Entity
@Table(name = "cg_customer")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_CG_SEQUENCE")
  @SequenceGenerator(name="EDI_CG_SEQUENCE",sequenceName="edi_cg_seq", allocationSize = 1)
  private Long id;

  @Column(name = "depth", length = 5)
  private String depth;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "parent", length = 100)
  private String parent;

  @Column(name = "file_type")
  private String file_type;

  @Column(name = "delete_user")
  private String deleteUser;

  @Column(name = "delete_date")
  private String delete_date;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  public static Category create(UserId userid, String name, String file_type, String depth, String parent) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    Category cg = new Category();
    cg.depth = depth;
    cg.name = name;
    cg.file_type= file_type;
    cg.parent = parent;
    cg.create_user = userid.value().toString();
    cg.create_date = sqldate;

    return cg;
  }

  public static Category update(UserId userid, Long id, String name, String type) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    Category cg = new Category();
    cg.name = name;
    cg.id = id;
    if(type.equals("update")){
      cg.update_user = userid.value().toString();
      cg.update_date = sqldate;
    }else if(type.equals("delete")){
      cg.deleteUser = userid.value().toString();
      cg.delete_date = sqldate;
    }

    return cg;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDepth() {
    return depth;
  }

  public void setDepth(String depth) {
    this.depth = depth;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public String getFile_type() {
    return file_type;
  }

  public void setFile_type(String file_type) {
    this.file_type = file_type;
  }

  public String getDelete_user() {
    return deleteUser;
  }

  public void setDelete_user(String delete_user) {
    this.deleteUser = delete_user;
  }

  public String getDelete_date() {
    return delete_date;
  }

  public void setDelete_date(String delete_date) {
    this.delete_date = delete_date;
  }

  public String getUpdate_user() {
    return update_user;
  }

  public void setUpdate_user(String update_user) {
    this.update_user = update_user;
  }

  public String getUpdate_date() {
    return update_date;
  }

  public void setUpdate_date(String update_date) {
    this.update_date = update_date;
  }

  public String getCreate_user() {
    return create_user;
  }

  public void setCreate_user(String create_user) {
    this.create_user = create_user;
  }

  public String getCreate_date() {
    return create_date;
  }

  public void setCreate_date(String create_date) {
    this.create_date = create_date;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((delete_date == null) ? 0 : delete_date.hashCode());
    result = prime * result + ((deleteUser == null) ? 0 : deleteUser.hashCode());
    result = prime * result + ((depth == null) ? 0 : depth.hashCode());
    result = prime * result + ((file_type == null) ? 0 : file_type.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    result = prime * result + ((update_date == null) ? 0 : update_date.hashCode());
    result = prime * result + ((update_user == null) ? 0 : update_user.hashCode());
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
    Category other = (Category) obj;
    if (create_user == null) {
      if (other.create_user != null)
        return false;
    } else if (!create_user.equals(other.create_user))
      return false;
    if (create_date == null) {
      if (other.create_date != null)
        return false;
    } else if (!create_date.equals(other.create_date))
      return false;
    if (delete_date == null) {
      if (other.delete_date != null)
        return false;
    } else if (!delete_date.equals(other.delete_date))
      return false;
    if (deleteUser == null) {
      if (other.deleteUser != null)
        return false;
    } else if (!deleteUser.equals(other.deleteUser))
      return false;
    if (depth == null) {
      if (other.depth != null)
        return false;
    } else if (!depth.equals(other.depth))
      return false;
    if (file_type == null) {
      if (other.file_type != null)
        return false;
    } else if (!file_type.equals(other.file_type))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (parent == null) {
      if (other.parent != null)
        return false;
    } else if (!parent.equals(other.parent))
      return false;
    if (update_date == null) {
      if (other.update_date != null)
        return false;
    } else if (!update_date.equals(other.update_date))
      return false;
    if (update_user == null) {
      if (other.update_user != null)
        return false;
    } else if (!update_user.equals(other.update_user))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Category [create_user=" + create_user + ", created_date=" + create_date + ", delete_date=" + delete_date
        + ", delete_user=" + deleteUser + ", depth=" + depth + ", file_type=" + file_type + ", id=" + id + ", name="
        + name + ", parent=" + parent + ", update_date=" + update_date + ", update_user=" + update_user + "]";
  }

}
