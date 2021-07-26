package com.edi.infrastructure.repository;

import java.util.List;

import javax.persistence.EntityManager;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.edi.domain.model.category.Category;
import com.edi.domain.model.category.CategoryRepository;

@Repository
public class HibernateCategoryRepository  extends HibernateSupport<Category> implements CategoryRepository {

  public HibernateCategoryRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public List<Category> getList() {
    Query<Category> query = getSession().createQuery("FROM Category where delete_user is null",Category.class);
    return query.list();
  }

  @Override
  public Category delete(Category cg) {
    getSession().createQuery("UPDATE from Category set delete_user = :delete_user, delete_date = :delete_date where id = :id and delete_user is null")
                              .setParameter("id", cg.getId())
                              .setParameter("delete_user", cg.getDelete_user())
                              .setParameter("delete_date", cg.getDelete_date())
                              .executeUpdate();
        return cg;
  }

  @Override
  public Category findById(String id) {
    Query<Category> query = getSession().createQuery("from Category where id = :id and delete_user is null", Category.class);
    query.setParameter("id", id);
    return query.uniqueResult();
  }

  @Override
  public Category update(Category cg) {
    getSession().createQuery("UPDATE from Category set name = :name, update_user = :update_user, update_date = :update_date where id = :id and delete_user is null")
                              .setParameter("id", cg.getId())
                              .setParameter("name", cg.getName())
                              .setParameter("update_user", cg.getUpdate_user())
                              .setParameter("update_date", cg.getUpdate_date())
                              .executeUpdate();

    return cg;
  }

}
